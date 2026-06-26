package com.app.lms.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.lms.dao.BookDao;
import com.app.lms.dao.BookIssueDao;
import com.app.lms.dao.BookIssueDetailsDao;
import com.app.lms.dao.DevoteDao;
import com.app.lms.entity.Book;
import com.app.lms.entity.BookIssue;
import com.app.lms.entity.BookIssueDetails;
import com.app.lms.model.BookIssueVO;

@Service
public class BookIssueService {
	
	 @Autowired
	 private BookIssueDetailsDao bookIssueDetailsDao;
	 
	 @Autowired
	 private BookIssueDao bookIssueDao;
	 
	 @Autowired
	 private BookDao bookDao;
		 
	public BookIssueVO getBookReturnDetails(String devoteId, String issueDate) {
		Date date = new Date();  
	    BookIssueVO returnObj=new BookIssueVO();
		try {
			// System.out.println("Date issueDate : "+issueDate); 
			 String[] stmp=issueDate.split("-");
			 String iDate=stmp[2]+"-"+stmp[1]+"-"+stmp[0];
			 List<Map<String, Object>> biData = bookIssueDao.getBookReturnDetails(devoteId,iDate);
			returnObj.setDataList(biData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return returnObj;
	}

	public BookIssueVO bookreturn(BookIssueVO bookVo) {
		 BookIssueVO returnObj=new BookIssueVO();
		 
			try {
				
				for(BookIssueDetails bObj : bookVo.getBookIssueDetailsList()) {
					
					if(bObj.getBookreturnDate() !=null) {
						//System.out.println("biData->"+bObj.toString());
						OffsetDateTime odt = OffsetDateTime.parse(bObj.getExpireDate());
						 Date date = Date.from(odt.toInstant());
						 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						 bObj.setBookexpireDate(date);
						 bookIssueDetailsDao.save(bObj);
						bookIssueDetailsDao.save(bObj);
						Optional<Book> bookObj = bookDao.findById(bObj.getBook_id());
						if(bookObj.isPresent()) {
							Book book = bookObj.get();
							book.setQty("1");
							book.setBookStatus("Available");
							bookDao.save(book);
						}
						
					}else {
						if(bObj.getExpireDate()!=null) {
							 OffsetDateTime odt = OffsetDateTime.parse(bObj.getExpireDate());
							 Date date = Date.from(odt.toInstant());
							 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							 bObj.setBookexpireDate(date);
							 bookIssueDetailsDao.save(bObj);
						}
						
					}
				}
				
				int rowCount=bookVo.getBookIssueDetailsList().size();
				int returnBookCount	=bookVo.getBookIssueDetailsList().stream().filter( b-> b.getBookreturnDate() !=null).collect(Collectors.toList()).size();
				if(rowCount == returnBookCount) {
					Optional<BookIssue> biObj= bookIssueDao.findById(bookVo.getBookIssueDetailsList().get(0).getBookissue_id());
					//System.out.println("biObj->"+biObj.toString());
					if(biObj.isPresent()) {
						BookIssue bookIssueObj = biObj.get();
						bookIssueObj.setStatus(false);
						bookIssueDao.save(bookIssueObj);
					}
					
				}else {
					
					for(BookIssueDetails bObj : bookVo.getBookIssueDetailsList()) {
							bookIssueDetailsDao.save(bObj);
					}
			
				}
				returnObj.setValid(true);
			} catch (Exception e) {
				returnObj.setValid(false);
				e.printStackTrace();
			}
			
			
			return returnObj;
	}

	public BookIssueVO getReturnByAccessno(String accessno) {
		BookIssueVO returnObj = new BookIssueVO();
		try {
			List<Map<String, Object>> biData = bookIssueDao.getReturnByAccessno(accessno);
			returnObj.setDataList(biData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnObj;
	}

	public BookIssueVO booklentdatewise(String issuestartDate, String issueendDate) {
		BookIssueVO returnobj=new BookIssueVO();
		//System.out.println("issuestartDate--"+issuestartDate);
		//System.out.println("issueendDate--"+issueendDate);
		returnobj.setDataList(bookIssueDao.booklentdatewise(issuestartDate, issueendDate));
		return returnobj;
	}

	public BookIssueVO bookwiselentreport(String bookId) {
		BookIssueVO returnobj=new BookIssueVO();
		//System.out.println("issuestartDate--"+issuestartDate);
		//System.out.println("issueendDate--"+issueendDate);
		returnobj.setDataList(bookIssueDao.bookwiselentreport(bookId));
		return returnobj;
	}
	 
	 
}
