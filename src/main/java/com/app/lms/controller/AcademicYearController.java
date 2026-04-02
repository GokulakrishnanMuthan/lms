package com.app.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.lms.entity.AcademicYear;
import com.app.lms.service.AcademicYearService;

@RestController
@RequestMapping("/academic-year")
public class AcademicYearController {

	@Autowired
	private AcademicYearService service;

	@GetMapping("/current")
	public ResponseEntity<AcademicYear> getCurrentYear() {
		return service.getCurrentYear()
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/next")
	public ResponseEntity<AcademicYear> addNextYear(@RequestBody AcademicYear request) {
		AcademicYear newYear = service.addNextYear(request.getFromDate(), request.getToDate());
		return ResponseEntity.ok(newYear);
	}


}
