package com.example.EMS.EmployeeEntity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;

@Entity
public class Experience {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long expId;
	private String company_name;
	private String job_title;
	private String emp_type_prev;
	private Date emp_start;
	private Date emp_end;
	
	private String currently_working;
	private String Duration;
	private String tech_used;
	private String roles_responsibilities;
	private String exp_letter;
	private String bankStatement_pdf;
	private String salarySlip_pdf;
	
	@ManyToOne
    @JoinColumn(name = "employee_id")
	@JsonIgnore 
    private Employee employee;
	
	
	
	public Long getExpId() {
		return expId;
	}
	public void setExpId(Long expId) {
		this.expId = expId;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public void setEmp_type_prev(String emp_type_prev) {
		this.emp_type_prev = emp_type_prev;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getJob_title() {
		return job_title;
	}
	public void setJob_title(String job_title) {
		this.job_title = job_title;
	}
	public String getEmp_type_prev() {
		return emp_type_prev;
	}
	public void setEmp_type(String emp_type_prev) {
		this.emp_type_prev = emp_type_prev;
	}
	public Date getEmp_start() {
		return emp_start;
	}
	public void setEmp_start(Date emp_start) {
		this.emp_start = emp_start;
	}
	public Date getEmp_end() {
		return emp_end;
	}
	public void setEmp_end(Date emp_end) {
		this.emp_end = emp_end;
	}
	public String getCurrently_working() {
		return currently_working;
	}
	public void setCurrently_working(String currently_working) {
		this.currently_working = currently_working;
	}
	public String getDuration() {
		return Duration;
	}
	public void setDuration(String duration) {
		Duration = duration;
	}
	public String getTech_used() {
		return tech_used;
	}
	public void setTech_used(String tech_used) {
		this.tech_used = tech_used;
	}
	public String getRoles_responsibilities() {
		return roles_responsibilities;
	}
	public void setRoles_responsibilities(String roles_responsibilities) {
		this.roles_responsibilities = roles_responsibilities;
	}
	public String getExp_letter() {
		return exp_letter;
	}
	public void setExp_letter(String exp_letter) {
		this.exp_letter = exp_letter;
	}
	public String getBankStatement_pdf() {
		return bankStatement_pdf;
	}
	public void setBankStatement_pdf(String bankStatement_pdf) {
		this.bankStatement_pdf = bankStatement_pdf;
	}
	public String getSalarySlip_pdf() {
		return salarySlip_pdf;
	}
	public void setSalarySlip_pdf(String salarySlip_pdf) {
		this.salarySlip_pdf = salarySlip_pdf;
	}
	
	
	
}
