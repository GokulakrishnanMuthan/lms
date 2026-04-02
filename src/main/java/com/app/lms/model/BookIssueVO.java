package com.app.lms.model;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.app.lms.entity.Book;
import com.app.lms.entity.BookIssue;
import com.app.lms.entity.BookIssueDetails;
import com.app.lms.entity.Devote;
import com.app.lms.entity.RackList;

import lombok.Data;

@Component
@Data
public class BookIssueVO {
		
	
		public boolean isValid;
		public Devote devoteObj;
		public List<Book> bookList;
		public String total;
		public List<BookIssue> bookIssueList;
		public List<Map<String, Object>> dataList;
		public List<BookIssueDetails> bookIssueDetailsList;
		public List<RackList> racksList;
}
