package com.example.EMS.EmployeeService;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.EMS.EmployeeEntity.InviteLink;
import com.example.EMS.EmployeeRepository.InviteLinkRepository;

import jakarta.mail.internet.MimeMessage;



@Service
public class InviteLinkService {

    @Autowired
    private InviteLinkRepository repository;

    @Autowired
    private JavaMailSender mailSender;
    
    

    public InviteLinkService(InviteLinkRepository repository, JavaMailSender mailSender) {
		this.repository = repository;
		this.mailSender = mailSender;
	}



	public String sendInvite(String email) {
         
        // Generate unique token
        String token = UUID.randomUUID().toString();

        // Expiry after 24 hours
        LocalDateTime expiry = LocalDateTime.now().plusHours(24);

        InviteLink invite = new InviteLink();
        
        invite.setEmail(email);
        invite.setToken(token);
        invite.setExpiryTime(expiry);

        repository.save(invite);

        // Frontend invite URL
        String inviteLink =
                "http://zenfuture/onboard?token=" + token;

        
       // SimpleMailMessage message = new SimpleMailMessage();
       
        try {
        	 MimeMessage message =  mailSender.createMimeMessage();
        	 MimeMessageHelper helper = new MimeMessageHelper(message, true);
        	 
        	 helper.setTo(email);
        	 helper.setSubject("Complete onboarding process");
        	 
        	 String htmlContent = """
					        	            <html>
					<body style="font-family: Arial; margin:0; padding:0;">
					
					    <!-- Header -->
					    <div style="
					        background-color:#f4f4f4;
					        padding:20px;
					        text-align:center;
					    ">
					
					        <img 
					            src="https://media.licdn.com/dms/image/v2/D560BAQFfFdC-4RZTrw/company-logo_200_200/B56ZlTUi8MKIAI-/0/1758039520031/zenfuture_tech_logo?e=2147483647&v=beta&t=0ZsPdTPbDIBxKfIRcoY8R-dsMDAiBOgbRqT-eAR9iTk"
					            alt="Zenfuture Logo"
					            style="
					                width:120px;
					                height:auto;
					                margin-bottom:10px;
					            "
					        />
					
					        <h2 style="margin:0; color:#111827;">
					            Welcome to Zenfuture
					        </h2>
					
					    </div>
					
					    <!-- Body -->
					    <div style="padding:30px;">
					
					        <p>
					            Click the button below to complete
					            your onboarding process.
					        </p>
					
					        <a href="%s"
					           style="
					              background-color:#2563eb;
					              color:white;
					              padding:12px 20px;
					              text-decoration:none;
					              border-radius:6px;
					              display:inline-block;
					           ">
					            Complete Onboarding
					        </a>
					
					        <p style="margin-top:20px;">
					            This link expires in 24 hours.
					        </p>
					
					    </div>
					
					</body>
					</html>
        	            """.formatted(inviteLink);
        	 

             helper.setText(htmlContent, true);
             mailSender.send(message);
             return "Invite sent successfully";
        	 
        }
        catch (Exception e) {

            e.printStackTrace();

            return "Failed to send invite";
        }
       
    }
	
	public ResponseEntity<?> getInviteDetails(){
		
		List<InviteLink> lst = repository.findAll();
		if(lst.size() == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invite details not found");
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(lst);
		
	}
	
	
	public String validateToken(String token) {

        Optional<InviteLink> optionalInvite =
                repository.findByToken(token);

        if(optionalInvite.isEmpty()) {
            return "Invalid Token";
        }

        InviteLink invite = optionalInvite.get();

        if(invite.isUsed()) {
            return "Link already used";
        }

        if(invite.getExpiryTime()
                .isBefore(LocalDateTime.now())) {

            return "Link expired";
        }

        return "Valid Link";
    }

	
	    @Scheduled(cron = "0 0 * * * *")
	    public void removeExpiredInvites() {
	    	System.out.println("Scheduler triggered at: " + LocalDateTime.now());
	        repository.deleteByExpiryTimeBefore(
	                LocalDateTime.now());

	        System.out.println("Expired invites deleted");
	    }

    
	
}
