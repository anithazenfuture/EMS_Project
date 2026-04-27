package com.example.EMS.EmployeeService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.EMS.EmployeeEntity.User;
import com.example.EMS.EmployeeRepository.UserRepository;

@Service
public class UserService {
	
	public UserRepository userRepo;
	public PasswordEncoder passwordEncoder;
	
	
	
	public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}



	public ResponseEntity<?> createUser(@RequestBody User user){
		
		Optional<User> emailuser = userRepo.findByEmail(user.getEmail());
		
		if(emailuser.isPresent()) {
			return  ResponseEntity.status(409).body("User Already exists");
		}
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User admin = userRepo.save(user);
		return ResponseEntity.ok(admin);
		
	}
	
		public ResponseEntity<?> getUserById(Long id){
			
			Optional<User> user = userRepo.findById(id);
			
			if(user.isPresent()) {
				return ResponseEntity.status(302).body(user);
			}
			else {
				return ResponseEntity.status(404).body("User not found");
			}
			
		
		
		}
	
	

}
