package com.goal.product.service.impl;

import cn.hutool.json.JSONUtil;
import com.goal.enums.BizCodeEnum;
import com.goal.exception.BizException;
import com.goal.product.domain.dto.CartItemDTO;
import com.goal.product.domain.vo.CartItemVO;
import com.goal.product.domain.vo.CartVO;
import com.goal.product.service.CartService;
import org.springframework.data.redis.core.BoundHashOperations;

import java.util.List;

abstract public class AbstractCartService implements CartService {


    @Override
    public CartVO getMyCart() {
        // 1. 查询所有购物项
        List<CartItemVO> cartItemVOList = buildCartItem(false);

        CartVO cartVO = new CartVO();
        cartVO.setCartItems(cartItemVOList);

        return cartVO;
    }

    @Override
    public void deleteItemById(Long productId) {
        BoundHashOperations<String, Object, Object> myCart = getCartOps();
        myCart.delete(productId);
    }

    @Override
    public void changeItemNum(CartItemDTO cartItemDTO) {
        BoundHashOperations<String, Object, Object> myCart = getCartOps();

        // 从缓存查询购物项
        Object cacheObj = myCart.get(cartItemDTO.getId());
        if (cacheObj == null) {
            throw new BizException(BizCodeEnum.PRODUCT_MODIFY_ERROR);
        }

        // 更新数量
        String cartItem = (String) cacheObj;
        CartItemVO cartItemVO = JSONUtil.toBean(cartItem, CartItemVO.class);
        cartItemVO.setBuyNum(cartItemDTO.getBuyNum());

        // 更新缓存
        myCart.put(cartItemVO.getProductId(), JSONUtil.toJsonStr(cartItemVO));
    }

    /**
     * 购物车缓存 key
     * @return
     */
    protected abstract String getCartKey();

    /**
     * 获取购物项
     * @param latestPrice 是否获取最新价格, 下单时一定要获取最新价格
     * @return
     */
    protected abstract List<CartItemVO> buildCartItem(boolean latestPrice);

    /**
     * 根据购物车key获取value
     * @return
     */
    protected abstract BoundHashOperations<String, Object, Object> getCartOps();
}
