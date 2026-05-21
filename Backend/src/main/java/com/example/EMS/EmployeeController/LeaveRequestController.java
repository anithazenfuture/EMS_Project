package com.example.EMS.EmployeeController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.EMS.EmployeeDTO.ReviewLeaveDto;
import com.example.EMS.EmployeeEntity.LeaveRequest;
import com.example.EMS.EmployeeService.LeaveRequestService;

@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {
	
	private final LeaveRequestService requestService;
	

	public LeaveRequestController(LeaveRequestService requestService) {
		this.requestService = requestService;
	}


	@PostMapping("/apply/{empId}")
	public ResponseEntity<?> applyLeave(@PathVariable Long empId, @RequestBody LeaveRequest request){
		if(request.getStartDate() == null || request.getEndDate() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide start date and end date");
		}
		LeaveRequest req = requestService.applyLeave(empId, request);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(req);
	}
	
	@PostMapping("/review/{empId}/{leaveId}")
	 public ResponseEntity<?> reviewLeave(@PathVariable Long empId,@PathVariable Long leaveId,@RequestBody ReviewLeaveDto dto) {
		
		if(empId == null || leaveId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide employee id and leave id");
		}
		
		return requestService.reviewLeave(empId, leaveId, dto);
	}
}
