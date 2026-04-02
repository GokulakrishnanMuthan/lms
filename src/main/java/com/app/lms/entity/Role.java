package com.app.lms.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Role {

    @Id
    private String roleName;
    private String roleDescription;

  
}
