package com.app.lms.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.lms.dao.UserDao;
import com.app.lms.entity.Devote;
import com.app.lms.entity.User;
import com.app.lms.model.UserRequest;
import com.app.lms.model.UserResponse;
import com.app.lms.service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserDao userDao;
    
   // @PostConstruct
	/*
	 * public void initRoleAndUser() { userService.initRoleAndUser(); }
	 * 
	 * @PostMapping({"/registerNewUser"}) public User registerNewUser(@RequestBody
	 * User user) { return userService.registerNewUser(user); }
	 */

    @GetMapping("/getAllUsers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    @PostMapping("/authentication")
    public ResponseEntity<UserResponse> authenticate(@RequestBody UserRequest userRequest) {
    	UserResponse response=(UserResponse) userService.authenticate(userRequest);
        return new ResponseEntity<UserResponse>(response, HttpStatus.OK);
    }

    
    
    @GetMapping("/getUser/{name}")
    public ResponseEntity<User> getUserByName(@PathVariable("name") String name) {
    	
    	Optional<User> tutorialData = userService.getUserByName(name);

		if (tutorialData.isPresent()) {
			return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
    }
    
    @GetMapping("/User/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
    	
    	Optional<User> tutorialData = userService.getUserById(id);

		if (tutorialData.isPresent()) {
			return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
    }
    
    @PostMapping("/user")
	public ResponseEntity<User> createUser(@RequestBody User user) {
		try {
			User newUser = userService.createUser(user);
			return new ResponseEntity<>(newUser, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
    
    @PutMapping("/user/{id}")
	public ResponseEntity<User> updateTutorial(@PathVariable("id") long id, @RequestBody User user) {
		Optional<User> userData = userService.getUserById(id);

		if (userData.isPresent()) {
			User _user = userData.get();
			_user.setEmail(user.getEmail());
			_user.setRole(user.getRole());
			//_user.setStatus(user.getIsActive());
			return new ResponseEntity<>(userService.createUser(_user), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
    
    @DeleteMapping("/user/{id}")
	public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
		try {
			userDao.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
    	
    @GetMapping("/getAllDevotes")
    public List<Devote> getAllDevotes() {
        return userService.getAllDevotes();
    }
    
    @GetMapping("/getAcademicYear")
    public String getAcademicYear() {
        return userService.getAcademicYear();
    }
}
