package com.glx.bookshop.services.Impl;

import com.glx.bookshop.dao.UserDao;
import com.glx.bookshop.pojo.User;
import com.glx.bookshop.services.UserService;

public class UserServiceImpl implements UserService {

    private UserDao userDao;
    @Override
    public User login(String uname, String pwd) {
        return userDao.getUser(uname,pwd);
    }
}
