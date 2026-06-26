package com.app.lms.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.transaction.Transactional;

import lombok.Data;

@Entity
@Data
public class Book implements Serializable {
	
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "id")
	    private long id;
	    
	    @Column(name = "rack_no")
	    private String rackNo;
	    
	    @Column(name = "row")
	    private String row;
	    
	    @Column(name = "sl_no")
	    private String slNo;
	    
	    @Column(name = "language")
	    private String language;
	    
	    @Column(name = "topic")
	    private String topic;
	    
	    @Column(name = "extn")
	    private String extn;
	    
	    @Column(name = "an")
	    private String an;
	    
	    @Column(name = "sn")
	    private String sn;
	    
	    @Column(name = "title")
	    private String title;
	    
	    @Column(name = "isbn")
	    private String isbn;
	    
	    @Column(name = "author")
	    private String author;
	    
	    @Column(name = "publisher")
	    private String publisher;
	    
	    @Column(name = "year")
	    private String year;
	    
	    @Column(name = "book_condition")
	    private String bookCondition;
	    
	    @Column(name = "book_status")
	    private String bookStatus;
	    
	    @Column(name = "remarks")
	    private String remarks;
	    
	    @Column(name = "qty")
	    private String qty;

	    @Column(name = "status")
	    private String status;
	    
	    @Column(name = "accessno")
	    private String accessno;

	    @Column(name = "classno")
	    private String classno;

	    @Column(name = "location")
	    private String location;

	    @Column(name = "total_copies")
	    private int totalCopies;
	    
	    @Column(name = "colno")
	    private String colno;

		@Column(name="barcode", columnDefinition="BLOB")
		private byte[] barcode;

	    @Transient
	    private String amount;
	    
	    
}
