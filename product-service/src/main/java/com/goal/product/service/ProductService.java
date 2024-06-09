package com.goal.product.service;

import com.goal.product.domain.dto.ProductLockDTO;
import com.goal.product.domain.po.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goal.product.domain.vo.ProductVO;
import com.goal.utils.Result;

import java.util.List;

/**
* @author Goal
* @description 针对表【product】的数据库操作Service
* @createDate 2024-06-04 19:12:59
*/
public interface ProductService extends IService<Product> {

    Result pageProduct(int page, int size);

    /**
     * 查询商品详情
     * @param productId
     * @return
     */
    ProductVO findDetailById(long productId);

    /**
     * 根据ID批量查询商品
     * @param productIdList 商品ID
     * @return
     */
    List<ProductVO> findProductByIdBatch(List<Long> productIdList);

    /**
     * 锁定商品库存
     * @param productLockDTO
     * @return
     */
    Result lockProducts(ProductLockDTO productLockDTO);
}
