package com.example.EMS.EmployeeService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.EMS.EmployeeEntity.BankDetails;
import com.example.EMS.EmployeeEntity.Education;
import com.example.EMS.EmployeeEntity.EmergencyContact;
import com.example.EMS.EmployeeEntity.EmployeeInvite;
import com.example.EMS.EmployeeEntity.EmployeePayroll;
import com.example.EMS.EmployeeEntity.Experience;
import com.example.EMS.EmployeeEntity.ProfessionalDetails;
import com.example.EMS.EmployeeRepository.EmployeeInviteRepository;

import jakarta.transaction.Transactional;

@Service
public class EmployeeInviteService {
	
	private final EmployeeInviteRepository empInviteRepo;
	
	
     
	public EmployeeInviteService(EmployeeInviteRepository empInviteRepo) {
		this.empInviteRepo = empInviteRepo;
	}
	
	
	public String saveFile(MultipartFile file, String folder) throws Exception{
		String upload = System.getProperty("user.dir") + "/"+ folder + "/";
		File dir = new File(upload);
		
		if(!dir.exists()) dir.mkdirs();
		
		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
		File destination = new File(upload + fileName);
		file.transferTo(destination);
		return folder + "/" +fileName;
		
	}



	public ResponseEntity<?> saveEmployee(@RequestPart("empInvite") EmployeeInvite empInvite, 
			@RequestPart(value= "file", required=false) MultipartFile file,
			@RequestPart(value= "passbook", required=false) MultipartFile passbook,
			@RequestPart(value= "education", required= false) MultipartFile education,
			@RequestPart(value="resume", required= false) MultipartFile resume,
			@RequestPart(value="offerLetter", required= false) MultipartFile offerLetter,
			@RequestPart(value="experienceLetter", required= false) List<MultipartFile> experienceLetter){
		
		
		if(empInvite.getEmail()== null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please enter email address...");
		}
		
		Optional<EmployeeInvite> existingUser =
		        empInviteRepo.findByEmail(empInvite.getEmail());

		


		if(existingUser.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already submitted response");
		    

		} else {

			if(file != null && !file.isEmpty()) {
				try {
					String fileName = saveFile(file, "onBoardingProfiles");
					empInvite.setImgFile(fileName);

				}
				catch(Exception e) {
					return ResponseEntity.status(500).body("Image upload failed "+e);
				}
			}
			
			if(passbook != null && !passbook.isEmpty()) {
				try {
					
					String fileName = saveFile(passbook, "onBoardingProfilesPdf");
					if(empInvite.getBankDetails() == null) {
						empInvite.setBankDetails(new BankDetails());
					}
					
					empInvite.getBankDetails().setPassbook_pdf(fileName);
					
					
					
				}
				catch(Exception e) {
					return ResponseEntity.status(500).body("Passbook Pdf upload failed "+e);
				}
			}
			
			if(education != null && !education.isEmpty()) {
				try {
					
					String fileName = saveFile(education, "onBoardingProfilesPdf");
					if(empInvite.getEducation() == null) {
						empInvite.setEducation(new Education());
					}
					
					empInvite.getEducation().setEducation_pdf(fileName);
					
				}
				catch(Exception e) {
					return ResponseEntity.status(500).body("Educational Pdf upload failed "+e);
				}
			}
			
			
			if(resume != null && !resume.isEmpty()) {
				try {
					String fileName = saveFile(resume, "onBoardingProfilesPdf");
					if(empInvite.getProfessional_details() == null) {
						empInvite.setProfessional_details(new ProfessionalDetails());
					}
					
					empInvite.getProfessional_details().setResume(fileName);
				}
				catch(Exception e) {
					return ResponseEntity.status(500).body("Resume Pdf upload failed "+ e);
				}
			}
			
			if(offerLetter != null && !offerLetter.isEmpty()) {
				try {
					String fileName = saveFile(offerLetter, "onBoardingProfilesPdf");
					if(empInvite.getProfessional_details() == null) {
						empInvite.setProfessional_details(new ProfessionalDetails());
					}
					
					empInvite.getProfessional_details().setOffer_letter(fileName);
				}
				catch(Exception e) {
					return ResponseEntity.status(500).body("Offer Letter Pdf upload failed: "+ e);
				}
			}
			
			if (experienceLetter != null && !experienceLetter.isEmpty()) {
			    try {
			        if (empInvite.getExperience() == null) {
			        	empInvite.setExperience(new ArrayList<>());
			        }

			        for (int i = 0; i < experienceLetter.size(); i++) {
			            MultipartFile files = experienceLetter.get(i);
			            String fileName = saveFile(files, "onBoardingProfilesPdf");
			            Experience exp;
			            if (empInvite.getExperience().size() > i) {
			                exp = empInvite.getExperience().get(i);
			            } else {
			                exp = new Experience();
			                empInvite.getExperience().add(exp);
			            }

			            exp.setExp_letter(fileName);
			        }

			    } catch (Exception e) {
			        return ResponseEntity.status(500).body("Experience upload failed "+e);
			    }
			}
			
			
		}
		
		EmployeeInvite employeeInvite = empInviteRepo.save(empInvite);
		return ResponseEntity.ok(employeeInvite);
		
		
	}
	
	
	public ResponseEntity<?> getAllForm(){
		List<EmployeeInvite> lst =  empInviteRepo.findAll();
		if(lst.size() == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invite details not found");
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(lst);
	}
	
	public ResponseEntity<?> getFormById(Long id){
		Optional<EmployeeInvite> empInvite = empInviteRepo.findById(id);
		if(empInvite.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data with id: "+id+" not found");
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(empInvite);
	}
	
	@Transactional
	public ResponseEntity<?> deleteFormById(Long id){
		Optional<EmployeeInvite> empInvite = empInviteRepo.findById(id);
		if(empInvite.isPresent()) {
			empInviteRepo.deleteById(id);
			return ResponseEntity.status(HttpStatus.OK).body("Data with id: "+id+" deleted");
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data with id: "+id+" not found");
	}
	
	public ResponseEntity<?> updateFormById(Long id,EmployeeInvite empInvite, MultipartFile file, MultipartFile passbook,MultipartFile education, MultipartFile resume, MultipartFile offerLetter, List<MultipartFile> experienceLetter) throws Exception{
		Optional<EmployeeInvite> employeeInvite = empInviteRepo.findById(id);
		
		if(employeeInvite.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data with id: "+id+" not found");
		}
		
		   EmployeeInvite existing = employeeInvite.get();
		    
		    if(empInvite != null) {
		    	 if (empInvite.getFirst_name() != null) existing.setFirst_name(empInvite.getFirst_name());
		 	    if (empInvite.getLast_name() != null) existing.setLast_name(empInvite.getLast_name());
		 	    if (empInvite.getEmail() != null) existing.setEmail(empInvite.getEmail());
		 	    if (empInvite.getPhone_number() != null) existing.setPhone_number(empInvite.getPhone_number());
		 	    if (empInvite.getDate_of_birth() != null) existing.setDate_of_birth(empInvite.getDate_of_birth());
		 	    if (empInvite.getMarital_status() != null) existing.setMarital_status(empInvite.getMarital_status());
		 	    if (empInvite.getGender() != null) existing.setGender(empInvite.getGender());
		 	    if (empInvite.getBlood_group() != null) existing.setBlood_group(empInvite.getBlood_group());
		 	    if (empInvite.getState() != null) existing.setState(empInvite.getState());
		 	    if (empInvite.getPincode() != null) existing.setPincode(empInvite.getPincode());
		 	    if (empInvite.getAadhar_number() != null) existing.setAadhar_number(empInvite.getAadhar_number());
		 	    if (empInvite.getPan_number() != null) existing.setPan_number(empInvite.getPan_number());
		 	    if (empInvite.getAddress() != null) existing.setAddress(empInvite.getAddress());
		 	    

		 	    if (empInvite.getBankDetails() != null) {
		 	        BankDetails newBank = empInvite.getBankDetails();
		 	        BankDetails existingBank = existing.getBankDetails() != null 
		 	                ? existing.getBankDetails() : new BankDetails();

		 	        if (newBank.getBankName() != null) existingBank.setBankName(newBank.getBankName());
		 	        if (newBank.getAccountHolderName() != null) existingBank.setAccountHolderName(newBank.getAccountHolderName());
		 	        if (newBank.getAccountNumber() != null) existingBank.setAccountNumber(newBank.getAccountNumber());
		 	        if (newBank.getConfirmAccountNumber() != null) existingBank.setConfirmAccountNumber(newBank.getConfirmAccountNumber());
		 	        if (newBank.getBranchName() != null) existingBank.setBranchName(newBank.getBranchName());
		 	        if (newBank.getIfsc_Number() != null) existingBank.setIfsc_Number(newBank.getIfsc_Number());


		 	        existing.setBankDetails(existingBank);
		 	    }

		 	    
		 	    if (empInvite.getEmpPayroll() != null) {
		 	        EmployeePayroll newPayroll = empInvite.getEmpPayroll();
		 	        EmployeePayroll existingPayroll = existing.getEmpPayroll() != null 
		 	                ? existing.getEmpPayroll() : new EmployeePayroll();

		 	        if (newPayroll.getBasicPay() != 0) existingPayroll.setBasicPay(newPayroll.getBasicPay());
		 	        if (newPayroll.getHRA() != 0) existingPayroll.setHRA(newPayroll.getHRA());
		 	        if (newPayroll.getSpecialAllowance() != 0) existingPayroll.setSpecialAllowance(newPayroll.getSpecialAllowance());
		 	        if (newPayroll.getLTA() != 0) existingPayroll.setLTA(newPayroll.getLTA());
		 	        if (newPayroll.getPF() != 0) existingPayroll.setPF(newPayroll.getPF());
		 	        if (newPayroll.getMedicalAllowance() != 0) existingPayroll.setMedicalAllowance(newPayroll.getMedicalAllowance());
		 	        if (newPayroll.getBonus() != 0) existingPayroll.setBonus(newPayroll.getBonus());
		 	        if (newPayroll.getAnnualCTC() != 0) existingPayroll.setAnnualCTC(newPayroll.getAnnualCTC());

		 	        existing.setEmpPayroll(existingPayroll);
		 	    }

		 	    // Emergency Contact
		 	    if (empInvite.getEmergency_contact() != null) {
		 	        EmergencyContact newEC = empInvite.getEmergency_contact();
		 	        EmergencyContact existingEC = existing.getEmergency_contact() != null 
		 	                ? existing.getEmergency_contact() : new EmergencyContact();

		 	        if (newEC.getName() != null) existingEC.setName(newEC.getName());
		 	        if (newEC.getRelation() != null) existingEC.setRelation(newEC.getRelation());
		 	        if (newEC.getPhone() != null) existingEC.setPhone(newEC.getPhone());

		 	        existing.setEmergency_contact(existingEC);
		 	    }

		 	    // Education
		 	    if (empInvite.getEducation() != null) {
		 	        Education newEdu = empInvite.getEducation();
		 	        Education existingEdu = existing.getEducation() != null 
		 	                ? existing.getEducation() : new Education();

		 	        if (newEdu.getEducationLevel() != null) existingEdu.setEducationLevel(newEdu.getEducationLevel());
		 	        if (newEdu.getEducationalBoard() != null) existingEdu.setEducationalBoard(newEdu.getEducationalBoard());
		 	        if (newEdu.getSchoolName() != null) existingEdu.setSchoolName(newEdu.getSchoolName());
		 	        if (newEdu.getPlace() != null) existingEdu.setPlace(newEdu.getPlace());
		 	        if (newEdu.getEducationalGroup() != null) existingEdu.setEducationalGroup(newEdu.getEducationalGroup());
		 	        if (newEdu.getSchool_from() != null) existingEdu.setSchool_from(newEdu.getSchool_from());
		 	        if (newEdu.getSchool_to() != null) existingEdu.setSchool_to(newEdu.getSchool_to());
		 	        if (newEdu.getSchool_percentage() != 0) existingEdu.setSchool_percentage(newEdu.getSchool_percentage());
		 	        if (newEdu.getHigherEducation() != null && !newEdu.getHigherEducation().isEmpty())
		 	            existingEdu.setHigherEducation(newEdu.getHigherEducation());

		 	        existing.setEducation(existingEdu);
		 	    }

		 	    
		 	    if (empInvite.getProfessional_details() != null) {
		 	        ProfessionalDetails newPD = empInvite.getProfessional_details();
		 	        ProfessionalDetails existingPD = existing.getProfessional_details() != null 
		 	                ? existing.getProfessional_details() : new ProfessionalDetails();

		 	        if (newPD.getProfessional_designation() != null) existingPD.setProfessional_designation(newPD.getProfessional_designation());
		 	        if (newPD.getProfessional_department() != null) existingPD.setProfessional_department(newPD.getProfessional_department());
		 	        if (newPD.getEmp_type() != null) existingPD.setEmp_type(newPD.getEmp_type());
		 	        if (newPD.getLocation() != null) existingPD.setLocation(newPD.getLocation());
		 	        if (newPD.getEmp_status() != null) existingPD.setEmp_status(newPD.getEmp_status());
		 	        if (newPD.getDoj() != null) existingPD.setDoj(newPD.getDoj());
		 	        if (newPD.getProbation_period() != null) existingPD.setProbation_period(newPD.getProbation_period());
		 	        if (newPD.getConfirmation_date() != null) existingPD.setConfirmation_date(newPD.getConfirmation_date());
		 	        if (newPD.getSkills() != null) existingPD.setSkills(newPD.getSkills());
		 	        if (newPD.getExp_level() != null) existingPD.setExp_level(newPD.getExp_level());
		 	       
		 	        existing.setProfessional_details(existingPD);
		 	    }

		 	    
		 	    if (empInvite.getExperience() != null && !empInvite.getExperience().isEmpty()) {
		 	        existing.setExperience(empInvite.getExperience());
		 	    }
			    
		    	
		    }


		    if (file != null && !file.isEmpty()) {
		        String fileName = saveFile(file, "onBoardingProfiles");
		        existing.setImgFile(fileName);
		    }

		    if (resume != null && !resume.isEmpty()) {
		        String fileName = saveFile(resume, "onBoardingProfilesPdf");
		        existing.getProfessional_details().setResume(fileName);
		    }

		    if (offerLetter != null && !offerLetter.isEmpty()) {
		        String fileName = saveFile(offerLetter, "onBoardingProfilesPdf");
		        existing.getProfessional_details().setOffer_letter(fileName);
		    }

		    if (passbook != null && !passbook.isEmpty()) {
		        String fileName = saveFile(passbook, "onBoardingProfilesPdf");
		        existing.getBankDetails().setPassbook_pdf(fileName);
		    }

		    if (education != null && !education.isEmpty()) {
		        String fileName = saveFile(education, "onBoardingProfilesPdf");
		        existing.getEducation().setEducation_pdf(fileName);
		    }

		    if (experienceLetter != null && !experienceLetter.isEmpty()) {
		        for (int i = 0; i < experienceLetter.size(); i++) {
		            MultipartFile file1 = experienceLetter.get(i);
		            String fileName = saveFile(file1, "onBoardingProfilesPdf");

		            if (existing.getExperience().size() > i) {
		                existing.getExperience().get(i).setExp_letter(fileName);
		            }
		        }
		    }

		    empInviteRepo.save(existing);

		    return ResponseEntity.ok(existing);
	}
	
	
}
