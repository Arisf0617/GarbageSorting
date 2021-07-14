package com.example.garbagesorting.dao;

import android.util.Log;

import com.example.garbagesorting.model.Collect;
import com.example.garbagesorting.model.Follow;
import com.example.garbagesorting.model.find;
import com.example.garbagesorting.utils.DBUtils;
import com.example.garbagesorting.utils.clusterutil.MarkerManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

//存在问题：模糊查询中文无结果

public class collectionDao {
    public boolean insert(Collect c) {
        int a=0;
        boolean get=false;
        Connection connection = DBUtils.getConn("GarbageSorting");
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "insert into collection(phone,collectionphone,finddate) values(?,?,?)";
//            String sql = "select * from MD_CHARGER";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setString(1,c.getPhone());
                    ps.setString(2,c.getCollectionphone());
                    ps.setString(3,c.getFinddate());
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
    public List<find> getInfoByPhone(String phone) {
        System.out.println("collectionDao");
        Connection connection = DBUtils.getConn("GarbageSorting");
        List<find> list = new ArrayList<>();
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "select find.phone,time,text,recyclable_garbage,icon from collection join find on find.phone=collection.phone and find.time=collection.finddate join user on user.phone=find.phone where collection.phone like \"%" + phone + "%\"" ;
            System.out.println(sql);
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    System.out.println("吃屎去吧");
                    if (rs != null) {
                        find f;
                        list=new ArrayList<>();
                        while (rs.next()) {
                            f=new find();
                            f.setPhone(rs.getString("phone"));
                            f.setTime(rs.getString("time"));
                            f.setText(rs.getString("text"));
                            f.setRecyclable_garbage(rs.getString("recyclable_garbage"));
                            f.setPic(rs.getString("icon"));
                            list.add(f);
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
    public boolean getCollectionByPhone(String phone,String collectionphone,String finddate) {
        System.out.println("followDao");
        Connection connection = DBUtils.getConn("GarbageSorting");
        List<Follow> list = new ArrayList<>();
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "select * from collection  where phone = '"+ phone +"' and collectionphone='"+collectionphone+"' and finddate='"+finddate+"'";
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
    public List<find> visitByPhone(String phone) {
        System.out.println("collectionDao");
        Connection connection = DBUtils.getConn("GarbageSorting");
        List<find> list = new ArrayList<>();
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "select find.phone,time,text,recyclable_garbage,icon from find join user on user.phone=find.phone where find.phone like \"%" + phone + "%\"" ;
            System.out.println(sql);
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    System.out.println("吃屎去吧");
                    if (rs != null) {
                        find f;
                        list=new ArrayList<>();
                        while (rs.next()) {
                            f=new find();
                            f.setPhone(rs.getString("phone"));
                            f.setTime(rs.getString("time"));
                            f.setText(rs.getString("text"));
                            f.setRecyclable_garbage(rs.getString("recyclable_garbage"));
                            f.setPic(rs.getString("icon"));
                            list.add(f);
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
    public String deleteByPhone(String phone,String collectionphone,String date) {
        System.out.println("collectionDao");
        Connection connection = DBUtils.getConn("GarbageSorting");
        List<find> list = new ArrayList<>();
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "delete from collection where phone ="+phone+" and collectionphone ="+collectionphone+" and finddate=\""+date+"\"" ;
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
}
