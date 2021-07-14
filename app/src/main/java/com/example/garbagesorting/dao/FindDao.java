package com.example.garbagesorting.dao;

import android.util.Log;

import com.example.garbagesorting.model.find;
import com.example.garbagesorting.utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class FindDao {
    public boolean insert(find f) {
        int a=0;
        boolean get=false;
        Connection connection = DBUtils.getConn("GarbageSorting");
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "insert into find(phone,text,recyclable_garbage,time) values(?,?,?,?)";
//            String sql = "select * from MD_CHARGER";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setString(1,f.getPhone());
                    ps.setString(2,f.getText());
                    ps.setString(3,f.getRecyclable_garbage());
                    ps.setString(4,f.getTime());
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
        if(a>0) get=true;
        return get;
    }
    public ArrayList<find> queryAll() {
        Connection connection = DBUtils.getConn("GarbageSorting");
        ArrayList<find> list=null;
        find f=null;
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "select find.phone,text,recyclable_garbage,icon,time from find join user on find.phone=user.phone order by time desc";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    if (rs != null) {
                        list=new ArrayList<>();
                        while (rs.next()) {
                            f=new find();
                            f.setPhone(rs.getString("phone"));
                            f.setText(rs.getString("text"));
                            f.setRecyclable_garbage(rs.getString("recyclable_garbage"));
                            f.setPic(rs.getString("icon"));
                            f.setTime(rs.getString("time"));
                            list.add(f);
                        }
                        connection.close();
                        ps.close();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DBUtils", "异常：" + e.getMessage());
            return null;
        }
        return list;
    }
}
