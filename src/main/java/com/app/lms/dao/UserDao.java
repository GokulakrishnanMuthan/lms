package com.app.lms.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.lms.entity.User;

@Repository
public interface UserDao extends CrudRepository<User, Long> {

//	@Query("select u from User u where lower(u.name) like lower(concat('%', ?1,'%'))")
//	Optional<User> getUserByName(String name);

	Optional<User> findByName(String name);

	@Query(value = "SELECT * FROM users u WHERE u.name = :name AND AES_DECRYPT(u.password, 'avg_lms') = :password", nativeQuery = true)
	User findbyNameAndPassword(@Param("name") String name, @Param("password") String password);
	
}
	