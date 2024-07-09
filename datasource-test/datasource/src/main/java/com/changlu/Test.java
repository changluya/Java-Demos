package com.changlu;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Test {
    public static void main(String[] args) {
        try (Connection conn = DBUtil.DS.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("show databases;")) {
            while (rs.next()) {
                rs.get
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
