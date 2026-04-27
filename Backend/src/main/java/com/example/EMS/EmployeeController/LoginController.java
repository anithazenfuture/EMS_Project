package com.example.EMS.EmployeeController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.EMS.EmployeeDTO.LoginRequest;
import com.example.EMS.EmployeeService.Loginservice;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
	
	private Loginservice empservice;
	
	public LoginController(Loginservice empservice) {
		this.empservice = empservice;
		
	}
	
	@PostMapping("/login")
	public ResponseEntity<?>  getLoginResponse(@RequestBody LoginRequest login){
		ResponseEntity<?> emp = empservice.empLoginService(login);
		return emp;
	}
	
	

}
