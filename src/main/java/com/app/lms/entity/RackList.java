package com.app.lms.entity;

import java.sql.Date;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Data
@Table(name="rack_list")
public class RackList {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rack_id")
    private long rackId;
    
    @Column(name = "rack_name")
    private String rackName;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "sub_title")
    private String subTitle;
        
	@Column(name = "is_deleted")
    private String isDeleted;
    @Column(name = "last_updated_by")
    private String lastUpdatedBy;
    @Column(name = "last_updated_date_time")
    private Date lastUpdatedDateTime;

    @Transient
    private  int totalBooks;
}
