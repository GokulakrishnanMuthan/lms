package com.app.lms.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.app.lms.Util.DateUtil;
import com.app.lms.dao.BookDao;
import com.app.lms.dao.BookIssueDao;
import com.app.lms.dao.BookIssueDetailsDao;
import com.app.lms.dao.DevoteDao;
import com.app.lms.entity.Book;
import com.app.lms.entity.BookIssue;
import com.app.lms.entity.BookIssueDetails;
import com.app.lms.entity.Devote;
import com.app.lms.entity.RackList;
import com.app.lms.model.BookIssueVO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

@Service
public class BookService {
	
	 @Autowired
    private BookDao bookDao;
	 
	 @Autowired
	 private DevoteDao devoteDao;
	 
	 @Autowired
	 private BookIssueDetailsDao bookIssueDetailsDao;
	 
	 @Autowired
	 private BookIssueDao bookIssueDao;

	@Autowired
	JdbcTemplate jdbcTemplate;

	public BookIssueVO getAllBooks() {
		// TODO Auto-generated method stub
		BookIssueVO returnobj=new BookIssueVO();
		returnobj.setDataList(bookIssueDao.findAllBooks());
		return returnobj;
	}
	
	public Optional<Book> getBookByName(String name) {
		return bookDao.findByTitle(name);
	}

	public Optional<Book> getBookById(long id) {
		return bookDao.findById(id);
	}

	public Book createBook(Book book) {
		Book newBook = new Book();
		try {
			newBook= bookDao.save(book);
			if(newBook.getAccessno().length()==0) {
				String lastAccessNo=bookDao.findlastAccessno();
				int lastAccessNoInt=Integer.parseInt(lastAccessNo)+1;
				newBook.setAccessno(String.valueOf(lastAccessNoInt));
				bookDao.save(newBook);
			}

			if(newBook.getBarcode()==null) {
				byte[] barcode= generateBarCodes(newBook.getAccessno());
				newBook.setBarcode(barcode);
				bookDao.save(newBook);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newBook;
	}

	public BookIssueVO bookIssue(BookIssueVO bookVO) {
		//System.out.println("bookVO->"+bookVO.toString());
	    BookIssueVO returnObj=new BookIssueVO();
	    Devote dObj =new Devote();
		try {
		  //  SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");  
		   // String strDate = formatter.format(new Date());  
		   // System.out.println("Date Format withyyyy-MM-dd : "+strDate);  
			//Date date1=new SimpleDateFormat("dd-MM-yyyy").parse(strDate);  
			
			//System.out.println("Date Format date1 : "+date1);  
			
			Devote sObj =bookVO.getDevoteObj();
			
			Optional<Devote> tData = devoteDao.findByPhone(sObj.getPhone());
			//System.out.println("-->"+tData);
			if (tData.isPresent()) {
				dObj =tData.get();
			} else {
				sObj.setStatus(true);
				dObj =devoteDao.save(sObj);
			}
			
		//	System.out.println("Devote->"+dObj.toString());
			//System.out.println("exprie date->"+( DateUtil.addDays(sObj.getIssueDate(), Integer.parseInt(sObj.getDueDateCount()) ) ) );
		 SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");  
			 String strDate = formatter.format(sObj.getIssueDate());  
			// System.out.println("strDate->"+strDate);
			 
			//System.out.println("date to string->"+DateUtil.datetoString(sObj.getIssueDate()));
			
			Optional<BookIssue> biData = bookIssueDao.getBookIssueByDevote(dObj.getId(),DateUtil.datetoString(sObj.getIssueDate()));
			//System.out.println("biData->"+biData.toString());
			BookIssue bIssueObj=new BookIssue();
			if (biData.isPresent()) {
				bIssueObj =biData.get();
			} else {
				bIssueObj.setDevote_id(dObj.getId());
				bIssueObj.setIssueDate(sObj.getIssueDate());
				bIssueObj.setExpireDate(sObj.getDueDate());
				//bIssueObj.setDueDate(sObj.getDueDate());
				bIssueObj.setStatus(true);
				bookIssueDao.save(bIssueObj);
			}
			
			for(Book bObj: bookVO.getBookList()) {
				BookIssueDetails bIDetails=new BookIssueDetails();
				bIDetails.setBook_id(bObj.getId());
				bIDetails.setBookissue_id(bIssueObj.getId());
				bookIssueDetailsDao.save(bIDetails);
				bObj.setQty("0");
				bObj.setBookStatus("Issued");
				bookDao.save(bObj);
			}
			
			returnObj.setDevoteObj(dObj);
			returnObj.setBookList(bookVO.getBookList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return returnObj;
	}

	public BookIssueVO getAllBookIssues() {
		// TODO Auto-generated method stub
		BookIssueVO returnobj=new BookIssueVO();
		returnobj.setDataList(bookIssueDao.findAllBookIssues());
		return returnobj;
	}
	
	
	public BookIssueVO loadDashboardDetails() {
		// TODO Auto-generated method stub
		BookIssueVO returnobj=new BookIssueVO();
		returnobj.setDataList(bookIssueDao.loadDashboardDetails());
		return returnobj;
	}

	public List<Book> getAvailableBooks() {
		return bookDao.getAvailableBooks();
	}

	public BookIssueVO getAllBookwiseIssuesList() {
		BookIssueVO returnobj=new BookIssueVO();
		returnobj.setDataList(bookIssueDao.getAllBookwiseIssuesList());
		return returnobj;
	}

	public byte[] generateBarCodes(String barcodeText) throws IOException {

		Code128Writer barcodeWriter = new Code128Writer();

		Map<EncodeHintType, Object> hints = new HashMap<>();
		hints.put(EncodeHintType.MARGIN, 0); // Set barcode margin
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); // Set error correction level


		BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.CODE_128, 300, 150, hints);

		BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

		// convert BufferedImage to byte[]
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(barcodeImage, "png", baos);
		byte[] imageData = baos.toByteArray();

		return imageData;

	}

	public BookIssueVO generateBarCodesForAllBooks() {
		BookIssueVO returnobj=new BookIssueVO();
		try {
			List<Book> books = (List<Book>) bookDao.findAll();
			for (Book book : books) {
				try {
					byte[] barcodeImage = generateBarCodes(book.getAccessno());
					book.setBarcode(barcodeImage);
					bookDao.save(book);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			returnobj.setValid(true);
		} catch (Exception e) {
			returnobj.setValid(false);
			throw new RuntimeException(e);
		}
		return returnobj;
	}

	public BookIssueVO getAllRackList() {
		BookIssueVO returnobj=new BookIssueVO();
		try {
			List<RackList> racksList = (List<RackList>) bookDao.getAllRackList();
		
			returnobj.setValid(true);
			returnobj.setRacksList(racksList);
		} catch (Exception e) {
			returnobj.setValid(false);
			throw new RuntimeException(e);
		}
		return  returnobj;		
	}

	public Optional<RackList> getRackById(long id) {
		return bookDao.getRackById(id);
	}

	public RackList saveRack(RackList rack) {
		Optional<RackList> newRack = Optional.of(new RackList());
		try {
			newRack= bookDao.getRackByName(rack.getRackName());
			if(newRack.isPresent()) {
				//newRack.get().setRackName(rack.getRackName());
				String sql="update rack_list set title='"+rack.getTitle()+"',sub_title='"+rack.getSubTitle()+"', last_updated_by='admin', last_updated_date_time=now() where rack_id="+newRack.get().getRackId();
				jdbcTemplate.update(sql);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newRack.orElse(null);
	}

	public BookIssueVO getAllRackListwithBookCount() {
		BookIssueVO returnobj=new BookIssueVO();
		try {

			String sql="SELECT rl.rack_id AS rackId,b.rack_no AS rackName, COUNT(b.id) AS totalBooks,rl.title AS title,rl.sub_title AS subTitle FROM book b, rack_list rl WHERE rl.rack_name=b.rack_no GROUP BY b.rack_no;";
			List<RackList> racksList =	jdbcTemplate.query(sql, (rs, rowNum) -> {
				RackList rackList = new RackList();
				rackList.setRackId(rs.getLong("rackId"));
				rackList.setRackName(rs.getString("rackName"));
				rackList.setTotalBooks(rs.getInt("totalBooks"));
				rackList.setTitle(rs.getString("title"));
				rackList.setSubTitle(rs.getString("subTitle"));
				return rackList;
			});

			returnobj.setValid(true);
			returnobj.setRacksList(racksList);
		} catch (Exception e) {
			returnobj.setValid(false);
			throw new RuntimeException(e);
		}
		return  returnobj;
	}
}
