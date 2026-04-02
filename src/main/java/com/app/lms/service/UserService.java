package com.app.lms.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.app.lms.dao.AcademicYearRepository;
import com.app.lms.dao.DevoteDao;
import com.app.lms.dao.RoleDao;
import com.app.lms.dao.UserDao;
import com.app.lms.entity.AcademicYear;
import com.app.lms.entity.Devote;
import com.app.lms.entity.User;
import com.app.lms.model.UserRequest;
import com.app.lms.model.UserResponse;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private DevoteDao devoteDao;
    
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private AcademicYearRepository academicYearRepository;
    
    

//
//    public void initRoleAndUser() {
//
//        Role adminRole = new Role();
//        adminRole.setRoleName("Admin");
//        adminRole.setRoleDescription("Admin role");
//        roleDao.save(adminRole);
//
//        Role userRole = new Role();
//        userRole.setRoleName("User");
//        userRole.setRoleDescription("Default role for newly created record");
//        roleDao.save(userRole);
//
//        User adminUser = new User();
//        adminUser.setUserName("admin123");
//        adminUser.setUserPassword("admin@pass");
//        adminUser.setUserFirstName("admin");
//        adminUser.setUserLastName("admin");
//        Set<Role> adminRoles = new HashSet<>();
//        adminRoles.add(adminRole);
//       // adminUser.setRole(adminRoles);
//        userDao.save(adminUser);
//
////        User user = new User();
////        user.setUserName("raj123");
////        user.setUserPassword(getEncodedPassword("raj@123"));
////        user.setUserFirstName("raj");
////        user.setUserLastName("sharma");
////        Set<Role> userRoles = new HashSet<>();
////        userRoles.add(userRole);
////        user.setRole(userRoles);
////        userDao.save(user);
//    }

	/*
	 * public User registerNewUser(User user) { Role role =
	 * roleDao.findById("User").get(); Set<Role> userRoles = new HashSet<>();
	 * userRoles.add(role); // user.setRole(userRoles);
	 * user.setUserPassword(user.getUserPassword());
	 * 
	 * return userDao.save(user); }
	 */

	public List<User> getAllUsers() {
		return (List<User>) userDao.findAll();
	}


	public Optional<User> getUserById(long id) {
		return  userDao.findById(id);
	}


	public User createUser(User user) {
		return  userDao.save(user);
	}


	public Optional<User> getUserByName(String name) {
		return  userDao.findByName(name);
	}


	public List<Devote> getAllDevotes() {
		return (List<Devote>) devoteDao.findByYearAndStatus(academicYearRepository.getCurrentAcademicYear(),true);
	}


	public UserResponse authenticate(UserRequest userRequest) {
		   Optional<User> user= userDao.findByName(userRequest.getName());
	        UserResponse response=new UserResponse();
	        if(user!=null){
	            User user1= userDao.findbyNameAndPassword(userRequest.getName(),userRequest.getPassword());
	            if(user1!=null){
                    response.setUsers(user1);
                    response.setValid(true);
                    response.setResponseMsg("Login Success");
                }else{
	                response.setValid(false);
	                response.setResponseMsg("Invalid User Name and Password");
	            }
	        }else{
	            response.setValid(false);
	            response.setResponseMsg("Invalid User Name ");
	        }

	        return response;
	}


	public String getAcademicYear() {
		return academicYearRepository.getCurrentAcademicYear();
	}

  
}
