package com.example.garbagesorting.dao;

import android.util.Log;

import com.example.garbagesorting.model.Integral;
import com.example.garbagesorting.utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

//存在问题：模糊查询中文无结果

public class integralDao {
    public List<Integral> getInfoByPhone(String phone) {
        System.out.println("integralDao");
        Connection connection = DBUtils.getConn("GarbageSorting");
        List<Integral> list = new ArrayList<>();
        try {
            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
            String sql = "select * from integral where phone like \"%" + phone + "%\"";
            System.out.println(sql);
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    System.out.println("吃屎去吧");
                    if (rs != null) {
                        while (rs.next()) {
                            String in_out = rs.getString("in_out");
                            String integrall = rs.getString("integral");//根据key值读取信息
                            String date = rs.getString("date");
                            String place = rs.getString("place");
                            Integral in = new Integral(phone,in_out,integrall,date,place);
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
