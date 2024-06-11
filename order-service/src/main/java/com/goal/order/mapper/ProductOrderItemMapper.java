package com.goal.order.mapper;

import com.goal.order.domain.po.ProductOrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Goal
* @description 针对表【product_order_item】的数据库操作Mapper
* @createDate 2024-06-06 16:42:25
* @Entity com.goal.order.domain.po.ProductOrderItem
*/
public interface ProductOrderItemMapper extends BaseMapper<ProductOrderItem> {

    /**
     * 批量插入订单项
     * @param productOrderItemList
     * @return
     */
    int insertBatch(@Param("itemList") List<ProductOrderItem> productOrderItemList);
}




