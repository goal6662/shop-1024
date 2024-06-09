package com.goal.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.product.domain.po.ProductTask;
import com.goal.product.service.ProductTaskService;
import com.goal.product.mapper.ProductTaskMapper;
import org.springframework.stereotype.Service;

/**
* @author Goal
* @description 针对表【product_task】的数据库操作Service实现
* @createDate 2024-06-09 11:17:41
*/
@Service
public class ProductTaskServiceImpl extends ServiceImpl<ProductTaskMapper, ProductTask>
    implements ProductTaskService{

}




