package com.glx.bookshop.dao.Impl;


import com.gg.myssm.basedao.BasicDAO;
import com.glx.bookshop.dao.UserDao;
import com.glx.bookshop.pojo.User;

public class UserDaoImpl extends BasicDAO<User> implements UserDao {
    @Override
    public User getUser(String uname, String pwd) {
        return (User)super.querySingleValue("select * from t_user where uname = ? and pwd = ?",uname,pwd);
    }
}
