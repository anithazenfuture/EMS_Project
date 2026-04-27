package com.example.EMS.EmployeeService;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.EMS.EmployeeDTO.LoginRequest;
import com.example.EMS.EmployeeDTO.LoginResponse;
import com.example.EMS.EmployeeEntity.User;
import com.example.EMS.EmployeeRepository.UserRepository;
import com.example.EMS.EmployeeSecurity.Jwtutil;

@Service
public class Loginservice {
	
	public final UserRepository userRepository;
	public final PasswordEncoder passwordEncoder;
	public final Jwtutil jwt;
	
	public Loginservice(UserRepository userRepository, PasswordEncoder passwordEncoder, Jwtutil jwt) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwt = jwt;

	}
	
	
	public ResponseEntity<?> empLoginService(@RequestBody LoginRequest login){
		Optional<User> user = userRepository.findByEmail(login.getEmail());
		if(user.isEmpty()) {
			return ResponseEntity.status(401).body("User not found");
		}
		
		User userOptional = user.get();
		
		if(!passwordEncoder.matches(login.getPassword(), userOptional.getPassword())) {
			return ResponseEntity.status(401).body("Password not matched");
		}
		
		String token = jwt.generateToken(userOptional.getEmail());
		
		LoginResponse response = new LoginResponse();
		response.setToken(token);
		response.setName(userOptional.getName());
		response.setEmail(userOptional.getEmail());
		response.setPassword(userOptional.getPassword());
		
		return ResponseEntity.ok(response);
		
		
	}

}
