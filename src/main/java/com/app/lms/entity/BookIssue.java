package com.app.lms.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.Data;

@Entity
@Data
@Table(name="bookissue")
public class BookIssue {
	
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private long id;
	    
	    @Column(name = "devote_id")
	    private long devote_id;
	    
	    @Column(name = "issue_date")
	    private Date issueDate;
	    
	    @Column(name = "expire_date")
	    private Date expireDate;
	    	    
	    @Column(name = "status")
	    private boolean status;
	    
	    @Transient
	    private String name;
	    @Transient
	    private String phone;
	    @Transient
	    private String address;
	  
}
