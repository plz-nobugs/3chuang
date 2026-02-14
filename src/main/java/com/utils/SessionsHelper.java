package com.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SessionsHelper {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    /**
     * 直接执行SQL查询商家
     */
    public List<Map<String, Object>> searchShops(String keyword) {
        List<Map<String, Object>> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
          //  System.out.println("直接执行SQL查询商家，关键词：" + keyword);

            // 建立数据库连接
            conn = DriverManager.getConnection(dbUrl, username, password);

            String sql;
            if (keyword == null || keyword.trim().isEmpty()) {
                // 查询所有商家
                sql = "SELECT * FROM dianzhu LIMIT 100";
                stmt = conn.prepareStatement(sql);
            } else {
                // 按关键词查询
                sql = "SELECT * FROM dianzhu WHERE dianzhuzhanghao LIKE ? OR dianzhuxingming LIKE ? LIMIT 100";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, "%" + keyword + "%");
                stmt.setString(2, "%" + keyword + "%");
            }

          //  System.out.println("执行SQL：" + sql);
            rs = stmt.executeQuery();

            // 处理结果集
            while (rs.next()) {
                Map<String, Object> shop = new HashMap<>();
                shop.put("id", rs.getLong("id"));
                shop.put("dianzhuzhanghao", rs.getString("dianzhuzhanghao"));
                shop.put("dianzhuxingming", rs.getString("dianzhuxingming"));
                shop.put("lianxidianhua", rs.getString("lianxidianhua"));
                result.add(shop);
            }

         //   System.out.println("SQL查询结果：" + result.size() + "个商家");

        } catch (SQLException e) {
         //   System.out.println("SQL查询出错：" + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}