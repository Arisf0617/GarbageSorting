package com.example.garbagesorting.dao;

import android.util.Log;

import com.example.garbagesorting.model.Reward;
import com.example.garbagesorting.utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

//存在问题：模糊查询中文无结果

public class  rewardDao {
    public static List<Reward> getInfoByPhone(String phone) {
        System.out.println("rewardDao");
        Connection connection = DBUtils.getConn("GarbageSorting");
        List<Reward> list = new ArrayList<>();
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "select * from integral where phone like \"%" + phone + "%\" and in_out like \"%-%\"";
            System.out.println(sql);
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    System.out.println("吃屎去吧");
                    if (rs != null) {
                        while (rs.next()) {
                            String rewardd = rs.getString("recovery");
                            String date = rs.getString("date");
                            String inter = rs.getString("integral");
                            Reward in = new Reward(rewardd,date,inter);
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
    public static List<Reward> getAllReward() {
        System.out.println("rewardDao");
        Connection connection = DBUtils.getConn("GarbageSorting");
        List<Reward> list = new ArrayList<>();
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "select * from reward";
            System.out.println(sql);
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    System.out.println("吃屎去吧");
                    if (rs != null) {
                        while (rs.next()) {
                            String rewardd = rs.getString("name");
                            String date = " ";
                            String inter = rs.getString("integral");
                            Reward in = new Reward(rewardd,date,inter);
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
}
