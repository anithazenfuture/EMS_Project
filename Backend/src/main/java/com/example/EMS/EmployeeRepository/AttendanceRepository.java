package com.example.EMS.EmployeeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.EMS.EmployeeEntity.Attendance;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    Optional<Attendance>
    findByEmployee_EmployeeIdAndAttendanceDate(
            String employeeId,
            LocalDate attendanceDate);

    Optional<List<Attendance>> findAllByEmployee_EmployeeId(String employeeId);

    void deleteByEmployee_EmployeeId(String employeeId);
}