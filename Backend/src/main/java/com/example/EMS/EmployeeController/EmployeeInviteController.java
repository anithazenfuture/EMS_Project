package com.example.EMS.EmployeeController;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	@GetMapping("/getAllform")
	public ResponseEntity<?> getAllForm(){
		return empInviteService.getAllForm();
	}
	
	
	@GetMapping("/getForm/{id}")
	public ResponseEntity<?> getFormById(@PathVariable Long id){
		return empInviteService.getFormById(id);
	}
	
	@DeleteMapping("/deleteForm/{id}")
	public ResponseEntity<?> deleteFormById(@PathVariable Long id){
		return empInviteService.deleteFormById(id);
	}
	
	@PostMapping("/convertList")
	public ResponseEntity<?> convertDataList(@RequestBody List<Long> empId){
		return empInviteService.convert(empId);
	}
	
	@PostMapping("/convert/{id}")
	public ResponseEntity<?> convertData(@PathVariable Long id){
		return empInviteService.convertByOne(id);
	}
	
	
	@PatchMapping("/updateForm/{id}")
	public ResponseEntity<?> updateFormById(@PathVariable Long id, @RequestPart("empInvite") EmployeeInvite empInvite, 
			@RequestPart(value= "file", required=false) MultipartFile file,
			@RequestPart(value= "passbook",required=false) MultipartFile passbook,
			@RequestPart(value= "education",required=false) MultipartFile education,
			@RequestPart(value="resume",required=false) MultipartFile resume,
			@RequestPart(value="offerLetter",required=false) MultipartFile offerLetter,
			@RequestPart(value="experienceLetter",required=false) List<MultipartFile> experienceLetter) throws Exception{
		return empInviteService.updateFormById(id, empInvite, file, passbook, education, resume, offerLetter, experienceLetter);
	}
	
	
	
	
	
	
}
