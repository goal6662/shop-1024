package com.goal.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goal.user.domain.Address;
import com.goal.user.service.AddressService;
import com.goal.user.mapper.AddressMapper;
import org.springframework.stereotype.Service;

/**
* @author Goal
* @description 针对表【address(电商-公司收发货地址表)】的数据库操作Service实现
* @createDate 2024-05-28 23:27:37
*/
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address>
    implements AddressService{

}




