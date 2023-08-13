package com.gg.myssm.trans;

import com.gg.myssm.util.ConnUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManger {

    public static void beginTrans() throws SQLException {
        ConnUtil.getConn().setAutoCommit(false);
    }

    public static void commit() throws SQLException {
        Connection conn = ConnUtil.getConn();
        conn.commit();
        ConnUtil.close();
    }

    public static void rollback() throws SQLException {
        ConnUtil.getConn().rollback();
    }

}
