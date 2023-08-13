package com.gg.myssm.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class ConnUtil {
    //德鲁伊数据库连接池
    private static ThreadLocal<Connection> tlConn = new ThreadLocal<>();
    private static ThreadLocal<List<PreparedStatement>> tlPps = new ThreadLocal<>();
    private static ThreadLocal<List<ResultSet>> tlRs = new ThreadLocal<>();

    static DataSource dataSource = null;

    static {
        Properties prop = new Properties();
        try {
            prop.load(ConnUtil.class.getResourceAsStream("druid.properties"));
            dataSource = DruidDataSourceFactory.createDataSource(prop);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PreparedStatement getPreparedStatement(String sqlCode,int statement) throws SQLException {
        PreparedStatement pps = getConn().prepareStatement(sqlCode, statement);
        List<PreparedStatement> ppsList = tlPps.get();
        if(ppsList == null) tlPps.set(ppsList = new LinkedList<>());
        ppsList.add(pps);
        return pps;
    }

    public static ResultSet getResultSet(PreparedStatement pps) throws SQLException {
        ResultSet rs = pps.executeQuery();
        List<ResultSet> rsList = tlRs.get();
        if(rsList == null)tlRs.set(rsList = new LinkedList<>());
        rsList.add(rs);
        return rs;
    }

    public static Connection getConn() throws SQLException {
        Connection conn = tlConn.get();
        if(conn == null){
            conn = dataSource.getConnection();
            tlConn.set(conn);
        }
        return conn;
    }

    public static void close() {
        try {

            List<ResultSet> rs = tlRs.get();
            if(rs !=null){
                for (ResultSet r : rs) r.close();
                tlRs.set(null);
            }

            List<PreparedStatement> pps = tlPps.get();
            if(pps !=null){
                for (PreparedStatement pp : pps) {
                    pp.close();
                }
                tlPps.set(null);
            }

            Connection conn = tlConn.get();
            if(conn !=null){
                conn.close();
                tlConn.set(null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
