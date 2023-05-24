package com.husc.payload.request;

import lombok.Data;

@Data
public class AddRoleRequest {
	private String username;
    private String roleName;
}
