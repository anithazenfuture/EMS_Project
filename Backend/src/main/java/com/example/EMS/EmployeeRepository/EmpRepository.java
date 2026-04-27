package com.example.EMS.EmployeeRepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.EMS.EmployeeEntity.Employee;
import com.example.EMS.EmployeeEntity.User;

public interface EmpRepository extends JpaRepository<Employee, Long>{
	Optional<Employee> findByEmail(String email);
	
	

}
