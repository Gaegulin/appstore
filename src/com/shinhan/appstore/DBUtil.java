package com.shinhan.appstore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {

	//DB연결 Util로 만들었음
	public static Connection getConnection() {
		Connection conn = null;
		String url = "jdbc:oracle:thin:@192.168.0.207:1521:xe";
		String userid = "app", userpass = "1234";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, userid, userpass);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return conn;
	}
	
	
	//DB연결시 사용한 자원해제 
	public static void dbDisconnect(
			 Connection conn, Statement st, ResultSet rs) {
		
		try {
			if(rs!=null) rs.close();
			if(st!=null) st.close();
			if(conn!=null) conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}