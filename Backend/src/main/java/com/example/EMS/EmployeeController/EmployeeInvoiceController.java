package com.example.EMS.EmployeeController;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.EMS.EmployeeEntity.EmployeeInvoice;
import com.example.EMS.EmployeeService.EmployeeInvoiceService;

@RestController
@RequestMapping("/api/invoice")
public class EmployeeInvoiceController {
	
	private final EmployeeInvoiceService invoiceService;
	
	public EmployeeInvoiceController(EmployeeInvoiceService invoiceService) {
		this.invoiceService = invoiceService;
		
	}



	@PostMapping("/create")
	public ResponseEntity<?> createInvoice(@RequestBody EmployeeInvoice invoice){
		if(invoice.getPhoneNumber() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phone number is null");
		}
		

		return invoiceService.createInvoice(invoice);
	}
	
	@GetMapping("/GetAll")
	public ResponseEntity<?> getAllInvoice(){
		return invoiceService.getAllInvoice();
	}
	
	@GetMapping("/GetById/{id}")
	public ResponseEntity<?> getAllInvoice(@PathVariable String id){
		return invoiceService.getInvoiceById(id);
	}
	
	@DeleteMapping("/deleteById/{id}")
	public ResponseEntity<?> getInvoice(@PathVariable String id){
		return invoiceService.deleteByInvoiceId(id);
	}
	
	@PatchMapping("/updateById/{id}")
	public ResponseEntity<?> updateById(@RequestBody EmployeeInvoice empInvoice, @PathVariable String id){
		return invoiceService.updateInvoice(empInvoice, id);
	}
	
	
}
