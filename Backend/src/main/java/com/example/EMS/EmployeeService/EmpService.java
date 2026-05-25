package com.example.EMS.EmployeeService;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.EMS.EmployeeEntity.Attendance;
import com.example.EMS.EmployeeEntity.BankDetails;
import com.example.EMS.EmployeeEntity.Education;
import com.example.EMS.EmployeeEntity.EmergencyContact;
import com.example.EMS.EmployeeEntity.Employee;
import com.example.EMS.EmployeeEntity.EmployeePayroll;
import com.example.EMS.EmployeeEntity.Experience;
import com.example.EMS.EmployeeEntity.HigherEducation;
import com.example.EMS.EmployeeEntity.LeaveBalance;
import com.example.EMS.EmployeeEntity.LeaveRequest;
import com.example.EMS.EmployeeEntity.ProfessionalDetails;
import com.example.EMS.EmployeeRepository.EmpRepository;
import com.example.EMS.EmployeeRepository.ProfessionalDetailRepository;
import com.example.EMS.enums.Role;

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
			
			@RequestPart(value= "aadhar", required=false) MultipartFile aadhar,
			@RequestPart(value= "pan_card", required=false) MultipartFile pan_card,
			@RequestPart(value= "higherEducation", required=false) List<MultipartFile> higherEducation,
			@RequestPart(value= "bankStatement", required=false) List<MultipartFile> bankStatement,
			@RequestPart(value= "salarySlip", required=false) List<MultipartFile> salarySlip,
			
			@RequestPart(value= "passbook", required=false) MultipartFile passbook,
			@RequestPart(value= "education", required= false) MultipartFile education,
			@RequestPart(value="resume", required= false) MultipartFile resume,
			@RequestPart(value="offerLetter", required= false) MultipartFile offerLetter,
			@RequestPart(value="prevExpLetter",required=false) List<MultipartFile> prevExpLetter,
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
		
		if(aadhar != null && !aadhar.isEmpty()) {
			try {
				
				String fileName = saveFile(aadhar, "uploadsPdf");
				emp.setAadhar_pdf(fileName);
	
			}
			catch(Exception e) {
				return ResponseEntity.status(500).body("Aadhar Pdf upload failed "+e);
			}
		}
		
		if(pan_card != null && !pan_card.isEmpty()) {
			try {
				
				String fileName = saveFile(pan_card, "uploadsPdf");
				emp.setPan_pdf(fileName);
	
			}
			catch(Exception e) {
				return ResponseEntity.status(500).body("Pan Pdf upload failed "+e);
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
		
		if(higherEducation != null && !higherEducation.isEmpty() ) {
			try {
				System.out.println("heree");
				if(emp.getEducation().getHigherEducation() == null) {
					emp.getEducation().setHigherEducation(new ArrayList<HigherEducation>());
				}
				
				for(int i=0;i<higherEducation.size();i++) {
					HigherEducation hr = emp.getEducation().getHigherEducation().get(i);
					String fileName1 = saveFile(higherEducation.get(i), "uploadsPdf");
					hr.setHigherEducation_pdf(fileName1);
				}
			}
			catch(Exception e) {
				return ResponseEntity.status(500).body("Higher Educational Pdf upload failed "+e);
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
		
		if (prevExpLetter != null && !prevExpLetter.isEmpty()) {
		    try {
		        if (emp.getExperience() == null) {
		            emp.setExperience(new ArrayList<>());
		        }

		        for (int i = 0; i < prevExpLetter.size(); i++) {
		            MultipartFile files = prevExpLetter.get(i);
		            String fileName = saveFile(files, "uploadsPdf");
		            Experience exp;
		            if (emp.getExperience().size() > i) {
		                exp = emp.getExperience().get(i);
		            } else {
		                exp = new Experience();
		                emp.getExperience().add(exp);
		            }

		            exp.setOfferLetter_exp(fileName);
		        }

		    } catch (Exception e) {
		        return ResponseEntity.status(500).body("Experience upload failed "+e);
		    }
		}
		
		if (bankStatement != null && !bankStatement.isEmpty()) {
		    try {

		    	if (emp.getExperience() == null) {
		            emp.setExperience(new ArrayList<>());
		        }

		        for (int i = 0; i < bankStatement.size(); i++) {

		            MultipartFile files = bankStatement.get(i);

		            String fileName = saveFile(files, "uploadsPdf");

		            Experience exp;

		            if (emp.getExperience().size() > i) {
		                exp = emp.getExperience().get(i);
		            } else {
		                exp = new Experience();
		                emp.getExperience().add(exp);
		            }

		            exp.setBankStatement_pdf(fileName);

		         
		        }

		    } catch (Exception e) {
		        return ResponseEntity.status(500)
		                .body("Bank statement upload failed " + e);
		    }
		}
		
		if (salarySlip != null && !salarySlip.isEmpty()) {
		    try {

		        if (emp.getExperience() == null) {
		            emp.setExperience(new ArrayList<>());
		        }

		        for (int i = 0; i < salarySlip.size(); i++) {

		            MultipartFile files = salarySlip.get(i);

		            String fileName = saveFile(files, "uploadsPdf");

		            Experience exp;

		            if (emp.getExperience().size() > i) {
		                exp = emp.getExperience().get(i);
		            } else {
		                exp = new Experience();
		                emp.getExperience().add(exp);
		            }

		            exp.setSalarySlip_pdf(fileName);
		        }

		    } catch (Exception e) {
		        return ResponseEntity.status(500)
		                .body("Salary slip upload failed " + e);
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
		

		if (emp.getEducation() != null &&
		    emp.getEducation().getHigherEducation() != null &&
		    !emp.getEducation().getHigherEducation().isEmpty()) {
		
		    for (HigherEducation hr :
		            emp.getEducation().getHigherEducation()) {
		
		        hr.setEducation(emp.getEducation());
		    }
		}
		

		Employee employee = empRepo.save(emp);
		return ResponseEntity.ok(employee);
		
	}


    // ══════════════════════════════════════════════════════════════════
    // CREATE — bulk upload via Excel  - Need to update for newly added fields
    // ══════════════════════════════════════════════════════════════════
	public ResponseEntity<?> createUserXL(

	        MultipartFile xlFile,

	        List<MultipartFile> file,
	        List<MultipartFile> aadhar,
	        List<MultipartFile> pan_card,
	        List<MultipartFile> higherEducation,
	        List<MultipartFile> bankStatement,
	        List<MultipartFile> salarySlip,
	        List<MultipartFile> passbook,
	        List<MultipartFile> education,
	        List<MultipartFile> resume,
	        List<MultipartFile> offerLetter,
	        List<MultipartFile> prevExpLetter,
	        List<MultipartFile> experienceLetter) {

	    try {

	        Workbook workbook = new XSSFWorkbook(xlFile.getInputStream());

	        Sheet sheet = workbook.getSheetAt(0);

	        Iterator<Row> rows = sheet.iterator();

	        if (rows.hasNext()) rows.next();

	        Map<String, Integer> map = new HashMap<>();
	        
	        Row headRow = null;
	        if(rows.hasNext()) {
	        	headRow = rows.next();
	        }
	        
	        if (headRow != null) {

	            for (Cell cell : headRow) {

	                map.put(
	                    cell.getStringCellValue().trim(),
	                    cell.getColumnIndex()
	                );
	            }
	        }
	        
	        

	        List<Employee> lst = new ArrayList<>();

	        int rowIndex = 0;

	        while (rows.hasNext()) {

	            Row row = rows.next();

	            if (row == null ||
	                row.getCell(0) == null ||
	                getCellValue(row.getCell(0)).isEmpty()) {
	                continue;
	            }

	            Employee emp = new Employee();

	            // =====================================================
	            // BASIC DETAILS
	            // =====================================================

	            emp.setFirst_name(getCellValue(row.getCell(0)));
	            emp.setLast_name(getCellValue(row.getCell(1)));

	            String email = getCellValue(row.getCell(2));

	            Optional<Employee> emailuser = empRepo.findByEmail(email);

	            if (emailuser.isPresent()) {
	                rowIndex++;
	                continue;
	            }

	            emp.setEmail(email);

	            Long phone = parseLong(getCellValue(row.getCell(3)));

	            if (phone != null) {
	                emp.setPhone_number(phone);
	            }

	            emp.setDate_of_birth(parseDate(row.getCell(4)));
	            emp.setMarital_status(getCellValue(row.getCell(5)));
	            emp.setGender(getCellValue(row.getCell(6)));
	            emp.setBlood_group(getCellValue(row.getCell(7)));
	            emp.setState(getCellValue(row.getCell(8)));
	            emp.setPincode(getCellValue(row.getCell(9)));
	            emp.setAadhar_number(getCellValue(row.getCell(10)));
	            emp.setPan_number(getCellValue(row.getCell(11)));
	            emp.setAddress(getCellValue(row.getCell(12)));

	            String roleValue = getCellValue(row.getCell(13));

	            if (roleValue != null) {

	                switch (roleValue.trim().toUpperCase()) {

	                    case "ADMIN":
	                        emp.setRole(Role.ADMIN);
	                        break;

	                    case "HR":
	                        emp.setRole(Role.HR);
	                        break;

	                    case "MANAGER":
	                        emp.setRole(Role.MANAGER);
	                        break;

	                    default:
	                        emp.setRole(Role.EMPLOYEE);
	                        break;
	                }
	            }

	            // =====================================================
	            // PROFILE IMAGE
	            // =====================================================

	            if (file != null &&
	                file.size() > rowIndex &&
	                file.get(rowIndex) != null &&
	                !file.get(rowIndex).isEmpty()) {

	                String fileName = saveFile(file.get(rowIndex), "uploads");

	                emp.setImgFile(fileName);
	            }

	            // =====================================================
	            // AADHAR PDF
	            // =====================================================

	            if (aadhar != null &&
	                aadhar.size() > rowIndex &&
	                aadhar.get(rowIndex) != null &&
	                !aadhar.get(rowIndex).isEmpty()) {

	                String fileName = saveFile(aadhar.get(rowIndex), "uploadsPdf");

	                emp.setAadhar_pdf(fileName);
	            }

	            // =====================================================
	            // PAN PDF
	            // =====================================================

	            if (pan_card != null &&
	                pan_card.size() > rowIndex &&
	                pan_card.get(rowIndex) != null &&
	                !pan_card.get(rowIndex).isEmpty()) {

	                String fileName = saveFile(pan_card.get(rowIndex), "uploadsPdf");

	                emp.setPan_pdf(fileName);
	            }

	            // =====================================================
	            // BANK DETAILS
	            // =====================================================

	            String bankName = getCellValue(row.getCell(14));

	            if (!bankName.isEmpty()) {

	                BankDetails bank = new BankDetails();

	                bank.setBankName(bankName);
	                bank.setAccountHolderName(getCellValue(row.getCell(15)));

	                Long accNo = parseLong(getCellValue(row.getCell(16)));

	                if (accNo != null) {
	                    bank.setAccountNumber(accNo);
	                    bank.setConfirmAccountNumber(accNo);
	                }

	                bank.setBranchName(getCellValue(row.getCell(17)));
	                bank.setIfsc_Number(getCellValue(row.getCell(18)));

	                if (passbook != null &&
	                    passbook.size() > rowIndex &&
	                    passbook.get(rowIndex) != null &&
	                    !passbook.get(rowIndex).isEmpty()) {

	                    String fileName = saveFile(passbook.get(rowIndex), "uploadsPdf");

	                    bank.setPassbook_pdf(fileName);
	                }

	                bank.setEmployee(emp);

	                emp.setBankDetails(bank);
	            }

	            // =====================================================
	            // PROFESSIONAL DETAILS
	            // =====================================================

	            String designation = getCellValue(row.getCell(19));

	            if (!designation.isEmpty()) {

	                ProfessionalDetails pd = new ProfessionalDetails();

	                pd.setProfessional_designation(designation);
	                pd.setProfessional_department(getCellValue(row.getCell(20)));
	                pd.setEmp_type(getCellValue(row.getCell(21)));
	                pd.setLocation(getCellValue(row.getCell(22)));
	                pd.setEmp_status(getCellValue(row.getCell(23)));
	                pd.setExp_level(getCellValue(row.getCell(24)));
	                pd.setSkills(getCellValue(row.getCell(25)));
	                pd.setDoj(parseDate(row.getCell(26)));
	                pd.setProbation_period(getCellValue(row.getCell(27)));
	                pd.setConfirmation_date(parseDate(row.getCell(28)));

	                if (resume != null &&
	                    resume.size() > rowIndex &&
	                    resume.get(rowIndex) != null &&
	                    !resume.get(rowIndex).isEmpty()) {

	                    String fileName = saveFile(resume.get(rowIndex), "uploadsPdf");

	                    pd.setResume(fileName);
	                }

	                if (offerLetter != null &&
	                    offerLetter.size() > rowIndex &&
	                    offerLetter.get(rowIndex) != null &&
	                    !offerLetter.get(rowIndex).isEmpty()) {

	                    String fileName = saveFile(offerLetter.get(rowIndex), "uploadsPdf");

	                    pd.setOffer_letter(fileName);
	                }

	                pd.setEmployee(emp);

	                emp.setProfessional_details(pd);

	                Long maxId = empRepo.findMaxId();

	                String detail = pd.getEmp_type();

	                String type = detail.substring(0, 1).toUpperCase();

	                long nextId = (maxId == null) ? 1 : maxId + 1;

	                emp.setEmployeeId(String.format("ZF%s-%03d", type, nextId));
	            }

	            // =====================================================
	            // PAYROLL
	            // =====================================================

	            String basicPayStr = getCellValue(row.getCell(29));

	            if (!basicPayStr.isEmpty()) {

	                EmployeePayroll payroll = new EmployeePayroll();

	                payroll.setBasicPay(parseDouble(getCellValue(row.getCell(29))));
	                payroll.setHRA(parseDouble(getCellValue(row.getCell(30))));
	                payroll.setSpecialAllowance(parseDouble(getCellValue(row.getCell(31))));
	                payroll.setLTA(parseDouble(getCellValue(row.getCell(32))));
	                payroll.setPF(parseDouble(getCellValue(row.getCell(33))));
	                payroll.setMedicalAllowance(parseDouble(getCellValue(row.getCell(34))));
	                payroll.setBonus(parseDouble(getCellValue(row.getCell(35))));

	                double ctc = calculateAnnualCTC(
	                        payroll.getBasicPay(),
	                        payroll.getHRA(),
	                        payroll.getSpecialAllowance(),
	                        payroll.getLTA(),
	                        payroll.getPF(),
	                        payroll.getMedicalAllowance(),
	                        payroll.getBonus());

	                payroll.setAnnualCTC(ctc);

	                payroll.setEmployee(emp);

	                emp.setEmpPayroll(payroll);
	            }

	            // =====================================================
	            // EMERGENCY CONTACT
	            // =====================================================

	            String ecName = getCellValue(row.getCell(37));

	            if (!ecName.isEmpty()) {

	                EmergencyContact ec = new EmergencyContact();

	                ec.setName(ecName);
	                ec.setRelation(getCellValue(row.getCell(38)));

	                Long ecPhone = parseLong(getCellValue(row.getCell(39)));

	                if (ecPhone != null) {
	                    ec.setPhone(ecPhone);
	                }

	                ec.setEmployee(emp);

	                emp.setEmergency_contact(ec);
	            }

	            // =====================================================
	            // EDUCATION
	            // =====================================================

	            String eduLevel = getCellValue(row.getCell(40));

	            if (!eduLevel.isEmpty()) {

	                Education edu = new Education();

	                edu.setEducationLevel(eduLevel);
	                edu.setEducationalBoard(getCellValue(row.getCell(41)));
	                edu.setSchoolName(getCellValue(row.getCell(42)));
	                edu.setPlace(getCellValue(row.getCell(43)));
	                edu.setEducationalGroup(getCellValue(row.getCell(44)));
	                edu.setSchool_from(getCellValue(row.getCell(45)));
	                edu.setSchool_to(getCellValue(row.getCell(46)));
	                edu.setSchool_percentage(parseDouble(getCellValue(row.getCell(47))));

	                if (education != null &&
	                    education.size() > rowIndex &&
	                    education.get(rowIndex) != null &&
	                    !education.get(rowIndex).isEmpty()) {

	                    String fileName = saveFile(education.get(rowIndex), "uploadsPdf");

	                    edu.setEducation_pdf(fileName);
	                }

	                // HIGHER EDUCATION

	                List<HigherEducation> higherList = new ArrayList<>();

	                String degree = getCellValue(row.getCell(57));

	                if (!degree.isEmpty()) {

	                    HigherEducation he = new HigherEducation();

	                    he.setDegree(degree);
	                    he.setInstituition(getCellValue(row.getCell(58)));
	                    he.setSpecialization(getCellValue(row.getCell(59)));
	                    he.setDegree_from(getCellValue(row.getCell(60)));
	                    he.setDegree_to(getCellValue(row.getCell(61)));
	                    he.setPercentage(parseDouble(getCellValue(row.getCell(62))));
	                    he.setCertification(getCellValue(row.getCell(63)));
	                    he.setCourseType(getCellValue(row.getCell(64)));

	                    if (higherEducation != null &&
	                        higherEducation.size() > rowIndex &&
	                        higherEducation.get(rowIndex) != null &&
	                        !higherEducation.get(rowIndex).isEmpty()) {

	                        String fileName = saveFile(
	                                higherEducation.get(rowIndex),
	                                "uploadsPdf");

	                        he.setHigherEducation_pdf(fileName);
	                    }

	                    he.setEducation(edu);

	                    higherList.add(he);
	                }

	                edu.setHigherEducation(higherList);

	                edu.setEmployee(emp);

	                emp.setEducation(edu);
	            }

	            // =====================================================
	            // EXPERIENCE
	            // =====================================================

	            String companyName = getCellValue(row.getCell(48));

	            if (!companyName.isEmpty()) {

	                Experience exp = new Experience();

	                exp.setCompany_name(companyName);
	                exp.setJob_title(getCellValue(row.getCell(49)));
	                exp.setEmp_type_prev(getCellValue(row.getCell(50)));
	                exp.setEmp_start(parseDate(row.getCell(51)));
	                exp.setEmp_end(parseDate(row.getCell(52)));
	                exp.setCurrently_working(getCellValue(row.getCell(53)));
	                exp.setDuration(getCellValue(row.getCell(54)));
	                exp.setTech_used(getCellValue(row.getCell(55)));
	                exp.setRoles_responsibilities(getCellValue(row.getCell(56)));

	                // PREVIOUS EXPERIENCE LETTER

	                if (prevExpLetter != null &&
	                    prevExpLetter.size() > rowIndex &&
	                    prevExpLetter.get(rowIndex) != null &&
	                    !prevExpLetter.get(rowIndex).isEmpty()) {

	                    String fileName = saveFile(
	                            prevExpLetter.get(rowIndex),
	                            "uploadsPdf");

	                    exp.setOfferLetter_exp(fileName);
	                }

	                // EXPERIENCE LETTER

	                if (experienceLetter != null &&
	                    experienceLetter.size() > rowIndex &&
	                    experienceLetter.get(rowIndex) != null &&
	                    !experienceLetter.get(rowIndex).isEmpty()) {

	                    String fileName = saveFile(
	                            experienceLetter.get(rowIndex),
	                            "uploadsPdf");

	                    exp.setExp_letter(fileName);
	                }

	                // BANK STATEMENT

	                if (bankStatement != null &&
	                    bankStatement.size() > rowIndex &&
	                    bankStatement.get(rowIndex) != null &&
	                    !bankStatement.get(rowIndex).isEmpty()) {

	                    String fileName = saveFile(
	                            bankStatement.get(rowIndex),
	                            "uploadsPdf");

	                    exp.setBankStatement_pdf(fileName);
	                }

	                // SALARY SLIP

	                if (salarySlip != null &&
	                    salarySlip.size() > rowIndex &&
	                    salarySlip.get(rowIndex) != null &&
	                    !salarySlip.get(rowIndex).isEmpty()) {

	                    String fileName = saveFile(
	                            salarySlip.get(rowIndex),
	                            "uploadsPdf");

	                    exp.setSalarySlip_pdf(fileName);
	                }

	                exp.setEmployee(emp);

	                emp.setExperience(new ArrayList<>());

	                emp.getExperience().add(exp);
	            }

	            Employee savedEmp = empRepo.save(emp);

	            lst.add(savedEmp);

	            rowIndex++;
	        }

	        workbook.close();

	        if (!lst.isEmpty()) {
	            return ResponseEntity.ok(lst);
	        }

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("No new records available to upload");

	    } catch (Exception e) {

	        e.printStackTrace();

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("Upload failed : " + e.getMessage());
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

            MultipartFile aadhar,
            MultipartFile panCard,
            List<MultipartFile> higherEducation,
            List<MultipartFile> prevExpLetter,
            List<MultipartFile> bankStatement,
            List<MultipartFile> salarySlip,

            MultipartFile passbookPdf,
            MultipartFile educationPdf,

            MultipartFile resume,
            MultipartFile offerLetter,

            List<MultipartFile> expLetter

    ) throws Exception {

        Optional<Employee> existingOpt = empRepo.findByEmployeeId(empId);

        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Employee not found");
        }

        Employee existing = existingOpt.get();

        // ================= BASIC DETAILS =================

        if (emp != null) {

            if (emp.getFirst_name() != null)
                existing.setFirst_name(emp.getFirst_name());

            if (emp.getLast_name() != null)
                existing.setLast_name(emp.getLast_name());

            if (emp.getEmail() != null)
                existing.setEmail(emp.getEmail());

            if (emp.getPhone_number() != null)
                existing.setPhone_number(emp.getPhone_number());

            if (emp.getDate_of_birth() != null)
                existing.setDate_of_birth(emp.getDate_of_birth());

            if (emp.getMarital_status() != null)
                existing.setMarital_status(emp.getMarital_status());

            if (emp.getGender() != null)
                existing.setGender(emp.getGender());

            if (emp.getBlood_group() != null)
                existing.setBlood_group(emp.getBlood_group());

            if (emp.getState() != null)
                existing.setState(emp.getState());

            if (emp.getPincode() != null)
                existing.setPincode(emp.getPincode());

            if (emp.getAadhar_number() != null)
                existing.setAadhar_number(emp.getAadhar_number());

            if (emp.getPan_number() != null)
                existing.setPan_number(emp.getPan_number());

            if (emp.getAddress() != null)
                existing.setAddress(emp.getAddress());

            if (emp.getRole() != null)
                existing.setRole(emp.getRole());

            // ================= BANK DETAILS =================

            if (emp.getBankDetails() != null) {

                BankDetails newBank = emp.getBankDetails();

                BankDetails existingBank = existing.getBankDetails() != null
                        ? existing.getBankDetails()
                        : new BankDetails();

                if (newBank.getBankName() != null)
                    existingBank.setBankName(newBank.getBankName());

                if (newBank.getAccountHolderName() != null)
                    existingBank.setAccountHolderName(newBank.getAccountHolderName());

                if (newBank.getAccountNumber() != null)
                    existingBank.setAccountNumber(newBank.getAccountNumber());

                if (newBank.getConfirmAccountNumber() != null)
                    existingBank.setConfirmAccountNumber(newBank.getConfirmAccountNumber());

                if (newBank.getBranchName() != null)
                    existingBank.setBranchName(newBank.getBranchName());

                if (newBank.getIfsc_Number() != null)
                    existingBank.setIfsc_Number(newBank.getIfsc_Number());

                existingBank.setEmployee(existing);

                existing.setBankDetails(existingBank);
            }

            // ================= PAYROLL =================

            if (emp.getEmpPayroll() != null) {

                EmployeePayroll newPayroll = emp.getEmpPayroll();

                EmployeePayroll existingPayroll = existing.getEmpPayroll() != null
                        ? existing.getEmpPayroll()
                        : new EmployeePayroll();

                if (newPayroll.getBasicPay() != 0)
                    existingPayroll.setBasicPay(newPayroll.getBasicPay());

                if (newPayroll.getHRA() != 0)
                    existingPayroll.setHRA(newPayroll.getHRA());

                if (newPayroll.getSpecialAllowance() != 0)
                    existingPayroll.setSpecialAllowance(newPayroll.getSpecialAllowance());

                if (newPayroll.getLTA() != 0)
                    existingPayroll.setLTA(newPayroll.getLTA());

                if (newPayroll.getPF() != 0)
                    existingPayroll.setPF(newPayroll.getPF());

                if (newPayroll.getMedicalAllowance() != 0)
                    existingPayroll.setMedicalAllowance(newPayroll.getMedicalAllowance());

                if (newPayroll.getBonus() != 0)
                    existingPayroll.setBonus(newPayroll.getBonus());

                double basic = existingPayroll.getBasicPay();
                double hra = existingPayroll.getHRA();
                double specialAllowance = existingPayroll.getSpecialAllowance();
                double lta = existingPayroll.getLTA();
                double pf = existingPayroll.getPF();
                double medical = existingPayroll.getMedicalAllowance();
                double bonus = existingPayroll.getBonus();

                double ctc = calculateAnnualCTC(
                        basic,
                        hra,
                        specialAllowance,
                        lta,
                        pf,
                        medical,
                        bonus
                );

                existingPayroll.setAnnualCTC(ctc);

                existingPayroll.setEmployee(existing);

                existing.setEmpPayroll(existingPayroll);
            }

            // ================= EMERGENCY CONTACT =================

            if (emp.getEmergency_contact() != null) {

                EmergencyContact newEC = emp.getEmergency_contact();

                EmergencyContact existingEC = existing.getEmergency_contact() != null
                        ? existing.getEmergency_contact()
                        : new EmergencyContact();

                if (newEC.getName() != null)
                    existingEC.setName(newEC.getName());

                if (newEC.getRelation() != null)
                    existingEC.setRelation(newEC.getRelation());

                if (newEC.getPhone() != null)
                    existingEC.setPhone(newEC.getPhone());

                existingEC.setEmployee(existing);

                existing.setEmergency_contact(existingEC);
            }

            // ================= EDUCATION =================

            if (emp.getEducation() != null) {

                Education newEdu = emp.getEducation();

                Education existingEdu = existing.getEducation() != null
                        ? existing.getEducation()
                        : new Education();

                if (newEdu.getEducationLevel() != null)
                    existingEdu.setEducationLevel(newEdu.getEducationLevel());

                if (newEdu.getEducationalBoard() != null)
                    existingEdu.setEducationalBoard(newEdu.getEducationalBoard());

                if (newEdu.getSchoolName() != null)
                    existingEdu.setSchoolName(newEdu.getSchoolName());

                if (newEdu.getPlace() != null)
                    existingEdu.setPlace(newEdu.getPlace());

                if (newEdu.getEducationalGroup() != null)
                    existingEdu.setEducationalGroup(newEdu.getEducationalGroup());

                if (newEdu.getSchool_from() != null)
                    existingEdu.setSchool_from(newEdu.getSchool_from());

                if (newEdu.getSchool_to() != null)
                    existingEdu.setSchool_to(newEdu.getSchool_to());

                if (newEdu.getSchool_percentage() != 0)
                    existingEdu.setSchool_percentage(newEdu.getSchool_percentage());

                if (newEdu.getHigherEducation() != null
                        && !newEdu.getHigherEducation().isEmpty()) {

                    for (HigherEducation hr : newEdu.getHigherEducation()) {
                        hr.setEducation(existingEdu);
                    }

                    existingEdu.setHigherEducation(newEdu.getHigherEducation());
                }

                existingEdu.setEmployee(existing);

                existing.setEducation(existingEdu);
            }

            // ================= PROFESSIONAL DETAILS =================

            if (emp.getProfessional_details() != null) {

                ProfessionalDetails newPD = emp.getProfessional_details();

                ProfessionalDetails existingPD =
                        existing.getProfessional_details() != null
                                ? existing.getProfessional_details()
                                : new ProfessionalDetails();

                if (newPD.getProfessional_designation() != null)
                    existingPD.setProfessional_designation(
                            newPD.getProfessional_designation());

                if (newPD.getProfessional_department() != null)
                    existingPD.setProfessional_department(
                            newPD.getProfessional_department());

                if (newPD.getEmp_type() != null) {

                    existingPD.setEmp_type(newPD.getEmp_type());

                    String id = existing.getEmployeeId();

                    String type = newPD.getEmp_type().toUpperCase();

                    char ch = type.charAt(0);

                    String newId = id.substring(0, 2)
                            + ch
                            + id.substring(3);

                    existing.setEmployeeId(newId);
                }

                if (newPD.getLocation() != null)
                    existingPD.setLocation(newPD.getLocation());

                if (newPD.getEmp_status() != null)
                    existingPD.setEmp_status(newPD.getEmp_status());

                if (newPD.getDoj() != null)
                    existingPD.setDoj(newPD.getDoj());

                if (newPD.getProbation_period() != null)
                    existingPD.setProbation_period(newPD.getProbation_period());

                if (newPD.getConfirmation_date() != null)
                    existingPD.setConfirmation_date(newPD.getConfirmation_date());

                if (newPD.getSkills() != null)
                    existingPD.setSkills(newPD.getSkills());

                if (newPD.getExp_level() != null)
                    existingPD.setExp_level(newPD.getExp_level());

                existingPD.setEmployee(existing);

                existing.setProfessional_details(existingPD);
            }

            // ================= EXPERIENCE =================

            if (emp.getExperience() != null
                    && !emp.getExperience().isEmpty()) {

                if (existing.getExperience() == null) {
                    existing.setExperience(new ArrayList<>());
                }

            //    existing.getExperience().clear();

                for (Experience exp : emp.getExperience()) {

                    exp.setEmployee(existing);

                    existing.getExperience().add(exp);
                }
            }

            // ================= ATTENDANCE =================

            if (emp.getAttendance() != null
                    && !emp.getAttendance().isEmpty()) {

                if (existing.getAttendance() == null) {
                    existing.setAttendance(new ArrayList<>());
                }

           //     existing.getAttendance().clear();

                for (Attendance attendance : emp.getAttendance()) {

                    attendance.setEmployee(existing);

                    existing.getAttendance().add(attendance);
                }
            }

            // ================= LEAVE BALANCE =================

            if (emp.getLeaveBalance() != null
                    && !emp.getLeaveBalance().isEmpty()) {

                if (existing.getLeaveBalance() == null) {
                    existing.setLeaveBalance(new ArrayList<>());
                }

        //        existing.getLeaveBalance().clear();

                for (LeaveBalance leave : emp.getLeaveBalance()) {

                    leave.setEmployee(existing);

                    existing.getLeaveBalance().add(leave);
                }
            }

            // ================= LEAVE REQUEST =================

            if (emp.getLeaveRequest() != null
                    && !emp.getLeaveRequest().isEmpty()) {

                if (existing.getLeaveRequest() == null) {
                    existing.setLeaveRequest(new ArrayList<>());
                }

        //        existing.getLeaveRequest().clear();

                for (LeaveRequest leave : emp.getLeaveRequest()) {

                    leave.setEmployee(existing);

                    existing.getLeaveRequest().add(leave);
                }
            }
        }

        // ================= IMAGE =================

        if (image != null && !image.isEmpty()) {

            String fileName = saveFile(image, "uploads");

            existing.setImgFile(fileName);
        }

        // ================= AADHAR =================

        if (aadhar != null && !aadhar.isEmpty()) {

            String fileName = saveFile(aadhar, "uploadsPdf");

            existing.setAadhar_pdf(fileName);
        }

        // ================= PAN =================

        if (panCard != null && !panCard.isEmpty()) {

            String fileName = saveFile(panCard, "uploadsPdf");

            existing.setPan_pdf(fileName);
        }

        // ================= RESUME =================

        if (resume != null && !resume.isEmpty()) {

            String fileName = saveFile(resume, "uploadsPdf");

            if (existing.getProfessional_details() == null) {
                existing.setProfessional_details(new ProfessionalDetails());
            }

            existing.getProfessional_details().setResume(fileName);
        }

        // ================= OFFER LETTER =================

        if (offerLetter != null && !offerLetter.isEmpty()) {

            String fileName = saveFile(offerLetter, "uploadsPdf");

            if (existing.getProfessional_details() == null) {
                existing.setProfessional_details(new ProfessionalDetails());
            }

            existing.getProfessional_details().setOffer_letter(fileName);
        }

        // ================= PASSBOOK =================

        if (passbookPdf != null && !passbookPdf.isEmpty()) {

            String fileName = saveFile(passbookPdf, "uploadsPdf");

            if (existing.getBankDetails() == null) {
                existing.setBankDetails(new BankDetails());
            }

            existing.getBankDetails().setPassbook_pdf(fileName);
        }

        // ================= EDUCATION PDF =================

        if (educationPdf != null && !educationPdf.isEmpty()) {

            String fileName = saveFile(educationPdf, "uploadsPdf");

            if (existing.getEducation() == null) {
                existing.setEducation(new Education());
            }

            existing.getEducation().setEducation_pdf(fileName);
        }

     // ================= HIGHER EDUCATION PDF =================

        if (higherEducation != null && !higherEducation.isEmpty()) {

            if (existing.getEducation() != null
                    && existing.getEducation().getHigherEducation() != null) {

                List<HigherEducation> higherEduList =
                        existing.getEducation().getHigherEducation();

                for (int i = 0; i < higherEducation.size(); i++) {

                    MultipartFile file = higherEducation.get(i);

                    String fileName = saveFile(file, "uploadsPdf");

                    if (higherEduList.size() > i) {

                        HigherEducation hr = higherEduList.get(i);

                        hr.setHigherEducation_pdf(fileName);

                        hr.setEducation(existing.getEducation());
                    }
                }
            }
        }

        // ================= EXPERIENCE LETTER =================

        if (expLetter != null && !expLetter.isEmpty()) {

            if (existing.getExperience() == null) {
                existing.setExperience(new ArrayList<>());
            }

            for (int i = 0; i < expLetter.size(); i++) {

                MultipartFile file = expLetter.get(i);

                String fileName = saveFile(file, "uploadsPdf");

                Experience experience;

                if (existing.getExperience().size() > i) {

                    experience = existing.getExperience().get(i);

                } else {

                    experience = new Experience();

                    experience.setEmployee(existing);

                    existing.getExperience().add(experience);
                }

                experience.setExp_letter(fileName);
            }
        }
        
        
        if (prevExpLetter != null && !prevExpLetter.isEmpty()) {

            if (existing.getExperience() == null) {
                existing.setExperience(new ArrayList<>());
            }

            for (int i = 0; i < prevExpLetter.size(); i++) {

                MultipartFile file = expLetter.get(i);

                String fileName = saveFile(file, "uploadsPdf");

                Experience experience;

                if (existing.getExperience().size() > i) {

                    experience = existing.getExperience().get(i);

                } else {

                    experience = new Experience();

                    experience.setEmployee(existing);

                    existing.getExperience().add(experience);
                }

                experience.setOfferLetter_exp(fileName);
            }
        }

        // ================= BANK STATEMENT =================

        if (bankStatement != null && !bankStatement.isEmpty()) {

            if (existing.getExperience() == null) {
                existing.setExperience(new ArrayList<>());
            }

            for (int i = 0; i < bankStatement.size(); i++) {

                MultipartFile file = bankStatement.get(i);

                String fileName = saveFile(file, "uploadsPdf");

                Experience experience;

                if (existing.getExperience().size() > i) {

                    experience = existing.getExperience().get(i);

                } else {

                    experience = new Experience();

                    experience.setEmployee(existing);

                    existing.getExperience().add(experience);
                }

                experience.setBankStatement_pdf(fileName);
            }
        }

        // ================= SALARY SLIP =================

        if (salarySlip != null && !salarySlip.isEmpty()) {

            if (existing.getExperience() == null) {
                existing.setExperience(new ArrayList<>());
            }

            for (int i = 0; i < salarySlip.size(); i++) {

                MultipartFile file = salarySlip.get(i);

                String fileName = saveFile(file, "uploadsPdf");

                Experience experience;

                if (existing.getExperience().size() > i) {

                    experience = existing.getExperience().get(i);

                } else {

                    experience = new Experience();

                    experience.setEmployee(existing);

                    existing.getExperience().add(experience);
                }

                experience.setSalarySlip_pdf(fileName);
            }
        }

        Employee updatedEmployee = empRepo.save(existing);

        return ResponseEntity.ok(updatedEmployee);
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

            List<MultipartFile> file,
            List<MultipartFile> aadhar,
            List<MultipartFile> pan_card,
            List<MultipartFile> passbook,
            List<MultipartFile> education,
            List<MultipartFile> higherEducation,
            List<MultipartFile> resume,
            List<MultipartFile> offerLetter,
            List<MultipartFile> prevExpLetter,
            List<MultipartFile> experienceLetter,
            List<MultipartFile> bankStatement,
            List<MultipartFile> salarySlip

    ) {

        try {

            if (xlFile == null || xlFile.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Excel file is required");
            }

            Workbook workbook = new XSSFWorkbook(xlFile.getInputStream());

            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) rows.next();
            if (rows.hasNext()) rows.next();

            int updatedCount = 0;
            int skippedCount = 0;

            int rowIndex = 0;

            while (rows.hasNext()) {

                Row row = rows.next();

                if (row == null
                        || row.getCell(0) == null
                        || getCellValue(row.getCell(0)).isEmpty()) {

                    rowIndex++;
                    continue;
                }

                String email = getCellValue(row.getCell(2));

                if (!hasValue(email)) {
                    skippedCount++;
                    rowIndex++;
                    continue;
                }

                Optional<Employee> optionalEmp = empRepo.findByEmail(email);

                if (!optionalEmp.isPresent()) {
                    skippedCount++;
                    rowIndex++;
                    continue;
                }

                Employee emp = optionalEmp.get();

                // =====================================================
                // BASIC DETAILS
                // =====================================================

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

                String aadharNo = getCellValue(row.getCell(10));

                if (hasValue(aadharNo)) {
                    emp.setAadhar_number(aadharNo);
                }

                String panNo = getCellValue(row.getCell(11));

                if (hasValue(panNo)) {
                    emp.setPan_number(panNo);
                }

                String address = getCellValue(row.getCell(12));

                if (hasValue(address)) {
                    emp.setAddress(address);
                }

                // =====================================================
                // ROLE
                // =====================================================

                String roleValue = getCellValue(row.getCell(13));

                if (roleValue != null) {

                    switch (roleValue.trim().toUpperCase()) {

                        case "ADMIN":
                            emp.setRole(Role.ADMIN);
                            break;

                        case "HR":
                            emp.setRole(Role.HR);
                            break;

                        case "MANAGER":
                            emp.setRole(Role.MANAGER);
                            break;

                        default:
                            emp.setRole(Role.EMPLOYEE);
                            break;
                    }
                }

                // =====================================================
                // PROFILE IMAGE
                // =====================================================

                if (file != null
                        && file.size() > rowIndex
                        && file.get(rowIndex) != null
                        && !file.get(rowIndex).isEmpty()) {

                    String fileName = saveFile(
                            file.get(rowIndex),
                            "uploads"
                    );

                    emp.setImgFile(fileName);
                }

                // =====================================================
                // AADHAR PDF
                // =====================================================

                if (aadhar != null
                        && aadhar.size() > rowIndex
                        && aadhar.get(rowIndex) != null
                        && !aadhar.get(rowIndex).isEmpty()) {

                    String fileName = saveFile(
                            aadhar.get(rowIndex),
                            "uploadsPdf"
                    );

                    emp.setAadhar_pdf(fileName);
                }

                // =====================================================
                // PAN PDF
                // =====================================================

                if (pan_card != null
                        && pan_card.size() > rowIndex
                        && pan_card.get(rowIndex) != null
                        && !pan_card.get(rowIndex).isEmpty()) {

                    String fileName = saveFile(
                            pan_card.get(rowIndex),
                            "uploadsPdf"
                    );

                    emp.setPan_pdf(fileName);
                }

                // =====================================================
                // BANK DETAILS
                // =====================================================

                BankDetails bank = emp.getBankDetails();

                if (bank == null) {
                    bank = new BankDetails();
                    bank.setEmployee(emp);
                }

                String bankName = getCellValue(row.getCell(14));

                if (hasValue(bankName)) {
                    bank.setBankName(bankName);
                }

                String holder = getCellValue(row.getCell(15));

                if (hasValue(holder)) {
                    bank.setAccountHolderName(holder);
                }

                Long accNo = parseLong(getCellValue(row.getCell(16)));

                if (accNo != null) {
                    bank.setAccountNumber(accNo);
                    bank.setConfirmAccountNumber(accNo);
                }

                String branch = getCellValue(row.getCell(17));

                if (hasValue(branch)) {
                    bank.setBranchName(branch);
                }

                String ifsc = getCellValue(row.getCell(18));

                if (hasValue(ifsc)) {
                    bank.setIfsc_Number(ifsc);
                }

                if (passbook != null
                        && passbook.size() > rowIndex
                        && passbook.get(rowIndex) != null
                        && !passbook.get(rowIndex).isEmpty()) {

                    String fileName = saveFile(
                            passbook.get(rowIndex),
                            "uploadsPdf"
                    );

                    bank.setPassbook_pdf(fileName);
                }

                emp.setBankDetails(bank);

                // =====================================================
                // PROFESSIONAL DETAILS
                // =====================================================

                ProfessionalDetails pd = emp.getProfessional_details();

                if (pd == null) {
                    pd = new ProfessionalDetails();
                    pd.setEmployee(emp);
                }

                String designation = getCellValue(row.getCell(19));

                if (hasValue(designation)) {
                    pd.setProfessional_designation(designation);
                }

                String dept = getCellValue(row.getCell(20));

                if (hasValue(dept)) {
                    pd.setProfessional_department(dept);
                }

                String empType = getCellValue(row.getCell(21));

                if (hasValue(empType)) {
                    pd.setEmp_type(empType);
                }

                String location = getCellValue(row.getCell(22));

                if (hasValue(location)) {
                    pd.setLocation(location);
                }

                String status = getCellValue(row.getCell(23));

                if (hasValue(status)) {
                    pd.setEmp_status(status);
                }

                String expLevel = getCellValue(row.getCell(24));

                if (hasValue(expLevel)) {
                    pd.setExp_level(expLevel);
                }

                String skills = getCellValue(row.getCell(25));

                if (hasValue(skills)) {
                    pd.setSkills(skills);
                }

                Date doj = parseDate(row.getCell(26));

                if (doj != null) {
                    pd.setDoj(doj);
                }

                String probation = getCellValue(row.getCell(27));

                if (hasValue(probation)) {
                    pd.setProbation_period(probation);
                }

                Date confirmDate = parseDate(row.getCell(28));

                if (confirmDate != null) {
                    pd.setConfirmation_date(confirmDate);
                }

                if (resume != null
                        && resume.size() > rowIndex
                        && resume.get(rowIndex) != null
                        && !resume.get(rowIndex).isEmpty()) {

                    String fileName = saveFile(
                            resume.get(rowIndex),
                            "uploadsPdf"
                    );

                    pd.setResume(fileName);
                }

                if (offerLetter != null
                        && offerLetter.size() > rowIndex
                        && offerLetter.get(rowIndex) != null
                        && !offerLetter.get(rowIndex).isEmpty()) {

                    String fileName = saveFile(
                            offerLetter.get(rowIndex),
                            "uploadsPdf"
                    );

                    pd.setOffer_letter(fileName);
                }

                emp.setProfessional_details(pd);

                // =====================================================
                // PAYROLL
                // =====================================================

                EmployeePayroll payroll = emp.getEmpPayroll();

                if (payroll == null) {
                    payroll = new EmployeePayroll();
                    payroll.setEmployee(emp);
                }

                Double basic = parseDouble(getCellValue(row.getCell(29)));
                Double hra = parseDouble(getCellValue(row.getCell(30)));
                Double special = parseDouble(getCellValue(row.getCell(31)));
                Double lta = parseDouble(getCellValue(row.getCell(32)));
                Double pf = parseDouble(getCellValue(row.getCell(33)));
                Double medical = parseDouble(getCellValue(row.getCell(34)));
                Double bonus = parseDouble(getCellValue(row.getCell(35)));

                if (basic != null) payroll.setBasicPay(basic);
                if (hra != null) payroll.setHRA(hra);
                if (special != null) payroll.setSpecialAllowance(special);
                if (lta != null) payroll.setLTA(lta);
                if (pf != null) payroll.setPF(pf);
                if (medical != null) payroll.setMedicalAllowance(medical);
                if (bonus != null) payroll.setBonus(bonus);

                double ctc = calculateAnnualCTC(
                        payroll.getBasicPay(),
                        payroll.getHRA(),
                        payroll.getSpecialAllowance(),
                        payroll.getLTA(),
                        payroll.getPF(),
                        payroll.getMedicalAllowance(),
                        payroll.getBonus()
                );

                payroll.setAnnualCTC(ctc);

                emp.setEmpPayroll(payroll);

                // =====================================================
                // EMERGENCY CONTACT
                // =====================================================

                EmergencyContact ec = emp.getEmergency_contact();

                if (ec == null) {
                    ec = new EmergencyContact();
                    ec.setEmployee(emp);
                }

                String ecName = getCellValue(row.getCell(37));

                if (hasValue(ecName)) {
                    ec.setName(ecName);
                }

                String relation = getCellValue(row.getCell(38));

                if (hasValue(relation)) {
                    ec.setRelation(relation);
                }

                Long ecPhone = parseLong(getCellValue(row.getCell(39)));

                if (ecPhone != null) {
                    ec.setPhone(ecPhone);
                }

                emp.setEmergency_contact(ec);

                // =====================================================
                // EDUCATION
                // =====================================================

                Education edu = emp.getEducation();

                if (edu == null) {
                    edu = new Education();
                    edu.setEmployee(emp);
                }

                edu.setEducationLevel(getCellValue(row.getCell(40)));
                edu.setEducationalBoard(getCellValue(row.getCell(41)));
                edu.setSchoolName(getCellValue(row.getCell(42)));
                edu.setPlace(getCellValue(row.getCell(43)));
                edu.setEducationalGroup(getCellValue(row.getCell(44)));
                edu.setSchool_from(getCellValue(row.getCell(45)));
                edu.setSchool_to(getCellValue(row.getCell(46)));

                Double eduPercentage = parseDouble(
                        getCellValue(row.getCell(47))
                );

                if (eduPercentage != null) {
                    edu.setSchool_percentage(eduPercentage);
                }

                if (education != null
                        && education.size() > rowIndex
                        && education.get(rowIndex) != null
                        && !education.get(rowIndex).isEmpty()) {

                    String fileName = saveFile(
                            education.get(rowIndex),
                            "uploadsPdf"
                    );

                    edu.setEducation_pdf(fileName);
                }

                // =====================================================
                // HIGHER EDUCATION
                // =====================================================

                List<HigherEducation> heList =
                        edu.getHigherEducation();

                if (heList == null) {
                    heList = new ArrayList<>();
                }

                HigherEducation he;

                if (!heList.isEmpty()) {
                    he = heList.get(0);
                } else {
                    he = new HigherEducation();
                }

                he.setDegree(getCellValue(row.getCell(48)));
                he.setInstituition(getCellValue(row.getCell(49)));
                he.setSpecialization(getCellValue(row.getCell(50)));
                he.setDegree_from(getCellValue(row.getCell(51)));
                he.setDegree_to(getCellValue(row.getCell(52)));

                Double hePercent = parseDouble(
                        getCellValue(row.getCell(53))
                );

                if (hePercent != null) {
                    he.setPercentage(hePercent);
                }

                he.setCertification(getCellValue(row.getCell(54)));
                he.setCourseType(getCellValue(row.getCell(55)));

                if (higherEducation != null
                        && higherEducation.size() > rowIndex
                        && higherEducation.get(rowIndex) != null
                        && !higherEducation.get(rowIndex).isEmpty()) {

                    String fileName = saveFile(
                            higherEducation.get(rowIndex),
                            "uploadsPdf"
                    );

                    he.setHigherEducation_pdf(fileName);
                }

                he.setEducation(edu);

                if (heList.isEmpty()) {
                    heList.add(he);
                }

                edu.setHigherEducation(heList);

                emp.setEducation(edu);

                // =====================================================
                // EXPERIENCE
                // =====================================================

                List<Experience> expList = emp.getExperience();

                if (expList == null) {
                    expList = new ArrayList<>();
                }

                Experience exp;

                if (!expList.isEmpty()) {
                    exp = expList.get(0);
                } else {
                    exp = new Experience();
                }

                exp.setCompany_name(getCellValue(row.getCell(56)));
                exp.setJob_title(getCellValue(row.getCell(57)));
                exp.setEmp_type_prev(getCellValue(row.getCell(58)));
                exp.setEmp_start(parseDate(row.getCell(59)));
                exp.setEmp_end(parseDate(row.getCell(60)));
                exp.setCurrently_working(getCellValue(row.getCell(61)));
                exp.setDuration(getCellValue(row.getCell(62)));
                exp.setTech_used(getCellValue(row.getCell(63)));
                exp.setRoles_responsibilities(getCellValue(row.getCell(64)));

                if (prevExpLetter != null
                        && prevExpLetter.size() > rowIndex
                        && prevExpLetter.get(rowIndex) != null
                        && !prevExpLetter.get(rowIndex).isEmpty()) {

                    String fileName = saveFile(
                            prevExpLetter.get(rowIndex),
                            "uploadsPdf"
                    );

                    exp.setExp_letter(fileName);
                }

                if (experienceLetter != null
                        && experienceLetter.size() > rowIndex
                        && experienceLetter.get(rowIndex) != null
                        && !experienceLetter.get(rowIndex).isEmpty()) {

                    String fileName = saveFile(
                            experienceLetter.get(rowIndex),
                            "uploadsPdf"
                    );

                    exp.setExp_letter(fileName);
                }

                if (bankStatement != null
                        && bankStatement.size() > rowIndex
                        && bankStatement.get(rowIndex) != null
                        && !bankStatement.get(rowIndex).isEmpty()) {

                    String fileName = saveFile(
                            bankStatement.get(rowIndex),
                            "uploadsPdf"
                    );

                    exp.setBankStatement_pdf(fileName);
                }

                if (salarySlip != null
                        && salarySlip.size() > rowIndex
                        && salarySlip.get(rowIndex) != null
                        && !salarySlip.get(rowIndex).isEmpty()) {

                    String fileName = saveFile(
                            salarySlip.get(rowIndex),
                            "uploadsPdf"
                    );

                    exp.setSalarySlip_pdf(fileName);
                }

                exp.setEmployee(emp);

                if (expList.isEmpty()) {
                    expList.add(exp);
                }

                emp.setExperience(expList);

                // =====================================================
                // SAVE
                // =====================================================

                empRepo.save(emp);

                updatedCount++;

                rowIndex++;
            }

            workbook.close();

            return ResponseEntity.ok(
                    "Excel Update Completed Successfully. Updated: "
                            + updatedCount
                            + ", Skipped: "
                            + skippedCount
            );

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