package com.app.lms.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.app.lms.entity.BookIssue;

public interface BookIssueDao extends CrudRepository<BookIssue, Long>{
	
//	@Query(value="SELECT bi.id AS id,bi.devote_id AS devoteid,DATE_FORMAT(bi.issue_date,'%d-%m-%Y') AS issueDate,DATE_FORMAT(bi.expire_date,'%d-%m-%Y') AS expireDate,d.name AS name,d.phone AS phone ,d.address AS address,d.email AS email,IF(DATE(bi.expire_date) < DATE(NOW()) ,1,0) AS overDue FROM bookissue bi,devote d WHERE bi.status=1 AND d.id=bi.devote_id",nativeQuery = true)
	@Query(value="SELECT bi.id AS id, bi.devote_id AS devoteId, IFNULL(DATE_FORMAT(bi.issue_date,'%d-%m-%Y'),'') AS issueDate, IFNULL(DATE_FORMAT(bi.expire_date,'%d-%m-%Y'),'') AS expireDate, IFNULL(d.name,'') AS name, IFNULL(d.phone,'') AS phone, IFNULL(d.address,'') AS address, IFNULL(d.email,'') AS email, IF(DATE(bi.expire_date) < DATE(NOW()),1,0) AS overDue,IF(DATE(bi.expire_date) < DATE(NOW()), DATEDIFF(DATE(NOW()), DATE(bi.expire_date)), 0) AS daysOverDue FROM bookissue bi JOIN devote d ON d.id = bi.devote_id JOIN academic_year ay ON bi.issue_date BETWEEN ay.from_date AND ay.to_date WHERE bi.status = 1 AND ay.active = 1;",nativeQuery = true)
	List<Map<String, Object>> findAllBookIssues();
	
	//@Query(value="SELECT * FROM (SELECT COUNT(*) AS bookCount FROM book WHERE STATUS='Y' )  AS bookCount ,(SELECT COUNT(*) AS bookIssueCount FROM bookissuedetails  WHERE book_return_date IS null) AS bookIssueCount ,(SELECT COUNT(*) AS devoteCount  FROM devote WHERE STATUS=1) AS devoteCount,(SELECT count(IF(DATE(bi.expire_date) < DATE(NOW()) ,1,NULL) ) AS overDueBooks FROM bookissuedetails bid,book b,bookissue bi,devote d WHERE b.id=bid.book_id AND bi.id=bid.bookissue_id AND bi.devote_id=d.id and bid.book_return_date IS NULL) AS overDueBooks",nativeQuery = true)
	@Query(value="SELECT (SELECT COUNT(*) FROM book WHERE status='Y') AS bookCount,(SELECT COUNT(*) FROM bookissuedetails bid JOIN bookissue bi ON bid.bookissue_id=bi.id JOIN academic_year ay ON bi.issue_date BETWEEN ay.from_date AND ay.to_date WHERE bid.book_return_date IS NULL AND ay.active=1) AS bookIssueCount,(SELECT COUNT(*) FROM devote d JOIN academic_year ay ON d.year=CONCAT(YEAR(ay.from_date),'-',YEAR(ay.to_date)) WHERE d.status=1 AND ay.active=1) AS devoteCount,(SELECT COUNT(*) FROM bookissuedetails bid JOIN bookissue bi ON bid.bookissue_id=bi.id JOIN book b ON b.id=bid.book_id JOIN devote d ON d.id=bi.devote_id JOIN academic_year ay ON bi.issue_date BETWEEN ay.from_date AND ay.to_date WHERE bid.book_return_date IS NULL AND DATE(bi.expire_date)<CURDATE() AND ay.active=1) AS overDueBooks,IFNULL((SELECT from_date FROM academic_year WHERE active=1 LIMIT 1),'') AS currentFromDate,IFNULL((SELECT to_date FROM academic_year WHERE active=1 LIMIT 1),'') AS currentToDate;",nativeQuery = true)
	List<Map<String, Object>> loadDashboardDetails();
	
	@Query(value="SELECT * FROM bookissue bi WHERE bi.devote_id=:devoteId AND bi.issue_date=:today AND bi.status=1",nativeQuery = true)
	Optional<BookIssue> getBookIssueByDevote(long  devoteId,String today);
	
	
	@Query(value="SELECT * FROM book b WHERE b.`status`='Y'",nativeQuery = true)
	List<Map<String, Object>> findAllBooks();
	
	@Query(value="SELECT bd.id AS id,b.id AS book_id,CONCAT_WS('-', b.title, b.`language`,b.author,b.year) AS title,DATE_FORMAT(bi.issue_date,'%d-%m-%Y') AS issueDate,DATE_FORMAT(bd.book_expire_date,'%d-%m-%Y') AS expireDate,bd.book_return_date AS bookreturnDate,bd.`status` AS status,bd.book_lost AS booklost,bd.comments AS comments,bd.fine_amount AS fineAmount,bd.bookissue_id AS bookissue_id FROM bookissue bi,bookissuedetails bd,book b WHERE  bi.id=bd.bookissue_id and b.id=bd.book_id and bi.devote_id=:devoteId AND bi.issue_date=:issueDate",nativeQuery = true)
	List<Map<String, Object>> getBookReturnDetails(String devoteId, String issueDate);

	// Returns the open (not-yet-returned) issue for a single copy, matched by its access number.
	// Same column shape as getBookReturnDetails so rows feed straight into POST /bookreturn, plus accessno.
	@Query(value="SELECT bd.id AS id,b.id AS book_id,CONCAT_WS('-', b.title, b.`language`,b.author,b.year) AS title,DATE_FORMAT(bi.issue_date,'%d-%m-%Y') AS issueDate,DATE_FORMAT(bd.book_expire_date,'%d-%m-%Y') AS expireDate,bd.book_return_date AS bookreturnDate,bd.`status` AS status,bd.book_lost AS booklost,bd.comments AS comments,bd.fine_amount AS fineAmount,bd.bookissue_id AS bookissue_id,b.accessno AS accessno FROM bookissue bi,bookissuedetails bd,book b WHERE bi.id=bd.bookissue_id AND b.id=bd.book_id AND bi.status=1 AND bd.book_return_date IS NULL AND b.accessno=:accessno",nativeQuery = true)
	List<Map<String, Object>> getReturnByAccessno(String accessno);
	
//	@Query(value="SELECT b.id AS bookId,bid.id AS bookIssueId,CONCAT_WS('-', b.title, b.`language`,b.author,b.year) AS title,d.id AS devoteId,d.name AS devoteName,d.phone AS phoneNo,d.address AS roomNo,DATE_FORMAT(bi.issue_date,'%d-%m-%Y') AS issueDate,DATE_FORMAT(bi.expire_date,'%d-%m-%Y') as returnDate,IF(DATE(bi.expire_date) < DATE(NOW()) ,1,0) AS overDue FROM bookissuedetails bid,book b,bookissue bi,devote d WHERE b.id=bid.book_id AND bi.id=bid.bookissue_id AND bi.devote_id=d.id and bid.book_return_date IS null",nativeQuery = true)
	@Query(value="SELECT b.id AS bookId, bid.id AS bookIssueId, CONCAT_WS('-', b.title, b.`language`, b.author, b.year) AS title, d.id AS devoteId, d.name AS devoteName, d.phone AS phoneNo, d.address AS roomNo, DATE_FORMAT(bi.issue_date,'%d-%m-%Y') AS issueDate, DATE_FORMAT(bi.expire_date,'%d-%m-%Y') AS returnDate, IF(DATE(bi.expire_date) < DATE(NOW()),1,0) AS overDue FROM bookissuedetails bid JOIN book b ON b.id = bid.book_id JOIN bookissue bi ON bi.id = bid.bookissue_id JOIN devote d ON bi.devote_id = d.id JOIN academic_year ay ON bi.issue_date BETWEEN ay.from_date AND ay.to_date WHERE bid.book_return_date IS NULL AND ay.active = 1;",nativeQuery = true)
	List<Map<String, Object>> getAllBookwiseIssuesList();
	
	@Query(value="SELECT b.id AS bookId,CONCAT_WS('-', b.title, b.`language`,b.author,b.year) AS title,d.id AS devoteId,d.name AS devoteName,d.phone AS phoneNo,d.address AS roomNo,DATE_FORMAT(bi.issue_date,'%d-%m-%Y') AS issueDate,DATE_FORMAT(bi.expire_date,'%d-%m-%Y') as returnDate,bid.book_return_date AS bookReturnDate FROM bookissuedetails bid,book b,bookissue bi,devote d WHERE b.id=bid.book_id AND bi.id=bid.bookissue_id AND bi.devote_id=d.id AND (DATE_FORMAT(bi.issue_date,'%d-%m-%Y') BETWEEN :issuestartDate AND :issueendDate)",nativeQuery = true)
	List<Map<String, Object>> booklentdatewise(String issuestartDate, String issueendDate);

	@Query(value="SELECT b.id AS bookId,CONCAT_WS('-', b.title, b.`language`,b.author,b.year) AS title,d.id AS devoteId,d.name AS devoteName,d.phone AS phoneNo,d.address AS roomNo,DATE_FORMAT(bi.issue_date,'%d-%m-%Y') AS issueDate,DATE_FORMAT(bi.expire_date,'%d-%m-%Y') as returnDate,bid.book_return_date AS bookReturnDate FROM bookissuedetails bid,book b,bookissue bi,devote d WHERE b.id=bid.book_id AND bi.id=bid.bookissue_id AND bi.devote_id=d.id AND  b.id=:bookId",nativeQuery = true)
	List<Map<String, Object>> bookwiselentreport(String bookId);
	
	
	
}
