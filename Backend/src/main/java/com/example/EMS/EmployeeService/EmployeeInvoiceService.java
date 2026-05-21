package com.example.EMS.EmployeeService;


import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.EMS.EmployeeEntity.EmployeeInvoice;
import com.example.EMS.EmployeeRepository.EmployeeInvoiceRepository;

@Service
public class EmployeeInvoiceService {
	
	private final EmployeeInvoiceRepository invoiceRepo;
	
	public EmployeeInvoiceService(EmployeeInvoiceRepository invoiceRepo) {
		this.invoiceRepo = invoiceRepo;
	}


	public ResponseEntity<?> createInvoice(EmployeeInvoice invoice){	
		Optional<EmployeeInvoice> inv = invoiceRepo.findByInvoiceNumber(invoice.getInvoiceNumber());
		if(inv.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invoice already found with: "+invoice.getInvoiceNumber());
		}
		EmployeeInvoice invRes = invoiceRepo.save(invoice);
		return ResponseEntity.ok(invRes);
	}
	
	
	public ResponseEntity<?> getAllInvoice(){	
		List<EmployeeInvoice> invList = invoiceRepo.findAll();
		return ResponseEntity.ok(invList);
	}
	
	public ResponseEntity<?> getInvoiceById(String id){
		Optional<EmployeeInvoice> res = invoiceRepo.findByInvoiceNumber(id);
		if(res.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No records found with id: "+id);
		}
		return ResponseEntity.ok(res);
		
	}
	
	public ResponseEntity<?> deleteByInvoiceId(String id){
		if(id == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide Invoice ID");
		}
		
		Optional<EmployeeInvoice> inv = invoiceRepo.findByInvoiceNumber(id);
		if(inv.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No records found with id: "+id);
		}
		
		invoiceRepo.deleteByInvoiceNumber(id);
		return ResponseEntity.ok("Invoice deleted with id: "+id);
	}
	
	public ResponseEntity<?> updateInvoice(EmployeeInvoice empInvoice, String id){
		if(id == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide Invoice ID");
		}
		
		Optional<EmployeeInvoice> inv = invoiceRepo.findByInvoiceNumber(id);
		if(inv.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No records found with id: "+id);
		}
		
		EmployeeInvoice existing = inv.get();
		
		if(empInvoice.getCustomerId() != null) {
			existing.setCustomerId(existing.getCustomerId());
		}
		if (empInvoice.getCustomerId() != null) {
		    existing.setCustomerId(empInvoice.getCustomerId());
		}

		if (empInvoice.getCompanyName() != null) {
		    existing.setCompanyName(empInvoice.getCompanyName());
		}

		if (empInvoice.getAddress() != null) {
		    existing.setAddress(empInvoice.getAddress());
		}

		if (empInvoice.getCity() != null) {
		    existing.setCity(empInvoice.getCity());
		}

		if (empInvoice.getState() != null) {
		    existing.setState(empInvoice.getState());
		}

		if (empInvoice.getWebsite() != null) {
		    existing.setWebsite(empInvoice.getWebsite());
		}

		if (empInvoice.getPhoneNumber() != null) {
		    existing.setPhoneNumber(empInvoice.getPhoneNumber());
		}

		if (empInvoice.getDescription() != null) {
		    existing.setDescription(empInvoice.getDescription());
		}

		if (empInvoice.getInvoiceNumber() != null) {
		    existing.setInvoiceNumber(empInvoice.getInvoiceNumber());
		}

		if (empInvoice.getInvoiceDate() != null) {
		    existing.setInvoiceDate(empInvoice.getInvoiceDate());
		}

		if (empInvoice.getDueDate() != null) {
		    existing.setDueDate(empInvoice.getDueDate());
		}

		if (empInvoice.getInvoiceStatus() != null) {
		    existing.setInvoiceStatus(empInvoice.getInvoiceStatus());
		}

		if (empInvoice.getCurrency() != null) {
		    existing.setCurrency(empInvoice.getCurrency());
		}

		if (empInvoice.getSubTotal() != 0) {
		    existing.setSubTotal(empInvoice.getSubTotal());
		}

		if (empInvoice.getCgst() != 0) {
		    existing.setCgst(empInvoice.getCgst());
		}

		if (empInvoice.getSgst() != 0) {
		    existing.setSgst(empInvoice.getSgst());
		}

		if (empInvoice.getGrandTotal() != 0) {
		    existing.setGrandTotal(empInvoice.getGrandTotal());
		}

		if (empInvoice.getProjectName() != null) {
		    existing.setProjectName(empInvoice.getProjectName());
		}
		
		EmployeeInvoice invoice = invoiceRepo.save(existing);
		
		return ResponseEntity.ok(invoice);
		


	}
	

}
