package com.glacier.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glacier.until.FileUntil;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * @author Mr-Glacier
 * @version 2.0
 * @since 2024/09/25
 */
public class DaoFather {
    protected Connection connection = null;
    protected Statement statement = null;

    protected String DriverName;
    protected String DBName;
    protected String ConnectionURL;
    protected String DBUser;
    protected String DBPass;
    protected String EntityName;
    protected String TableName;
    protected String PrimaryKey;

    public DaoFather(int choseDB, int choseTable) {
        String content = FileUntil.methodGetFileContent("src/main/resources/", "DBConfig.json");
        JSONObject DBItems1 = JSON.parseObject(content);
        JSONArray DBArray = DBItems1.getJSONArray("Parameter");
        JSONObject DBChose = DBArray.getJSONObject(choseDB);
        this.DriverName = DBChose.getString("DBDriver");
        System.out.println(DriverName);
        this.DBName = DBChose.getString("DBName");
        this.ConnectionURL = DBChose.getString("DBConnectionStr");
        this.DBUser = DBChose.getString("DBUserName");
        this.DBPass = DBChose.getString("DBUserPass");
        String beanPath = DBChose.getString("EntityPath");
        JSONArray TableArray = DBChose.getJSONArray("EntityList");
        JSONObject TableChose = TableArray.getJSONObject(choseTable);
        this.EntityName = "" + beanPath + TableChose.getString("EntityName");
        this.PrimaryKey = TableChose.getString("PrimaryKey");
        this.TableName = TableChose.getString("TableName");
        System.out.println("本次调用Dao 参数情况如下:\n本次数据库名称: " + DBName + "\n" + "本次执行表名: " + this.TableName);
    }


    public void MethodCreateSomeObject() {
        try {
            Class.forName(this.DriverName);
            if (null == connection || connection.isClosed()) {
                if (DriverName.contains("mysql")) {
                    connection = DriverManager.getConnection(this.ConnectionURL + this.DBName, this.DBUser, this.DBPass);
                } else {
                    connection = DriverManager.getConnection(this.ConnectionURL + "databaseName=" + this.DBName, this.DBUser, this.DBPass);
                }
            }
            if (null == statement || statement.isClosed()) {
                statement = connection.createStatement();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void Method_IUD(String sql) {
        MethodCreateSomeObject();
        try {
            statement.executeUpdate(sql.replace("\t", "").replace("\n", "").replace("\r", ""));
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Method_Insert(Object obj) {
        try {
            Class c = obj.getClass();
//            获取其中的get方法
            Method[] methods = c.getDeclaredMethods();
            String ColumnList = "";
            String ValueList = "";
            for (Method method : methods) {
                if (method.getName().equals("get" + this.PrimaryKey)) {
                    continue;
                }
                if (method.getName().startsWith("get")) {
                    String columnName = method.getName().replace("get", "");
                    ColumnList += columnName + ",";
                    String value = method.invoke(obj) == null ? "-" : method.invoke(obj).toString();
                    if (method.getReturnType() == String.class) {
                        ValueList += "'" + value + "',";
                    } else {
                        ValueList += value + ",";
                    }
                }
            }
            ColumnList = ColumnList.substring(0, ColumnList.length() - 1);
            ValueList = ValueList.substring(0, ValueList.length() - 1);
            String sql = "INSERT INTO  " + this.TableName + "(" + ColumnList + ")Values(" + ValueList + ");";
            Method_IUD(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void MethodInsert(Object obj) {
        try {
            Class c = obj.getClass();
            Method[] methods = c.getDeclaredMethods();
            String columnList = "";
            String valueList = "";
            for (Method method : methods) {
                if (method.getName().equals("get" + this.PrimaryKey)) {
                    continue;
                }
                if (method.getName().startsWith("get")) {
                    String columnName = method.getName().replace("get", "");
                    columnList += columnName + ",";
                    String value = method.invoke(obj) == null ? "-" : method.invoke(obj).toString().replace("\t", "").replace("\n", "").replace("\r", "");
                    //如果为空则替换为-
                    if (method.getReturnType() == String.class) {
                        valueList += "N'" + value + "',";
                    } else {
                        valueList += value + ",";
                    }
                }
            }
            columnList = columnList.substring(0, columnList.length() - 1);
            valueList = valueList.substring(0, valueList.length() - 1);
            String sql = "INSERT INTO " + this.TableName + "( " + columnList + " )values( " + valueList + " )";
            Method_IUD(sql);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<Object> Method_Find() {
        ArrayList<Object> BeanLsit = new ArrayList<>();
        try {
            String sql = "Select * from " + this.TableName;
            MethodCreateSomeObject();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Class C = Class.forName(this.EntityName);

                Object o = C.newInstance();

//                获取列名
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

//                获取列数
                int lins = resultSetMetaData.getColumnCount();

                for (int i = 0; i < lins; i++) {
                    String columnName = resultSetMetaData.getColumnName(i + 1);
//                    获取值
                    Object columnValue = resultSet.getObject(i + 1);
                    Field field = C.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(o, columnValue);
                }
                BeanLsit.add(o);
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BeanLsit;
    }

    //condition
    public ArrayList<Object> Method_FindByCondition(String condition) {
        ArrayList<Object> BeanLsit = new ArrayList<>();
        try {
            String sql = "Select * from " + this.TableName + " where " + condition;
            MethodCreateSomeObject();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Class C = Class.forName(this.EntityName);

                Object o = C.newInstance();

//                获取列名
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

//                获取列数
                int lins = resultSetMetaData.getColumnCount();

                for (int i = 0; i < lins; i++) {
                    String columnName = resultSetMetaData.getColumnName(i + 1);
//                    获取值
                    Object columnValue = resultSet.getObject(i + 1);
                    Field field = C.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(o, columnValue);
                }
                BeanLsit.add(o);
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BeanLsit;
    }


    public void Method_OnlyChangeState(int C_ID) {
        String sql = "update " + this.TableName + " set C_DownState = '是'  where C_ID = " + C_ID;
        Method_IUD(sql);
    }

    public void Method_ChangeState(int C_ID) {
        String sql = "update " + this.TableName + " set C_DownState = '是'  where C_ID = " + C_ID;
        Method_IUD(sql);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String time = df.format(new Date());
        String sql1 = "update " + this.TableName + " set C_DownTime = '" + time + "'  where C_ID  = " + C_ID;
        Method_IUD(sql1);
    }

    public void Method_ChangeState2(int C_ID) {
        String sql = "update " + this.TableName + " set C_DownState = '无车款信息'  where C_ID = " + C_ID;
        Method_IUD(sql);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String time = df.format(new Date());
        String sql1 = "update " + this.TableName + " set C_DownTime = '" + time + "'  where C_ID  = " + C_ID;
        Method_IUD(sql1);
    }

    /**
     * 自由查询
     */
    public List<Map<String, String>> methodFindFree(List<String> columnList, String queryCondition) {
        List<Map<String, String>> beanList = new ArrayList<>();
        try {
            // 拼接Sql语句
            StringBuilder column = new StringBuilder();
            for (String s : columnList) {
                column.append(s).append(",");
            }
            String sql = "Select " + column.substring(0, column.length() - 1) + " from " + this.TableName + queryCondition;
            System.out.println(sql);
            MethodCreateSomeObject();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Map<String, String> map = new HashMap<>();
                for (String columnName : columnList) {
                    map.put(columnName, resultSet.getString(columnName));
                }
                beanList.add(map);
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beanList;
    }


    public void methodUpdateDiYiCountyDetail(int C_ID, String pageNum) {
        String sql = "update " + this.TableName + " set C_downState = '是'  where C_ID = " + C_ID;
        Method_IUD(sql);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String time = df.format(new Date());
        String sql1 = "update " + this.TableName + " set C_downTime = '" + time + "'  where C_ID  = " + C_ID;
        Method_IUD(sql1);

        String sql2 = "update " + this.TableName + " set C_countyPage = '" + pageNum + "'  where C_ID  = " + C_ID;
        Method_IUD(sql2);

    }


    public void methodUpdateStateTime(int C_ID) {
        String sql = "update " + this.TableName + " set C_downState = '是'  where C_ID = " + C_ID;
        Method_IUD(sql);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String time = df.format(new Date());
        String sql1 = "update " + this.TableName + " set C_downTime = '" + time + "'  where C_ID  = " + C_ID;
        Method_IUD(sql1);
    }

    public void methodUpdateStateTime(int C_ID, String otherStr) {
        String sql = "update " + this.TableName + " set C_downState = '是'  where C_ID = " + C_ID;
        Method_IUD(sql);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String time = df.format(new Date());
        String sql1 = "update " + this.TableName + " set C_downTime = '" + time + "'  where C_ID  = " + C_ID;
        Method_IUD(sql1);
        String sql2 = "update " + this.TableName + otherStr + " where C_ID  = " + C_ID;
        Method_IUD(sql2);
    }


}
