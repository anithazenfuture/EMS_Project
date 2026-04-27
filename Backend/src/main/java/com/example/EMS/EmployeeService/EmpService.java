package com.example.EMS.EmployeeService;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.EMS.EmployeeEntity.Employee;
import com.example.EMS.EmployeeRepository.EmpRepository;

@Service
public class EmpService {
	
	public EmpRepository empRepo;
	public PasswordEncoder passwordEncoder;
	
	
	
	public EmpService(EmpRepository empRepo, PasswordEncoder passwordEncoder) {
		this.empRepo = empRepo;
		this.passwordEncoder = passwordEncoder;
	}



	public ResponseEntity<?> createUser(@RequestBody Employee emp){
		
		Optional<Employee> emailuser = empRepo.findByEmail(emp.getEmail());
		
		if(emailuser.isPresent()) {
			return ResponseEntity.status(409).body("User Already exists");
		}
		
		emp.setPassword(passwordEncoder.encode(emp.getPassword()));
		Employee employee = empRepo.save(emp);
		return ResponseEntity.ok(employee);
		
	}
	
	public ResponseEntity<?> createUserIMG(@RequestPart("employee") Employee emp, @RequestPart(value= "file", required=false) MultipartFile file){
		
		Optional<Employee> emailuser = empRepo.findByEmail(emp.getEmail());
		
		if(emailuser.isPresent()) {
			return ResponseEntity.status(409).body("User Already exists");
		}
		
		if(file != null && !file.isEmpty()) {
			try {
				String upload = System.getProperty("user.dir") + "/uploads/";
				File dir = new File(upload);

				if (!dir.exists()) dir.mkdirs(); // better than mkdir()

				String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

				File destination = new File(upload + fileName);

				System.out.println("Saving to: " + destination.getAbsolutePath());
				System.out.println("File is empty: " + file.isEmpty());
				System.out.println("File name: " + file.getOriginalFilename());
				System.out.println("File size: " + file.getSize());

				file.transferTo(destination);

				emp.setImgFile(fileName);
				
				
			}
			catch(Exception e) {
				return ResponseEntity.status(500).body("Image upload failed");
			}
		}
		
		emp.setPassword(passwordEncoder.encode(emp.getPassword()));
		Employee employee = empRepo.save(emp);
		return ResponseEntity.ok(employee);
		
	}
	

}
