package com.example.EMS.EmployeeEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.EMS.enums.LeaveStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="employee_id")
	@JsonBackReference
	private Employee employee;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer totalDays;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private LeaveStatus status;
	private String reason;
	private String  hrRemarks;
	
	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="reviwed_by")
	private Employee reviewedBy;
	
	private LocalDateTime reviewedAt;
	
	 @CreationTimestamp 
	 private LocalDateTime createdAt;

	 public Long getId() {
		 return id;
	 }

	 public void setId(Long id) {
		 this.id = id;
	 }

	 public Employee getEmployee() {
		 return employee;
	 }

	 public void setEmployee(Employee employee) {
		 this.employee = employee;
	 }

	 public LocalDate getStartDate() {
		 return startDate;
	 }

	 public void setStartDate(LocalDate startDate) {
		 this.startDate = startDate;
	 }

	 public LocalDate getEndDate() {
		 return endDate;
	 }

	 public void setEndDate(LocalDate endDate) {
		 this.endDate = endDate;
	 }

	 public Integer getTotalDays() {
		 return totalDays;
	 }

	 public void setTotalDays(Integer totalDays) {
		 this.totalDays = totalDays;
	 }

	 public String getReason() {
		 return reason;
	 }

	 public void setReason(String reason) {
		 this.reason = reason;
	 }

	 public String getHrRemarks() {
		 return hrRemarks;
	 }

	 public void setHrRemarks(String hrRemarks) {
		 this.hrRemarks = hrRemarks;
	 }

	 public Employee getReviewedBy() {
		 return reviewedBy;
	 }

	 public void setReviewedBy(Employee reviewedBy) {
		 this.reviewedBy = reviewedBy;
	 }

	 public LocalDateTime getCreatedAt() {
		 return createdAt;
	 }

	 public void setCreatedAt(LocalDateTime createdAt) {
		 this.createdAt = createdAt;
	 }

	 public LeaveStatus getStatus() {
		 return status;
	 }

	 public void setStatus(LeaveStatus status) {
		 this.status = status;
	 }

	 public LocalDateTime getReviewedAt() {
		 return reviewedAt;
	 }

	 public void setReviewedAt(LocalDateTime reviewedAt) {
		 this.reviewedAt = reviewedAt;
	 }
	 
	 
	
}
