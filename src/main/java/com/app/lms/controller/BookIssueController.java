package com.app.lms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.lms.model.BookIssueVO;
import com.app.lms.service.BookIssueService;

@RestController
public class BookIssueController {
	
	@Autowired
    private BookIssueService bookService;
	
	
	@GetMapping("/returnBook/{devoteId}/{issueDate}")
    public List<Map<String, Object>> getBookReturnDetails(@PathVariable("devoteId") String devoteId,@PathVariable("issueDate") String issueDate) {
		BookIssueVO returnObj=  bookService.getBookReturnDetails(devoteId,issueDate);
		return returnObj.getDataList();

    }
    
	  @PostMapping("/bookreturn")
	public ResponseEntity<BookIssueVO> bookreturn(@RequestBody BookIssueVO bookVo) {
		try {
			BookIssueVO newBook = bookService.bookreturn(bookVo);
			return new ResponseEntity<>(newBook, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	  @GetMapping("/booklentdatewise")
	    public List<Map<String, Object>> booklentdatewise(@RequestParam("start") String issuestartDate,@RequestParam("end") String issueendDate) {
			BookIssueVO returnObj=  bookService.booklentdatewise(issuestartDate,issueendDate);
			return returnObj.getDataList();

	   }
	   
	  @GetMapping("/bookwiselentreport/{bookId}")
	    public List<Map<String, Object>> bookwiselentreport(@PathVariable("bookId") String bookId) {
			BookIssueVO returnObj=  bookService.bookwiselentreport(bookId);
			return returnObj.getDataList();

	    }
	  
	  
	  
	
}
