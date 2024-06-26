package com.goal.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.domain.mq.ProductMessage;
import com.goal.enums.BizCodeEnum;
import com.goal.enums.order.ProductOrderStateEnum;
import com.goal.exception.BizException;
import com.goal.product.domain.ProductTaskStatusEnum;
import com.goal.product.domain.dto.CartItemDTO;
import com.goal.product.domain.dto.ProductLockDTO;
import com.goal.product.domain.po.Product;
import com.goal.product.domain.po.ProductTask;
import com.goal.product.domain.vo.ProductVO;
import com.goal.product.feigin.OrderFeignService;
import com.goal.product.mapper.ProductTaskMapper;
import com.goal.product.mq.RabbitMQService;
import com.goal.product.service.ProductService;
import com.goal.product.mapper.ProductMapper;
import com.goal.utils.Result;
import com.goal.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author Goal
* @description 针对表【product】的数据库操作Service实现
* @createDate 2024-06-04 19:12:59
*/
@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private ProductTaskMapper productTaskMapper;

    @Resource
    private RabbitMQService rabbitMQService;

    @Resource
    private OrderFeignService orderFeignService;

    @Override
    public Result pageProduct(int page, int size) {
        Page<Product> pageInfo = new Page<>(page, size);

        Page<Product> selectPage = productMapper.selectPage(pageInfo,
                new LambdaQueryWrapper<Product>().orderByDesc(Product::getCreateTime));

        // 封装分页返回结果
        Map<String, Object> pageMap = Map.of(
                "total_record", selectPage.getTotal(),
                "total_page", selectPage.getPages(),
                "current_data", selectPage.getRecords()
                        .stream().map((this::transferToVO)).collect(Collectors.toList())
        );

        return Result.success(pageMap);
    }

    @Override
    public ProductVO findDetailById(long productId) {
        Product product = productMapper.selectById(productId);
        return product == null ? null : transferToVO(product);
    }

    @Override
    public List<ProductVO> findProductByIdBatch(List<Long> productIdList) {
        if (productIdList.isEmpty()) {
            return null;
        }

        List<Product> products = productMapper.listByIdBatch(productIdList);
        return products.stream().map(this::transferToVO).collect(Collectors.toList());
    }

    /**
     * 锁定商品库存
     * @param productLockDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result lockProducts(ProductLockDTO productLockDTO) {

        Long userId = UserContext.getUser().getId();
        // 获取订单号
        String outTradeNo = productLockDTO.getOutTradeNo();

        if (!hasPermission(userId, outTradeNo)) {
            throw new BizException(BizCodeEnum.ORDER_NO_EXIST);
        }

        List<CartItemDTO> orderItemList = productLockDTO.getOrderItemList();

        // 获取商品项ID
        List<Long> orderItemIdList = orderItemList.stream().map(CartItemDTO::getProductId)
                .collect(Collectors.toList());

        // 查询对应的商品项
        List<ProductVO> productVOList = findProductByIdBatch(orderItemIdList);
        Map<Long, ProductVO> productVOMap = productVOList.stream()
                .collect(Collectors.toMap(ProductVO::getId, Function.identity()));

        for (CartItemDTO item : orderItemList) {
            int rows = productMapper.lockProductStock(item.getProductId(), item.getBuyNum());
            if (rows != 1) {
                // 库存锁定失败
                throw new BizException(BizCodeEnum.PRODUCT_NO_STOCK);
            } else {
                // 插入锁定记录
                ProductVO productVO = productVOMap.get(item.getProductId());
                ProductTask productTask = new ProductTask();

                productTask.setProductId(item.getProductId());
                productTask.setProductName(productVO.getTitle());
                productTask.setBuyNum(item.getBuyNum());
                productTask.setLockState(ProductTaskStatusEnum.LOCK.name());
                productTask.setCreateTime(new Date());
                productTask.setOutTradeNo(outTradeNo);

                productTaskMapper.insert(productTask);
                log.info("商品库存锁定，插入task任务：{}", productTask);

                // 发送延迟消息，恢复商品库存
                ProductMessage productMessage = new ProductMessage();

                productMessage.setOutTradeNo(outTradeNo);
                productMessage.setTaskId(productTask.getId());

                // 发送延迟消息
                rabbitMQService.sendMessageToDelayQueue(productMessage);
                log.info("商品库存锁定，延迟消息发送成功：{}", productMessage);
            }
        }
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean releaseProductStock(ProductMessage productMessage) {
        // 1. 查找任务单是否存在
        ProductTask productTask = productTaskMapper.selectById(productMessage.getTaskId());
        if (productTask == null) {
            log.warn("锁定库存-任务单不存在：{}", productMessage);
            return true;
        }

        if (productTask.getLockState().equalsIgnoreCase(ProductTaskStatusEnum.LOCK.name())) {
            Result<String> result = orderFeignService.queryProductOrderState(productMessage.getOutTradeNo());

            if (result.getCode() == BizCodeEnum.OPS_SUCCESS.getCode()) {
                // 响应成功代表找到了订单
                if (result.getData().equalsIgnoreCase(ProductOrderStateEnum.PAY.name())) {
                    // 订单已支付，设置任务为完成
                    productTask.setLockState(ProductTaskStatusEnum.FINISH.name());
                    productTaskMapper.updateById(productTask);
                    return true;
                }

                if (result.getData().equalsIgnoreCase(ProductOrderStateEnum.NEW.name())) {
                    // 订单还未被取消，重新投放消息
                    return false;
                }

            }

            log.error("订单不存在或被取消：{}", productMessage);
            // 设置任务为取消状态
            productTask.setLockState(ProductTaskStatusEnum.CANCEL.name());
            productTaskMapper.updateById(productTask);

            // 恢复库存
            productMapper.changLockStockById(productTask.getProductId(), productTask.getBuyNum());
        } else {
            log.warn("任务单不是锁定状态，不进行处理：{}", productMessage);
        }
        return true;
    }

    private boolean hasPermission(Long userId, String outTradeNo) {
        // TODO: 2024/6/9 查询用户是否拥有该订单
        return true;
    }

    private ProductVO transferToVO(Product product) {
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(product, productVO);
        return productVO;
    }

}




