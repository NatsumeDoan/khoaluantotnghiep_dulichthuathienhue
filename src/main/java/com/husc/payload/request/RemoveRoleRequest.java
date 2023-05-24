package com.husc.payload.request;

import lombok.Data;

@Data
public class RemoveRoleRequest {
	private String username;
    private String roleName;
}
