package com.app.lms.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.lms.entity.AcademicYear;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    Optional<AcademicYear> findByActiveTrue(); // current year
    
    @Query(value = "SELECT CONCAT(YEAR(from_date), '-', YEAR(to_date)) FROM academic_year WHERE CURDATE() BETWEEN from_date AND to_date LIMIT 1", nativeQuery = true)
    String getCurrentAcademicYear();
    
}

