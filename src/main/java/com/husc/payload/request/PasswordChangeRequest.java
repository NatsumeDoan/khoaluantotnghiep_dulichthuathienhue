package com.husc.payload.request;

import lombok.Data;

@Data
public class PasswordChangeRequest {
	private String oldPassword;
	
	private String oldPassword2;

	private String newPassword;

}
