package com.goal.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.product.domain.po.Product;
import com.goal.product.domain.vo.ProductVO;
import com.goal.product.service.ProductService;
import com.goal.product.mapper.ProductMapper;
import com.goal.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author Goal
* @description 针对表【product】的数据库操作Service实现
* @createDate 2024-06-04 19:12:59
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService {

    @Resource
    private ProductMapper productMapper;

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

    private ProductVO transferToVO(Product product) {
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(product, productVO);
        return productVO;
    }

}




