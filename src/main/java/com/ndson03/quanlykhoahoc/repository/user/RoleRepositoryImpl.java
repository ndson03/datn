package com.ndson03.quanlykhoahoc.repository.user;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ndson03.quanlykhoahoc.domain.entity.Role;

@Repository
public class RoleRepositoryImpl implements RoleRepository {
	
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public Role findRoleByName(String theRoleName) {
		
		Session session = entityManager.unwrap(Session.class);
		
		Query<Role> query = session.createQuery("from Role where name=:role", Role.class);
		query.setParameter("role", theRoleName);
		
		try {
			return query.getSingleResult();
		} catch (Exception exc) {
			return null;
		}
		
	}

}
