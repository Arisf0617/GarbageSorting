package com.example.garbagesorting.dao;

import android.util.Log;

import com.example.garbagesorting.bean.Garbage;
import com.example.garbagesorting.utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
//存在问题：模糊查询中文无结果

public class garbageDao {
    public List<Garbage> getInfoByName(String name) {
        System.out.println("garbageDao");
        Connection connection = DBUtils.getConn("GarbageSorting");
        List<Garbage> list = new ArrayList<>();
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "select * from garbages where name like \"%" + name + "%\"";
            System.out.println(sql);
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    System.out.println("吃屎去吧");
                    if (rs.isBeforeFirst()) {
                        while (rs.next()) {
                            String name2 = rs.getString("name");//根据key值读取信息
                            String category = rs.getString("category");
                            System.out.println(name2);
                            Garbage g = new Garbage(name2,category);
                            System.out.println(name2+category);
                            list.add(g);
                        }
                        connection.close();
                        ps.close();
                        return list;
                    } else {
                        return getInfoByName2(name);
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
    public List<Garbage> getInfoByName2(String name) {
        System.out.println("garbageDao");
        int i=2;
        Connection connection = DBUtils.getConn("GarbageSorting");
        List<Garbage> list = new ArrayList<>();
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            while(i<=name.length()){
                String name_2=name.substring(name.length()-i);
                String sql = "select * from garbages where name like \"%" +  name_2 + "%\"";
                System.out.println(sql);
                if (connection != null) {// connection不为null表示与数据库建立了连接
                    PreparedStatement ps = connection.prepareStatement(sql);
                    if (ps != null) {
                        // 执行sql查询语句并返回结果集
                        ResultSet rs = ps.executeQuery();
                        System.out.println("吃屎去吧");
                        if (rs.isBeforeFirst()) {
                            while (rs.next()) {
                                String name2 = rs.getString("name");//根据key值读取信息
                                String category = rs.getString("category");
                                System.out.println(name2);
                                Garbage g = new Garbage(name2,category);
                                System.out.println(name2+category);
                                list.add(g);
                            }
                            connection.close();
                            ps.close();
                            return list;
                        } else {
                            i=i+1;
                        }
                    } else {
                        return list;
                    }
                } else {
                    return list;
                }
            }
            //String name_2=name.substring(0,2);
           // System.out.println(sql);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DBUtils", "异常：" + e.getMessage());
        }
        return list;
    }

    public String getNumByPhone(String phone,String name) {
        System.out.println("garbageDao");
        Connection connection = DBUtils.getConn("GarbageSorting");
        String num="";
        try {
            String sql="";
            if(name=="integral") {
                sql = "select (select SUM(integral) integral from "+name+" where phone like \"%" + phone + "%\" and in_out like \"%+%\")-(select SUM(integral) integral from "+name+" where phone like \"%" + phone + "%\" and in_out like \"%-%\") as integral";
            }else if(name=="reward"){
                sql = "select COUNT(*) "+name+" from integral where phone like \"%" + phone + "%\" and in_out like \"%-%\"";
            }
            else{
                sql = "select COUNT(*) "+name+" from " + name + " where phone like \"%" + phone + "%\"";
            }
            System.out.println(sql);
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    System.out.println("吃屎去吧");
                    if (rs != null) {
                        while (rs.next()) {
                            num = rs.getString(name);//根据key值读取信息
                            if(num==null) num="0";
                            System.out.println(num);
                        }
                        connection.close();
                        ps.close();
                        return num;
                    } else {
                        return num;
                    }
                } else {
                    return num;
                }
            } else {
                return num;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DBUtils", "异常：" + e.getMessage());
            return num;
        }
    }
}
