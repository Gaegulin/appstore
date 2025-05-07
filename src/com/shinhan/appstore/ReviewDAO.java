package com.shinhan.appstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

	// 리뷰 삭제하기
	public int deleteReview(int reviewId, String loggedInUserId) {
		String sql = "DELETE FROM Review WHERE review_id = ?";

		// 관리자인 경우 user_id를 확인할 필요 없음.
		if (loggedInUserId != null && !loggedInUserId.equals("admin")) { // 관리자 아닌 경우만 작성자 확인
			sql += " AND user_id = ?";
		}

		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String appId = null; // 나중에 평점 갱신용
		try {
			conn = DBUtil.getConnection();
			// 수정 전 해당 리뷰의 앱 ID 조회 (평점 갱신에 필요)
			String findAppSql = "SELECT app_id FROM Review WHERE review_id = ?";
			try (PreparedStatement findStmt = conn.prepareStatement(findAppSql)) {
				findStmt.setInt(1, reviewId);
				try (ResultSet rs = findStmt.executeQuery()) {
					if (rs.next()) {
						appId = rs.getString("app_id");
					} else {
						System.out.println("해당 리뷰를 찾을 수 없습니다.");
						return 0;
					}
				}
			}
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, reviewId);

			// 관리자가 아닐 경우, user_id 조건 추가
			if (loggedInUserId != null && !loggedInUserId.equals("admin")) {
				pstmt.setString(2, loggedInUserId);
			}

			result = pstmt.executeUpdate();

			// 평점 수정 후 앱 평점 갱신
			if (result > 0 && appId != null) {
				updateAppRating(appId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.dbDisconnect(conn, pstmt, null);
		}

		return result;
	}

	// 리뷰 수정하기
	public int updateReview(int reviewId, String content, double rating, String loggedInUserId) {
		String sql = "UPDATE Review SET content = ?, rating = ? WHERE review_id = ?";

		// 관리자인 경우 user_id를 확인할 필요 없음.
		if (loggedInUserId != null && !loggedInUserId.equals("admin")) { // 관리자 아닌 경우만 작성자 확인
			sql += " AND user_id = ?";
		}

		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String appId = null; // 나중에 평점 갱신용
		try {
			conn = DBUtil.getConnection();
			// 수정 전 해당 리뷰의 앱 ID 조회 (평점 갱신에 필요)
			String findAppSql = "SELECT app_id FROM Review WHERE review_id = ?";
			try (PreparedStatement findStmt = conn.prepareStatement(findAppSql)) {
				findStmt.setInt(1, reviewId);
				try (ResultSet rs = findStmt.executeQuery()) {
					if (rs.next()) {
						appId = rs.getString("app_id");
					} else {
						System.out.println("해당 리뷰를 찾을 수 없습니다.");
						return 0;
					}
				}
			}

			// 리뷰 수정
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, content);
			pstmt.setDouble(2, rating);
			pstmt.setInt(3, reviewId);

			if (loggedInUserId != null && !loggedInUserId.equals("admin")) {
				pstmt.setString(4, loggedInUserId);
			}

			result = pstmt.executeUpdate();

			// 평점 수정 후 앱 평점 갱신
			if (result > 0 && appId != null) {
				updateAppRating(appId);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.dbDisconnect(conn, pstmt, null);
		}

		return result;
	}

	// 자신이 작성한 리뷰만 수정,삭제할수 있도록 추가한 메서드
	public String getReviewOwner(int reviewId) {
		String sql = "SELECT user_id FROM Review WHERE review_id = ?";
		String userId = null;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, reviewId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				userId = rs.getString("user_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.dbDisconnect(conn, pstmt, rs);
		}

		return userId;
	}

	// 리뷰 좋아요 기능
	public int likeReview(int reviewId) {
		String sql = "UPDATE Review SET like_count = like_count + 1 WHERE review_id = ?";
		return updateReviewCounter(sql, reviewId);
	}

	// 리뷰 신고 기능
	public int reportReview(int reviewId) {
		String sql = "UPDATE Review SET report_count = report_count + 1 WHERE review_id = ?";
		return updateReviewCounter(sql, reviewId);
	}

	// 공통 카운터 업데이트 메소드
	public int updateReviewCounter(String sql, int reviewId) {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, reviewId);

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.dbDisconnect(conn, pstmt, null);
		}

		return result;
	}

	// 리뷰가 삽입될 때 들어간 평점으로 앱 평점 바꾸기
	public void updateAppRating(String appId) {
		String sql = """
				UPDATE Store
				SET rating = (
				    SELECT ROUND(AVG(rating), 2)
				    FROM Review
				    WHERE app_id = ?
				)
				WHERE app_id = ?
				""";

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, appId); // 서브쿼리용
			pstmt.setString(2, appId); // 업데이트할 Store용

			int updated = pstmt.executeUpdate();
			if (updated > 0) {
				System.out.println("앱 평점이 업데이트되었습니다!");
			} else {
				System.out.println("앱 평점 업데이트 실패...");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.dbDisconnect(conn, pstmt, null);
		}
	}

	// 사용자 id로 조회
	public List<ReviewDTO> selectReviewsByUser(String userId) {
		List<ReviewDTO> reviews = new ArrayList<>();
		String sql = """
				SELECT r.review_id, u.name AS user_name, r.rating, r.content, r.review_date,
				       r.like_count, r.report_count, s.app_name
				FROM Review r
				JOIN UserTable u ON r.user_id = u.user_id
				JOIN Store s ON r.app_id = s.app_id
				WHERE r.user_id = ?
				ORDER BY r.review_date DESC
				""";

		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, userId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					ReviewDTO review = new ReviewDTO();
					review.setReview_id(rs.getInt("review_id"));
					review.setUser_name(rs.getString("user_name"));
					review.setRating(rs.getDouble("rating"));
					review.setContent(rs.getString("content"));
					review.setReview_date(rs.getDate("review_date"));
					review.setLike_count(rs.getInt("like_count"));
					review.setReport_count(rs.getInt("report_count"));
					reviews.add(review);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return reviews;
	}

	// 신고 수가 특정 수 이상인 리뷰 조회 (JOIN)
	public List<ReviewDTO> selectReportedReviews(int minReportCount) {
		List<ReviewDTO> reviews = new ArrayList<>();
		String sql = "SELECT r.review_id, u.name AS user_name, s.app_name, r.rating, r.content, r.review_date, r.like_count, r.report_count "
				+ "FROM Review r " + "JOIN UserTable u ON r.user_id = u.user_id "
				+ "JOIN Store s ON r.app_id = s.app_id " + "WHERE r.report_count >= ? "
				+ "ORDER BY r.report_count DESC";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, minReportCount);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				ReviewDTO review = new ReviewDTO();
				review.setReview_id(rs.getInt("review_id"));
				review.setUser_name(rs.getString("user_name") + " (" + rs.getString("app_name") + ")");
				review.setRating(rs.getDouble("rating"));
				review.setContent(rs.getString("content"));
				review.setReview_date(rs.getDate("review_date"));
				review.setLike_count(rs.getInt("like_count"));
				review.setReport_count(rs.getInt("report_count"));

				reviews.add(review);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.dbDisconnect(conn, pstmt, rs);
		}

		return reviews;
	}

	// Reviewid로 리뷰 조회하기
	public ArrayList<ReviewDTO> findReviewsByReviewId(int review_id) {
		ArrayList<ReviewDTO> reviews = new ArrayList<>();
		String sql = """
				SELECT r.review_id, u.name AS user_name, r.rating, r.content, r.review_date, r.like_count, r.report_count
				FROM Review r
				JOIN Usertable u ON r.user_id = u.user_id
				WHERE r.review_id = ?
				""";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, review_id);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				ReviewDTO review = ReviewDTO.builder().review_id(rs.getInt("review_id"))
						.user_name(rs.getString("user_name")) // 작성자 이름
						.rating(rs.getDouble("rating")).content(rs.getString("content"))
						.review_date(rs.getDate("review_date")).like_count(rs.getInt("like_count"))
						.report_count(rs.getInt("report_count")).build();
				reviews.add(review);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.dbDisconnect(conn, pstmt, rs);
		}

		return reviews;
	}

	// APPid로 리뷰 조회하기
	public ArrayList<ReviewDTO> findReviewsByAppId(String appId) {
		ArrayList<ReviewDTO> reviews = new ArrayList<>();
		String sql = """
				SELECT r.review_id, u.name AS user_name, r.rating, r.content, r.review_date, r.like_count, r.report_count
				FROM Review r
				JOIN Usertable u ON r.user_id = u.user_id
				WHERE r.app_id = ?
				ORDER BY r.review_date DESC
				""";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, appId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				ReviewDTO review = ReviewDTO.builder().review_id(rs.getInt("review_id"))
						.user_name(rs.getString("user_name")) // 작성자 이름
						.rating(rs.getDouble("rating")).content(rs.getString("content"))
						.review_date(rs.getDate("review_date")).like_count(rs.getInt("like_count"))
						.report_count(rs.getInt("report_count")).build();
				reviews.add(review);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.dbDisconnect(conn, pstmt, rs);
		}

		return reviews;
	}

	// 리뷰 작성
	public int writeReview(String userId, String appId, double rating, String content) {
		int result = 0;
		String sql = "INSERT INTO Review(review_id, user_id, app_id, rating, content, review_date, like_count, report_count) "
				+ "VALUES (review_seq.NEXTVAL, ?, ?, ?, ?, SYSDATE, 0, 0)";

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setString(2, appId);
			pstmt.setDouble(3, rating);
			pstmt.setString(4, content);

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.dbDisconnect(conn, pstmt, null);
		}

		return result;
	}
}
