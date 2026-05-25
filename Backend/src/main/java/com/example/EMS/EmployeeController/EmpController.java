package com.example.EMS.EmployeeController;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.EMS.EmployeeEntity.Employee;
import com.example.EMS.EmployeeService.EmpService;

@RestController
@CrossOrigin("*")
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
	
	
	
	@PostMapping("/registerEmp")
	public ResponseEntity<?> createUserImg(@RequestPart("employee") Employee emp, 
			@RequestPart(value= "file", required=false) MultipartFile file,
			
			@RequestPart(value= "aadhar", required=false) MultipartFile aadhar,
			@RequestPart(value= "pan_card", required=false) MultipartFile pan_card,
			@RequestPart(value= "higherEducation", required=false) MultipartFile higherEducation,
			@RequestPart(value= "bankStatement", required=false) List<MultipartFile> bankStatement,
			@RequestPart(value= "salarySlip", required=false) List<MultipartFile> salarySlip,
			
			@RequestPart(value= "passbook",required=false) MultipartFile passbook,
			@RequestPart(value= "education",required=false) MultipartFile education,
			@RequestPart(value="resume",required=false) MultipartFile resume,
			@RequestPart(value="offerLetter",required=false) MultipartFile offerLetter,
			@RequestPart(value="prevExpLetter",required=false) List<MultipartFile> prevExpLetter,
			@RequestPart(value="experienceLetter",required=false) List<MultipartFile> experienceLetter){
		
		
		return empService.createEmpIMG(emp, file,aadhar,pan_card, higherEducation,bankStatement, salarySlip, passbook, education, resume, offerLetter, prevExpLetter,experienceLetter);
	}
	
	
	@GetMapping("/getData")
	public ResponseEntity<?> getAllEmployeeDetails(){
		
		return empService.getAllEmployeeDetails();
	}
	
	@GetMapping("/getPayroll/{empId}")
	public ResponseEntity<?> getEmployeePayrollById(@PathVariable String empId){
		return empService.getPayrollById(empId);
		
	}
	
	@GetMapping("/getEmployee/{empId}")
	public ResponseEntity<?> getEmployeeById(@PathVariable String empId){
		return empService.getEmployeeById(empId);
		
	}
	
	@DeleteMapping("/deleteEmployee/{empId}")
	public ResponseEntity<?> deleteEmployeeById(@PathVariable String empId){
		return empService.deleteEmployeeById(empId);
		
	}
	
	@PutMapping(value = "/updateEmployee/{empId}", consumes = "multipart/form-data")
	public ResponseEntity<?> updateEmployee(

	        @PathVariable String empId,
	        @RequestPart(value = "employee", required = false) Employee emp,
	        @RequestPart(value = "file", required = false) MultipartFile file,
	        @RequestPart(value = "aadhar", required = false) MultipartFile aadhar,
	        @RequestPart(value = "pan_card", required = false) MultipartFile pan_card,
	        @RequestPart(value = "higherEducation", required = false) MultipartFile higherEducation,
	        @RequestPart(value="prevExpLetter",required=false) List<MultipartFile> prevExpLetter,
	        @RequestPart(value = "bankStatement", required = false)
	        List<MultipartFile> bankStatement,
	        
	        @RequestPart(value = "salarySlip", required = false)
	        List<MultipartFile> salarySlip,

	        @RequestPart(value = "passbook", required = false)
	        MultipartFile passbook,

	        @RequestPart(value = "education", required = false)
	        MultipartFile education,

	        @RequestPart(value = "resume", required = false)
	        MultipartFile resume,

	        @RequestPart(value = "offerLetter", required = false)
	        MultipartFile offerLetter,

	        @RequestPart(value = "experienceLetter", required = false)
	        List<MultipartFile> experienceLetter

	) throws Exception {

	    return empService.updateEmployeeAll(
	            empId,emp,file,aadhar,pan_card,higherEducation,bankStatement,salarySlip,prevExpLetter,passbook,education,resume,offerLetter,experienceLetter
	    );
	}
	
	@PostMapping(value = "/uploadExcel", consumes = "multipart/form-data")
	public ResponseEntity<?> createUserXL(

	        @RequestPart(value = "xlFile")
	        MultipartFile xlFile,

	        @RequestPart(value = "file", required = false)
	        List<MultipartFile> file,

	        @RequestPart(value = "aadhar", required = false)
	        List<MultipartFile> aadhar,

	        @RequestPart(value = "pan_card", required = false)
	        List<MultipartFile> pan_card,

	        @RequestPart(value = "higherEducation", required = false)
	        List<MultipartFile> higherEducation,

	        @RequestPart(value = "bankStatement", required = false)
	        List<MultipartFile> bankStatement,

	        @RequestPart(value = "salarySlip", required = false)
	        List<MultipartFile> salarySlip,

	        @RequestPart(value = "passbook", required = false)
	        List<MultipartFile> passbook,

	        @RequestPart(value = "education", required = false)
	        List<MultipartFile> education,

	        @RequestPart(value = "resume", required = false)
	        List<MultipartFile> resume,

	        @RequestPart(value = "offerLetter", required = false)
	        List<MultipartFile> offerLetter,

	        @RequestPart(value = "prevExpLetter", required = false)
	        List<MultipartFile> prevExpLetter,

	        @RequestPart(value = "experienceLetter", required = false)
	        List<MultipartFile> experienceLetter) {

	    return empService.createUserXL(
	            xlFile,
	            file,
	            aadhar,
	            pan_card,
	            higherEducation,
	            bankStatement,
	            salarySlip,
	            passbook,
	            education,
	            resume,
	            offerLetter,
	            prevExpLetter,
	            experienceLetter);
	}
	
	@PatchMapping(value = "/updateExcel", consumes = "multipart/form-data")
	public ResponseEntity<?> updateUserXL(

	        @RequestPart(value = "xlFile", required = false)
	        MultipartFile xlFile,

	        @RequestPart(value = "file", required = false)
	        List<MultipartFile> file,

	        @RequestPart(value = "aadhar", required = false)
	        List<MultipartFile> aadhar,

	        @RequestPart(value = "pan_card", required = false)
	        List<MultipartFile> pan_card,

	        @RequestPart(value = "passbook", required = false)
	        List<MultipartFile> passbook,

	        @RequestPart(value = "education", required = false)
	        List<MultipartFile> education,

	        @RequestPart(value = "higherEducation", required = false)
	        List<MultipartFile> higherEducation,

	        @RequestPart(value = "resume", required = false)
	        List<MultipartFile> resume,

	        @RequestPart(value = "offerLetter", required = false)
	        List<MultipartFile> offerLetter,

	        @RequestPart(value = "prevExpLetter", required = false)
	        List<MultipartFile> prevExpLetter,

	        @RequestPart(value = "experienceLetter", required = false)
	        List<MultipartFile> experienceLetter,

	        @RequestPart(value = "bankStatement", required = false)
	        List<MultipartFile> bankStatement,

	        @RequestPart(value = "salarySlip", required = false)
	        List<MultipartFile> salarySlip

	) {

	    return empService.updateUserXL(
	            xlFile,
	            file,
	            aadhar,
	            pan_card,
	            passbook,
	            education,
	            higherEducation,
	            resume,
	            offerLetter,
	            prevExpLetter,
	            experienceLetter,
	            bankStatement,
	            salarySlip
	    );
	}
	
	
	
	
	
	
	
	
	

}
