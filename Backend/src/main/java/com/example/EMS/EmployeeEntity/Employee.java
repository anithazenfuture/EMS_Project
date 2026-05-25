package com.example.EMS.EmployeeEntity;

import java.util.Date;
import java.util.List;

import com.example.EMS.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String employeeId;
	private String first_name;
	private String last_name;
	private String email;
	private Long phone_number;
	private Date date_of_birth;
	private String marital_status;
    private String gender;
    private String blood_group;
    private String state;
    private String pincode;
    private String aadhar_pdf;
    private String pan_pdf;
    private String address;
    private String imgFile;
    private String aadhar_number;
    private String pan_number;
    @Enumerated(EnumType.STRING)
    private Role role;
    
	@OneToOne(mappedBy="employee", cascade= CascadeType.ALL, orphanRemoval = true)
    private BankDetails bankDetails;
	
	@OneToOne(mappedBy="employee", cascade= CascadeType.ALL, orphanRemoval = true)
	private EmployeePayroll empPayroll;
	
	@OneToOne(mappedBy="employee", cascade= CascadeType.ALL, orphanRemoval = true)
    private EmergencyContact emergency_contact;
	
	@OneToOne(mappedBy="employee", cascade= CascadeType.ALL, orphanRemoval = true)
	private Education education;
	
	@OneToOne(mappedBy="employee", cascade= CascadeType.ALL, orphanRemoval = true)
	private ProfessionalDetails professional_details;
	
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL ,orphanRemoval = true)
	private List<Experience> experience;
	
	
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Attendance> attendance;
	
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<LeaveRequest> leaveRequest;
	
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<LeaveBalance> leaveBalance;
	
	
	
	
	public EmployeePayroll getEmpPayroll() {
		return empPayroll;
	}
	public void setEmpPayroll(EmployeePayroll empPayroll) {
		this.empPayroll = empPayroll;
	}
	public List<Experience> getExperience() {
		return experience;
	}
	public void setExperience(List<Experience> experience) {
		this.experience = experience;
	}
	
	public ProfessionalDetails getProfessional_details() {
		return professional_details;
	}


	public void setProfessional_details(ProfessionalDetails professional_details) {
		this.professional_details = professional_details;
	}


	public Education getEducation() {
		return education;
	}


	public void setEducation(Education education) {
		this.education = education;
	}



	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}




	public String getFirst_name() {
		return first_name;
	}


	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}


	public String getLast_name() {
		return last_name;
	}


	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public Long getPhone_number() {
		return phone_number;
	}


	public void setPhone_number(Long phone_number) {
		this.phone_number = phone_number;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getBlood_group() {
		return blood_group;
	}


	public void setBlood_group(String blood_group) {
		this.blood_group = blood_group;
	}

	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public Date getDate_of_birth() {
		return date_of_birth;
	}


	public void setDate_of_birth(Date date_of_birth) {
		this.date_of_birth = date_of_birth;
	}


	public String getMarital_status() {
		return marital_status;
	}


	public void setMarital_status(String marital_status) {
		this.marital_status = marital_status;
	}


	public String getEmployeeId() {
		return employeeId;
	}


	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}


	public EmergencyContact getEmergency_contact() {
		return emergency_contact;
	}


	public void setEmergency_contact(EmergencyContact emergency_contact) {
		this.emergency_contact = emergency_contact;
	}



	public String getImgFile() {
		return imgFile;
	}


	public void setImgFile(String imgFile) {
		this.imgFile = imgFile;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getPincode() {
		return pincode;
	}


	public void setPincode(String pincode) {
		this.pincode = pincode;
	}


	public BankDetails getBankDetails() {
		return bankDetails;
	}


	public void setBankDetails(BankDetails bankDetails) {
		this.bankDetails = bankDetails;
	}
	public List<Attendance> getAttendance() {
		return attendance;
	}
	public void setAttendance(List<Attendance> attendance) {
		this.attendance = attendance;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public List<LeaveRequest> getLeaveRequest() {
		return leaveRequest;
	}
	public void setLeaveRequest(List<LeaveRequest> leaveRequest) {
		this.leaveRequest = leaveRequest;
	}
	public List<LeaveBalance> getLeaveBalance() {
		return leaveBalance;
	}
	public void setLeaveBalance(List<LeaveBalance> leaveBalance) {
		this.leaveBalance = leaveBalance;
	}
	public String getAadhar_pdf() {
		return aadhar_pdf;
	}
	public void setAadhar_pdf(String aadhar_pdf) {
		this.aadhar_pdf = aadhar_pdf;
	}
	public String getPan_pdf() {
		return pan_pdf;
	}
	public void setPan_pdf(String pan_pdf) {
		this.pan_pdf = pan_pdf;
	}
	public String getAadhar_number() {
		return aadhar_number;
	}
	public void setAadhar_number(String aadhar_number) {
		this.aadhar_number = aadhar_number;
	}
	public String getPan_number() {
		return pan_number;
	}
	public void setPan_number(String pan_number) {
		this.pan_number = pan_number;
	}
	
	
	
	
	
	
	
	
}
