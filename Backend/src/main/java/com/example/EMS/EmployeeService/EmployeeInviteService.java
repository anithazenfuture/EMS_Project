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
import com.example.EMS.EmployeeEntity.Employee;
import com.example.EMS.EmployeeEntity.EmployeeInvite;
import com.example.EMS.EmployeeEntity.EmployeePayroll;
import com.example.EMS.EmployeeEntity.Experience;
import com.example.EMS.EmployeeEntity.HigherEducation;
import com.example.EMS.EmployeeEntity.LeaveBalance;
import com.example.EMS.EmployeeEntity.ProfessionalDetails;
import com.example.EMS.EmployeeRepository.EmpRepository;
import com.example.EMS.EmployeeRepository.EmployeeInviteRepository;

import jakarta.transaction.Transactional;

@Service
public class EmployeeInviteService {
	
	private final EmployeeInviteRepository empInviteRepo;
	private final EmpService empService;
	private final EmpRepository empRepo;
	
	
     
	public EmployeeInviteService(EmployeeInviteRepository empInviteRepo, EmpService empService, EmpRepository empRepo) {
		this.empInviteRepo = empInviteRepo;
		this.empService = empService;
		this.empRepo = empRepo;
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



	public ResponseEntity<?> saveEmployee(

	        @RequestPart("empInvite") EmployeeInvite empInvite,
	        @RequestPart(value = "file", required = false) MultipartFile file,

	        @RequestPart(value = "aadhar", required = false) MultipartFile aadhar,
	        @RequestPart(value = "pan_card", required = false) MultipartFile pan_card,
	        @RequestPart(value = "higherEducation", required = false)
	        List<MultipartFile> higherEducation,

	        @RequestPart(value = "bankStatement", required = false)
	        List<MultipartFile> bankStatement,

	        @RequestPart(value = "salarySlip", required = false)
	        List<MultipartFile> salarySlip,

	        @RequestPart(value = "passbook", required = false)
	        MultipartFile passbook,

	        @RequestPart(value = "education", required = false)
	        MultipartFile education,

	        @RequestPart(value = "resume", required = false)
	        MultipartFile resume,

	        @RequestPart(value = "offerLetter", required = false)
	        MultipartFile offerLetter,

	        @RequestPart(value = "prevExpLetter", required = false)
	        List<MultipartFile> prevExpLetter,
	        
	       
	        
	        @RequestPart(value = "experienceLetter", required = false)
	        List<MultipartFile> experienceLetter) {

	    try {

	        // ================= EMAIL VALIDATION =================

	        if (empInvite.getEmail() == null
	                || empInvite.getEmail().trim().isEmpty()) {

	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("Please enter email address...");
	        }

	        Optional<EmployeeInvite> existingUser =
	                empInviteRepo.findByEmail(empInvite.getEmail());

	        if (existingUser.isPresent()) {

	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("User already submitted response with email: "+empInvite.getEmail());
	        }

	        // ================= PROFILE IMAGE =================

	        if (file != null && !file.isEmpty()) {

	            String fileName =
	                    saveFile(file, "onBoardingProfiles");

	            empInvite.setImgFile(fileName);
	        }

	        // ================= AADHAR PDF =================

	        if (aadhar != null && !aadhar.isEmpty()) {

	            String fileName =
	                    saveFile(aadhar, "onBoardingProfilesPdf");

	            empInvite.setAadhar_pdf(fileName);
	        }

	        // ================= PAN PDF =================

	        if (pan_card != null && !pan_card.isEmpty()) {

	            String fileName =
	                    saveFile(pan_card, "onBoardingProfilesPdf");

	            empInvite.setPan_pdf(fileName);
	        }

	        // ================= PASSBOOK PDF =================

	        if (passbook != null && !passbook.isEmpty()) {

	            String fileName =
	                    saveFile(passbook, "onBoardingProfilesPdf");

	            if (empInvite.getBankDetails() == null) {

	                empInvite.setBankDetails(new BankDetails());
	            }

	            empInvite.getBankDetails()
	                    .setPassbook_pdf(fileName);
	        }

	        // ================= EDUCATION PDF =================

	        if (education != null && !education.isEmpty()) {

	            String fileName =
	                    saveFile(education, "onBoardingProfilesPdf");

	            if (empInvite.getEducation() == null) {

	                empInvite.setEducation(new Education());
	            }

	            empInvite.getEducation()
	                    .setEducation_pdf(fileName);
	        }

	        // ================= RESUME PDF =================

	        if (resume != null && !resume.isEmpty()) {

	            String fileName =
	                    saveFile(resume, "onBoardingProfilesPdf");

	            if (empInvite.getProfessional_details() == null) {

	                empInvite.setProfessional_details(
	                        new ProfessionalDetails());
	            }

	            empInvite.getProfessional_details()
	                    .setResume(fileName);
	        }

	        // ================= OFFER LETTER PDF =================

	        if (offerLetter != null && !offerLetter.isEmpty()) {

	            String fileName =
	                    saveFile(offerLetter, "onBoardingProfilesPdf");

	            if (empInvite.getProfessional_details() == null) {

	                empInvite.setProfessional_details(
	                        new ProfessionalDetails());
	            }

	            empInvite.getProfessional_details()
	                    .setOffer_letter(fileName);
	        }

	        // ================= EXPERIENCE LIST INIT =================

	        if (empInvite.getExperience() == null) {

	            empInvite.setExperience(new ArrayList<>());
	        }

	        // ================= EXPERIENCE LETTER =================

	        if (experienceLetter != null
	                && !experienceLetter.isEmpty()) {

	            for (int i = 0; i < experienceLetter.size(); i++) {

	                MultipartFile files =
	                        experienceLetter.get(i);

	                String fileName =
	                        saveFile(files, "onBoardingProfilesPdf");

	                Experience exp;

	                if (empInvite.getExperience().size() > i) {

	                    exp = empInvite.getExperience().get(i);

	                } else {

	                    exp = new Experience();

	                    empInvite.getExperience().add(exp);
	                }

	                exp.setExp_letter(fileName);
	            }
	        }

	        // ================= PREVIOUS EXPERIENCE LETTER =================

	        if (prevExpLetter != null
	                && !prevExpLetter.isEmpty()) {

	            for (int i = 0; i < prevExpLetter.size(); i++) {

	                MultipartFile files =
	                        prevExpLetter.get(i);

	                String fileName =
	                        saveFile(files, "onBoardingProfilesPdf");

	                Experience exp;

	                if (empInvite.getExperience().size() > i) {

	                    exp = empInvite.getExperience().get(i);

	                } else {

	                    exp = new Experience();

	                    empInvite.getExperience().add(exp);
	                }

	                exp.setOfferLetter_exp(fileName);
	            }
	        }

	        // ================= BANK STATEMENT PDF =================

	        if (bankStatement != null
	                && !bankStatement.isEmpty()) {

	            for (int i = 0; i < bankStatement.size(); i++) {

	                MultipartFile files =
	                        bankStatement.get(i);

	                String fileName =
	                        saveFile(files, "onBoardingProfilesPdf");

	                Experience exp;

	                if (empInvite.getExperience().size() > i) {

	                    exp = empInvite.getExperience().get(i);

	                } else {

	                    exp = new Experience();

	                    empInvite.getExperience().add(exp);
	                }

	                exp.setBankStatement_pdf(fileName);
	            }
	        }

	        // ================= SALARY SLIP PDF =================

	        if (salarySlip != null
	                && !salarySlip.isEmpty()) {

	            for (int i = 0; i < salarySlip.size(); i++) {

	                MultipartFile files =
	                        salarySlip.get(i);

	                String fileName =
	                        saveFile(files, "onBoardingProfilesPdf");

	                Experience exp;

	                if (empInvite.getExperience().size() > i) {

	                    exp = empInvite.getExperience().get(i);

	                } else {

	                    exp = new Experience();

	                    empInvite.getExperience().add(exp);
	                }

	                exp.setSalarySlip_pdf(fileName);
	            }
	        }

	        // ================= HIGHER EDUCATION PDF =================

	        if (higherEducation != null
	                && !higherEducation.isEmpty()) {

	            if (empInvite.getEducation() == null) {

	                empInvite.setEducation(new Education());
	            }

	            if (empInvite.getEducation()
	                    .getHigherEducation() == null) {

	                empInvite.getEducation()
	                        .setHigherEducation(new ArrayList<>());
	            }

	            List<HigherEducation> higherEduList =
	                    empInvite.getEducation()
	                            .getHigherEducation();

	            for (int i = 0; i < higherEducation.size(); i++) {

	                MultipartFile filePdf =
	                        higherEducation.get(i);

	                String fileName =
	                        saveFile(filePdf,
	                                "onBoardingProfilesPdf");

	                HigherEducation hr;

	                if (higherEduList.size() > i) {

	                    hr = higherEduList.get(i);

	                } else {

	                    hr = new HigherEducation();

	                    higherEduList.add(hr);
	                }
	              

	                hr.setHigherEducation_pdf(fileName);

	                hr.setEducation(empInvite.getEducation());
	            }
	        }

	        // ================= STATUS =================

	        empInvite.setStatus("Completed");

	        EmployeeInvite employeeInvite =
	                empInviteRepo.save(empInvite);

	        return ResponseEntity.ok(employeeInvite);

	    } catch (Exception e) {

	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error while saving employee: "
	                        + e.getMessage());
	    }
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
	
	public ResponseEntity<?> updateFormById(

        Long id,
        EmployeeInvite empInvite,

        MultipartFile file,

        MultipartFile aadhar,
        MultipartFile pan_card,

        List<MultipartFile> higherEducation,
        List<MultipartFile> bankStatement,
        List<MultipartFile> salarySlip,

        MultipartFile passbook,
        MultipartFile education,
        MultipartFile resume,
        MultipartFile offerLetter,

        List<MultipartFile> prevExpLetter,
        List<MultipartFile> experienceLetter

) throws Exception {

    Optional<EmployeeInvite> employeeInvite =
            empInviteRepo.findById(id);

    if (employeeInvite.isEmpty()) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Data with id: " + id + " not found");
    }

    EmployeeInvite existing = employeeInvite.get();

    // =====================================================
    // BASIC DETAILS
    // =====================================================

    if (empInvite != null) {

        if (empInvite.getFirst_name() != null)
            existing.setFirst_name(empInvite.getFirst_name());

        if (empInvite.getLast_name() != null)
            existing.setLast_name(empInvite.getLast_name());

        if (empInvite.getEmail() != null)
            existing.setEmail(empInvite.getEmail());

        if (empInvite.getPhone_number() != null)
            existing.setPhone_number(empInvite.getPhone_number());

        if (empInvite.getDate_of_birth() != null)
            existing.setDate_of_birth(empInvite.getDate_of_birth());

        if (empInvite.getMarital_status() != null)
            existing.setMarital_status(empInvite.getMarital_status());

        if (empInvite.getGender() != null)
            existing.setGender(empInvite.getGender());

        if (empInvite.getBlood_group() != null)
            existing.setBlood_group(empInvite.getBlood_group());

        if (empInvite.getState() != null)
            existing.setState(empInvite.getState());

        if (empInvite.getPincode() != null)
            existing.setPincode(empInvite.getPincode());

        if (empInvite.getAadhar_number() != null)
            existing.setAadhar_number(empInvite.getAadhar_number());

        if (empInvite.getPan_number() != null)
            existing.setPan_number(empInvite.getPan_number());

        if (empInvite.getAddress() != null)
            existing.setAddress(empInvite.getAddress());

        // =====================================================
        // BANK DETAILS
        // =====================================================

        if (empInvite.getBankDetails() != null) {

            BankDetails newBank =
                    empInvite.getBankDetails();

            BankDetails bank =
                    existing.getBankDetails() != null
                            ? existing.getBankDetails()
                            : new BankDetails();

            if (newBank.getBankName() != null)
                bank.setBankName(newBank.getBankName());

            if (newBank.getAccountHolderName() != null)
                bank.setAccountHolderName(
                        newBank.getAccountHolderName());

            if (newBank.getAccountNumber() != null)
                bank.setAccountNumber(
                        newBank.getAccountNumber());

            if (newBank.getConfirmAccountNumber() != null)
                bank.setConfirmAccountNumber(
                        newBank.getConfirmAccountNumber());

            if (newBank.getBranchName() != null)
                bank.setBranchName(
                        newBank.getBranchName());

            if (newBank.getIfsc_Number() != null)
                bank.setIfsc_Number(
                        newBank.getIfsc_Number());

            // IMPORTANT FIX
            bank.setEmployeeInvite(existing);

            existing.setBankDetails(bank);
        }

        // =====================================================
        // SAFE EXPERIENCE INIT
        // =====================================================

        if (existing.getExperience() == null) {
            existing.setExperience(new ArrayList<>());
        }
    }

    // =====================================================
    // FILES
    // =====================================================

    if (file != null && !file.isEmpty()) {

        existing.setImgFile(
                saveFile(file, "onBoardingProfiles"));
    }

    if (aadhar != null && !aadhar.isEmpty()) {

        existing.setAadhar_pdf(
                saveFile(aadhar, "onBoardingProfiles"));
    }

    if (pan_card != null && !pan_card.isEmpty()) {

        existing.setPan_pdf(
                saveFile(pan_card, "onBoardingProfiles"));
    }

    // =====================================================
    // PROFESSIONAL DETAILS
    // =====================================================

    if (resume != null && !resume.isEmpty()) {

        if (existing.getProfessional_details() == null) {

            ProfessionalDetails pd =
                    new ProfessionalDetails();

            pd.setEmployeeInvite(existing);

            existing.setProfessional_details(pd);
        }

        existing.getProfessional_details()
                .setResume(
                        saveFile(resume,
                                "onBoardingProfilesPdf"));
    }

    if (offerLetter != null && !offerLetter.isEmpty()) {

        if (existing.getProfessional_details() == null) {

            ProfessionalDetails pd =
                    new ProfessionalDetails();

            pd.setEmployeeInvite(existing);

            existing.setProfessional_details(pd);
        }

        existing.getProfessional_details()
                .setOffer_letter(
                        saveFile(offerLetter,
                                "onBoardingProfilesPdf"));
    }

    // =====================================================
    // BANK PASSBOOK
    // =====================================================

    if (passbook != null && !passbook.isEmpty()) {

        if (existing.getBankDetails() == null) {

            BankDetails bd = new BankDetails();

            bd.setEmployeeInvite(existing);

            existing.setBankDetails(bd);
        }

        existing.getBankDetails()
                .setPassbook_pdf(
                        saveFile(passbook,
                                "onBoardingProfilesPdf"));
    }

    // =====================================================
    // EDUCATION
    // =====================================================

    if (education != null && !education.isEmpty()) {

        if (existing.getEducation() == null) {

            Education edu = new Education();

            edu.setEmployeeInvite(existing);

            existing.setEducation(edu);
        }

        existing.getEducation()
                .setEducation_pdf(
                        saveFile(education,
                                "onBoardingProfilesPdf"));
    }

    // =====================================================
    // HIGHER EDUCATION
    // =====================================================

    if (higherEducation != null
            && !higherEducation.isEmpty()) {

        if (existing.getEducation() == null) {

            Education edu = new Education();

            edu.setEmployeeInvite(existing);

            existing.setEducation(edu);
        }

        if (existing.getEducation()
                .getHigherEducation() == null) {

            existing.getEducation()
                    .setHigherEducation(
                            new ArrayList<>());
        }

        for (int i = 0;
             i < higherEducation.size();
             i++) {

            MultipartFile f =
                    higherEducation.get(i);

            if (f != null && !f.isEmpty()) {

                while (existing.getEducation()
                        .getHigherEducation()
                        .size() <= i) {

                    HigherEducation hr =
                            new HigherEducation();

                    hr.setEducation(
                            existing.getEducation());

                    existing.getEducation()
                            .getHigherEducation()
                            .add(hr);
                }

                HigherEducation hr =
                        existing.getEducation()
                                .getHigherEducation()
                                .get(i);

                hr.setHigherEducation_pdf(
                        saveFile(f,
                                "onBoardingProfilesPdf"));
            }
        }
    }

    // =====================================================
    // EXPERIENCE - BANK STATEMENT
    // =====================================================

    if (bankStatement != null
            && !bankStatement.isEmpty()) {

        for (int i = 0;
             i < bankStatement.size();
             i++) {

            MultipartFile f =
                    bankStatement.get(i);

            if (f != null && !f.isEmpty()) {

                while (existing.getExperience()
                        .size() <= i) {

                    Experience exp =
                            new Experience();

                    // IMPORTANT FIX
                    exp.setEmployeeInvite(existing);

                    existing.getExperience()
                            .add(exp);
                }

                existing.getExperience()
                        .get(i)
                        .setBankStatement_pdf(
                                saveFile(f,
                                        "onBoardingProfilesPdf"));
            }
        }
    }

    // =====================================================
    // SALARY SLIP
    // =====================================================

    if (salarySlip != null
            && !salarySlip.isEmpty()) {

        for (int i = 0;
             i < salarySlip.size();
             i++) {

            MultipartFile f =
                    salarySlip.get(i);

            if (f != null && !f.isEmpty()) {

                while (existing.getExperience()
                        .size() <= i) {

                    Experience exp =
                            new Experience();

                    exp.setEmployeeInvite(existing);

                    existing.getExperience()
                            .add(exp);
                }

                existing.getExperience()
                        .get(i)
                        .setSalarySlip_pdf(
                                saveFile(f,
                                        "onBoardingProfilesPdf"));
            }
        }
    }

    // =====================================================
    // PREVIOUS OFFER LETTER
    // =====================================================

    if (prevExpLetter != null
            && !prevExpLetter.isEmpty()) {

        for (int i = 0;
             i < prevExpLetter.size();
             i++) {

            MultipartFile f =
                    prevExpLetter.get(i);

            if (f != null && !f.isEmpty()) {

                while (existing.getExperience()
                        .size() <= i) {

                    Experience exp =
                            new Experience();

                    exp.setEmployeeInvite(existing);

                    existing.getExperience()
                            .add(exp);
                }

                existing.getExperience()
                        .get(i)
                        .setOfferLetter_exp(
                                saveFile(f,
                                        "onBoardingProfilesPdf"));
            }
        }
    }

    // =====================================================
    // EXPERIENCE LETTER
    // =====================================================

    if (experienceLetter != null
            && !experienceLetter.isEmpty()) {

        for (int i = 0;
             i < experienceLetter.size();
             i++) {

            MultipartFile f =
                    experienceLetter.get(i);

            if (f != null && !f.isEmpty()) {

                while (existing.getExperience()
                        .size() <= i) {

                    Experience exp =
                            new Experience();

                    exp.setEmployeeInvite(existing);

                    existing.getExperience()
                            .add(exp);
                }

                existing.getExperience()
                        .get(i)
                        .setExp_letter(
                                saveFile(f,
                                        "onBoardingProfilesPdf"));
            }
        }
    }

    // =====================================================
    // SAVE
    // =====================================================

    EmployeeInvite saved =
            empInviteRepo.save(existing);

    return ResponseEntity.ok(saved);
}
	
	
	public ResponseEntity<?> convert(List<Long> lst) {

	    List<Employee> res = new ArrayList<>();

	    if (lst == null || lst.isEmpty()) {

	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("Invite details not found");
	    }

	    try {

	        for (Long n : lst) {

	            Optional<EmployeeInvite> ob =
	                    empInviteRepo.findById(n);

	            if (ob.isEmpty()) {

	                return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                        .body("Employee invite data with id: "
	                                + n + " not found");
	            }

	            EmployeeInvite obj = ob.get();

	            Employee emp = new Employee();

	            // ================= BASIC DETAILS =================

	            emp.setFirst_name(obj.getFirst_name());
	            emp.setLast_name(obj.getLast_name());
	            emp.setEmail(obj.getEmail());
	            emp.setPhone_number(obj.getPhone_number());
	            emp.setDate_of_birth(obj.getDate_of_birth());
	            emp.setMarital_status(obj.getMarital_status());
	            emp.setGender(obj.getGender());
	            emp.setBlood_group(obj.getBlood_group());
	            emp.setState(obj.getState());
	            emp.setPincode(obj.getPincode());
	            emp.setAadhar_number(obj.getAadhar_number());
	            emp.setPan_number(obj.getPan_number());
	            emp.setAddress(obj.getAddress());

	            emp.setImgFile(obj.getImgFile());

	            // Missing PDFs
	            emp.setAadhar_pdf(obj.getAadhar_pdf());
	            emp.setPan_pdf(obj.getPan_pdf());

	            // ================= BANK DETAILS =================

	            if (obj.getBankDetails() != null) {

	                BankDetails bd = new BankDetails();

	                bd.setBankName(
	                        obj.getBankDetails().getBankName());

	                bd.setAccountHolderName(
	                        obj.getBankDetails()
	                                .getAccountHolderName());

	                bd.setAccountNumber(
	                        obj.getBankDetails()
	                                .getAccountNumber());

	                bd.setConfirmAccountNumber(
	                        obj.getBankDetails()
	                                .getConfirmAccountNumber());

	                bd.setBranchName(
	                        obj.getBankDetails().getBranchName());

	                bd.setIfsc_Number(
	                        obj.getBankDetails().getIfsc_Number());

	                bd.setPassbook_pdf(
	                        obj.getBankDetails().getPassbook_pdf());

	                bd.setEmployee(emp);

	                emp.setBankDetails(bd);
	            }

	            // ================= PAYROLL =================

	            if (obj.getEmpPayroll() != null) {

	                EmployeePayroll payroll =
	                        new EmployeePayroll();

	                payroll.setBasicPay(
	                        obj.getEmpPayroll().getBasicPay());

	                payroll.setHRA(
	                        obj.getEmpPayroll().getHRA());

	                payroll.setSpecialAllowance(
	                        obj.getEmpPayroll()
	                                .getSpecialAllowance());

	                payroll.setLTA(
	                        obj.getEmpPayroll().getLTA());

	                payroll.setPF(
	                        obj.getEmpPayroll().getPF());

	                payroll.setMedicalAllowance(
	                        obj.getEmpPayroll()
	                                .getMedicalAllowance());

	                payroll.setBonus(
	                        obj.getEmpPayroll().getBonus());

	                payroll.setAnnualCTC(
	                        obj.getEmpPayroll().getAnnualCTC());

	                payroll.setEmployee(emp);

	                emp.setEmpPayroll(payroll);
	            }

	            // ================= EMERGENCY CONTACT =================

	            if (obj.getEmergency_contact() != null) {

	                EmergencyContact ec =
	                        new EmergencyContact();

	                ec.setName(
	                        obj.getEmergency_contact().getName());

	                ec.setRelation(
	                        obj.getEmergency_contact()
	                                .getRelation());

	                ec.setPhone(
	                        obj.getEmergency_contact().getPhone());

	                ec.setEmployee(emp);

	                emp.setEmergency_contact(ec);
	            }

	            // ================= EDUCATION =================

	            if (obj.getEducation() != null) {

	                Education edu = new Education();

	                edu.setEducationLevel(
	                        obj.getEducation()
	                                .getEducationLevel());

	                edu.setEducationalBoard(
	                        obj.getEducation()
	                                .getEducationalBoard());

	                edu.setSchoolName(
	                        obj.getEducation()
	                                .getSchoolName());

	                edu.setPlace(
	                        obj.getEducation().getPlace());

	                edu.setEducationalGroup(
	                        obj.getEducation()
	                                .getEducationalGroup());

	                edu.setSchool_from(
	                        obj.getEducation()
	                                .getSchool_from());

	                edu.setSchool_to(
	                        obj.getEducation()
	                                .getSchool_to());

	                edu.setSchool_percentage(
	                        obj.getEducation()
	                                .getSchool_percentage());

	                edu.setEducation_pdf(
	                        obj.getEducation()
	                                .getEducation_pdf());

	                edu.setEmployee(emp);

	                // ================= HIGHER EDUCATION =================

	                List<HigherEducation> higherList =
	                        new ArrayList<>();

	                if (obj.getEducation()
	                        .getHigherEducation() != null) {

	                    for (HigherEducation oldHe :
	                            obj.getEducation()
	                                    .getHigherEducation()) {

	                        HigherEducation he =
	                                new HigherEducation();

	                        he.setDegree(oldHe.getDegree());

	                        he.setInstituition(
	                                oldHe.getInstituition());

	                        he.setSpecialization(
	                                oldHe.getSpecialization());

	                        he.setDegree_from(
	                                oldHe.getDegree_from());

	                        he.setDegree_to(
	                                oldHe.getDegree_to());

	                        he.setPercentage(
	                                oldHe.getPercentage());

	                        he.setCertification(
	                                oldHe.getCertification());

	                        he.setCourseType(
	                                oldHe.getCourseType());

	                        // Missing PDF
	                        he.setHigherEducation_pdf(
	                                oldHe.getHigherEducation_pdf());

	                        // Relationship
	                        he.setEducation(edu);

	                        higherList.add(he);
	                    }
	                }

	                edu.setHigherEducation(higherList);

	                emp.setEducation(edu);
	            }

	            // ================= PROFESSIONAL DETAILS =================

	            if (obj.getProfessional_details() != null) {

	                ProfessionalDetails pd =
	                        new ProfessionalDetails();

	                pd.setProfessional_designation(
	                        obj.getProfessional_details()
	                                .getProfessional_designation());

	                pd.setProfessional_department(
	                        obj.getProfessional_details()
	                                .getProfessional_department());

	                pd.setEmp_type(
	                        obj.getProfessional_details()
	                                .getEmp_type());

	                pd.setLocation(
	                        obj.getProfessional_details()
	                                .getLocation());

	                pd.setEmp_status(
	                        obj.getProfessional_details()
	                                .getEmp_status());

	                pd.setDoj(
	                        obj.getProfessional_details()
	                                .getDoj());

	                pd.setProbation_period(
	                        obj.getProfessional_details()
	                                .getProbation_period());

	                pd.setConfirmation_date(
	                        obj.getProfessional_details()
	                                .getConfirmation_date());

	                pd.setSkills(
	                        obj.getProfessional_details()
	                                .getSkills());

	                pd.setExp_level(
	                        obj.getProfessional_details()
	                                .getExp_level());

	                pd.setResume(
	                        obj.getProfessional_details()
	                                .getResume());

	                pd.setOffer_letter(
	                        obj.getProfessional_details()
	                                .getOffer_letter());

	                pd.setEmployee(emp);

	                emp.setProfessional_details(pd);
	            }

	            // ================= EXPERIENCE =================

	            List<Experience> expList =
	                    new ArrayList<>();

	            if (obj.getExperience() != null) {

	                for (Experience oldExp :
	                        obj.getExperience()) {

	                    Experience exp =
	                            new Experience();

	                    exp.setCompany_name(
	                            oldExp.getCompany_name());

	                    exp.setJob_title(
	                            oldExp.getJob_title());

	                    exp.setEmp_type_prev(
	                            oldExp.getEmp_type_prev());

	                    exp.setEmp_start(
	                            oldExp.getEmp_start());

	                    exp.setEmp_end(
	                            oldExp.getEmp_end());

	                    exp.setCurrently_working(
	                            oldExp.getCurrently_working());

	                    exp.setDuration(
	                            oldExp.getDuration());

	                    exp.setTech_used(
	                            oldExp.getTech_used());

	                    exp.setRoles_responsibilities(
	                            oldExp.getRoles_responsibilities());

	                    // Missing PDFs
	                    exp.setExp_letter(
	                            oldExp.getExp_letter());

	                    exp.setOfferLetter_exp(
	                            oldExp.getOfferLetter_exp());

	                    exp.setBankStatement_pdf(
	                            oldExp.getBankStatement_pdf());

	                    exp.setSalarySlip_pdf(
	                            oldExp.getSalarySlip_pdf());

	                    exp.setEmployee(emp);

	                    expList.add(exp);
	                }
	            }

	            emp.setExperience(expList);

	            // ================= EMPLOYEE ID =================

	            Long maxId = empRepo.findMaxId();

	            String type = "E";

	            if (emp.getProfessional_details() != null
	                    && emp.getProfessional_details()
	                    .getEmp_type() != null) {

	                type = emp.getProfessional_details()
	                        .getEmp_type()
	                        .substring(0, 1)
	                        .toUpperCase();
	            }

	            long nextId =
	                    (maxId == null) ? 1 : maxId + 1;

	            emp.setEmployeeId(
	                    String.format("ZF%s-%03d",
	                            type,
	                            nextId));

	            // ================= SAVE =================

	            ResponseEntity<?> response =
	                    empService.createUser(emp);

	            System.out.println(response);

	            empInviteRepo.deleteById(obj.getId());

	            res.add(emp);
	        }

	    } catch (Exception e) {

	        e.printStackTrace();

	        return ResponseEntity.status(
	                        HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error while converting employee: "
	                        + e.getMessage());
	    }

	    return ResponseEntity.ok(res);
	}
	
	
	public ResponseEntity<?> convertByOne(Long id) {

	    Optional<EmployeeInvite> obj1 =
	            empInviteRepo.findById(id);

	    if (obj1.isEmpty()) {

	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("Employee invite data with id: "
	                        + id + " not found");
	    }

	    EmployeeInvite obj = obj1.get();

	    try {

	        // =========================
	        // CHECK EXISTING EMPLOYEE
	        // =========================

	        Optional<Employee> existingEmp =
	                empRepo.findByEmail(obj.getEmail());

	        if (existingEmp.isPresent()) {

	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("Employee already exists");
	        }

	        Employee emp = new Employee();

	        // =========================
	        // BASIC DETAILS
	        // =========================

	        emp.setFirst_name(obj.getFirst_name());
	        emp.setLast_name(obj.getLast_name());
	        emp.setEmail(obj.getEmail());
	        emp.setPhone_number(obj.getPhone_number());
	        emp.setDate_of_birth(obj.getDate_of_birth());
	        emp.setMarital_status(obj.getMarital_status());
	        emp.setGender(obj.getGender());
	        emp.setBlood_group(obj.getBlood_group());
	        emp.setState(obj.getState());
	        emp.setPincode(obj.getPincode());
	        emp.setAadhar_number(obj.getAadhar_number());
	        emp.setPan_number(obj.getPan_number());
	        emp.setAddress(obj.getAddress());
	   

	        // =========================
	        // FILES
	        // =========================

	        emp.setImgFile(obj.getImgFile());
	        emp.setAadhar_pdf(obj.getAadhar_pdf());
	        emp.setPan_pdf(obj.getPan_pdf());

	        // =========================
	        // BANK DETAILS
	        // =========================

	        if (obj.getBankDetails() != null) {

	            BankDetails bd = new BankDetails();

	            bd.setBankName(
	                    obj.getBankDetails().getBankName());

	            bd.setAccountHolderName(
	                    obj.getBankDetails()
	                            .getAccountHolderName());

	            bd.setAccountNumber(
	                    obj.getBankDetails()
	                            .getAccountNumber());

	            bd.setConfirmAccountNumber(
	                    obj.getBankDetails()
	                            .getConfirmAccountNumber());

	            bd.setBranchName(
	                    obj.getBankDetails()
	                            .getBranchName());

	            bd.setIfsc_Number(
	                    obj.getBankDetails()
	                            .getIfsc_Number());

	            bd.setPassbook_pdf(
	                    obj.getBankDetails()
	                            .getPassbook_pdf());

	            bd.setEmployee(emp);

	            emp.setBankDetails(bd);
	        }

	        // =========================
	        // PAYROLL
	        // =========================

	        if (obj.getEmpPayroll() != null) {

	            EmployeePayroll payroll =
	                    new EmployeePayroll();

	            payroll.setBasicPay(
	                    obj.getEmpPayroll().getBasicPay());

	            payroll.setHRA(
	                    obj.getEmpPayroll().getHRA());

	            payroll.setSpecialAllowance(
	                    obj.getEmpPayroll()
	                            .getSpecialAllowance());

	            payroll.setLTA(
	                    obj.getEmpPayroll().getLTA());

	            payroll.setPF(
	                    obj.getEmpPayroll().getPF());

	            payroll.setMedicalAllowance(
	                    obj.getEmpPayroll()
	                            .getMedicalAllowance());

	            payroll.setBonus(
	                    obj.getEmpPayroll().getBonus());

	            payroll.setAnnualCTC(
	                    obj.getEmpPayroll()
	                            .getAnnualCTC());

	            payroll.setEmployee(emp);

	            emp.setEmpPayroll(payroll);
	        }

	        // =========================
	        // EMERGENCY CONTACT
	        // =========================

	        if (obj.getEmergency_contact() != null) {

	            EmergencyContact ec =
	                    new EmergencyContact();

	            ec.setName(
	                    obj.getEmergency_contact()
	                            .getName());

	            ec.setRelation(
	                    obj.getEmergency_contact()
	                            .getRelation());

	            ec.setPhone(
	                    obj.getEmergency_contact()
	                            .getPhone());

	            ec.setEmployee(emp);

	            emp.setEmergency_contact(ec);
	        }

	        // =========================
	        // EDUCATION
	        // =========================

	        if (obj.getEducation() != null) {

	            Education edu = new Education();

	            edu.setEducationLevel(
	                    obj.getEducation()
	                            .getEducationLevel());

	            edu.setEducationalBoard(
	                    obj.getEducation()
	                            .getEducationalBoard());

	            edu.setSchoolName(
	                    obj.getEducation()
	                            .getSchoolName());

	            edu.setPlace(
	                    obj.getEducation()
	                            .getPlace());

	            edu.setEducationalGroup(
	                    obj.getEducation()
	                            .getEducationalGroup());

	            edu.setSchool_from(
	                    obj.getEducation()
	                            .getSchool_from());

	            edu.setSchool_to(
	                    obj.getEducation()
	                            .getSchool_to());

	            edu.setSchool_percentage(
	                    obj.getEducation()
	                            .getSchool_percentage());

	            edu.setEducation_pdf(
	                    obj.getEducation()
	                            .getEducation_pdf());

	            edu.setEmployee(emp);

	            // =========================
	            // HIGHER EDUCATION
	            // =========================

	            List<HigherEducation> higherList =
	                    new ArrayList<>();

	            if (obj.getEducation()
	                    .getHigherEducation() != null) {

	                for (HigherEducation oldHe :
	                        obj.getEducation()
	                                .getHigherEducation()) {

	                    HigherEducation he =
	                            new HigherEducation();

	                    he.setDegree(oldHe.getDegree());

	                    he.setInstituition(
	                            oldHe.getInstituition());

	                    he.setSpecialization(
	                            oldHe.getSpecialization());

	                    he.setDegree_from(
	                            oldHe.getDegree_from());

	                    he.setDegree_to(
	                            oldHe.getDegree_to());

	                    he.setPercentage(
	                            oldHe.getPercentage());

	                    he.setCertification(
	                            oldHe.getCertification());

	                    he.setCourseType(
	                            oldHe.getCourseType());

	                    he.setHigherEducation_pdf(
	                            oldHe.getHigherEducation_pdf());

	                    he.setEducation(edu);

	                    higherList.add(he);
	                }
	            }

	            edu.setHigherEducation(higherList);

	            emp.setEducation(edu);
	        }

	        // =========================
	        // PROFESSIONAL DETAILS
	        // =========================

	        if (obj.getProfessional_details() != null) {

	            ProfessionalDetails pd =
	                    new ProfessionalDetails();

	            pd.setProfessional_designation(
	                    obj.getProfessional_details()
	                            .getProfessional_designation());

	            pd.setProfessional_department(
	                    obj.getProfessional_details()
	                            .getProfessional_department());

	            pd.setEmp_type(
	                    obj.getProfessional_details()
	                            .getEmp_type());

	            pd.setLocation(
	                    obj.getProfessional_details()
	                            .getLocation());

	            pd.setEmp_status(
	                    obj.getProfessional_details()
	                            .getEmp_status());

	            pd.setDoj(
	                    obj.getProfessional_details()
	                            .getDoj());

	            pd.setProbation_period(
	                    obj.getProfessional_details()
	                            .getProbation_period());

	            pd.setConfirmation_date(
	                    obj.getProfessional_details()
	                            .getConfirmation_date());

	            pd.setSkills(
	                    obj.getProfessional_details()
	                            .getSkills());

	            pd.setExp_level(
	                    obj.getProfessional_details()
	                            .getExp_level());

	            pd.setResume(
	                    obj.getProfessional_details()
	                            .getResume());

	            pd.setOffer_letter(
	                    obj.getProfessional_details()
	                            .getOffer_letter());

	            pd.setEmployee(emp);

	            emp.setProfessional_details(pd);
	        }

	        // =========================
	        // EXPERIENCE
	        // =========================

	        List<Experience> expList =
	                new ArrayList<>();

	        if (obj.getExperience() != null) {

	            for (Experience oldExp :
	                    obj.getExperience()) {

	                Experience exp =
	                        new Experience();

	                exp.setCompany_name(
	                        oldExp.getCompany_name());

	                exp.setJob_title(
	                        oldExp.getJob_title());

	                exp.setEmp_type_prev(
	                        oldExp.getEmp_type_prev());

	                exp.setEmp_start(
	                        oldExp.getEmp_start());

	                exp.setEmp_end(
	                        oldExp.getEmp_end());

	                exp.setCurrently_working(
	                        oldExp.getCurrently_working());

	                exp.setDuration(
	                        oldExp.getDuration());

	                exp.setTech_used(
	                        oldExp.getTech_used());

	                exp.setRoles_responsibilities(
	                        oldExp.getRoles_responsibilities());

	                exp.setExp_letter(
	                        oldExp.getExp_letter());

	                exp.setOfferLetter_exp(
	                        oldExp.getOfferLetter_exp());

	                exp.setBankStatement_pdf(
	                        oldExp.getBankStatement_pdf());

	                exp.setSalarySlip_pdf(
	                        oldExp.getSalarySlip_pdf());

	                exp.setEmployee(emp);

	                expList.add(exp);
	            }
	        }

	        emp.setExperience(expList);

	        // =========================
	        // EMPLOYEE ID GENERATION
	        // =========================

	        Long maxId = empRepo.findMaxId();

	        String type = "E";

	        if (emp.getProfessional_details() != null
	                && emp.getProfessional_details()
	                .getEmp_type() != null
	                && !emp.getProfessional_details()
	                .getEmp_type()
	                .isBlank()) {

	            type = emp.getProfessional_details()
	                    .getEmp_type()
	                    .substring(0, 1)
	                    .toUpperCase();
	        }

	        long nextId =
	                (maxId == null) ? 1 : maxId + 1;

	        emp.setEmployeeId(
	                String.format("ZF%s-%03d",
	                        type,
	                        nextId));

	        // =========================
	        // SAVE EMPLOYEE
	        // =========================

	        Employee savedEmp = empRepo.save(emp);

	        // =========================
	        // DELETE INVITE
	        // =========================

	        empInviteRepo.deleteById(obj.getId());

	        return ResponseEntity.ok(savedEmp);

	    } catch (Exception e) {

	        e.printStackTrace();

	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Conversion Failed: "
	                        + e.getMessage());
	    }
	}
	
}
