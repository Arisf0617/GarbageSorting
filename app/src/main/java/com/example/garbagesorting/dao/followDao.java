package com.example.garbagesorting.dao;

import android.util.Log;

import com.example.garbagesorting.model.Follow;
import com.example.garbagesorting.model.User;
import com.example.garbagesorting.model.find;
import com.example.garbagesorting.utils.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

//存在问题：模糊查询中文无结果

public class followDao {
    public List<Follow> getInfoByPhone(String phone) {
        System.out.println("followDao");
        Connection connection = DBUtils.getConn("GarbageSorting");
        List<Follow> list = new ArrayList<>();
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "select follow,icon from follow join user on follow.follow=user.phone where follow.phone like \"%" + phone + "%\"";
            System.out.println(sql);
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    System.out.println("吃屎去吧");
                    if (rs != null) {
                        while (rs.next()) {
                            String follow = rs.getString("follow");//根据key值读取信息
                            String icon = rs.getString("icon");
                            Follow in = new Follow(phone,follow,icon);
                            list.add(in);
                        }
                        connection.close();
                        ps.close();
                        return list;
                    } else {
                        return list;
                    }
                } else {
                    return list;
                }
            } else {
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DBUtils", "异常：" + e.getMessage());
            return list;
        }
    }
    public boolean getFollowByPhone(String phone,String follow) {
        System.out.println("followDao");
        Connection connection = DBUtils.getConn("GarbageSorting");
        List<Follow> list = new ArrayList<>();
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "select * from follow  where phone = '"+ phone +"' and follow='"+follow+"'";
            System.out.println(sql);
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    System.out.println("吃屎去吧");
                    if (rs != null) {
                        while (rs.next()) {
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
        }
        return false;
    }
    public String deleteByPhone(String phone,String follow) {
        System.out.println("followDao");
        Connection connection = DBUtils.getConn("GarbageSorting");
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "delete from follow where phone ="+phone+" and follow ="+follow;
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                int a=ps.executeUpdate();
                connection.close();
                ps.close();
                return "取消成功";
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DBUtils", "异常：" + e.getMessage());
        }
        return "取消失败";
    }

    public String followByPhone(String phone,String follow) {
        System.out.println("followDao");
        int a;
        Connection connection = DBUtils.getConn("GarbageSorting");
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "insert into follow(phone,follow) values(?,?)";
//            String sql = "select * from MD_CHARGER";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setString(1,phone);
                    ps.setString(2,follow);
                    a=ps.executeUpdate();
                    System.out.println("吃屎去吧");
                    connection.close();
                    ps.close();
                } else {
                    return "关注失败";
                }
            } else {
                return "关注失败";
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DBUtils", "异常：" + e.getMessage());
            return "关注失败";
        }
        return "关注成功";
    }
}
