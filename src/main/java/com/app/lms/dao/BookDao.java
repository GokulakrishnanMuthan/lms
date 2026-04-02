package com.app.lms.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.app.lms.entity.Book;
import com.app.lms.entity.RackList;

@Repository
public interface BookDao extends CrudRepository<Book, Long>{

	Optional<Book> findByTitle(String name);
	
	@Query(value="SELECT b.* FROM Book b WHERE b.qty=1",nativeQuery = true)
	List<Book> getAvailableBooks();

	@Query(value="SELECT b.accessno FROM Book b WHERE b.accessno!='' order by b.id desc limit 1",nativeQuery = true)
    String findlastAccessno();

	@Query(value="SELECT b FROM RackList b")
	List<RackList> getAllRackList();
	
	@Query(value="SELECT b FROM RackList b where b.rackId=:id")
	Optional<RackList> getRackById(long id);

	@Query(value="SELECT b FROM RackList b where b.rackName=:name")
	Optional<RackList> getRackByName(String name);
}
