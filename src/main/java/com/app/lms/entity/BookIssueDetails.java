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
@Table(name="bookissuedetails")
public class BookIssueDetails {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "bookissue_id")
    private long bookissue_id;
    
    @Column(name = "book_id")
    private long book_id;
    
    @Column(name = "book_return_date")
    private Date bookreturnDate;
    
    @Column(name = "book_expire_date")
    private Date bookexpireDate;
    
    @Column(name = "comments")
    private String comments;
    
    @Column(name = "book_lost")
    private String booklost;
    
    @Column(name = "fine_amount")
    private String fineAmount;
    
    @Column(name = "status")
    private String status;
    
    @Transient
    private String title;
   
    @Transient
    private String issueDate;
    
    @Transient
    private String expireDate;
    
    @Transient
    private int rowCount;
    
    @Transient
    private int returnDateCount;
    
}
