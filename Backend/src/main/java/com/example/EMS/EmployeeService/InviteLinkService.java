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

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(24);

        InviteLink invite = new InviteLink();
        invite.setEmail(email);
        invite.setToken(token);
        invite.setExpiryTime(expiry);
        repository.save(invite);

        String inviteLink = "http://zenfuture/onboard?token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Complete onboarding process");

            String htmlContent = """
            	    <!DOCTYPE html>
            	    <html lang="en">
            	    <head>
            	      <meta charset="UTF-8"/>
            	      <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
            	    </head>
            	    <body style="margin:0;padding:0;background:#f0ede8;font-family:Arial,Helvetica,sans-serif;">

            	      <table width="100%" cellpadding="0" cellspacing="0" border="0"
            	             style="background:#f0ede8;padding:24px 8px;">
            	        <tr>
            	          <td align="center">

            	            <table cellpadding="0" cellspacing="0" border="0"
            	                   style="width:100%;max-width:600px;background:#ffffff;border-radius:6px;overflow:hidden;">

            	              <!-- HEADER -->
            	              <tr>
            	                <td style="background:#0f1117;padding:36px 32px 32px;">

            	                  <table cellpadding="0" cellspacing="0" border="0" style="margin-bottom:28px;">
            	                    <tr>
            	                      <td style="vertical-align:middle;padding-right:12px;">
            	                        <img src="https://media.licdn.com/dms/image/v2/D560BAQFfFdC-4RZTrw/company-logo_200_200/B56ZlTUi8MKIAI-/0/1758039520031/zenfuture_tech_logo?e=2147483647&v=beta&t=0ZsPdTPbDIBxKfIRcoY8R-dsMDAiBOgbRqT-eAR9iTk"
            	                             alt="Zenfuture" width="40" height="40"
            	                             style="display:block;border-radius:8px;background:#ffffff;padding:3px;"/>
            	                      </td>
            	                      <td style="vertical-align:middle;">
            	                        <span style="font-size:18px;color:#ffffff;font-weight:700;">Zenfuture</span>
            	                      </td>
            	                    </tr>
            	                  </table>

            	                  <div style="display:inline-block;background:rgba(37,99,235,0.25);border:1px solid rgba(37,99,235,0.5);color:#93c5fd;font-size:10px;font-weight:600;letter-spacing:0.12em;text-transform:uppercase;padding:4px 12px;border-radius:100px;margin-bottom:14px;">
            	                    Onboarding
            	                  </div>

            	                  <div style="font-size:32px;color:#ffffff;line-height:1.25;font-weight:700;">
            	                    Welcome<br/>to the <span style="color:#60a5fa;">team.</span>
            	                  </div>

            	                </td>
            	              </tr>

            	              <!-- BODY -->
            	              <tr>
            	                <td style="padding:36px 32px;">

            	                  <p style="font-size:15px;color:#6b7280;line-height:1.75;margin:0 0 28px 0;border-left:3px solid #e5e7eb;padding-left:14px;">
            	                    We're thrilled to have you on board. To get you started on the right foot,
            	                    please complete your onboarding process by clicking the button below.
            	                    It only takes a few minutes.
            	                  </p>

            	                  <!-- CTA block -->
            	                  <table cellpadding="0" cellspacing="0" border="0" width="100%"
            	                         style="background:#f8faff;border:1px solid #dbeafe;border-radius:12px;margin-bottom:24px;">
            	                    <tr>
            	                      <td style="padding:24px 24px 20px 24px;">
            	                        <div style="font-size:16px;font-weight:600;color:#111827;margin-bottom:4px;">
            	                          Complete Your Onboarding
            	                        </div>
            	                        <div style="font-size:13px;color:#9ca3af;margin-bottom:20px;">
            	                          Set up your profile &amp; access
            	                        </div>
            	                        <table cellpadding="0" cellspacing="0" border="0" width="100%">
            	                          <tr>
            	                            <td align="center">
            	                              <a href="INVITE_LINK_PLACEHOLDER" target="_blank"
            	                                 style="display:block;width:100%;background:#2563eb;color:#ffffff;font-size:15px;font-weight:600;text-decoration:none;padding:14px 0;border-radius:8px;text-align:center;">
            	                                Get Started &#8594;
            	                              </a>
            	                            </td>
            	                          </tr>
            	                        </table>
            	                      </td>
            	                    </tr>
            	                  </table>

            	                  <!-- Expiry note -->
            	                  <table cellpadding="0" cellspacing="0" border="0" width="100%"
            	                         style="background:#fffbeb;border:1px solid #fde68a;border-radius:8px;margin-bottom:32px;">
            	                    <tr>
            	                      <td style="padding:10px 14px;font-size:12px;color:#f59e0b;">
            	                        &#9201; This link is valid for <strong>24 hours</strong> from the time it was sent.
            	                      </td>
            	                    </tr>
            	                  </table>

            	                  <!-- Divider -->
            	                  <table cellpadding="0" cellspacing="0" border="0" width="100%" style="margin-bottom:24px;">
            	                    <tr>
            	                      <td style="border-top:1px solid #f3f4f6;font-size:0;line-height:0;">&nbsp;</td>
            	                    </tr>
            	                  </table>

            	                  <!-- Signature label -->
            	                  <div style="font-size:11px;font-weight:600;letter-spacing:0.1em;text-transform:uppercase;color:#9ca3af;margin-bottom:14px;">
            	                    Your point of contact
            	                  </div>

            	                  <!-- Sig card -->
            	                  <table cellpadding="0" cellspacing="0" border="0">
            	                    <tr>
            	                      <td style="vertical-align:top;padding-right:14px;">
            	                        <div style="width:44px;height:44px;border-radius:10px;background:#2563eb;text-align:center;line-height:44px;font-size:18px;color:#ffffff;font-weight:700;">
            	                          Z
            	                        </div>
            	                      </td>
            	                      <td style="vertical-align:top;">
            	                        <div style="font-size:14px;font-weight:600;color:#111827;margin-bottom:2px;">
            	                          HR &#8211; Talent Acquisition Group
            	                        </div>
            	                        <div style="font-size:12px;color:#6b7280;margin-bottom:10px;">
            	                          Zenfuture Tech
            	                        </div>
            	                        <div style="font-size:12px;color:#4b5563;line-height:2.2;">
            	                          &#128222;&nbsp;+91-9092979396<br/>
            	                          &#128231;&nbsp;<a href="mailto:tag@zenfuture.in"
            	                                           target="_blank"
            	                                           style="color:#4b5563;text-decoration:none;">tag@zenfuture.in</a><br/>
            	                          &#127760;&nbsp;<a href="https://www.zenfuture.in"
            	                                           target="_blank"
            	                                           style="color:#4b5563;text-decoration:none;">www.zenfuture.in</a>
            	                        </div>
            	                        <div style="font-size:11px;color:#9ca3af;margin-top:10px;line-height:1.7;">
            	                          No. 3/313A, First Floor, Krishnagiri Main Road<br/>
            	                          Dharmapuri &#8211; 636701, Tamil Nadu, India
            	                        </div>
            	                      </td>
            	                    </tr>
            	                  </table>

            	                </td>
            	              </tr>

            	              <!-- FOOTER -->
            	              <tr>
            	                <td style="background:#f9fafb;border-top:1px solid #f3f4f6;padding:14px 32px;">
            	                  <table cellpadding="0" cellspacing="0" border="0" width="100%">
            	                    <tr>
            	                      <td style="font-size:11px;color:#9ca3af;">
            	                        &#169; 2025 Zenfuture Tech. All rights reserved.
            	                      </td>
            	                      <td align="right" style="font-size:11px;">
            	                        <a href="https://www.zenfuture.in"
            	                           target="_blank"
            	                           style="color:#2563eb;text-decoration:none;">www.zenfuture.in</a>
            	                      </td>
            	                    </tr>
            	                  </table>
            	                </td>
            	              </tr>

            	            </table>

            	          </td>
            	        </tr>
            	      </table>

            	    </body>
            	    </html>
            	""".replace("INVITE_LINK_PLACEHOLDER", inviteLink);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            return "Invite sent successfully";

        } catch (Exception e) {
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
