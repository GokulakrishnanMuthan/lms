package com.app.lms.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.lms.dao.DevoteDao;
import com.app.lms.entity.Devote;

@Service
public class DevoteService {


	@Autowired
	private DevoteDao devoteDao;

	public Optional<Devote> getDevoteByPhone(String phone) {
		return devoteDao.findByPhone(phone);
	}

	public Iterable<Devote> getAllDevote() {
		return devoteDao.findAll();
	}

	public boolean softDeleteDevote(Long id) {
		Optional<Devote> devoteOpt = devoteDao.findById(id);
		if (devoteOpt.isPresent()) {
			Devote devote = devoteOpt.get();
			devote.setStatus(false); // set inactive
			devoteDao.save(devote);
			return true;
		}
		return false;
	}

	public Devote saveDevote(Devote devote) {
		return devoteDao.save(devote);
	}

}
