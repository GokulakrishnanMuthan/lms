package com.app.lms.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.lms.entity.Devote;
import com.app.lms.service.DevoteService;

@RestController
public class DevoteController {


	@Autowired
	private DevoteService devoteService;


	@GetMapping("/devote/{phone}")
	public ResponseEntity<Devote> getDevoteByPhone(@PathVariable("phone") String phone) {

		Optional<Devote> tData = devoteService.getDevoteByPhone(phone);

		if (tData.isPresent()) {
			return new ResponseEntity<>(tData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(new Devote(), HttpStatus.OK);
		}
	}

	@GetMapping("/devote")
	public ResponseEntity<Iterable<Devote>> getAllDevote() {
		Iterable<Devote> tData = devoteService.getAllDevote();
		return new ResponseEntity<>(tData, HttpStatus.OK);
	}


	@PutMapping("/devotees/{id}/delete")
	public ResponseEntity<Void> softDeleteDevote(@PathVariable Long id) {
		boolean deleted = devoteService.softDeleteDevote(id);
		if (deleted) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/devotesave")
	public Devote saveDevote(@RequestBody Devote devote) {
		return devoteService.saveDevote(devote);
	}




}
