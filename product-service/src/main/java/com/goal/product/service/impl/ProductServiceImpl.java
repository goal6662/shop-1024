package com.goal.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.product.domain.po.Product;
import com.goal.product.service.ProductService;
import com.goal.product.mapper.ProductMapper;
import org.springframework.stereotype.Service;

/**
* @author Goal
* @description 针对表【product】的数据库操作Service实现
* @createDate 2024-06-04 19:12:59
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService {

}




