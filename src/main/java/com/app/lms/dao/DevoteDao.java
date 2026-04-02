package com.app.lms.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.app.lms.entity.Devote;

public interface DevoteDao extends CrudRepository<Devote, Long>{
	
	Optional<Devote> findByPhone(String phone);
	List<Devote> findByStatus(boolean b);
	List<Devote> findByYearAndStatus(String academicYear, boolean status);
}
