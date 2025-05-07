package com.shinhan.appstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class PurchaseDAO {
	
	// 중복 구매 방지 
	public boolean isAlreadyPurchased(String userId, String appId) {
	    String sql = "SELECT COUNT(*) FROM Purchase WHERE user_id = ? AND app_id = ?"; //count 해서 조회된 값이 있으면 중복 앱임
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    boolean result = false;

	    try {
	        conn = DBUtil.getConnection();
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, userId);
	        pstmt.setString(2, appId);
	        rs = pstmt.executeQuery();

	        if (rs.next() && rs.getInt(1) > 0) {
	            result = true; // 이미 구매함
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        DBUtil.dbDisconnect(conn, pstmt, rs);
	    }

	    return result;
	}

	
	//내가 가지고 있는 앱 불러오기
	public ArrayList<StoreDTO> findMyApps(String userId) {
	    ArrayList<StoreDTO> myApps = new ArrayList<>();
	    String sql = """
	        SELECT s.app_id, s.app_name, s.category, s.price, s.rating
	        FROM Purchase p
	        JOIN Store s ON p.app_id = s.app_id
	        WHERE p.user_id = ?
	        ORDER BY p.purchase_date DESC
	        """;

	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        conn = DBUtil.getConnection();
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, userId);
	        rs = pstmt.executeQuery();

	        while (rs.next()) {
	            StoreDTO app = StoreDTO.builder()
	                    .app_id(rs.getString("app_id"))
	                    .app_name(rs.getString("app_name"))
	                    .category(rs.getString("category"))
	                    .price(rs.getInt("price"))
	                    .rating(rs.getDouble("rating"))
	                    .build();
	            myApps.add(app);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        DBUtil.dbDisconnect(conn, pstmt, rs);
	    }

	    return myApps;
	}
	
	//앱 구매
    public int buyApp(String userId, String appId, int payment) {
        int result = 0;
        String sql = "INSERT INTO Purchase(purchase_id, user_id, app_id, purchase_date, payment) "
                   + "VALUES (purchase_seq.NEXTVAL, ?, ?, SYSDATE, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, appId);
            pstmt.setInt(3, payment);

            result = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.dbDisconnect(conn, pstmt, null);
        }

        return result;
    }
}
