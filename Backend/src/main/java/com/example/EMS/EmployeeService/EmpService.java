package com.example.EMS.EmployeeService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.EMS.EmployeeEntity.BankDetails;
import com.example.EMS.EmployeeEntity.Education;
import com.example.EMS.EmployeeEntity.EmergencyContact;
import com.example.EMS.EmployeeEntity.Employee;
import com.example.EMS.EmployeeEntity.EmployeePayroll;
import com.example.EMS.EmployeeEntity.Experience;
import com.example.EMS.EmployeeEntity.ProfessionalDetails;
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
		
		if(emp.getEmail() == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Please enter employee mail id"); 
		}
		
		Optional<Employee> emailuser = empRepo.findByEmail(emp.getEmail());
		
		if(emailuser.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Already exists");
		}
		
		Employee employee = empRepo.save(emp);
		return ResponseEntity.ok(employee);
		
	}
	
	public double calculateAnnualCTC(double basicPay,double HRA, double specialAllowance,double LTA,double PF,double medicalAllowance, double bonus) {
		 double monthlyCTC = basicPay + HRA + specialAllowance + LTA + PF + medicalAllowance + bonus;
		  return monthlyCTC * 12;
		
	}
	
	
	
	public ResponseEntity<?> createEmpIMG(@RequestPart("employee") Employee emp, 
			@RequestPart(value= "file", required=false) MultipartFile file,
			@RequestPart(value= "passbook", required=false) MultipartFile passbook,
			@RequestPart(value= "education", required= false) MultipartFile education,
			@RequestPart(value="resume", required= false) MultipartFile resume,
			@RequestPart(value="offerLetter", required= false) MultipartFile offerLetter,
			@RequestPart(value="experienceLetter", required= false) List<MultipartFile> experienceLetter){
		
		Optional<Employee> empId = empRepo.findByEmployeeId(emp.getEmployeeId());
		if(empId.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Already exists with Employee Id: "+ empId.get().getEmployeeId());
		}
		
		Optional<Employee> emailuser = empRepo.findByEmail(emp.getEmail());
		
		if(emailuser.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Already exists with Email: "+ emailuser.get().getEmail());
		}
		
		Long maxId = empRepo.findMaxId();
		long nextId = (maxId == null) ? 1 : maxId + 1;

	    emp.setEmployeeId(String.format("ZF%03d", nextId));
		
		
		if(file != null && !file.isEmpty()) {
			try {
				String fileName = saveFile(file, "uploads");
				emp.setImgFile(fileName);

			}
			catch(Exception e) {
				return ResponseEntity.status(500).body("Image upload failed "+e);
			}
		}
		
		if(passbook != null && !passbook.isEmpty()) {
			try {
				
				String fileName = saveFile(passbook, "uploadsPdf");
				if(emp.getBankDetails() == null) {
					emp.setBankDetails(new BankDetails());
				}
				
				emp.getBankDetails().setPassbook_pdf(fileName);
				
				
				
			}
			catch(Exception e) {
				return ResponseEntity.status(500).body("Passbook Pdf upload failed "+e);
			}
		}
		
		if(education != null && !education.isEmpty()) {
			try {
				
				String fileName = saveFile(education, "uploadsPdf");
				if(emp.getEducation() == null) {
					emp.setEducation(new Education());
				}
				
				emp.getEducation().setEducation_pdf(fileName);
				
			}
			catch(Exception e) {
				return ResponseEntity.status(500).body("Educational Pdf upload failed "+e);
			}
		}
		
		
		if(resume != null && !resume.isEmpty()) {
			try {
				String fileName = saveFile(resume, "uploadsPdf");
				if(emp.getProfessional_details() == null) {
					emp.setProfessional_details(new ProfessionalDetails());
				}
				
				emp.getProfessional_details().setResume(fileName);
			}
			catch(Exception e) {
				return ResponseEntity.status(500).body("Resume Pdf upload failed "+ e);
			}
		}
		
		if(offerLetter != null && !offerLetter.isEmpty()) {
			try {
				String fileName = saveFile(offerLetter, "uploadsPdf");
				if(emp.getProfessional_details() == null) {
					emp.setProfessional_details(new ProfessionalDetails());
				}
				
				emp.getProfessional_details().setOffer_letter(fileName);
			}
			catch(Exception e) {
				return ResponseEntity.status(500).body("Offer Letter Pdf upload failed: "+ e);
			}
		}
		
		if (experienceLetter != null && !experienceLetter.isEmpty()) {
		    try {
		        if (emp.getExperience() == null) {
		            emp.setExperience(new ArrayList<>());
		        }

		        for (int i = 0; i < experienceLetter.size(); i++) {
		            MultipartFile files = experienceLetter.get(i);
		            String fileName = saveFile(files, "uploadsPdf");
		            Experience exp;
		            if (emp.getExperience().size() > i) {
		                exp = emp.getExperience().get(i);
		            } else {
		                exp = new Experience();
		                emp.getExperience().add(exp);
		            }

		            exp.setExp_letter(fileName);
		        }

		    } catch (Exception e) {
		        return ResponseEntity.status(500).body("Experience upload failed "+e);
		    }
		}
		
		double basic = emp.getEmpPayroll().getBasicPay();
		double hra = emp.getEmpPayroll().getHRA();
		double specialAllowance = emp.getEmpPayroll().getSpecialAllowance();
		double lta = emp.getEmpPayroll().getLTA();
		double pf = emp.getEmpPayroll().getPF();
		double medical = emp.getEmpPayroll().getMedicalAllowance();
		double bonus = emp.getEmpPayroll().getBonus();
		double ctc = calculateAnnualCTC(basic,hra,specialAllowance,lta,pf,medical,bonus);
		emp.getEmpPayroll().setAnnualCTC(ctc);
		
		emp.getBankDetails().setEmployee(emp);
		emp.getEmpPayroll().setEmployee(emp);
		emp.getEmergency_contact().setEmployee(emp);
		emp.getEducation().setEmployee(emp);
		emp.getProfessional_details().setEmployee(emp);
		if (emp.getExperience() != null) {
		    for (Experience exp : emp.getExperience()) {
		        exp.setEmployee(emp); 
		    }
		}
		

		Employee employee = empRepo.save(emp);
		return ResponseEntity.ok(employee);
		
	}
	
	public ResponseEntity<?> getAllEmployeeDetails(){
		List<Employee> list = empRepo.findAll();
		return ResponseEntity.ok(list);
	}
	
	public ResponseEntity<?> getPayrollById(String empId){
		 Optional<Employee> empOptional = empRepo.findByEmployeeId(empId);

		 if (empOptional.isEmpty()) {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found with ID: " + empId);
		    }

		  Employee emp = empOptional.get();

		 if (emp.getEmpPayroll() == null) {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payroll details not found for Employee ID: " + empId);
		  }

		    return ResponseEntity.ok(emp.getEmpPayroll());
	}
	
	public ResponseEntity<?> getEmployeeById(String id){
		
		Optional<Employee> emp = empRepo.findByEmployeeId(id);
		if(emp.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(emp);
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee with id: "+id+" not found");
	}
		
	@Transactional
	public ResponseEntity<?> deleteEmployeeById(String id){
		
		Optional<Employee> emp = empRepo.findByEmployeeId(id);
		if(emp.isPresent()) {
			empRepo.deleteByEmployeeId(id);
			return ResponseEntity.status(HttpStatus.OK).body("Employee deleted with id: "+id);
		}
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee with id: "+id+" not found");
	}
		
	public ResponseEntity<?> updateEmployee(String empId, Employee emp) {
	    Optional<Employee> existingEmp = empRepo.findByEmployeeId(empId);
	    
	    if (!existingEmp.isPresent()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("Employee not found with id: " + empId);
	    }

	    Employee existing = existingEmp.get();

	    
	    if (emp.getFirst_name() != null) existing.setFirst_name(emp.getFirst_name());
	    if (emp.getLast_name() != null) existing.setLast_name(emp.getLast_name());
	    if (emp.getEmail() != null) existing.setEmail(emp.getEmail());
	    if (emp.getPhone_number() != null) existing.setPhone_number(emp.getPhone_number());
	    if (emp.getDate_of_birth() != null) existing.setDate_of_birth(emp.getDate_of_birth());
	    if (emp.getMarital_status() != null) existing.setMarital_status(emp.getMarital_status());
	    if (emp.getGender() != null) existing.setGender(emp.getGender());
	    if (emp.getBlood_group() != null) existing.setBlood_group(emp.getBlood_group());
	    if (emp.getState() != null) existing.setState(emp.getState());
	    if (emp.getPincode() != null) existing.setPincode(emp.getPincode());
	    if (emp.getAadhar_number() != null) existing.setAadhar_number(emp.getAadhar_number());
	    if (emp.getPan_number() != null) existing.setPan_number(emp.getPan_number());
	    if (emp.getAddress() != null) existing.setAddress(emp.getAddress());
	    if (emp.getImgFile() != null) existing.setImgFile(emp.getImgFile());

	    if (emp.getBankDetails() != null) {
	        BankDetails newBank = emp.getBankDetails();
	        BankDetails existingBank = existing.getBankDetails() != null 
	                ? existing.getBankDetails() : new BankDetails();

	        if (newBank.getBankName() != null) existingBank.setBankName(newBank.getBankName());
	        if (newBank.getAccountHolderName() != null) existingBank.setAccountHolderName(newBank.getAccountHolderName());
	        if (newBank.getAccountNumber() != null) existingBank.setAccountNumber(newBank.getAccountNumber());
	        if (newBank.getConfirmAccountNumber() != null) existingBank.setConfirmAccountNumber(newBank.getConfirmAccountNumber());
	        if (newBank.getBranchName() != null) existingBank.setBranchName(newBank.getBranchName());
	        if (newBank.getIfsc_Number() != null) existingBank.setIfsc_Number(newBank.getIfsc_Number());
	        if (newBank.getPassbook_pdf() != null) existingBank.setPassbook_pdf(newBank.getPassbook_pdf());

	        existing.setBankDetails(existingBank);
	    }

	    
	    if (emp.getEmpPayroll() != null) {
	        EmployeePayroll newPayroll = emp.getEmpPayroll();
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
	    if (emp.getEmergency_contact() != null) {
	        EmergencyContact newEC = emp.getEmergency_contact();
	        EmergencyContact existingEC = existing.getEmergency_contact() != null 
	                ? existing.getEmergency_contact() : new EmergencyContact();

	        if (newEC.getName() != null) existingEC.setName(newEC.getName());
	        if (newEC.getRelation() != null) existingEC.setRelation(newEC.getRelation());
	        if (newEC.getPhone() != null) existingEC.setPhone(newEC.getPhone());

	        existing.setEmergency_contact(existingEC);
	    }

	    // Education
	    if (emp.getEducation() != null) {
	        Education newEdu = emp.getEducation();
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
	        if (newEdu.getEducation_pdf() != null) existingEdu.setEducation_pdf(newEdu.getEducation_pdf());
	        if (newEdu.getHigherEducation() != null && !newEdu.getHigherEducation().isEmpty())
	            existingEdu.setHigherEducation(newEdu.getHigherEducation());

	        existing.setEducation(existingEdu);
	    }

	    
	    if (emp.getProfessional_details() != null) {
	        ProfessionalDetails newPD = emp.getProfessional_details();
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
	        if (newPD.getResume() != null) existingPD.setResume(newPD.getResume());
	        if (newPD.getOffer_letter() != null) existingPD.setOffer_letter(newPD.getOffer_letter());

	        existing.setProfessional_details(existingPD);
	    }

	    
	    if (emp.getExperience() != null && !emp.getExperience().isEmpty()) {
	        existing.setExperience(emp.getExperience());
	    }

	    Employee saved = empRepo.save(existing);
	    return ResponseEntity.status(HttpStatus.OK).body(saved);
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
	
	public ResponseEntity<?> updateEmployeeImage(String empId, MultipartFile image) throws Exception{
	    Optional<Employee> empOpt = empRepo.findByEmployeeId(empId);
	    if (!empOpt.isPresent())
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");

	    try {
	        Employee existing = empOpt.get();
	        String path = saveFile(image, "uploads");
	        existing.setImgFile(path);
	        empRepo.save(existing);
	        return ResponseEntity.ok("Image updated successfully for id: "+empId);
	    } catch (IOException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed: " + e.getMessage());
	    }
	}
	
	public ResponseEntity<?> updateEmployeeFile(String empId, MultipartFile file, String fileType) throws Exception {
	    Optional<Employee> empOpt = empRepo.findByEmployeeId(empId);
	    if (!empOpt.isPresent())
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");

	    try {
	        Employee existing = empOpt.get();
	        String path = saveFile(file, "uploadsPdf");

	        switch (fileType) {
	            case "resume":
	                if (existing.getProfessional_details() == null)
	                    existing.setProfessional_details(new ProfessionalDetails());
	                existing.getProfessional_details().setResume(path);
	                break;
	            case "offerLetter":
	                if (existing.getProfessional_details() == null)
	                    existing.setProfessional_details(new ProfessionalDetails());
	                existing.getProfessional_details().setOffer_letter(path);
	                break;
	            case "passbookPdf":
	                if (existing.getBankDetails() == null)
	                    existing.setBankDetails(new BankDetails());
	                existing.getBankDetails().setPassbook_pdf(path);
	                break;
	            case "educationPdf":
	                if (existing.getEducation() == null)
	                    existing.setEducation(new Education());
	                existing.getEducation().setEducation_pdf(path);
	                break;
	            case "expLetter":
	                if (existing.getExperience() != null && !existing.getExperience().isEmpty())
	                    existing.getExperience().get(0).setExp_letter(path); // update latest
	                break;
	            default:
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown file type");
	        }

	        empRepo.save(existing);
	        return ResponseEntity.ok(fileType + " updated successfully for id: "+empId);

	    } catch (IOException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
	    }
	}
	
	public ResponseEntity<?> updateEmployeeAll(
	        String empId,
	        Employee emp,
	        MultipartFile image,
	        MultipartFile resume,
	        MultipartFile offerLetter,
	        MultipartFile passbookPdf,
	        MultipartFile educationPdf,
	        List<MultipartFile> expLetter) throws Exception {

	    Optional<Employee> existingOpt = empRepo.findByEmployeeId(empId);

	    if (existingOpt.isEmpty()) {
	        return ResponseEntity.status(404).body("Employee not found");
	    }

	    Employee existing = existingOpt.get();
	    
	    if(emp != null) {
	    	 if (emp.getFirst_name() != null) existing.setFirst_name(emp.getFirst_name());
	 	    if (emp.getLast_name() != null) existing.setLast_name(emp.getLast_name());
	 	    if (emp.getEmail() != null) existing.setEmail(emp.getEmail());
	 	    if (emp.getPhone_number() != null) existing.setPhone_number(emp.getPhone_number());
	 	    if (emp.getDate_of_birth() != null) existing.setDate_of_birth(emp.getDate_of_birth());
	 	    if (emp.getMarital_status() != null) existing.setMarital_status(emp.getMarital_status());
	 	    if (emp.getGender() != null) existing.setGender(emp.getGender());
	 	    if (emp.getBlood_group() != null) existing.setBlood_group(emp.getBlood_group());
	 	    if (emp.getState() != null) existing.setState(emp.getState());
	 	    if (emp.getPincode() != null) existing.setPincode(emp.getPincode());
	 	    if (emp.getAadhar_number() != null) existing.setAadhar_number(emp.getAadhar_number());
	 	    if (emp.getPan_number() != null) existing.setPan_number(emp.getPan_number());
	 	    if (emp.getAddress() != null) existing.setAddress(emp.getAddress());
	 	    

	 	    if (emp.getBankDetails() != null) {
	 	        BankDetails newBank = emp.getBankDetails();
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

	 	    
	 	    if (emp.getEmpPayroll() != null) {
	 	        EmployeePayroll newPayroll = emp.getEmpPayroll();
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
	 	    if (emp.getEmergency_contact() != null) {
	 	        EmergencyContact newEC = emp.getEmergency_contact();
	 	        EmergencyContact existingEC = existing.getEmergency_contact() != null 
	 	                ? existing.getEmergency_contact() : new EmergencyContact();

	 	        if (newEC.getName() != null) existingEC.setName(newEC.getName());
	 	        if (newEC.getRelation() != null) existingEC.setRelation(newEC.getRelation());
	 	        if (newEC.getPhone() != null) existingEC.setPhone(newEC.getPhone());

	 	        existing.setEmergency_contact(existingEC);
	 	    }

	 	    // Education
	 	    if (emp.getEducation() != null) {
	 	        Education newEdu = emp.getEducation();
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

	 	    
	 	    if (emp.getProfessional_details() != null) {
	 	        ProfessionalDetails newPD = emp.getProfessional_details();
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

	 	    
	 	    if (emp.getExperience() != null && !emp.getExperience().isEmpty()) {
	 	        existing.setExperience(emp.getExperience());
	 	    }
		    
	    	
	    }


	    if (image != null && !image.isEmpty()) {
	        String fileName = saveFile(image, "uploads");
	        existing.setImgFile(fileName);
	    }

	    if (resume != null && !resume.isEmpty()) {
	        String fileName = saveFile(resume, "uploadsPdf");
	        existing.getProfessional_details().setResume(fileName);
	    }

	    if (offerLetter != null && !offerLetter.isEmpty()) {
	        String fileName = saveFile(offerLetter, "uploadsPdf");
	        existing.getProfessional_details().setOffer_letter(fileName);
	    }

	    if (passbookPdf != null && !passbookPdf.isEmpty()) {
	        String fileName = saveFile(passbookPdf, "uploadsPdf");
	        existing.getBankDetails().setPassbook_pdf(fileName);
	    }

	    if (educationPdf != null && !educationPdf.isEmpty()) {
	        String fileName = saveFile(educationPdf, "uploadsPdf");
	        existing.getEducation().setEducation_pdf(fileName);
	    }

	    if (expLetter != null && !expLetter.isEmpty()) {
	        for (int i = 0; i < expLetter.size(); i++) {
	            MultipartFile file = expLetter.get(i);
	            String fileName = saveFile(file, "uploadsPdf");

	            if (existing.getExperience().size() > i) {
	                existing.getExperience().get(i).setExp_letter(fileName);
	            }
	        }
	    }

	    empRepo.save(existing);

	    return ResponseEntity.ok(existing);
	}
	
	

}
