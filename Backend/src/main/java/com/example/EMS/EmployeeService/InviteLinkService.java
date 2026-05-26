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

//      String inviteLink = "http://zenfuture/onboard?token=" + token;
        String inviteLink =
                "http://localhost:3000/employeeinvite";

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
             style="background:#f0ede8;padding:16px 8px;">
        <tr>
          <td align="center">

            <table cellpadding="0" cellspacing="0" border="0"
                   style="width:100%;max-width:600px;background:#ffffff;border-radius:6px;overflow:hidden;">

              <!-- HEADER -->
              <tr>
                <td style="background:#0f1117;padding:24px 20px;">

                  <!-- Logo + Brand + Badge: stacked on small, row on large -->
                  <table cellpadding="0" cellspacing="0" border="0" width="100%"
                         style="margin-bottom:24px;">
                    <tr>
                      <!-- Logo -->
                      <td style="vertical-align:middle;width:60px;padding-right:10px;">
                        <img src="https://zenfuture.in/assets/images/logo.png"
                             alt="Zenfuture" width="60"
                             style="display:block;width:60px;border-radius:5px; padding:10px; background-color: white;"/>
                      </td>

                      <!-- Brand -->
                      <td style="vertical-align:middle;">
                        <div style="font-size:16px;color:#ffffff;font-weight:700;line-height:1.2;">
                          Zenfuture Technologies
                        </div>
                        <div style="font-size:10px;color:#6b7280;margin-top:3px;letter-spacing:0.05em;text-transform:uppercase;">
                          HR &bull; Talent Acquisition
                        </div>
                      </td>

                      <!-- Badge -->
                      <td align="right" style="vertical-align:middle;">
                        <table cellpadding="0" cellspacing="0" border="0">
                          <tr>
                            <td style="background:#1e3a8a;border:1px solid #3b82f6;border-radius:100px;padding:4px 10px;">
                              <span style="font-size:9px;font-weight:700;color:#93c5fd;letter-spacing:0.1em;text-transform:uppercase;white-space:nowrap;">
                                Onboarding
                              </span>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>

                  <!-- Divider -->
                  <table cellpadding="0" cellspacing="0" border="0" width="100%"
                         style="margin-bottom:20px;">
                    <tr>
                      <td style="border-top:1px solid #1f2937;font-size:0;line-height:0;">&nbsp;</td>
                    </tr>
                  </table>

                  <!-- Eyebrow -->
                  <div style="font-size:11px;color:#60a5fa;font-weight:600;letter-spacing:0.12em;text-transform:uppercase;margin-bottom:8px;">
                    &#127881;&nbsp;&nbsp;You're In!
                  </div>

                  <!-- Headline -->
                  <div style="font-size:26px;color:#ffffff;line-height:1.25;font-weight:700;margin-bottom:8px;">
                    Welcome to the<br/>
                    <span style="color:#60a5fa;">Zenfuture Team.</span>
                  </div>

                  <!-- Subheadline -->
                  <div style="font-size:13px;color:#6b7280;line-height:1.6;">
                    We're excited to have you with us.<br/>
                    Let's get you set up and ready to go.
                  </div>

                </td>
              </tr>

              <!-- BODY -->
              <tr>
                <td style="padding:28px 20px;">

                  <!-- Greeting -->
                  <p style="font-size:14px;color:#6b7280;line-height:1.75;margin:0 0 24px 0;border-left:3px solid #e5e7eb;padding-left:12px;">
                    We're thrilled to have you on board. To get you started on the right foot,
                    please complete your onboarding process by clicking the button below.
                    It only takes a few minutes.
                  </p>

                  <!-- CTA block -->
                  <table cellpadding="0" cellspacing="0" border="0" width="100%"
                         style="background:#f8faff;border:1px solid #dbeafe;border-radius:12px;margin-bottom:20px;">
                    <tr>
                      <td style="padding:20px;">
                        <div style="font-size:15px;font-weight:600;color:#111827;margin-bottom:4px;">
                          Complete Your Onboarding
                        </div>
                        <div style="font-size:12px;color:#9ca3af;margin-bottom:16px;">
                          Set up your profile &amp; access
                        </div>
                        <table cellpadding="0" cellspacing="0" border="0" width="100%">
                          <tr>
                            <td>
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
                         style="background:#fffbeb;border:1px solid #fde68a;border-radius:8px;margin-bottom:28px;">
                    <tr>
                      <td style="padding:10px 14px;font-size:12px;color:#f59e0b;">
                        &#9201; This link is valid for <strong>24 hours</strong> from the time it was sent.
                      </td>
                    </tr>
                  </table>

                  <!-- Divider -->
                  <table cellpadding="0" cellspacing="0" border="0" width="100%"
                         style="margin-bottom:20px;">
                    <tr>
                      <td style="border-top:1px solid #f3f4f6;font-size:0;line-height:0;">&nbsp;</td>
                    </tr>
                  </table>

                  <!-- Signature label -->
                  <div style="font-size:11px;font-weight:600;letter-spacing:0.1em;text-transform:uppercase;color:#9ca3af;margin-bottom:14px;">
                    Your point of contact
                  </div>

                  <!-- Sig card -->
                  <table cellpadding="0" cellspacing="0" border="0" width="100%">
                    <tr>
                      <!-- Avatar -->
                      <td style="vertical-align:top;width:52px;padding-right:12px;">
                        <table cellpadding="0" cellspacing="0" border="0">
                          <tr>
                            <td style="background:#2563eb;border-radius:10px;width:44px;height:44px;text-align:center;vertical-align:middle;font-size:18px;color:#ffffff;font-weight:700;line-height:44px;">
                              Z
                            </td>
                          </tr>
                        </table>
                      </td>

                      <!-- Info -->
                      <td style="vertical-align:top;">
                        <div style="font-size:14px;font-weight:600;color:#111827;margin-bottom:2px;">
                          HR &#8211; Talent Acquisition Group
                        </div>
                        <div style="font-size:12px;color:#6b7280;margin-bottom:8px;">
                          Zenfuture Technologies
                        </div>
                        <table cellpadding="0" cellspacing="0" border="0">
                          <tr>
                            <td style="font-size:12px;color:#4b5563;line-height:2.0;">
                              &#128222;&nbsp;+91-9092979396<br/>
                              &#128231;&nbsp;<a href="mailto:tag@zenfuture.in"
                                               target="_blank"
                                               style="color:#4b5563;text-decoration:none;">tag@zenfuture.in</a><br/>
                              &#127760;&nbsp;<a href="https://www.zenfuture.in"
                                               target="_blank"
                                               style="color:#4b5563;text-decoration:none;">www.zenfuture.in</a>
                            </td>
                          </tr>
                        </table>
                        <div style="font-size:11px;color:#9ca3af;margin-top:8px;line-height:1.7;">
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
                <td style="background:#f9fafb;border-top:1px solid #f3f4f6;padding:12px 20px;">
                  <table cellpadding="0" cellspacing="0" border="0" width="100%">
                    <tr>
                      <td style="font-size:11px;color:#9ca3af;">
                        &#169; 2025 Zenfuture Technologies. All rights reserved.
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
