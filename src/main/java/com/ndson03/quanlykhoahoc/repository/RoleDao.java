package com.ndson03.quanlykhoahoc.repository;

import com.ndson03.quanlykhoahoc.entity.Role;

public interface RoleDao {
	
	public Role findRoleByName(String theRoleName);
}
