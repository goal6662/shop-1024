package com.goal.order.mapper;

import com.goal.order.domain.po.ProductOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author Goal
* @description 针对表【product_order】的数据库操作Mapper
* @createDate 2024-06-06 16:42:25
* @Entity com.goal.order.domain.po.ProductOrder
*/
public interface ProductOrderMapper extends BaseMapper<ProductOrder> {

    /**
     * 根据订单号查询订单状态
     * @param outTradeNo 订单号
     * @return 订单
     */
    ProductOrder getStateByOutTradeNo(@Param("outTradeNo") String outTradeNo);

    /**
     * 根据订单号变更订单状态
     * @param outTradeNo 订单号
     * @param to 目标状态
     * @param from 源状态
     * @return 影响行数
     */
    int updateOrderPayStatus(@Param("outTradeNo") String outTradeNo, @Param("to") String to,
                             @Param("from") String from);

    /**
     * 通过订单号获取订单
     * @param outTradeNo 订单号
     * @return 订单
     */
    ProductOrder getOrderByOutTradeNo(@Param("outTradeNo") String outTradeNo);
}




