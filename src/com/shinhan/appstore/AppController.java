package com.shinhan.appstore;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AppController {

	static Scanner sc = new Scanner(System.in);
	private static String loginUserId = null; // 현재 로그인한 사용자 ID
	static AppService appService = new AppService();

	public static void main(String[] args) {
		boolean isStop = false;
		while (!isStop) {
			if (loginUserId == null) {
				AppView.menuDisplay();
				int job = sc.nextInt();
				switch (job) {
				case 1 -> f_login();
				case 2 -> f_register();
				case 3 -> f_distributorMenu();
				case 99 -> {
					System.out.println("프로그램을 종료합니다.");
					isStop = true;
				}
				}
			} else {
				AppView.loginMenu();
				int job = sc.nextInt();
				switch (job) {
				case 1 -> viewAllApps();
				case 2 -> buyApp();
				case 3 -> viewMyPurchases();
				case 4 -> f_viewMyReviews(loginUserId);
				case 99 -> logout();
				default -> System.out.println("잘못된 입력입니다.");
				}
			}
		}
	}



	// 배포자 메뉴
	private static void f_distributorMenu() {
		AppView.disMenu();
		System.out.print("작업선택> ");
		int job = sc.nextInt();
		switch (job) {
		case 1 -> f_registerApp();
		case 2 -> f_checkRevenue();
		default -> System.out.println("잘못된 입력입니다.");

		}

	}
	// 로그인
	private static void f_login() {
		System.out.print("아이디 입력: ");
		String userId = sc.next();
		System.out.print("비밀번호 입력: ");
		String password = sc.next();

		UserDAO userDao = new UserDAO();
		boolean isLogin;
		try {
			isLogin = appService.login(userId, password);
			String userLevel = userDao.getUserLevel(userId);
			if (isLogin) {
				System.out.println("로그인 성공!");
				loginUserId = userId; // 로그인 성공하면 현재 사용자 기억
				if (userLevel.equals("A")) {
					// 관리자 메뉴 보여주기
					showAdminMenu(userId);
				} else {
					// 일반 사용자 메뉴 보여주기
					AppView.loginMenu();
					int job = sc.nextInt();
					switch (job) {
					case 1 -> viewAllApps();
					case 2 -> buyApp();
					case 3 -> viewMyPurchases();
					case 4 -> f_viewMyReviews(loginUserId);
					case 99 -> logout();

					default -> System.out.println("잘못된 입력입니다.");
					}
				}

			} else {
				System.out.println("로그인 실패... 아이디 또는 비밀번호를 확인하세요.");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	// 관리자 메뉴
	private static void showAdminMenu(String userId) {
		boolean isAdminMenuActive = true;

		while (isAdminMenuActive) {
			AppView.adminMenu();
			System.out.print("선택: ");
			int adminChoice = sc.nextInt();

			switch (adminChoice) {
			case 1 -> f_editApp();
			case 2 -> f_deleteApp();
			case 3 -> f_viewReportedReviews();
			case 4 -> f_editReview(loginUserId);
			case 5 -> f_deleteReview(loginUserId);
			case 99 -> {
				isAdminMenuActive = false;
				logout();
			}
			default -> System.out.println("잘못된 선택입니다.");
			}
		}
	}
	
	//관리자 - 앱 삭제
	private static void f_deleteApp() {
		System.out.print("삭제할 앱 ID 입력: ");
		String appId = sc.next();
		sc.nextLine(); // 버퍼 비우기

		StoreDAO storeDAO = new StoreDAO();
		int result = storeDAO.deleteApp(appId);
		
		if (result > 0) {
			System.out.println("앱 삭제 완료!");
		} else {
			System.out.println("앱 삭제 실패!");
		}
	}
	// 관리자 - 앱 수정
	private static void f_editApp() {
		System.out.print("수정할 앱 ID 입력: ");
		String appId = sc.next();
		sc.nextLine(); // 버퍼 비우기
		StoreDTO app = appService.findById(appId);

		System.out.println("기존 이름 : " + app.getApp_name());
		System.out.print("새로운 앱 이름: ");
		String appName = sc.nextLine();

		System.out.println("기존 설명: " + app.getDescription());
		System.out.print("새로운 앱 설명: ");
		String description = sc.nextLine();

		System.out.println("기존 가격: " + app.getPrice());
		System.out.print("새로운 가격: ");
		int price = sc.nextInt();

		StoreDAO storeDAO = new StoreDAO();
		int result = storeDAO.updateApp(appId, appName, description, price);

		if (result > 0) {
			System.out.println("앱 정보 수정 완료!");
		} else {
			System.out.println("앱 수정 실패!");
		}
	}

	// 내 리뷰조회
	private static void f_viewMyReviews(String userId) {
		ReviewDAO reviewDAO = new ReviewDAO();
		List<ReviewDTO> reviewList = reviewDAO.selectReviewsByUser(userId);

		System.out.println("=========== 내가 작성한 리뷰 목록 ===========");

		if (reviewList.isEmpty()) {
			System.out.println("작성한 리뷰가 없습니다.");
			return;
		}
		AppView.reviewDisplay(reviewList);
		System.out.println("1. 리뷰 수정하기");
		System.out.println("2. 리뷰 삭제하기");
		System.out.println("3. 메뉴로 돌아가기");
		System.out.print("작업선택> ");
		int choice = sc.nextInt();

		switch (choice) {
		case 1:
			f_editReview(loginUserId);
			break;
		case 2:
			f_deleteReview(loginUserId);
			break;
		case 3:
			System.out.println("사용자 메뉴로 돌아갑니다.");
			break;
		default:
			System.out.println("잘못된 선택입니다. 사용자 메뉴로 돌아갑니다.");
			break;
		}
	}

	// 리뷰 수정
	private static void f_editReview(String loggedInUserId) {
		System.out.print("수정할 리뷰 ID 입력: ");
		int reviewId = sc.nextInt();
		sc.nextLine(); // 버퍼 비우기

		// 수정할 리뷰 내용 입력
		System.out.print("새로운 리뷰 내용: ");
		String content = sc.nextLine();

		System.out.print("수정할 평점을 입력하세요 (0.0 ~ 5.0): ");
		double rating = sc.nextDouble();
		if (rating < 0 || rating > 5) {
			System.out.println("유효하지 않은 평점입니다. 0.0 ~ 5.0 사이로 입력해주세요.");
			return;
		}

		// 리뷰 수정 (관리자는 모든 리뷰 수정 가능)
		ReviewDAO reviewDAO = new ReviewDAO();
		int result = reviewDAO.updateReview(reviewId, content, rating, loggedInUserId);

		if (result > 0) {
			System.out.println("리뷰 내용 수정 완료!");
		} else {
			System.out.println("리뷰 수정 실패!");
		}
	}

	// 신고된 리뷰 조회
	private static void f_viewReportedReviews() {
		System.out.print("몇 번 이상 신고된 리뷰를 조회하시겠습니까? 입력: ");
		int minReportCount = sc.nextInt();

		ReviewDAO reviewDAO = new ReviewDAO();
		List<ReviewDTO> reviewList = reviewDAO.selectReportedReviews(minReportCount);

		System.out.println("------------ 신고된 리뷰 목록 ------------");
		AppView.reviewDisplay(reviewList);

		if (reviewList.isEmpty()) {
			System.out.println("해당 조건을 만족하는 리뷰가 없습니다.");
		}
	}

	// 리뷰 삭제
	private static void f_deleteReview(String loggedInUserId) {
		System.out.print("삭제할 리뷰 ID 입력: ");
		int reviewId = sc.nextInt();
		sc.nextLine(); // 버퍼 비우기

		// 리뷰 삭제 (관리자는 모든 리뷰 삭제 가능)
		ReviewDAO reviewDAO = new ReviewDAO();
		int result = reviewDAO.deleteReview(reviewId, loggedInUserId);

		if (result > 0) {
			System.out.println("리뷰 삭제 완료!");

		} else {
			System.out.println("리뷰 삭제 실패!");
		}
	}

	// 회원가입
	private static void f_register() {
		System.out.print("아이디 입력: ");
		String userId = sc.next();
		System.out.print("비밀번호 입력: ");
		String password = sc.next();
		System.out.print("이름 입력: ");
		String name = sc.next();
		System.out.print("이메일 입력: ");
		String email = sc.next();

		UserDTO newUser = UserDTO.builder().user_id(userId).password(password).name(name).email(email).build();
		int result;
		try {
			result = appService.register(newUser);
			if (result > 0) {
				System.out.println("회원가입 성공!");
			} else {
				System.out.println("회원가입 실패... 다시 시도하세요.");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void viewAllApps() {
		System.out.println("[전체 앱 목록 보기 기능]");
		ArrayList<StoreDTO> apps = appService.selectAll();

		if (apps.isEmpty()) {
			System.out.println("등록된 앱이 없습니다.");
			return;
		}
		AppView.appDisplay(apps);
		System.out.println("1. 앱 리뷰 보기");
		System.out.println("2. 사용자 메뉴로 돌아가기");
		System.out.print("작업선택> ");
		int choice = sc.nextInt();

		switch (choice) {
		case 1:
			System.out.print("리뷰를 보고 싶은 앱의 ID를 입력하세요: ");
			String appId = sc.next();
			f_viewAppReviews(appId); // 리뷰 보는 함수 호출
			System.out.println("1. 리뷰 좋아요/신고");
			System.out.println("2. 돌아가기");
			System.out.print("작업선택> ");
			int choice2 = sc.nextInt();

			switch (choice2) {
			case 1:
				likeOrReportReview();
				break;
			case 2:
				System.out.println("사용자 메뉴로 돌아갑니다.");
				break;
			default:
				break;
			}
			break;
		case 2:
			System.out.println("사용자 메뉴로 돌아갑니다.");
			break;
		default:
			System.out.println("잘못된 선택입니다. 사용자 메뉴로 돌아갑니다.");
			break;
		}
	}

	private static void buyApp() {
		List<StoreDTO> appList = appService.selectAllApps();
		System.out.println("[앱 구매]");
		AppView.simpleAppDisplay(appList);

		System.out.print("구매할 앱ID를 입력하세요: ");
		String appId = sc.next();

		// 앱 존재 여부 확인
		StoreDTO app = appService.findById(appId);

		// 중복 구매 확인
		PurchaseDAO purchaseDAO = new PurchaseDAO();
		if (purchaseDAO.isAlreadyPurchased(loginUserId, appId)) {
			System.out.println("이미 이 앱을 구매하셨습니다. 중복 구매는 불가능합니다.");
			return;
		}

		if (app == null) {
			System.out.println("존재하지 않는 앱입니다.");
			return;
		}

		System.out.println("구매할 앱 정보: " + app.getApp_name() + " (" + app.getPrice() + "원)");

		System.out.print("정말 구매하시겠습니까? (Y/N): ");
		String answer = sc.next();
		if (answer.equalsIgnoreCase("Y")) {
			int result = purchaseDAO.buyApp(loginUserId, appId, app.getPrice());
			if (result > 0) {
				System.out.println("구매 성공! 감사합니다.");
			} else {
				System.out.println("구매 실패... 다시 시도해주세요.");
			}
		} else {
			System.out.println("구매가 취소되었습니다.");
		}
	}

	private static void viewMyPurchases() {
		System.out.println("[내가 구매한 앱 목록]");

		ArrayList<StoreDTO> myApps = appService.findMyApps(loginUserId);

		if (myApps.isEmpty()) {
			System.out.println("구매한 앱이 없습니다.");
			return;
		}
		AppView.myappDisplay(myApps);

		System.out.println("1. 앱 리뷰 작성하기");
		System.out.println("2. 사용자 메뉴로 돌아가기");
		System.out.print("작업선택> ");
		int choice = sc.nextInt();

		switch (choice) {
		case 1:
			writeReview();
			break;
		case 2:
			break;
		}
	}

	private static void writeReview() {
		System.out.println("[리뷰 작성]");

		// 1. 내 구매한 앱 조회
		ArrayList<StoreDTO> myApps = appService.findMyApps(loginUserId);

		if (myApps.isEmpty()) {
			System.out.println("리뷰를 작성할 수 없습니다. (구매한 앱 없음)");
			return;
		}

		// 2. 앱 선택
		System.out.print("리뷰할 앱ID를 입력하세요: ");
		String appId = sc.next();

		// 구매한 앱인지 다시 확인
		boolean isPurchased = false;
		for (StoreDTO app : myApps) {
			if (app.getApp_id().equals(appId)) {
				isPurchased = true;
				break;
			}
		}

		if (!isPurchased) {
			System.out.println("해당 앱은 구매하지 않았습니다. 리뷰 작성 불가!");
			return;
		}

		// 3. 별점과 내용 입력
		double rating = 0;
		while (true) {
			System.out.print("별점(0.0 ~ 5.0)을 입력하세요: ");
			try {
				rating = sc.nextDouble();
				if (rating >= 0 && rating <= 5) {
					break; // 올바른 입력이면 루프 종료
				} else {
					System.out.println("별점은 0.0 ~ 5.0 사이만 가능합니다.");
				}
			} catch (InputMismatchException e) {
				System.out.println("숫자를 입력해야 합니다.");
				sc.nextLine(); // 잘못된 입력 제거
			}
		}

		sc.nextLine(); // 버퍼 비우기
		System.out.print("리뷰 내용을 입력하세요: ");
		String content = sc.nextLine();

		// 5. DB 저장
		int result = appService.writeReview(loginUserId, appId, rating, content);

		if (result > 0) {
			System.out.println("리뷰 작성 완료!");
			appService.updateAppRating(appId); // ★ 작성 후 평점 업데이트
		} else {
			System.out.println("리뷰 작성 실패... 다시 시도해주세요.");
		}
	}

	// 리뷰 불러오기
	private static void f_viewAppReviews(String appId) {
		ReviewDAO reviewDAO = new ReviewDAO();
		List<ReviewDTO> reviewList = reviewDAO.findReviewsByAppId(appId);

		if (reviewList.isEmpty()) {
			System.out.println("해당 앱에 등록된 리뷰가 없습니다.");
			return;
		}

		System.out.println("------------ 리뷰 목록 ------------");
		AppView.reviewDisplay(reviewList);
	}

	// 리뷰 좋아요 / 신고기능
	private static void likeOrReportReview() {
		System.out.println("[리뷰 좋아요/신고]");
		System.out.print("대상 리뷰 ID를 입력하세요: ");
		int reviewId = sc.nextInt();
		ArrayList<ReviewDTO> reviews = appService.findReviewsByReviewId(reviewId);

		if (reviews.isEmpty()) {
			System.out.println("잘못된 리뷰 ID입니다.");
			return;
		}
		AppView.reviewChoice(reviews);

		System.out.print("1. 좋아요  2. 신고  선택하세요: ");
		int choice = sc.nextInt();

		int result = 0;
		if (choice == 1) {
			result = appService.likeReview(reviewId);
			if (result > 0) {
				System.out.println("리뷰에 좋아요를 눌렀습니다!");
			} else {
				System.out.println("좋아요 실패...");
			}
		} else if (choice == 2) {
			result = appService.reportReview(reviewId);
			if (result > 0) {
				System.out.println("리뷰를 신고했습니다!");
			} else {
				System.out.println("신고 실패...");
			}
		} else {
			System.out.println("잘못된 선택입니다.");
		}
	}

	// 앱 등록
	private static void f_registerApp() {
		List<Map<String, String>> distributorList = appService.getAllDistributors();
		AppView.distriDisplay(distributorList);
		System.out.print("배급사 ID 입력: ");
		String distributorId = sc.next();
		sc.nextLine(); // 버퍼 클리어

		System.out.print("앱 ID 입력: ");
		String appId = sc.nextLine();
		System.out.print("앱 이름 입력: ");
		String appName = sc.nextLine();
		System.out.print("카테고리 입력: ");
		String category = sc.nextLine();
		System.out.print("버전 입력 (예: 1.0): ");
		String version = sc.nextLine();
		System.out.print("설명 입력: ");
		String description = sc.nextLine();
		System.out.print("가격 입력 (무료면 0): ");
		int price = sc.nextInt();

		StoreDTO app = StoreDTO.builder().app_id(appId).app_name(appName).category(category).version(version)
				.description(description).price(price).distributor_id(distributorId).build();

		StoreDAO dao = new StoreDAO();
		int result = dao.insertApp(app);
		if (result > 0) {
			System.out.println("✅ 앱 등록 완료!");
		} else {
			System.out.println("❌ 앱 등록 실패!");
		}
	}

	// 수익 확인하기
	private static void f_checkRevenue() {

		List<Map<String, String>> distributorList = appService.getAllDistributors();
		AppView.distriDisplay(distributorList);
		System.out.print("배급사 ID 입력: ");
		String distributorId = sc.next();

		StoreDAO dao = new StoreDAO();
		List<Map<String, Object>> revenueList = dao.getRevenueByDistributor(distributorId);
		if (revenueList.isEmpty()) {
			System.out.println("해당 배급사의 앱이 없거나 판매 수익이 없습니다.");
		} else {
			AppView.revenueDisplye(revenueList);
		}
	}

	private static void logout() {
		System.out.println("로그아웃 되었습니다.");
		loginUserId = null; // 로그인 상태 해제
	}
}
