package com.example.EMS.EmployeeController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EMSController {

	@GetMapping("/check")
	public String getString() {
		return "Welcome";
	}
	
	
	
}
