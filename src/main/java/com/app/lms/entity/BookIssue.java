package com.app.lms.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

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
