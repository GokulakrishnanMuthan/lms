package com.app.lms.model;

import java.util.List;

import com.app.lms.entity.User;

import lombok.Data;

@Data
public class UserResponse {

	private User users;
	private Boolean valid;
	private String responseMsg;
	private Boolean firstLogin=false;

	private int menuId;
	private String displayName;
	private String menuName;
	private int roleId;
	private String roleName;

	List<UserResponse> screenList = null;
	List<User> usersList = null;
}
