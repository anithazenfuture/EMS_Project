package com.example.EMS.EmployeeController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.EMS.EmployeeEntity.Employee;
import com.example.EMS.EmployeeService.EmpService;

@RestController
@RequestMapping("/api/employee")
public class EmpController {
	
	public EmpService empService;
	
	public EmpController(EmpService empService) {
		this.empService = empService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> createUserControll(@RequestBody Employee emp){
		return empService.createUser(emp);
		
	}
	
	@PostMapping("/registerImg")
	public ResponseEntity<?> createUserImg(@RequestPart("employee") Employee emp, @RequestPart(value= "file", required=false) MultipartFile file){
		return empService.createUserIMG(emp, file);
	}

}
