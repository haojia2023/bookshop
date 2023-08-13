package com.glx.bookshop.services;

import com.glx.bookshop.pojo.User;

public interface UserService {
    User login(String uname, String pwd);
}
