package com.example.EMS.EmployeeRepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.EMS.EmployeeEntity.EmployeeInvite;

public interface EmployeeInviteRepository extends JpaRepository<EmployeeInvite, Long> {
	
	Optional<EmployeeInvite> findByEmail(String email);

}
