package com.app.lms.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "user_role")
    private String role;
    
    @Column(name = "password_due_date")
    private Date passwordDueDate;
    
    @Column(name = "last_login_date_time")
    private Date lastLoginDateTime;
    
    @Column(name = "last_password_update")
    private Date lastPasswordUpdate;
    
    @Column(name = "is_deleted")
    private String isDeleted;
    @Column(name = "last_updated_by")
    private String lastUpdatedBy;
    @Column(name = "last_updated_date_time")
    private Date lastUpdatedDateTime;



}
