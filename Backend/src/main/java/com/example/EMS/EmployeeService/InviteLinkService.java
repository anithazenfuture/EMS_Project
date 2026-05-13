package com.example.EMS.EmployeeService;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.EMS.EmployeeEntity.InviteLink;
import com.example.EMS.EmployeeRepository.InviteLinkRepository;



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

        
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("Employee Onboarding Invite");

        message.setText(
                "Click below link to complete onboarding:\n\n"
                        + inviteLink
                        + "\n\nThis link expires in 24 hours."
        );

        mailSender.send(message);

        return "Invite sent successfully";
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

	        repository.deleteByExpiryTimeBefore(
	                LocalDateTime.now());

	        System.out.println("Expired invites deleted");
	    }

    
	
}
