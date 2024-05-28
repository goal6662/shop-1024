package com.goal.user.controller;

import com.goal.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/${app.config.api.version}/addresses")
public class AddressController {

    @Resource
    private UserService userService;

    @GetMapping("{id}")
    public Object detail(@PathVariable Long id) {
        return userService.getById(id);
    }


}
