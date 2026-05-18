package com.example.EMS.EmployeeService;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.example.EMS.EmployeeRepository.ProfessionalDetailRepository;

@Service
public class EmpService {

    public EmpRepository empRepo;
    public PasswordEncoder passwordEncoder;
    public ProfessionalDetailRepository professionalRepo;

    public EmpService(EmpRepository empRepo, PasswordEncoder passwordEncoder,
            ProfessionalDetailRepository professionalRepo) {
        this.empRepo = empRepo;
        this.passwordEncoder = passwordEncoder;
        this.professionalRepo = professionalRepo;
    }

    // ══════════════════════════════════════════════════════════════════
    // CREATE — single employee via JSON body
    // ══════════════════════════════════════════════════════════════════
    public ResponseEntity<?> createUser(Employee emp) {
        if (emp.getEmail() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Please enter employee mail id");
        }
        Optional<Employee> emailuser = empRepo.findByEmail(emp.getEmail());
        if (emailuser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User Already exists");
        }
        Employee employee = empRepo.save(emp);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(employee);
    }

    // ══════════════════════════════════════════════════════════════════
    // CREATE — single employee with multipart files
    // ══════════════════════════════════════════════════════════════════
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
		String detail =  emp.getProfessional_details().getEmp_type();
		String type = detail.substring(0, 1).toUpperCase(); 
		long nextId = (maxId == null) ? 1 : maxId + 1;
		emp.setEmployeeId(String.format("ZF%s-%03d", type, nextId));
		
		
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


    // ══════════════════════════════════════════════════════════════════
    // CREATE — bulk upload via Excel
    // ══════════════════════════════════════════════════════════════════
    public ResponseEntity<?> createUserXL(
            MultipartFile xlFile,
            MultipartFile file,
            MultipartFile passbook,
            MultipartFile education,
            MultipartFile resume,
            MultipartFile offerLetter,
            List<MultipartFile> experienceLetter) {

        try {
            Workbook workbook = new XSSFWorkbook(xlFile.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) rows.next(); 
            if (rows.hasNext()) rows.next(); 

            while (rows.hasNext()) {
                Row row = rows.next();

                if (row == null || row.getCell(0) == null
                        || getCellValue(row.getCell(0)).isEmpty()) continue;

                // ── 1. BASIC DETAILS (cols 0–12) ──────────────────────
                Employee emp = new Employee();
                emp.setFirst_name(getCellValue(row.getCell(0)));
                emp.setLast_name(getCellValue(row.getCell(1)));
                String email = getCellValue(row.getCell(2));
                
                Optional<Employee> empId = empRepo.findByEmployeeId(emp.getEmployeeId());
        		if(empId != null && empId.isPresent()) {
        			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Already exists with Employee Id: "+ empId.get().getEmployeeId());
        		}
        		
        		Optional<Employee> emailuser = empRepo.findByEmail(email);
        		
        		if(emailuser.isPresent()) {
        			continue;
        		}
                emp.setEmail(email);
                

                Long phone = parseLong(getCellValue(row.getCell(3)));
                if (phone != null) emp.setPhone_number(phone);

                emp.setDate_of_birth(parseDate(row.getCell(4)));
                emp.setMarital_status(getCellValue(row.getCell(5)));
                emp.setGender(getCellValue(row.getCell(6)));
                emp.setBlood_group(getCellValue(row.getCell(7)));
                emp.setState(getCellValue(row.getCell(8)));
                emp.setPincode(getCellValue(row.getCell(9)));
                emp.setAadhar_number(getCellValue(row.getCell(10)));
                emp.setPan_number(getCellValue(row.getCell(11)));
                emp.setAddress(getCellValue(row.getCell(12)));

                // ── 2. BANK DETAILS (cols 13–17) ──────────────────────
                String bankName = getCellValue(row.getCell(13));
                if (!bankName.isEmpty()) {
                    BankDetails bank = new BankDetails();
                    bank.setBankName(bankName);
                    bank.setAccountHolderName(getCellValue(row.getCell(14)));

                    Long accNo = parseLong(getCellValue(row.getCell(15)));
                    if (accNo != null) {
                        bank.setAccountNumber(accNo);
                        bank.setConfirmAccountNumber(accNo);
                    }

                    bank.setBranchName(getCellValue(row.getCell(16)));
                    bank.setIfsc_Number(getCellValue(row.getCell(17)));
                    bank.setEmployee(emp);
                    emp.setBankDetails(bank);
                }

                // ── 3. PROFESSIONAL DETAILS (cols 18–27) ──────────────
                String designation = getCellValue(row.getCell(18));
                if (!designation.isEmpty()) {
                    ProfessionalDetails pd = new ProfessionalDetails();
                    pd.setProfessional_designation(designation);
                    pd.setProfessional_department(getCellValue(row.getCell(19)));
                    pd.setEmp_type(getCellValue(row.getCell(20)));
                    pd.setLocation(getCellValue(row.getCell(21)));
                    pd.setEmp_status(getCellValue(row.getCell(22)));
                    pd.setExp_level(getCellValue(row.getCell(23)));
                    pd.setSkills(getCellValue(row.getCell(24)));
                    pd.setDoj(parseDate(row.getCell(25)));
                    pd.setProbation_period(getCellValue(row.getCell(26)));
                    pd.setConfirmation_date(parseDate(row.getCell(27)));
                    pd.setEmployee(emp);
                    emp.setProfessional_details(pd);
                    
                    
                    Long maxId = empRepo.findMaxId();
            		String detail =  emp.getProfessional_details().getEmp_type();
            		String type = detail.substring(0, 1).toUpperCase(); 
            		long nextId = (maxId == null) ? 1 : maxId + 1;
            		emp.setEmployeeId(String.format("ZF%s-%03d", type, nextId));
                }

                // ── 4. PAYROLL (cols 28–35) ────────────────────────────
                String basicPayStr = getCellValue(row.getCell(28));
                if (!basicPayStr.isEmpty()) {
                    EmployeePayroll payroll = new EmployeePayroll();
                    payroll.setBasicPay(parseDouble(getCellValue(row.getCell(28))));
                    payroll.setHRA(parseDouble(getCellValue(row.getCell(29))));
                    payroll.setSpecialAllowance(parseDouble(getCellValue(row.getCell(30))));
                    payroll.setLTA(parseDouble(getCellValue(row.getCell(31))));
                    payroll.setPF(parseDouble(getCellValue(row.getCell(32))));
                    payroll.setMedicalAllowance(parseDouble(getCellValue(row.getCell(33))));
                    payroll.setBonus(parseDouble(getCellValue(row.getCell(34))));
                    payroll.setAnnualCTC(parseDouble(getCellValue(row.getCell(35))));
                    payroll.setEmployee(emp);
                    emp.setEmpPayroll(payroll);
                }

                // ── 5. EMERGENCY CONTACT (cols 36–38) ─────────────────
                String ecName = getCellValue(row.getCell(36));
                if (!ecName.isEmpty()) {
                    EmergencyContact ec = new EmergencyContact();
                    ec.setName(ecName);
                    ec.setRelation(getCellValue(row.getCell(37)));

                    Long ecPhone = parseLong(getCellValue(row.getCell(38)));
                    if (ecPhone != null) ec.setPhone(ecPhone);

                    ec.setEmployee(emp);
                    emp.setEmergency_contact(ec);
                }

                // ── 6. EDUCATION (cols 39–46) ──────────────────────────
                String eduLevel = getCellValue(row.getCell(39));
                if (!eduLevel.isEmpty()) {
                    Education edu = new Education();
                    edu.setEducationLevel(eduLevel);
                    edu.setEducationalBoard(getCellValue(row.getCell(40)));
                    edu.setSchoolName(getCellValue(row.getCell(41)));
                    edu.setPlace(getCellValue(row.getCell(42)));
                    edu.setEducationalGroup(getCellValue(row.getCell(43)));
                    edu.setSchool_from(getCellValue(row.getCell(44)));
                    edu.setSchool_to(getCellValue(row.getCell(45)));
                    edu.setSchool_percentage(parseDouble(getCellValue(row.getCell(46))));
                    edu.setEmployee(emp);
                    emp.setEducation(edu);
                }

                // ── 7. EXPERIENCE (cols 47–55) ─────────────────────────
                String companyName = getCellValue(row.getCell(47));
                if (!companyName.isEmpty()) {
                    Experience exp = new Experience();
                    exp.setCompany_name(companyName);
                    exp.setJob_title(getCellValue(row.getCell(48)));
                    exp.setEmp_type_prev(getCellValue(row.getCell(49)));
                    exp.setEmp_start(parseDate(row.getCell(50)));
                    exp.setEmp_end(parseDate(row.getCell(51)));
                    exp.setCurrently_working(getCellValue(row.getCell(52)));
                    exp.setDuration(getCellValue(row.getCell(53)));
                    exp.setTech_used(getCellValue(row.getCell(54)));
                    exp.setRoles_responsibilities(getCellValue(row.getCell(55)));
                    exp.setEmployee(emp);
                    emp.setExperience(List.of(exp));
                }

                empRepo.save(emp);
            }

            workbook.close();
            return ResponseEntity.ok("Excel uploaded successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // READ
    // ══════════════════════════════════════════════════════════════════
    public ResponseEntity<?> getAllEmployeeDetails() {
        return ResponseEntity.ok(empRepo.findAll());
    }

    public ResponseEntity<?> getEmployeeById(String id) {
        Optional<Employee> emp = empRepo.findByEmployeeId(id);
        if (emp.isPresent()) return ResponseEntity.ok(emp);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Employee with id: " + id + " not found");
    }

    public ResponseEntity<?> getPayrollById(String empId) {
        Optional<Employee> empOptional = empRepo.findByEmployeeId(empId);
        if (empOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Employee not found with ID: " + empId);
        Employee emp = empOptional.get();
        if (emp.getEmpPayroll() == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Payroll details not found for Employee ID: " + empId);
        return ResponseEntity.ok(emp.getEmpPayroll());
    }

    // ══════════════════════════════════════════════════════════════════
    // DELETE
    // ══════════════════════════════════════════════════════════════════
    @Transactional
    public ResponseEntity<?> deleteEmployeeById(String id) {
        Optional<Employee> emp = empRepo.findByEmployeeId(id);
        if (emp.isPresent()) {
            empRepo.deleteByEmployeeId(id);
            return ResponseEntity.ok("Employee deleted with id: " + id);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Employee with id: " + id + " not found");
    }

    // ══════════════════════════════════════════════════════════════════
    // UPDATE — JSON body only
    // ══════════════════════════════════════════════════════════════════
    public ResponseEntity<?> updateEmployee(String empId, Employee emp) {
        Optional<Employee> existingEmp = empRepo.findByEmployeeId(empId);
        if (!existingEmp.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Employee not found with id: " + empId);

        Employee existing = existingEmp.get();
        applyBasicFields(existing, emp);

        if (emp.getBankDetails() != null)        applyBank(existing, emp.getBankDetails());
        if (emp.getEmpPayroll() != null)         applyPayroll(existing, emp.getEmpPayroll());
        if (emp.getEmergency_contact() != null)  applyEmergency(existing, emp.getEmergency_contact());
        if (emp.getEducation() != null)          applyEducation(existing, emp.getEducation());
        if (emp.getProfessional_details() != null) applyProfessional(existing, emp.getProfessional_details());
        if (emp.getExperience() != null && !emp.getExperience().isEmpty())
            existing.setExperience(emp.getExperience());

        return ResponseEntity.ok(empRepo.save(existing));
    }

    // ══════════════════════════════════════════════════════════════════
    // UPDATE — image only
    // ══════════════════════════════════════════════════════════════════
    public ResponseEntity<?> updateEmployeeImage(String empId, MultipartFile image) throws Exception {
        Optional<Employee> empOpt = empRepo.findByEmployeeId(empId);
        if (!empOpt.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        try {
            Employee existing = empOpt.get();
            existing.setImgFile(saveFile(image, "uploads"));
            empRepo.save(existing);
            return ResponseEntity.ok("Image updated successfully for id: " + empId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Image upload failed: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // UPDATE — single file by type
    // ══════════════════════════════════════════════════════════════════
    public ResponseEntity<?> updateEmployeeFile(String empId, MultipartFile file,
            String fileType) throws Exception {
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
                        existing.getExperience().get(0).setExp_letter(path);
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown file type");
            }
            empRepo.save(existing);
            return ResponseEntity.ok(fileType + " updated successfully for id: " + empId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // UPDATE — all fields + files together
    // ══════════════════════════════════════════════════════════════════
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
	 	        if (newPD.getEmp_type() != null) {
	 	        	existingPD.setEmp_type(newPD.getEmp_type());
	 	        	String id = existing.getEmployeeId(); //ZFP-001	
	 	        	System.out.println("Iddd:  "+id);
	 	        	String type = newPD.getEmp_type().toUpperCase();
	 	        	char ch = type.charAt(0);
	 	        	String new_id = id.substring(0,2) + ch + id.substring(3);
	 	        	existing.setEmployeeId(new_id);
	 	        }
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


    // ══════════════════════════════════════════════════════════════════
    // PRIVATE — field mergers (avoids duplicate null-check blocks)
    // ══════════════════════════════════════════════════════════════════
    private void applyBasicFields(Employee existing, Employee emp) {
        if (emp.getFirst_name() != null)    existing.setFirst_name(emp.getFirst_name());
        if (emp.getLast_name() != null)     existing.setLast_name(emp.getLast_name());
        if (emp.getEmail() != null)         existing.setEmail(emp.getEmail());
        if (emp.getPhone_number() != null)  existing.setPhone_number(emp.getPhone_number());
        if (emp.getDate_of_birth() != null) existing.setDate_of_birth(emp.getDate_of_birth());
        if (emp.getMarital_status() != null) existing.setMarital_status(emp.getMarital_status());
        if (emp.getGender() != null)        existing.setGender(emp.getGender());
        if (emp.getBlood_group() != null)   existing.setBlood_group(emp.getBlood_group());
        if (emp.getState() != null)         existing.setState(emp.getState());
        if (emp.getPincode() != null)       existing.setPincode(emp.getPincode());
        if (emp.getAadhar_number() != null) existing.setAadhar_number(emp.getAadhar_number());
        if (emp.getPan_number() != null)    existing.setPan_number(emp.getPan_number());
        if (emp.getAddress() != null)       existing.setAddress(emp.getAddress());
        if (emp.getImgFile() != null)       existing.setImgFile(emp.getImgFile());
    }

    private void applyBank(Employee existing, BankDetails newBank) {
        BankDetails b = existing.getBankDetails() != null
                ? existing.getBankDetails() : new BankDetails();
        if (newBank.getBankName() != null)           b.setBankName(newBank.getBankName());
        if (newBank.getAccountHolderName() != null)  b.setAccountHolderName(newBank.getAccountHolderName());
        if (newBank.getAccountNumber() != null)      b.setAccountNumber(newBank.getAccountNumber());
        if (newBank.getConfirmAccountNumber() != null) b.setConfirmAccountNumber(newBank.getConfirmAccountNumber());
        if (newBank.getBranchName() != null)         b.setBranchName(newBank.getBranchName());
        if (newBank.getIfsc_Number() != null)        b.setIfsc_Number(newBank.getIfsc_Number());
        if (newBank.getPassbook_pdf() != null)       b.setPassbook_pdf(newBank.getPassbook_pdf());
        existing.setBankDetails(b);
    }

    private void applyPayroll(Employee existing, EmployeePayroll newPayroll) {
        EmployeePayroll p = existing.getEmpPayroll() != null
                ? existing.getEmpPayroll() : new EmployeePayroll();
        if (newPayroll.getBasicPay() != 0)          p.setBasicPay(newPayroll.getBasicPay());
        if (newPayroll.getHRA() != 0)               p.setHRA(newPayroll.getHRA());
        if (newPayroll.getSpecialAllowance() != 0)  p.setSpecialAllowance(newPayroll.getSpecialAllowance());
        if (newPayroll.getLTA() != 0)               p.setLTA(newPayroll.getLTA());
        if (newPayroll.getPF() != 0)                p.setPF(newPayroll.getPF());
        if (newPayroll.getMedicalAllowance() != 0)  p.setMedicalAllowance(newPayroll.getMedicalAllowance());
        if (newPayroll.getBonus() != 0)             p.setBonus(newPayroll.getBonus());
        if (newPayroll.getAnnualCTC() != 0)         p.setAnnualCTC(newPayroll.getAnnualCTC());
        existing.setEmpPayroll(p);
    }

    private void applyEmergency(Employee existing, EmergencyContact newEC) {
        EmergencyContact ec = existing.getEmergency_contact() != null
                ? existing.getEmergency_contact() : new EmergencyContact();
        if (newEC.getName() != null)     ec.setName(newEC.getName());
        if (newEC.getRelation() != null) ec.setRelation(newEC.getRelation());
        if (newEC.getPhone() != null)    ec.setPhone(newEC.getPhone());
        existing.setEmergency_contact(ec);
    }

    private void applyEducation(Employee existing, Education newEdu) {
        Education edu = existing.getEducation() != null
                ? existing.getEducation() : new Education();
        if (newEdu.getEducationLevel() != null)    edu.setEducationLevel(newEdu.getEducationLevel());
        if (newEdu.getEducationalBoard() != null)  edu.setEducationalBoard(newEdu.getEducationalBoard());
        if (newEdu.getSchoolName() != null)        edu.setSchoolName(newEdu.getSchoolName());
        if (newEdu.getPlace() != null)             edu.setPlace(newEdu.getPlace());
        if (newEdu.getEducationalGroup() != null)  edu.setEducationalGroup(newEdu.getEducationalGroup());
        if (newEdu.getSchool_from() != null)       edu.setSchool_from(newEdu.getSchool_from());
        if (newEdu.getSchool_to() != null)         edu.setSchool_to(newEdu.getSchool_to());
        if (newEdu.getSchool_percentage() != 0)    edu.setSchool_percentage(newEdu.getSchool_percentage());
        if (newEdu.getEducation_pdf() != null)     edu.setEducation_pdf(newEdu.getEducation_pdf());
        if (newEdu.getHigherEducation() != null && !newEdu.getHigherEducation().isEmpty())
            edu.setHigherEducation(newEdu.getHigherEducation());
        existing.setEducation(edu);
    }

    private void applyProfessional(Employee existing, ProfessionalDetails newPD) {
        ProfessionalDetails pd = existing.getProfessional_details() != null
                ? existing.getProfessional_details() : new ProfessionalDetails();
        if (newPD.getProfessional_designation() != null)  pd.setProfessional_designation(newPD.getProfessional_designation());
        if (newPD.getProfessional_department() != null)   pd.setProfessional_department(newPD.getProfessional_department());
        if (newPD.getEmp_type() != null)                  pd.setEmp_type(newPD.getEmp_type());
        if (newPD.getLocation() != null)                  pd.setLocation(newPD.getLocation());
        if (newPD.getEmp_status() != null)                pd.setEmp_status(newPD.getEmp_status());
        if (newPD.getDoj() != null)                       pd.setDoj(newPD.getDoj());
        if (newPD.getProbation_period() != null)          pd.setProbation_period(newPD.getProbation_period());
        if (newPD.getConfirmation_date() != null)         pd.setConfirmation_date(newPD.getConfirmation_date());
        if (newPD.getSkills() != null)                    pd.setSkills(newPD.getSkills());
        if (newPD.getExp_level() != null)                 pd.setExp_level(newPD.getExp_level());
        if (newPD.getResume() != null)                    pd.setResume(newPD.getResume());
        if (newPD.getOffer_letter() != null)              pd.setOffer_letter(newPD.getOffer_letter());
        existing.setProfessional_details(pd);
    }

    // ══════════════════════════════════════════════════════════════════
    // PRIVATE — file save
    // ══════════════════════════════════════════════════════════════════
    public String saveFile(MultipartFile file, String folder) throws Exception {
        String upload = System.getProperty("user.dir") + "/" + folder + "/";
        File dir = new File(upload);
        if (!dir.exists()) dir.mkdirs();
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        file.transferTo(new File(upload + fileName));
        return folder + "/" + fileName;
    }

    // ══════════════════════════════════════════════════════════════════
    // PRIVATE — Excel helpers
    // ══════════════════════════════════════════════════════════════════

    // DataFormatter reads every cell as a string — never throws type mismatch
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return new DataFormatter().formatCellValue(cell).trim();
    }

    // Checks NUMERIC type before calling DateUtil — then falls back to string parse
    private Date parseDate(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC
                && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        }
        String val = getCellValue(cell);
        if (val.isEmpty()) return null;
        for (String fmt : new String[]{"dd-MM-yyyy", "dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd"}) {
            try { return new SimpleDateFormat(fmt).parse(val); }
            catch (ParseException ignored) {}
        }
        return null;
    }

    // Strips ".0" that Excel appends to numeric cells e.g. "9876543210.0"
    private Long parseLong(String val) {
        if (val == null || val.isEmpty()) return null;
        try {
            if (val.contains(".")) val = val.substring(0, val.indexOf('.'));
            return Long.parseLong(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private double parseDouble(String val) {
        if (val == null || val.isEmpty()) return 0.0;
        try { return Double.parseDouble(val.trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }

    // ══════════════════════════════════════════════════════════════════
    // PRIVATE — payroll calculator
    // ══════════════════════════════════════════════════════════════════
    public double calculateAnnualCTC(double basicPay, double HRA, double specialAllowance,
            double LTA, double PF, double medicalAllowance, double bonus) {
        return (basicPay + HRA + specialAllowance + LTA + PF + medicalAllowance + bonus) * 12;
    }
    
   
    public ResponseEntity<?> updateUserXL(
            MultipartFile xlFile,
            MultipartFile file,
            MultipartFile passbook,
            MultipartFile education,
            MultipartFile resume,
            MultipartFile offerLetter,
            List<MultipartFile> experienceLetter) {

        try {

            if (xlFile == null || xlFile.isEmpty()) {
                return ResponseEntity.badRequest().body("Excel file is required");
            }

            Workbook workbook = new XSSFWorkbook(xlFile.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip heading rows
            if (rows.hasNext()) rows.next();
            if (rows.hasNext()) rows.next();

            int updatedCount = 0;
            int skippedCount = 0;

            while (rows.hasNext()) {

                Row row = rows.next();

                if (row == null || row.getCell(0) == null
                        || getCellValue(row.getCell(0)).isEmpty()) {
                    continue;
                }

                // =========================
                // FIND EMPLOYEE BY EMAIL
                // =========================

                String email = getCellValue(row.getCell(2));

                if (!hasValue(email)) {
                    skippedCount++;
                    continue;
                }

                Optional<Employee> optionalEmp = empRepo.findByEmail(email);

                if (!optionalEmp.isPresent()) {
                    skippedCount++;
                    continue;
                }

                Employee emp = optionalEmp.get();

                // =========================
                // 1. BASIC DETAILS
                // =========================

                String firstName = getCellValue(row.getCell(0));
                if (hasValue(firstName)) {
                    emp.setFirst_name(firstName);
                }

                String lastName = getCellValue(row.getCell(1));
                if (hasValue(lastName)) {
                    emp.setLast_name(lastName);
                }

                Long phone = parseLong(getCellValue(row.getCell(3)));
                if (phone != null) {
                    emp.setPhone_number(phone);
                }

                Date dob = parseDate(row.getCell(4));
                if (dob != null) {
                    emp.setDate_of_birth(dob);
                }

                String marital = getCellValue(row.getCell(5));
                if (hasValue(marital)) {
                    emp.setMarital_status(marital);
                }

                String gender = getCellValue(row.getCell(6));
                if (hasValue(gender)) {
                    emp.setGender(gender);
                }

                String blood = getCellValue(row.getCell(7));
                if (hasValue(blood)) {
                    emp.setBlood_group(blood);
                }

                String state = getCellValue(row.getCell(8));
                if (hasValue(state)) {
                    emp.setState(state);
                }

                String pincode = getCellValue(row.getCell(9));
                if (hasValue(pincode)) {
                    emp.setPincode(pincode);
                }

                String aadhar = getCellValue(row.getCell(10));
                if (hasValue(aadhar)) {
                    emp.setAadhar_number(aadhar);
                }

                String pan = getCellValue(row.getCell(11));
                if (hasValue(pan)) {
                    emp.setPan_number(pan);
                }

                String address = getCellValue(row.getCell(12));
                if (hasValue(address)) {
                    emp.setAddress(address);
                }

                // =========================
                // 2. BANK DETAILS
                // =========================

                String bankName = getCellValue(row.getCell(13));

                if (hasValue(bankName)) {

                    BankDetails bank = emp.getBankDetails();

                    if (bank == null) {
                        bank = new BankDetails();
                        bank.setEmployee(emp);
                    }

                    bank.setBankName(bankName);

                    String holderName = getCellValue(row.getCell(14));
                    if (hasValue(holderName)) {
                        bank.setAccountHolderName(holderName);
                    }

                    Long accNo = parseLong(getCellValue(row.getCell(15)));

                    if (accNo != null) {
                        bank.setAccountNumber(accNo);
                        bank.setConfirmAccountNumber(accNo);
                    }

                    String branch = getCellValue(row.getCell(16));
                    if (hasValue(branch)) {
                        bank.setBranchName(branch);
                    }

                    String ifsc = getCellValue(row.getCell(17));
                    if (hasValue(ifsc)) {
                        bank.setIfsc_Number(ifsc);
                    }

                    emp.setBankDetails(bank);
                }

                // =========================
                // 3. PROFESSIONAL DETAILS
                // =========================

                String designation = getCellValue(row.getCell(18));

                if (hasValue(designation)) {

                    ProfessionalDetails pd = emp.getProfessional_details();

                    if (pd == null) {
                        pd = new ProfessionalDetails();
                        pd.setEmployee(emp);
                    }

                    pd.setProfessional_designation(designation);

                    String dept = getCellValue(row.getCell(19));
                    if (hasValue(dept)) {
                        pd.setProfessional_department(dept);
                    }

                    String empType = getCellValue(row.getCell(20));
                    if (hasValue(empType)) {
                        pd.setEmp_type(empType);
                    }

                    String location = getCellValue(row.getCell(21));
                    if (hasValue(location)) {
                        pd.setLocation(location);
                    }

                    String status = getCellValue(row.getCell(22));
                    if (hasValue(status)) {
                        pd.setEmp_status(status);
                    }

                    String expLevel = getCellValue(row.getCell(23));
                    if (hasValue(expLevel)) {
                        pd.setExp_level(expLevel);
                    }

                    String skills = getCellValue(row.getCell(24));
                    if (hasValue(skills)) {
                        pd.setSkills(skills);
                    }

                    Date doj = parseDate(row.getCell(25));
                    if (doj != null) {
                        pd.setDoj(doj);
                    }

                    String probation = getCellValue(row.getCell(26));
                    if (hasValue(probation)) {
                        pd.setProbation_period(probation);
                    }

                    Date confirmDate = parseDate(row.getCell(27));
                    if (confirmDate != null) {
                        pd.setConfirmation_date(confirmDate);
                    }

                    emp.setProfessional_details(pd);
                }

                // =========================
                // 4. PAYROLL
                // =========================

                String basicPayStr = getCellValue(row.getCell(28));

                if (hasValue(basicPayStr)) {

                    EmployeePayroll payroll = emp.getEmpPayroll();

                    if (payroll == null) {
                        payroll = new EmployeePayroll();
                        payroll.setEmployee(emp);
                    }

                    Double basicPay = parseDouble(getCellValue(row.getCell(28)));
                    if (basicPay != null) {
                        payroll.setBasicPay(basicPay);
                    }

                    Double hra = parseDouble(getCellValue(row.getCell(29)));
                    if (hra != null) {
                        payroll.setHRA(hra);
                    }

                    Double special = parseDouble(getCellValue(row.getCell(30)));
                    if (special != null) {
                        payroll.setSpecialAllowance(special);
                    }

                    Double lta = parseDouble(getCellValue(row.getCell(31)));
                    if (lta != null) {
                        payroll.setLTA(lta);
                    }

                    Double pf = parseDouble(getCellValue(row.getCell(32)));
                    if (pf != null) {
                        payroll.setPF(pf);
                    }

                    Double medical = parseDouble(getCellValue(row.getCell(33)));
                    if (medical != null) {
                        payroll.setMedicalAllowance(medical);
                    }

                    Double bonus = parseDouble(getCellValue(row.getCell(34)));
                    if (bonus != null) {
                        payroll.setBonus(bonus);
                    }

                    Double ctc = parseDouble(getCellValue(row.getCell(35)));
                    if (ctc != null) {
                        payroll.setAnnualCTC(ctc);
                    }

                    emp.setEmpPayroll(payroll);
                }

                // =========================
                // 5. EMERGENCY CONTACT
                // =========================

                String ecName = getCellValue(row.getCell(36));

                if (hasValue(ecName)) {

                    EmergencyContact ec = emp.getEmergency_contact();

                    if (ec == null) {
                        ec = new EmergencyContact();
                        ec.setEmployee(emp);
                    }

                    ec.setName(ecName);

                    String relation = getCellValue(row.getCell(37));
                    if (hasValue(relation)) {
                        ec.setRelation(relation);
                    }

                    Long ecPhone = parseLong(getCellValue(row.getCell(38)));

                    if (ecPhone != null) {
                        ec.setPhone(ecPhone);
                    }

                    emp.setEmergency_contact(ec);
                }

                // =========================
                // 6. EDUCATION
                // =========================

                String eduLevel = getCellValue(row.getCell(39));

                if (hasValue(eduLevel)) {

                    Education edu = emp.getEducation();

                    if (edu == null) {
                        edu = new Education();
                        edu.setEmployee(emp);
                    }

                    edu.setEducationLevel(eduLevel);

                    String board = getCellValue(row.getCell(40));
                    if (hasValue(board)) {
                        edu.setEducationalBoard(board);
                    }

                    String school = getCellValue(row.getCell(41));
                    if (hasValue(school)) {
                        edu.setSchoolName(school);
                    }

                    String place = getCellValue(row.getCell(42));
                    if (hasValue(place)) {
                        edu.setPlace(place);
                    }

                    String group = getCellValue(row.getCell(43));
                    if (hasValue(group)) {
                        edu.setEducationalGroup(group);
                    }

                    String from = getCellValue(row.getCell(44));
                    if (hasValue(from)) {
                        edu.setSchool_from(from);
                    }

                    String to = getCellValue(row.getCell(45));
                    if (hasValue(to)) {
                        edu.setSchool_to(to);
                    }

                    Double percentage = parseDouble(getCellValue(row.getCell(46)));

                    if (percentage != null) {
                        edu.setSchool_percentage(percentage);
                    }

                    emp.setEducation(edu);
                }

                // =========================
                // 7. EXPERIENCE
                // =========================

                String companyName = getCellValue(row.getCell(47));

                if (hasValue(companyName)) {

                    Experience exp;

                    if (emp.getExperience() != null && !emp.getExperience().isEmpty()) {
                        exp = emp.getExperience().get(0);
                    } else {
                        exp = new Experience();
                        exp.setEmployee(emp);
                    }

                    exp.setCompany_name(companyName);

                    String jobTitle = getCellValue(row.getCell(48));
                    if (hasValue(jobTitle)) {
                        exp.setJob_title(jobTitle);
                    }

                    String prevType = getCellValue(row.getCell(49));
                    if (hasValue(prevType)) {
                        exp.setEmp_type_prev(prevType);
                    }

                    Date start = parseDate(row.getCell(50));
                    if (start != null) {
                        exp.setEmp_start(start);
                    }

                    Date end = parseDate(row.getCell(51));
                    if (end != null) {
                        exp.setEmp_end(end);
                    }

                    String working = getCellValue(row.getCell(52));
                    if (hasValue(working)) {
                        exp.setCurrently_working(working);
                    }

                    String duration = getCellValue(row.getCell(53));
                    if (hasValue(duration)) {
                        exp.setDuration(duration);
                    }

                    String tech = getCellValue(row.getCell(54));
                    if (hasValue(tech)) {
                        exp.setTech_used(tech);
                    }

                    String roles = getCellValue(row.getCell(55));
                    if (hasValue(roles)) {
                        exp.setRoles_responsibilities(roles);
                    }

                    emp.setExperience(List.of(exp));
                }

                // =========================
                // SAVE
                // =========================

                empRepo.save(emp);
                updatedCount++;
            }

            workbook.close();

            return ResponseEntity.ok(
                    "Excel Update Completed Successfully. Updated: "
                            + updatedCount
                            + ", Skipped: "
                            + skippedCount);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body("Update Failed : " + e.getMessage());
        }
    }
    
    
    private boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }
    

    
}