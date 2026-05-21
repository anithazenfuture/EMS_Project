package com.example.EMS.EmployeeEntity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class HigherEducation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String degree;
	private String instituition;
	private String specialization;
	private String degree_from;
	private String degree_to;
	private double percentage;
	private String certification;
	private String courseType;
	private String higherEducation_pdf;
	
	@ManyToOne
	@JoinColumn(name="education_id")
	private Education education;
	
	public String getDegree() {
		return degree;
	}
	public void setDegree(String degree) {
		this.degree = degree;
	}
	public String getInstituition() {
		return instituition;
	}
	public void setInstituition(String instituition) {
		this.instituition = instituition;
	}
	public String getSpecialization() {
		return specialization;
	}
	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}
	public String getDegree_from() {
		return degree_from;
	}
	public void setDegree_from(String degree_from) {
		this.degree_from = degree_from;
	}
	public String getDegree_to() {
		return degree_to;
	}
	public void setDegree_to(String degree_to) {
		this.degree_to = degree_to;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	public String getCertification() {
		return certification;
	}
	public void setCertification(String certification) {
		this.certification = certification;
	}
	public String getCourseType() {
		return courseType;
	}
	public void setCourseType(String courseType) {
		this.courseType = courseType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getHigherEducation_pdf() {
		return higherEducation_pdf;
	}
	public void setHigherEducation_pdf(String higherEducation_pdf) {
		this.higherEducation_pdf = higherEducation_pdf;
	}
	public Education getEducation() {
		return education;
	}
	public void setEducation(Education education) {
		this.education = education;
	}
	
	
	

}
