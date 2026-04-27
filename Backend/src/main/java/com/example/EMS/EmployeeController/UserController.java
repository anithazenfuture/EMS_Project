package com.example.EMS.EmployeeController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.EMS.EmployeeEntity.User;
import com.example.EMS.EmployeeService.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	public UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> createUserControll(@RequestBody User user){
		return userService.createUser(user);
		
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Long id){
		 return userService.getUserById(id);

	}
	

}
