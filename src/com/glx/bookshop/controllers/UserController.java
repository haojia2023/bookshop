package com.glx.bookshop.controllers;

import com.glx.bookshop.services.UserService;

public class UserController {

    UserService userService;

    public  String login(String uname,String pwd){
        userService.login(uname,pwd);
        return "index";
    }
}
