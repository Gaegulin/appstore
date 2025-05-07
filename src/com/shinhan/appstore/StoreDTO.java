package com.shinhan.appstore;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StoreDTO {
	private String app_id;
	private String app_name;
	private String category;
	private String version;
	private String description;
	private int price;
	private String distributor_id;
	private Date release_date;
	private double rating;
}
