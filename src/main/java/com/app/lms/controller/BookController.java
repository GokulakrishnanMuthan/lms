package com.app.lms.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.app.lms.Util.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.lms.dao.BookDao;
import com.app.lms.entity.Book;
import com.app.lms.entity.RackList;
import com.app.lms.model.BookIssueVO;
import com.app.lms.service.BookService;

@RestController
public class BookController {
	
		@Autowired
	    private BookService bookService;
	    
	    @Autowired
	    private BookDao bookDao;
	    
	   
	    @GetMapping("/getAllBooks")
	    public List<Map<String, Object>>  getAllBooks() {
	        return bookService.getAllBooks().getDataList();
	    }
	    
	    @GetMapping("/book/{name}")
	    public ResponseEntity<Book> getBookByName(@PathVariable("name") String name) {
	    	
	    	Optional<Book> tData = bookService.getBookByName(name);

			if (tData.isPresent()) {
				return new ResponseEntity<>(tData.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new Book(), HttpStatus.OK);
			}
	    }
	    @GetMapping("/books/{id}")
	    public ResponseEntity<Book> getBookById(@PathVariable("id") long id) {
	    	
	    	Optional<Book> tData = bookService.getBookById(id);

			if (tData.isPresent()) {
				return new ResponseEntity<>(tData.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new Book(), HttpStatus.OK);
			}
	    }
	    
	    @PostMapping("/book")
		public ResponseEntity<Book> createBook(@RequestBody Book book) {
			try {
				Book newBook = bookService.createBook(book);
				return new ResponseEntity<>(newBook, HttpStatus.CREATED);
			} catch (Exception e) {
				return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	    
	    @PutMapping("/book/{id}")
		public ResponseEntity<Book> updateTutorial(@PathVariable("id") long id, @RequestBody Book book) {
			Optional<Book> bData = bookService.getBookById(id);

			if (bData.isPresent()) {
				Book _book = bData.get();
				//_book.setStatus(book.isStatus());
				//_user.setStatus(user.getIsActive());
				return new ResponseEntity<>(bookService.createBook(_book), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new Book(), HttpStatus.OK);
			}
		}
	    
	    @DeleteMapping("/book/{id}")
		public ResponseEntity<HttpStatus> deleteBook(@PathVariable("id") long id) {
			try {
				bookDao.deleteById(id);
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} catch (Exception e) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	    
	    @PostMapping("/bookIssue")
		public ResponseEntity<BookIssueVO> bookIssue(@RequestBody BookIssueVO bookVO) {
			try {
				BookIssueVO newBook = bookService.bookIssue(bookVO);
				return new ResponseEntity<>(newBook, HttpStatus.CREATED);
			} catch (Exception e) {
				return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	    
	    @GetMapping("/getAllBookIssues")
	    public List<Map<String, Object>>  getAllBookIssues() {
	        return bookService.getAllBookIssues().getDataList();
	    }
	    

	    @GetMapping("/loadDashboardDetails")
	    public List<Map<String, Object>>  loadDashboardDetails() {
	        return bookService.loadDashboardDetails().getDataList();
	    }
	    
	    @GetMapping("/getAvailableBooks")
	    public List<Book> getAvailableBooks() {
	        return bookService.getAvailableBooks();
	    }
	    
	    @GetMapping("/getAllBookwiseIssuesList")
	    public List<Map<String, Object>>  getAllBookwiseIssuesList() {
	        return bookService.getAllBookwiseIssuesList().getDataList();
	    }

	@GetMapping("/generateBarCodesForAllBooks")
	public ResponseEntity<BookIssueVO> generateBarCodesForAllBooks() {
		try {
			BookIssueVO newBook = bookService.generateBarCodesForAllBooks();
			return new ResponseEntity<>(newBook, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@GetMapping(value = "/book/{id}/barcode", produces = MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<byte[]> getBookBarcode(@PathVariable Long id) {
		Book book = bookService.getBookById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
		byte[] barcodeImage = book.getBarcode();
		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(barcodeImage);
	}
	
	@GetMapping("/getAllRackList")
    public ResponseEntity<BookIssueVO> getAllRackList() {
		try {
			BookIssueVO newBook = bookService.getAllRackList();
			return new ResponseEntity<>(newBook, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	  @GetMapping("/rack/{id}")
	    public ResponseEntity<RackList> getRackById(@PathVariable("id") long id) {
	    	
	    	Optional<RackList> tData = bookService.getRackById(id);

			if (tData.isPresent()) {
				return new ResponseEntity<>(tData.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new RackList(), HttpStatus.OK);
			}
	    }

		@PostMapping("/saveRack")
		public ResponseEntity<RackList> saveRack(@RequestBody RackList rack) {
			try {
				RackList newRack = bookService.saveRack(rack);
				return new ResponseEntity<>(newRack, HttpStatus.CREATED);
			} catch (Exception e) {
				return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		@GetMapping("/getAllRackListwithBookCount")
	    public ResponseEntity<BookIssueVO> getAllRackListwithBookCount() {
			try {
				BookIssueVO newRack = bookService.getAllRackListwithBookCount();
				return new ResponseEntity<>(newRack, HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
			}
	    }
//	    @GetMapping("/getAvailableBooksbyExcel")
//	   // public List<Book> getAvailableBooksbyExcel() throws IOException {
//	    public String getAvailableBooksbyExcel() throws IOException {
//	    	 XSSFWorkbook workbook = new XSSFWorkbook(); 
//    		 ByteArrayOutputStream bos = new ByteArrayOutputStream();
//    		 String encoded11 ="";
//	    	 try
//             {    
//	    		
//		        XSSFSheet sheet = workbook.createSheet("sheet1");// creating a blank sheet
//		         int rownum = 0;
//		         for (Book book : bookService.getAvailableBooks())
//		            {
//		            Row row = sheet.createRow(rownum++);
//		            createList(book, row);
//		           }                   
//		            workbook.write(bos);
//		            byte[] barray = bos.toByteArray();
//		            InputStream is = new ByteArrayInputStream(barray);
//		            byte[] bytes1 = IOUtils.toByteArray(is);
//		            encoded11 = Base64.getEncoder().encodeToString(bytes1);
//		          //  System.out.println(encoded11);
//		            is.close();
//             	} 
//		        catch (Exception e) 
//		        {
//		            e.printStackTrace();
//		        }
//	    	 return encoded11;
//	    	 
//	    }
//	    
//	    private void createList(Book book , Row row) // creating cells for each row
//	    {
//	            Cell cell = row.createCell(0);
//	            cell.setCellValue(book.getId());
//	            cell = row.createCell(1);
//	            cell.setCellValue(book.getTitle());
//	       }
	    
	    
}
