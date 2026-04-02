package com.app.lms.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
public class UserRequest {

	private String name;
    private String password;

    private String userrole;
    private String email;
    private int userId;
    
}
