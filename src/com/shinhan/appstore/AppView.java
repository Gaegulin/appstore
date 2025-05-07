package com.shinhan.appstore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppView {
	
	public static void appDisplay(ArrayList<StoreDTO> apps) {
		System.out.printf("%-10s %-20s %-15s %-10s %-5s\n", "앱ID", "앱이름", "카테고리", "가격", "평점");
		System.out.println("---------------------------------------------------------------");
		for (StoreDTO app : apps) {
			System.out.printf("%-10s %-20s %-15s %-10d %.1f\n", app.getApp_id(), app.getApp_name(), app.getCategory(),
					app.getPrice(), app.getRating());
		}
	}
	public static void simpleAppDisplay(List<StoreDTO> appList) {
		System.out.println("------------ 구매 가능한 앱 목록 ------------");
		for (StoreDTO app : appList) {
			System.out.println(
					"앱 ID: " + app.getApp_id() + " / 앱 이름: " + app.getApp_name() + " / 앱 가격: " + app.getPrice());
		}
		System.out.println("-------------------------------------------");
	}
	
	public static void menuDisplay() {
		System.out.println("-------------------------");
		System.out.println("1.로그인 2.회원가입 3.배포자 메뉴 99.종료");
		System.out.println("-------------------------");
		System.out.print("작업선택>");
	}

	public static void loginMenu() {
		System.out.println("\n========= AppStore 메뉴 =========");
		System.out.println("1. 전체 앱 목록 보기 	2. 앱 구매하기");
		System.out.println("3. 내가 구매한 앱 보기 	4. 내 리뷰 보기");
		System.out.println("99. 로그아웃 ");
		System.out.println("===============================");
		System.out.print("작업선택> ");

	}
	public static void disMenu() {
		System.out.println("============ 배포자 메뉴 ============");
		System.out.println("1. 앱 등록하기");
		System.out.println("2. 수익금 확인하기");
		System.out.println("===================================");
	}
	public static void adminMenu() {
		System.out.println("------------ 관리자 메뉴 ------------");
		System.out.println("1. 앱 수정");
		System.out.println("2. 앱 삭제");
		System.out.println("3. 신고 리뷰 조회");
		System.out.println("4. 리뷰 수정");
		System.out.println("5. 리뷰 삭제");
		System.out.println("99. 로그아웃");
	}
	
	public static void reviewDisplay(List<ReviewDTO> reviewList) {
		for (ReviewDTO review : reviewList) {
			System.out.println("리뷰 ID: " + review.getReview_id());
			System.out.println("작성자: " + review.getUser_name()); // 사용자명 (앱명) 형태
			System.out.println("평점: " + review.getRating());
			System.out.println("내용: " + review.getContent());
			System.out.println("작성일: " + review.getReview_date());
			System.out.println("좋아요 수: " + review.getLike_count());
			System.out.println("신고 수: " + review.getReport_count());
			System.out.println("----------------------------------------");
		}
	}

	public static void myappDisplay(ArrayList<StoreDTO> myApps) {
		System.out.printf("%-10s %-20s %-15s %-10s %-5s\n", "앱ID", "앱이름", "카테고리", "가격", "평점");
		System.out.println("---------------------------------------------------------------");
		for (StoreDTO app : myApps) {
			System.out.printf("%-10s %-20s %-15s %-10d %.1f\n", app.getApp_id(), app.getApp_name(), app.getCategory(),
					app.getPrice(), app.getRating());
		}
	}

	public static void reviewChoice(ArrayList<ReviewDTO> reviews) {
		System.out.printf("%-5s %-10s %-5s %-30s %-12s %-6s %-6s\n", "ID", "작성자", "별점", "내용", "작성일", "♥", "⚠");
		System.out.println("--------------------------------------------------------------------------------");

		for (ReviewDTO review : reviews) {
			String shortContent = review.getContent();
			if (shortContent.length() > 28) {
				shortContent = shortContent.substring(0, 27) + "...";
			}

			System.out.printf("%-5d %-10s %-5.1f %-30s %-12s %-6d %-6d\n", review.getReview_id(), review.getUser_name(),
					review.getRating(), shortContent, review.getReview_date(), review.getLike_count(),
					review.getReport_count());
		}
	}
	
	public static void distriDisplay(List<Map<String, String>> distributorList) {
    	System.out.println("배급사 목록");
        System.out.println("-----------------------------");
        System.out.println("ID       | 회사명");
        System.out.println("-----------------------------");
		for (Map<String, String> d : distributorList) {
            System.out.printf("%-8s | %s\n", d.get("id"), d.get("company"));
        }
	}
	public static void revenueDisplye(List<Map<String, Object>> revenueList) {
		System.out.println("앱 ID | 앱 이름 | 총 판매 수익");
        System.out.println("----------------------------");
        for (Map<String, Object> revenue : revenueList) {
            System.out.printf("%-6s %-20s %-12d\n", 
                    revenue.get("app_id"), 
                    revenue.get("app_name"), 
                    revenue.get("total_revenue"));
        }
	}
}
