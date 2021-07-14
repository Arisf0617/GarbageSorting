package com.example.garbagesorting.dao;

import android.util.Log;

import com.example.garbagesorting.model.User;
import com.example.garbagesorting.utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDao {
    public boolean login(String phone,String password) {
        Connection connection = DBUtils.getConn("GarbageSorting");
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "select * from user where phone = '"+phone+"'and password='"+password+"'";
//            String sql = "select * from MD_CHARGER";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    System.out.println("吃屎去吧");
                    if (rs != null) {
                        while (rs.next()) {
                            String phone_cfg=rs.getString("phone");
                            String password_cfg=rs.getString("password");
                            System.out.println(phone);
                            if(phone_cfg.equals(phone)&&password_cfg.equals(password))
                            return true;
                        }
                        connection.close();
                        ps.close();
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DBUtils", "异常：" + e.getMessage());
            return false;
        }
        return false;
    }
    public boolean insert(User u) {
        int a=0;
        boolean register=false;
        Connection connection = DBUtils.getConn("GarbageSorting");
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "insert into user(phone,password) values(?,?)";
//            String sql = "select * from MD_CHARGER";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setString(1,u.getPhone());
                    ps.setString(2,u.getPassword());
                    a=ps.executeUpdate();
                    System.out.println("吃屎去吧");
                    connection.close();
                    ps.close();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DBUtils", "异常：" + e.getMessage());
            return false;
        }
        if(a>0) register=true;
        return register;
    }
    public boolean isExistPhone(String phone) {
        Connection connection = DBUtils.getConn("GarbageSorting");
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "select * from user where phone = '"+phone+"'";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    if (rs != null) {
                        while (rs.next()) {
                            String phone_cfg=rs.getString("phone");
                            System.out.println(phone);
                            if(phone_cfg.equals(phone))
                                return true;
                        }
                        connection.close();
                        ps.close();
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DBUtils", "异常：" + e.getMessage());
            return false;
        }
        return false;
    }
    public boolean updatePassword(String password,String phone) {
        int a=0;
        Connection connection = DBUtils.getConn("GarbageSorting");
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "update user set password= '"+password+"' where phone='"+phone+"'";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                        a=ps.executeUpdate();
                        System.out.println(a);
                        connection.close();
                        ps.close();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DBUtils", "异常：" + e.getMessage());
            return false;
        }
        if (a>0)return true;
        else return false;
    }
    public boolean updateIcon(String image,String phone) {
        int a=0;
        boolean setIcon=false;
        Connection connection = DBUtils.getConn("GarbageSorting");
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "update user set icon='"+image+"' where phone='"+phone+"'";
//            String sql = "select * from MD_CHARGER";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    a=ps.executeUpdate();
                    System.out.println("吃屎去吧");
                    connection.close();
                    ps.close();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DBUtils", "异常：" + e.getMessage());
            return false;
        }
        if(a>0) setIcon=true;
        return setIcon;
    }
    public String getIcon(String phone) {
        int a=0;
        Connection connection = DBUtils.getConn("GarbageSorting");
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "select * from user where phone='"+phone+"'";
           if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    if (rs != null) {
                        while (rs.next()) {
                            String phone_cfg=rs.getString("phone");
                            String image_cfg=rs.getString("icon");
                            System.out.println(phone);
                            if(phone_cfg.equals(phone))
                                return image_cfg;
                        }
                        connection.close();
                        ps.close();
                    } else {
                        return "false";
                    }
                } else {
                    return "false";
                }
            } else {
               return "false";
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DBUtils", "异常：" + e.getMessage());
            return "false";
        }
        return "false";
    }
}
