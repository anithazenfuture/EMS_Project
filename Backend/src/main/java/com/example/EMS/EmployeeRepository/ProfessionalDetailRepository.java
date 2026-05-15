package com.example.EMS.EmployeeRepository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.example.EMS.EmployeeEntity.ProfessionalDetails;

public interface ProfessionalDetailRepository 
        extends JpaRepository<ProfessionalDetails, Long> {

    ProfessionalDetails findByEmployeeEmployeeId(String employeeId);

}