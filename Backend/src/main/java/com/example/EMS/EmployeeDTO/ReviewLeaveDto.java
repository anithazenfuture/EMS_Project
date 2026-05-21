package com.example.EMS.EmployeeDTO;

import org.antlr.v4.runtime.misc.NotNull;

import com.example.EMS.enums.LeaveStatus;

import lombok.Data;

@Data
public class ReviewLeaveDto {
    @NotNull 
    private LeaveStatus status;
    private String hrRemarks;
	public LeaveStatus getStatus() {
		return status;
	}
	public void setStatus(LeaveStatus status) {
		this.status = status;
	}
	public String getHrRemarks() {
		return hrRemarks;
	}
	public void setHrRemarks(String hrRemarks) {
		this.hrRemarks = hrRemarks;
	}
    
    
    
    
}