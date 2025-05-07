package com.shinhan.appstore;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppService {

	DistributorDAO disDAO = new DistributorDAO();
	PurchaseDAO purDAO = new PurchaseDAO();
	StoreDAO storeDAO = new StoreDAO();
	UserDAO userDAO = new UserDAO();
	ReviewDAO reviewDAO = new ReviewDAO();

	// =================================== StoreDAO ===========================================
	public int updateApp(String appId, String appName,String description, int price) {
		return storeDAO.updateApp(appId, appName, description, price);
	}

	public List<Map<String, Object>> getRevenueByDistributor(String distributorId) {
		return storeDAO.getRevenueByDistributor(distributorId);
	}

	public int insertApp(StoreDTO app) {
		return storeDAO.insertApp(app);
	}
	public int deleteApp(String appId) {
	    return storeDAO.deleteApp(appId);
	}
	
	public StoreDTO findById(String appId) {
		return storeDAO.findById(appId);
	}

	public ArrayList<StoreDTO> selectAll() {
		return storeDAO.selectAll();
	}

    public List<StoreDTO> selectAllApps() {
    	return storeDAO.selectAllApps();
    }
    
    
    // ======================== userDAO ==================================
    public boolean login(String userId, String password) {
    	return userDAO.login(userId, password);
    }
    
    public int register(UserDTO user) {
    	return userDAO.register(user);
    }
    

    public String getUserLevel(String userId) {
    	return userDAO.getUserLevel(userId);
    }
    
    //ReviewDAO
	public int deleteReview(int reviewId, String loggedInUserId) {
		return reviewDAO.deleteReview(reviewId, loggedInUserId);
	}
	
	public int updateReview(int reviewId, String content, double rating, String loggedInUserId) {
		return reviewDAO.updateReview(reviewId, content, rating, loggedInUserId);
	}
	
	public String getReviewOwner(int reviewId) {
		return reviewDAO.getReviewOwner(reviewId);
	}
	
	public int likeReview(int reviewId) {
		return reviewDAO.likeReview(reviewId);
	}
	
	public int reportReview(int reviewId) {
	    return reviewDAO.reportReview(reviewId);
	}

	// 공통 카운터 업데이트 메소드
	public int updateReviewCounter(String sql, int reviewId) {
	
	    return reviewDAO.updateReviewCounter(sql, reviewId);
	}
	
	
	// 리뷰가 삽입될 때 들어간 평점으로 앱 평점 바꾸기
	public void updateAppRating(String appId) {
		reviewDAO.updateAppRating(appId);
	}
	
	
	//사용자 id로 조회
	public List<ReviewDTO> selectReviewsByUser(String userId) {
	
	    return reviewDAO.selectReviewsByUser(userId);
	}
	
	
	// 신고 수가 특정 수 이상인 리뷰 조회 (JOIN)
	public List<ReviewDTO> selectReportedReviews(int minReportCount) {
	
	    return reviewDAO.selectReportedReviews(minReportCount);
	}

	
	// Reviewid로 리뷰 조회하기
	public ArrayList<ReviewDTO> findReviewsByReviewId(int review_id) {
	
	    return reviewDAO.findReviewsByReviewId(review_id);
	}
		
	// APPid로 리뷰 조회하기
	public ArrayList<ReviewDTO> findReviewsByAppId(String appId) {
	
	    return reviewDAO.findReviewsByAppId(appId);
	}

	// 리뷰 작성
    public int writeReview(String userId, String appId, double rating, String content) {
  
        return reviewDAO.writeReview(userId, appId, rating, content);
    }
    
    
    //================= purchaseDAO ==========================
	public boolean isAlreadyPurchased(String userId, String appId) {
	

	    return purDAO.isAlreadyPurchased(userId, appId);
	}

	
	//내가 가지고 있는 앱 불러오기
	public ArrayList<StoreDTO> findMyApps(String userId) {
	
	    return purDAO.findMyApps(userId);
	}
	
	//앱 구매
    public int buyApp(String userId, String appId, int payment) {
   
        return purDAO.buyApp(userId, appId, payment);
    }
    
    // =================disDAO==========================
    public List<Map<String, String>> getAllDistributors() {
    	return disDAO.getAllDistributors();
    }
    
}

