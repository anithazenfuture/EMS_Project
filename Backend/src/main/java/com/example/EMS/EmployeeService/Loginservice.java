package com.example.EMS.EmployeeService;

import java.util.Optional;

import org.springframework.http.HttpStatus;
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
	
	
	public ResponseEntity<?> empLoginService(
	        @RequestBody LoginRequest login) {

	    Optional<User> user =
	            userRepository.findByEmail(login.getEmail());

	    if (user.isEmpty()) {

	        return ResponseEntity
	                .status(HttpStatus.UNAUTHORIZED)
	                .body("User not found");
	    }

	    User existingUser = user.get();

	    if (!passwordEncoder.matches(
	            login.getPassword(),
	            existingUser.getPassword())) {

	        return ResponseEntity
	                .status(HttpStatus.UNAUTHORIZED)
	                .body("Invalid password");
	    }

	    String token = jwt.generateToken(existingUser.getEmail());

	    LoginResponse response = new LoginResponse();

	    response.setToken(token);
	    response.setName(existingUser.getName());
	    response.setEmail(existingUser.getEmail());

	    
	    response.setRole(existingUser.getRoles());

	    return ResponseEntity.ok(response);
	}

}
