package com.example.EMS.EmployeeEntity;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class BankDetails {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	private String bankName;
	private String accountHolderName;
	private Long accountNumber;
	private Long confirmAccountNumber;
	private String branchName;
	private String ifsc_Number;
	private String passbook_pdf;
	
	@OneToOne
	@JoinColumn(name="employee_id")
	@JsonIgnore 
	private Employee employee;
	
	@OneToOne
    @JoinColumn(name = "id")
	@JsonBackReference
    private EmployeeInvite employeeInvite;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public String getPassbook_pdf() {
		return passbook_pdf;
	}
	public void setPassbook_pdf(String passbook_pdf) {
		this.passbook_pdf = passbook_pdf;
	}
	
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getAccountHolderName() {
		return accountHolderName;
	}
	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}
	public Long getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}
	public Long getConfirmAccountNumber() {
		return confirmAccountNumber;
	}
	public void setConfirmAccountNumber(Long confirmAccountNumber) {
		this.confirmAccountNumber = confirmAccountNumber;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getIfsc_Number() {
		return ifsc_Number;
	}
	public void setIfsc_Number(String ifsc_Number) {
		this.ifsc_Number = ifsc_Number;
	}
	public EmployeeInvite getEmployeeInvite() {
		return employeeInvite;
	}
	public void setEmployeeInvite(EmployeeInvite employeeInvite) {
		this.employeeInvite = employeeInvite;
	}
	
	
	
	
	
	

}
