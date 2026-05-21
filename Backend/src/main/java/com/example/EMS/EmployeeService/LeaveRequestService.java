package com.example.EMS.EmployeeService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.EMS.EmployeeDTO.ReviewLeaveDto;
import com.example.EMS.EmployeeEntity.Employee;
import com.example.EMS.EmployeeEntity.LeaveBalance;
import com.example.EMS.EmployeeEntity.LeaveRequest;
import com.example.EMS.EmployeeException.BadRequestException;
import com.example.EMS.EmployeeException.ResourceNotFoundException;
import com.example.EMS.EmployeeRepository.EmpRepository;
import com.example.EMS.EmployeeRepository.LeaveBalanceRepository;
import com.example.EMS.EmployeeRepository.LeaveRequestRepository;
import com.example.EMS.enums.LeaveStatus;
import com.example.EMS.enums.Role;

import jakarta.transaction.Transactional;

@Service
public class LeaveRequestService {
	
	private final EmpRepository empRepository;
	private final LeaveBalanceRepository leaveBalanceRepository;
	private final LeaveRequestRepository leaveRequestRepository;
		
	public LeaveRequestService(EmpRepository empRepository, LeaveBalanceRepository leaveBalanceRepository, LeaveRequestRepository leaveRequestRepository) {
		this.empRepository = empRepository;
		this.leaveBalanceRepository = leaveBalanceRepository;
		this.leaveRequestRepository = leaveRequestRepository;
	}


	public LeaveRequest applyLeave(Long empId, LeaveRequest request){
		Employee emp = getUserByEmployeeId(empId);
		
		 if (request.getStartDate().isBefore(LocalDate.now()))
	            throw new BadRequestException("Start date cannot be in the past");
		 
        if (request.getEndDate().isBefore(request.getStartDate()))
            throw new BadRequestException("End date must be on or after start date");
        
        int workingDays = countWorkingDays(request.getStartDate(), request.getEndDate());
        int year = request.getStartDate().getYear();
        

        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndYear(emp.getId(), year);
        
        if (balance.getRemainingDays() < workingDays)
            throw new BadRequestException("Insufficient balance. Available: "
                    + balance.getRemainingDays() + ", Requested: " + workingDays);
        
        
        request.setEmployee(emp);
        return leaveRequestRepository.save(request);
        
	      	
		
	}
	
	 
	    @Transactional
	    public ResponseEntity<?> reviewLeave(Long empId, Long leaveId, ReviewLeaveDto dto) {   
	        
	        Employee emp = getUserByEmployeeId(empId);
	        LeaveRequest req = getLeaveById(leaveId);
	        
	        
	        if(emp.getRole() != Role.MANAGER && emp.getRole() != Role.HR) {
	        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not authorized to review leave requests");
	        }
	        

	        if (req.getStatus() == LeaveStatus.CANCELLED)
	            throw new BadRequestException("Cannot review a cancelled request");
	        if (req.getStatus() == LeaveStatus.APPROVED || req.getStatus() == LeaveStatus.REJECTED)
	            throw new BadRequestException("Request has already been reviewed");
	        if (dto.getStatus() != LeaveStatus.APPROVED && dto.getStatus() != LeaveStatus.REJECTED)
	            throw new BadRequestException("Status must be APPROVED or REJECTED");
	        
	        if (dto.getStatus() == LeaveStatus.APPROVED) {
	            LeaveBalance balance = leaveBalanceRepository
	                    .findByEmployeeIdAndYear(
	                            req.getEmployee().getId(),
	                            req.getStartDate().getYear());
	               

	            if (balance.getRemainingDays() < req.getTotalDays())
	                throw new BadRequestException("Insufficient balance to approve");

	            balance.setUsedDays(balance.getUsedDays() + req.getTotalDays());
	            leaveBalanceRepository.save(balance);
	        }
	         
	        req.setStatus(dto.getStatus());
	        req.setHrRemarks(dto.getHrRemarks());
	        req.setReviewedBy(emp);
	        req.setReviewedAt(LocalDateTime.now());

	        LeaveRequest res = leaveRequestRepository.save(req);
	        return ResponseEntity.ok(res);
	    }
	       
	 private LeaveRequest getLeaveById(Long id) {
	        return leaveRequestRepository.findById(id)
	                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + id));
	    }
	
	private int countWorkingDays(LocalDate start, LocalDate end) {
        int count = 0;
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1))
            if (d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY)
                count++;
        return count;
    }
	
	
	 private Employee getUserByEmployeeId(Long empId) {
	        return empRepository.findById(empId)
	                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + empId));
	    }

}
