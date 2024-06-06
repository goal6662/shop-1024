package com.goal.product.service.impl;

import cn.hutool.json.JSONUtil;
import com.goal.enums.BizCodeEnum;
import com.goal.exception.BizException;
import com.goal.product.domain.dto.CartItemDTO;
import com.goal.product.domain.vo.CartItemVO;
import com.goal.product.domain.vo.ProductVO;
import com.goal.product.service.CartService;
import com.goal.product.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl extends AbstractCartService implements CartService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ProductService productService;

    @Override
    public void addToCart(CartItemDTO cartItemDTO) {

        // 1. 获取商品数据
        long productId = cartItemDTO.getId();
        int buyNum = cartItemDTO.getBuyNum();

        // 2. 获取购物车
        BoundHashOperations<String, Object, Object> cart = getCartOps();

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

    @Override
    public void clearUserCart() {
        redisTemplate.delete(getCartKey());
    }


    private BoundHashOperations<String, Object, Object> getCartOps() {
        String cartKey = getCartKey();
        return redisTemplate.boundHashOps(cartKey);
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
}
