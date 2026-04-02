package com.app.lms.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.lms.dao.AcademicYearRepository;
import com.app.lms.entity.AcademicYear;

@Service
public class AcademicYearService {

    @Autowired
    private AcademicYearRepository repository;

    // Get current year
    public Optional<AcademicYear> getCurrentYear() {
        return repository.findByActiveTrue();
    }

    // Add next year and deactivate current one
    public AcademicYear addNextYear(LocalDate fromDate, LocalDate toDate) {
        // deactivate old one
        repository.findByActiveTrue().ifPresent(year -> {
            year.setActive(false);
            repository.save(year);
        });

        // add new academic year
        AcademicYear newYear = new AcademicYear();
        newYear.setFromDate(fromDate);
        newYear.setToDate(toDate);
        newYear.setActive(true);
        return repository.save(newYear);
    }
}
