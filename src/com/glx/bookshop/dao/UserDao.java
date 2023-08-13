package com.glx.bookshop.dao;

import com.glx.bookshop.pojo.User;

public interface UserDao {
    User getUser(String uname, String pwd);
}
