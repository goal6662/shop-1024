package com.goal.user.mapper;

import com.goal.user.domain.Address;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 获取用户的地址信息
     * @param userId
     * @param id
     * @return
     */
    Address getUserAddressById(@Param("userId") Long userId,
                               @Param("id") Long id);

    /**
     * 删除用户的地址信息
     * @param id 地址ID
     * @param userId 用户ID
     * @return 受影响的行数
     */
    int deleteUserAddressById(@Param("id") Long id, @Param("userId") Long userId);

    List<Address> listByUserId(@Param("userId") Long userId);
}




