package com.example.EMS.EmployeeDTO;

import java.util.Date;
import java.util.List;

import com.example.EMS.EmployeeEntity.BankDetails;
import com.example.EMS.EmployeeEntity.Education;
import com.example.EMS.EmployeeEntity.EmergencyContact;
import com.example.EMS.EmployeeEntity.EmployeePayroll;
import com.example.EMS.EmployeeEntity.Experience;
import com.example.EMS.EmployeeEntity.ProfessionalDetails;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeInviteDTO {
	
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
    private String aadhar_number;
    private String pan_number;
    private String address;
    private String imgFile;


    private String onboardingStatus;
    
    private BankDetails bankDetails;
	
	
	private EmployeePayroll empPayroll;
	
	
    private EmergencyContact emergency_contact;
	
	
	private Education education;
	
	
	private ProfessionalDetails professional_details;
	

	private List<Experience> experience;


	public String getEmployeeId() {
		return employeeId;
	}


	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
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


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getImgFile() {
		return imgFile;
	}


	public void setImgFile(String imgFile) {
		this.imgFile = imgFile;
	}


	public String getOnboardingStatus() {
		return onboardingStatus;
	}


	public void setOnboardingStatus(String onboardingStatus) {
		this.onboardingStatus = onboardingStatus;
	}


	public BankDetails getBankDetails() {
		return bankDetails;
	}


	public void setBankDetails(BankDetails bankDetails) {
		this.bankDetails = bankDetails;
	}


	public EmployeePayroll getEmpPayroll() {
		return empPayroll;
	}


	public void setEmpPayroll(EmployeePayroll empPayroll) {
		this.empPayroll = empPayroll;
	}


	public EmergencyContact getEmergency_contact() {
		return emergency_contact;
	}


	public void setEmergency_contact(EmergencyContact emergency_contact) {
		this.emergency_contact = emergency_contact;
	}


	public Education getEducation() {
		return education;
	}


	public void setEducation(Education education) {
		this.education = education;
	}


	public ProfessionalDetails getProfessional_details() {
		return professional_details;
	}


	public void setProfessional_details(ProfessionalDetails professional_details) {
		this.professional_details = professional_details;
	}


	public List<Experience> getExperience() {
		return experience;
	}


	public void setExperience(List<Experience> experience) {
		this.experience = experience;
	}
	
	
	
	
	

}
