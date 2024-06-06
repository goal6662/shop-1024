package com.goal.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.order.domain.po.ProductOrder;
import com.goal.order.service.ProductOrderService;
import com.goal.order.mapper.ProductOrderMapper;
import org.springframework.stereotype.Service;

/**
* @author Goal
* @description 针对表【product_order】的数据库操作Service实现
* @createDate 2024-06-06 16:42:25
*/
@Service
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder>
    implements ProductOrderService{

}




