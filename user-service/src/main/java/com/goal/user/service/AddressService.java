package com.goal.user.service;

import com.goal.user.domain.Address;
import com.baomidou.mybatisplus.extension.service.IService;
import com.goal.user.domain.dto.AddressAddDTO;
import com.goal.user.domain.vo.AddressVO;
import com.goal.utils.Result;

/**
* @author Goal
* @description 针对表【address(电商-公司收发货地址表)】的数据库操作Service
* @createDate 2024-05-28 23:27:37
*/
public interface AddressService extends IService<Address> {

    /**
     * 新增收获地址
     * @param addressAddDTO
     * @return
     */
    Result add(AddressAddDTO addressAddDTO);

    /**
     * 获取详细地址
     * @param id 地址ID
     * @return
     */
    Result<AddressVO> getDetailById(Long id);

    /**
     * 根据id删除地址
     * @param id 地址id
     * @return
     */
    Result deleteById(Long id);
}
