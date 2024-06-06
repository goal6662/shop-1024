package com.goal.product.mapper;

import com.goal.product.domain.po.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goal.product.domain.vo.ProductVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Goal
* @description 针对表【product】的数据库操作Mapper
* @createDate 2024-06-04 19:12:59
* @Entity com.goal.coupon.domain.po.Product
*/
public interface ProductMapper extends BaseMapper<Product> {

    List<Product> listByIdBatch(@Param("idList") List<Long> productIdList);
}




