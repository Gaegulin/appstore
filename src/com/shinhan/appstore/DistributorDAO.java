package com.shinhan.appstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistributorDAO {
    // 배급사 ID와 회사명 조회
    public List<Map<String, String>> getAllDistributors() {
        List<Map<String, String>> list = new ArrayList<>();
        String sql = "SELECT distributor_id, company FROM Distributor";

        try (
            Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
        ) {
            while (rs.next()) {
                Map<String, String> distributor = new HashMap<>();
                distributor.put("id", rs.getString("distributor_id"));
                distributor.put("company", rs.getString("company"));
                list.add(distributor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
