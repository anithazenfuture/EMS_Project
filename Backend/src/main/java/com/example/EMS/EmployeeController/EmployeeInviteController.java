package com.example.EMS.EmployeeController;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.example.EMS.EmployeeEntity.EmployeeInvite;
import com.example.EMS.EmployeeService.EmployeeInviteService;

@RestController
@RequestMapping("/api/employeeInvite")
public class EmployeeInviteController {
	
	private final EmployeeInviteService empInviteService;

	public EmployeeInviteController(EmployeeInviteService empInviteService) {
		this.empInviteService = empInviteService;
	}
	
	@PostMapping("/save")
	public ResponseEntity<?> saveEmployee(@RequestPart("empInvite") EmployeeInvite empInvite, 
			@RequestPart(value= "file", required=false) MultipartFile file,
			@RequestPart(value= "passbook",required=false) MultipartFile passbook,
			@RequestPart(value= "education",required=false) MultipartFile education,
			@RequestPart(value="resume",required=false) MultipartFile resume,
			@RequestPart(value="offerLetter",required=false) MultipartFile offerLetter,
			@RequestPart(value="experienceLetter",required=false) List<MultipartFile> experienceLetter){
		
		return empInviteService.saveEmployee(empInvite, file, passbook, education, resume, offerLetter, experienceLetter);
	}
	
	
	
	
	
	

}
