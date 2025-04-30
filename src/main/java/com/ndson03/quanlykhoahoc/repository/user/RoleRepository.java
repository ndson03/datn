package com.ndson03.quanlykhoahoc.repository.user;

import com.ndson03.quanlykhoahoc.domain.entity.Role;

public interface RoleRepository {
	
	public Role findRoleByName(String theRoleName);
}
