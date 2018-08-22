package com.mmall.controller.backend;

import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/manage/user")
@Controller
public class UserManageController {


    @Autowired
    private IUserService iUserService;
}
