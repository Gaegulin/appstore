package com.shinhan.appstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
	
	
	
    // 로그인 기능
    public boolean login(String userId, String password) {
        String sql = "SELECT * FROM UserTable WHERE user_id = ? AND password = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            
            return rs.next(); // 결과가 있으면 로그인 성공
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.dbDisconnect(conn, pstmt, rs);
        }
    }

    // 회원가입 기능
    public int register(UserDTO user) {
        String sql = "INSERT INTO UserTable (user_id, password, name) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUser_id());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            return pstmt.executeUpdate(); // 성공하면 1 반환
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.dbDisconnect(conn, pstmt, null);
        }
    }
    // 관리자 체크
    public String getUserLevel(String userId) {
        String sql = "SELECT user_level FROM UserTable WHERE user_id = ?";
        String userLevel = "";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                userLevel = rs.getString("user_level");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.dbDisconnect(conn, pstmt, rs);
        }

        return userLevel;
    }

}
