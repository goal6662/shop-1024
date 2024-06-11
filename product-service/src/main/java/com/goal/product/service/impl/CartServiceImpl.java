package com.goal.product.service.impl;

import cn.hutool.json.JSONUtil;
import com.goal.constant.CacheKey;
import com.goal.enums.BizCodeEnum;
import com.goal.exception.BizException;
import com.goal.product.domain.dto.CartItemDTO;
import com.goal.product.domain.vo.CartItemVO;
import com.goal.product.domain.vo.ProductVO;
import com.goal.product.service.CartService;
import com.goal.product.service.ProductService;
import com.goal.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl extends AbstractCartService implements CartService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ProductService productService;

    @Override
    public void addToCart(CartItemDTO cartItemDTO) {
        BoundHashOperations<String, Object, Object> cart = getCartOps();
        this.addItemToCart(cart, cartItemDTO);
    }

    @Override
    public void clearUserCart() {
        redisTemplate.delete(getCartKey());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CartItemVO> confirmOrderCartItems(List<Long> productIdList) {

        // 获取全部购物项以及最新价格
        List<CartItemVO> cartItemVOList = buildCartItem(true);

        // 获取到所有下单的购物项
        List<CartItemVO> resultList = cartItemVOList.stream().filter((item) -> {
            if (productIdList.contains(item.getProductId())) {

                // 删除购物项
                this.deleteItemById(item.getProductId());
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        log.info("本次下单的购物项有：{}", resultList);

        return resultList;
    }

    @Override
    public void addItemsToCart(List<CartItemDTO> cartItemDTOList, Long userId) {
        String cartKey = CacheKey.getCartKey(userId);
        BoundHashOperations<String, Object, Object> myCart = redisTemplate.boundHashOps(cartKey);

        cartItemDTOList.forEach((item) -> this.addItemToCart(myCart, item));

    }

    @Override
    protected BoundHashOperations<String, Object, Object> getCartOps() {
        String cartKey = getCartKey();
        return redisTemplate.boundHashOps(cartKey);
    }

    @Override
    protected String getCartKey()  {
        Long userId = UserContext.getUser().getId();
        return CacheKey.getCartKey(userId);
    }

    @Override
    protected List<CartItemVO> buildCartItem(boolean latestPrice) {

        BoundHashOperations<String, Object, Object> myCart = getCartOps();

        List<Object> itemList = myCart.values();

        List<Long> productList = new ArrayList<>();
        List<CartItemVO> cartItemVOList = new ArrayList<>();
        if (itemList != null) {
            cartItemVOList = itemList.stream().map((item) -> {
                        CartItemVO cartItemVO = JSONUtil.toBean((String) item, CartItemVO.class);
                        // 保存ID
                        productList.add(cartItemVO.getProductId());
                        return cartItemVO;
                    })
                    .collect(Collectors.toList());
        }

        // 要查询最新价格
        if (latestPrice) {
            setProductLatestPrice(cartItemVOList, productList);
        }

        return cartItemVOList;
    }

    /**
     * 查询并设置最新的商品价格
     * @param cartItemVOList
     * @param productIdList
     */
    private void setProductLatestPrice(List<CartItemVO> cartItemVOList, List<Long> productIdList) {

        // 建立ID和Product实体的映射
        List<ProductVO> productVOList = productService.findProductByIdBatch(productIdList);
        Map<Long, ProductVO> productVOMap = productVOList.stream()
                .collect(Collectors.toMap(ProductVO::getId, Function.identity()));

        // 设置最新价格
        cartItemVOList.forEach((item) -> {
            ProductVO productVO = productVOMap.get(item.getProductId());
            item.setPrice(productVO.getPrice());
        });

    }

    /**
     * 添加物品项到购物车
     * @param cart 购物车
     * @param cartItemDTO 物品项
     */
    private void addItemToCart(BoundHashOperations<String, Object, Object> cart, CartItemDTO cartItemDTO) {

        // 1. 获取商品数据
        long productId = cartItemDTO.getProductId();
        int buyNum = cartItemDTO.getBuyNum();

        // 从购物车获取商品
        Object product = cart.get(productId);
        String result = "";

        // 没有获取到商品
        if (product != null) {
            result = (String) product;
        }

        CartItemVO cartItemVO;
        if (StringUtils.isBlank(result)) {
            // 新建一个商品
            cartItemVO = new CartItemVO();

            // 查找商品详情
            ProductVO productVO = productService.findDetailById(productId);
            if (productVO == null) {
                throw new BizException(BizCodeEnum.PRODUCT_UNAVAILABLE);
            }

            cartItemVO.setPrice(productVO.getPrice());
            cartItemVO.setBuyNum(buyNum);
            cartItemVO.setProductId(productId);
            cartItemVO.setProductImg(productVO.getCoverImg());
            cartItemVO.setProductTitle(productVO.getTitle());

        } else {
            // 已存在商品，修改数量
            cartItemVO = JSONUtil.toBean(result, CartItemVO.class);
            cartItemVO.setBuyNum(cartItemVO.getBuyNum() + buyNum);
        }
        // 更新数据
        cart.put(productId, JSONUtil.toJsonStr(cartItemVO));

    }
}
