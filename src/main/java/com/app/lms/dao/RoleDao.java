package com.app.lms.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.app.lms.entity.Role;

@Repository
public interface RoleDao extends CrudRepository<Role, String> {

}
