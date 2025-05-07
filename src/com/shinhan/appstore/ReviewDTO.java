package com.shinhan.appstore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ReviewDTO {
    private int review_id;
    private String user_name;
    private double rating;
    private String content;
    private Date review_date;
    private int like_count;
    private int report_count;
}
