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
import com.example.EMS.EmployeeEntity.EmployeeInvite;
import com.example.EMS.EmployeeEntity.Experience;
import com.example.EMS.EmployeeEntity.ProfessionalDetails;
import com.example.EMS.EmployeeRepository.EmployeeInviteRepository;

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
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please enter email address...");
		}
		
		Optional<EmployeeInvite> existingUser =
		        empInviteRepo.findByEmail(empInvite.getEmail());

		


		if(existingUser.isPresent()) {
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already submitted response");
		    

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
}
