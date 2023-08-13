package com.gg.myssm.basedao;

import com.gg.myssm.util.ConnUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class BasicDAO<T> {

    private Class<T> tClass;
    private final static String keyName = "Id";

    public BasicDAO(){
        //获取T的class地址，并加载进tClass
        String typeName = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName();
        try {
            tClass = (Class<T>) Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private PreparedStatement query(String sqlCode, Object... steadObj) throws SQLException {
        return query(sqlCode,Statement.NO_GENERATED_KEYS,steadObj);
    }

    private PreparedStatement query(String sqlCode,int statement, Object... steadObj) throws SQLException {
        PreparedStatement pps = ConnUtil.getPreparedStatement(sqlCode,statement);
        if(steadObj !=null)
            for (int i = 0; i < steadObj.length; i++)
                pps.setObject(i+1, steadObj[i]);
        return pps;
    }

    private Object resultSingleObj(ResultSet resultSet, Integer columnCount) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        return resultSingleObj(resultSet, columnCount,null);
    }
    private Object resultSingleObj(ResultSet resultSet, Integer columnCount,T sourceObj) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            if(sourceObj==null)
                sourceObj = (T) tClass.getDeclaredConstructor().newInstance();
            for (int i = 1; i <= columnCount; i++) {
                Field tClassField = tClass.getDeclaredField(resultSet.getMetaData().getColumnLabel(i));
                tClassField.setAccessible(true);
                Object resultSetObject = resultSet.getObject(i);
                String memberStr = tClassField.getType().getName();
                if(!isMyType(memberStr))
                    resultSetObject = Class.forName(memberStr).getDeclaredConstructor(Integer.class).newInstance(resultSetObject);
                tClassField.set(sourceObj, resultSetObject);
            }
            return sourceObj;
        } catch (NoSuchFieldException e) {
            return resultSet.getObject(1);
        }
    }

    private boolean isMyType(String typeName){
        return typeName.startsWith("java");
    }

    public void querySetT(T tObj,String sqlCode,Object...steadObj){
        try {
            ResultSet resultSet = ConnUtil.getResultSet(query(sqlCode, steadObj));
            if(resultSet.next())
                resultSingleObj(resultSet,resultSet.getMetaData().getColumnCount(),tObj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DaoException("Dao层出现错误");
        }
    }


    public Object querySingleValue(String sqlCode,Object... steadObj){
        try {
            ResultSet resultSet = ConnUtil.getResultSet(query(sqlCode, steadObj));
            if(resultSet.next())
                return resultSingleObj(resultSet,resultSet.getMetaData().getColumnCount());
        } catch (Exception e) {
            e.printStackTrace();
            throw new DaoException("Dao层出现错误");
        }
        return null;
    }

    public List queryList(String sqlCode,Object... steadObj)  {
        List list = new LinkedList();
        try {
            ResultSet resultSet = ConnUtil.getResultSet(query(sqlCode, steadObj));
            int columnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next())
                list.add(resultSingleObj(resultSet,columnCount));
        } catch (Exception e) {
            e.printStackTrace();
            throw new DaoException("Dao层出现错误");
        }
        return !list.isEmpty()?list:null;
    }

    public boolean update(String sqlCode,Object... steadObj) {
        try {
            return query(sqlCode, steadObj).executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("Dao层出现错误");
        }
    }
    public void updateAndSetKey(T tObj,String sqlCode,Object... steadObj) {
        try {
            PreparedStatement query = query(sqlCode, Statement.RETURN_GENERATED_KEYS, steadObj);
            if(query.executeUpdate() > 0) {
                ResultSet generatedKeys = query.getGeneratedKeys();
                if(generatedKeys.next())
                    tClass.getDeclaredMethod("set"+keyName,Integer.class).invoke(tObj,generatedKeys.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DaoException("Dao层出现错误");
        }
    }

}
