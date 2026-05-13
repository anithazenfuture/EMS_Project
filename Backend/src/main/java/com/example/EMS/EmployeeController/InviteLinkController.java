package com.example.EMS.EmployeeController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.EMS.EmployeeDTO.InviteRequestDTO;
import com.example.EMS.EmployeeService.InviteLinkService;

@RestController
@RequestMapping("/api/invite")
public class InviteLinkController {

	  
	    private InviteLinkService inviteService;
	    
	    

	    public InviteLinkController(InviteLinkService inviteService) {
			this.inviteService = inviteService;
		}



		@PostMapping("/request")
	    public ResponseEntity<?> sendInvite(@RequestBody InviteRequestDTO request) {

	        return ResponseEntity.ok(
	                inviteService.sendInvite(request.getEmail())
	        );
	    }
		
		@GetMapping("/validate")
		public ResponseEntity<?> validateToken(
		        @RequestParam String token) {

		    return ResponseEntity.ok(
		            inviteService.validateToken(token)
		    );
		}
}
