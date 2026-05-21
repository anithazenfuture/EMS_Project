package com.example.EMS.EmployeeRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.EMS.EmployeeEntity.EmployeeInvoice;

import jakarta.transaction.Transactional;

public interface EmployeeInvoiceRepository extends JpaRepository<EmployeeInvoice, Long> {

	Optional<EmployeeInvoice> findByCompanyName(String name);
	List<EmployeeInvoice> findAllByCustomerId(String id);
	Optional<EmployeeInvoice> findByInvoiceNumber(String num);
	@Transactional
    void deleteByInvoiceNumber(String num);
}
