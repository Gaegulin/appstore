package com.shinhan.appstore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDTO {
	private String user_id;
	private String password; 
	private String name;
	private String email;
	private String user_level;
}
