package com.goal.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.order.domain.po.ProductOrderItem;
import com.goal.order.service.ProductOrderItemService;
import com.goal.order.mapper.ProductOrderItemMapper;
import org.springframework.stereotype.Service;

/**
* @author Goal
* @description 针对表【product_order_item】的数据库操作Service实现
* @createDate 2024-06-06 16:42:25
*/
@Service
public class ProductOrderItemServiceImpl extends ServiceImpl<ProductOrderItemMapper, ProductOrderItem>
    implements ProductOrderItemService{

}




