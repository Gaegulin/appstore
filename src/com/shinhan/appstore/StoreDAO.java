package com.shinhan.appstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StoreDAO {
	
	// 앱 삭제
		public int deleteApp(String appId) {

		    String sql1 = "DELETE FROM store where app_id = ?";

		    int result = 0;
		    Connection conn = null;
		    PreparedStatement pstmt1 = null;

		    try {
		        conn = DBUtil.getConnection();
		        pstmt1 = conn.prepareStatement(sql1);
		        pstmt1.setString(1, appId);
		        result = pstmt1.executeUpdate();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    } finally {
		        DBUtil.dbDisconnect(conn, pstmt1, null);

		    }

		    return result;
		}
	
	
	// 앱 수정
	public int updateApp(String appId, String appName, String description, int price) {
	    String sql = "UPDATE Store SET app_name = ?,description = ?, price = ? WHERE app_id = ?";

	    int result = 0;
	    Connection conn = null;
	    PreparedStatement pstmt = null;

	    try {
	        conn = DBUtil.getConnection();
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, appName);
	        pstmt.setString(2, description);
	        pstmt.setInt(3, price);
	        pstmt.setString(4, appId);

	        result = pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        DBUtil.dbDisconnect(conn, pstmt, null);
	    }

	    return result;
	}
	
	
	// 수익 조회
	public List<Map<String, Object>> getRevenueByDistributor(String distributorId) {
	    String sql = """
	        SELECT s.app_id, s.app_name, SUM(p.payment) AS total_revenue
	        FROM Store s
	        JOIN Purchase p ON s.app_id = p.app_id
	        WHERE s.distributor_id = ?
	        GROUP BY s.app_id, s.app_name
	        ORDER BY total_revenue DESC
	        """;

	    List<Map<String, Object>> resultList = new ArrayList<>();
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        conn = DBUtil.getConnection();
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, distributorId);
	        rs = pstmt.executeQuery();

	        while (rs.next()) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("app_id", rs.getString("app_id"));
	            map.put("app_name", rs.getString("app_name"));
	            map.put("total_revenue", rs.getInt("total_revenue"));
	            resultList.add(map);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        DBUtil.dbDisconnect(conn, pstmt, rs);
	    }

	    return resultList;
	}

	
	//앱 등록
	public int insertApp(StoreDTO app) {
	    String sql = """
	        INSERT INTO Store(app_id, app_name, category, version, description, price, distributor_id)
	        VALUES (?, ?, ?, ?, ?, ?, ?)
	        """;

	    int result = 0;
	    Connection conn = null;
	    PreparedStatement pstmt = null;

	    try {
	        conn = DBUtil.getConnection();
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, app.getApp_id());
	        pstmt.setString(2, app.getApp_name());
	        pstmt.setString(3, app.getCategory());
	        pstmt.setString(4, app.getVersion());
	        pstmt.setString(5, app.getDescription());
	        pstmt.setInt(6, app.getPrice());
	        pstmt.setString(7, app.getDistributor_id());

	        result = pstmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        DBUtil.dbDisconnect(conn, pstmt, null);
	    }

	    return result;
	}

	
	// 앱 id로 검색
	public StoreDTO findById(String appId) {
	    StoreDTO app = null;
	    String sql = "SELECT app_id, app_name, category, price, rating, description FROM Store WHERE app_id = ?";

	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        conn = DBUtil.getConnection();
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, appId);
	        rs = pstmt.executeQuery();

	        if (rs.next()) {
	            app = StoreDTO.builder()
	                    .app_id(rs.getString("app_id"))
	                    .app_name(rs.getString("app_name"))
	                    .category(rs.getString("category"))
	                    .price(rs.getInt("price"))
	                    .rating(rs.getDouble("rating"))
	                    .description(rs.getString("description"))
	                    .build();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        DBUtil.dbDisconnect(conn, pstmt, rs);
	    }

	    return app;
	}
	
	// 전체 앱 조회(평점순으로)
    public ArrayList<StoreDTO> selectAll() {
        ArrayList<StoreDTO> appList = new ArrayList<>();
        String sql = "SELECT app_id, app_name, category, price, rating FROM Store order by rating desc NULLS LAST";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                StoreDTO app = StoreDTO.builder()
                        .app_id(rs.getString("app_id"))
                        .app_name(rs.getString("app_name"))
                        .category(rs.getString("category"))
                        .price(rs.getInt("price"))
                        .rating(rs.getDouble("rating"))
                        .build();

                appList.add(app);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.dbDisconnect(conn, pstmt, rs);
        }

        return appList;
    }
    
    
    
    // 앱 id,제목, 가격만 불러오기
    public List<StoreDTO> selectAllApps() {
        List<StoreDTO> appList = new ArrayList<>();
        String sql = "SELECT app_id, app_name,price FROM Store";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                StoreDTO app = new StoreDTO();
                app.setApp_id(rs.getString("app_id"));
                app.setApp_name(rs.getString("app_name"));
                app.setPrice(rs.getInt("price"));
                appList.add(app);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.dbDisconnect(conn, pstmt, rs);
        }

        return appList;
    }
}
