package com.goal.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.domain.LoginUser;
import com.goal.user.domain.Address;
import com.goal.user.domain.dto.AddressAddDTO;
import com.goal.user.service.AddressService;
import com.goal.user.mapper.AddressMapper;
import com.goal.utils.Result;
import com.goal.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
* @author Goal
* @description 针对表【address(电商-公司收发货地址表)】的数据库操作Service实现
* @createDate 2024-05-28 23:27:37
*/
@Slf4j
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address>
    implements AddressService{

    @Resource
    private AddressMapper addressMapper;

    @Override
    public Result add(AddressAddDTO addressAddDTO) {
        LoginUser loginUser = UserContext.getUser();

        Address address = new Address();
        BeanUtils.copyProperties(addressAddDTO, address);
        address.setUserId(loginUser.getId());


        Address defaultAddress;
        // 新增地址是默认收获地址
        if (addressAddDTO.getDefaultStatus().equals(1) &&
                // 且已存在默认收获地址
                (defaultAddress = addressMapper.getDefaultAddressByUserId(address.getUserId())) != null) {

            // 修改默认收获地址为非默认收获地址
            addressMapper.updateDefaultAddressById(defaultAddress.getId());
        }
        address.setCreateTime(new Date());
        int row = addressMapper.insert(address);
        log.info("新增收获地址：row={}, address={}", row, address);
        return Result.success();
    }
}




