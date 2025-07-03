package com.mydating.dating.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.mydating.dating.dao.UserDao;
import com.mydating.dating.dto.MatchingUser;
import com.mydating.dating.entity.User;
import com.mydating.dating.util.UserGender;
import com.mydating.dating.util.UserSorting;

@Service
public class UserService {

	@Autowired
	UserDao userDao;

	public ResponseEntity<?> saveUser(User user) {
		User saveUser = userDao.saveUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(saveUser);
	}

	public ResponseEntity<?> findAllMaleUsers() {
		List<User> maleUsers = userDao.findAllMaleUsers();
		if(maleUsers.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No male users present");
		}else {
			return ResponseEntity.status(HttpStatus.OK).body(maleUsers);
		}
	}

	public ResponseEntity<?> findAllFemaleUsers() {
		List<User> femaleUsers = userDao.findAllFemaleUsers();
		if(femaleUsers.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No female found");
		}else {
			return ResponseEntity.status(HttpStatus.OK).body(femaleUsers);
		}
	}

	public ResponseEntity<?> findBestMatch(int id, int top) {
		Optional<User> optional = userDao.findById(id);
		if(optional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found with the id");
		}
		User user = optional.get();
		List<User> users = new ArrayList<>();
		if(user.getGender().equals(UserGender.MALE)) {
			users = userDao.findAllFemaleUsers();
		}else {
			users = userDao.findAllMaleUsers();
		}
		List<MatchingUser> matchingUsers = new ArrayList<>();
		for(User u : users) {
			MatchingUser mu = new MatchingUser();
			mu.setId(u.getId());
			mu.setName(u.getName());
			mu.setEmail(u.getEmail());
			mu.setPhone(u.getPhone());
			mu.setAge(u.getAge());
			mu.setGender(u.getGender());
			mu.setInterests(u.getInterests());
			
			mu.setAd(Math.abs(u.getAge()-user.getAge()));
			
			List<String> i1 = u.getInterests();
			List<String> i2 = user.getInterests();
			int ic=0;
			for(String s : i1){
				if(i2.contains(s))
					ic++;
			}
			
			mu.setImc(ic);
			
			matchingUsers.add(mu);
		
		}
		
		System.out.println(matchingUsers);
		Collections.sort(matchingUsers,new UserSorting());
		ArrayList<MatchingUser> res = new ArrayList<>();
		for(MatchingUser mu : matchingUsers) {
			if(top==0) {
				break;
			}
			res.add(mu);
			top--;
		}
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}
	
}
