package com.goal.user.mapper;

import com.goal.user.domain.Address;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Goal
* @description 针对表【address(电商-公司收发货地址表)】的数据库操作Mapper
* @createDate 2024-05-28 23:27:37
* @Entity com.goal.user.domain.Address
*/
public interface AddressMapper extends BaseMapper<Address> {

    /**
     * 查询用户默认收获地址
     * @param userId 用户ID
     * @return
     */
    Address getDefaultAddressByUserId(Long userId);

    /**
     * 修改默认收获地址为非默认收获地址
     * @param id 地址id
     */
    void updateDefaultAddressById(Long id);
}




