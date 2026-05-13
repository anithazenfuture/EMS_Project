package com.example.EMS.EmployeeService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.EMS.EmployeeDTO.AttendanceRequestDTO;
import com.example.EMS.EmployeeEntity.Attendance;
import com.example.EMS.EmployeeEntity.Employee;
import com.example.EMS.EmployeeRepository.AttendanceRepository;

import jakarta.transaction.Transactional;

@Service
public class AttendanceService {
	
	private AttendanceRepository attendanceRepo;
	
	
	
	public AttendanceService(AttendanceRepository attendanceRepo) {
		this.attendanceRepo = attendanceRepo;
	}



	public ResponseEntity<?> registerService(Employee emp, LocalTime checkIn,LocalTime checkOut){
        LocalDate today = LocalDate.now();

  
        boolean alreadyExists =attendanceRepo.findByEmployee_EmployeeIdAndAttendanceDate(emp.getEmployeeId(),today).isPresent();

        if (alreadyExists) {

            return ResponseEntity.badRequest()
                    .body("Attendance already marked today");
        }

        Attendance attendance = new Attendance();

        attendance.setEmployee(emp);

        attendance.setEmpName(
                emp.getFirst_name() + " "
                + emp.getLast_name());

        attendance.setDepartment(
                emp.getProfessional_details()
                   .getProfessional_department());

        attendance.setDesignation(
                emp.getProfessional_details().getProfessional_designation());

        attendance.setAttendanceDate(today);

        attendance.setCheckIn(checkIn);

        attendance.setCheckOut(checkOut);

        attendance.setStatus("Present");

       
        if (checkIn != null && checkOut != null) {

            Duration duration =
                    Duration.between(checkIn, checkOut);

            attendance.setTotalWorkingHours(
                    duration.toHours());
        }

        attendanceRepo.save(attendance);

        return ResponseEntity.ok(
                "Attendance Registered Successfully");
		
		
	}
	
	public ResponseEntity<?> getAllAttendance() {
        List<Attendance> attendanceList = attendanceRepo.findAll();

        if(attendanceList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No attendance records found");
        }

        return ResponseEntity.ok(attendanceList);
    }


   
    public ResponseEntity<?> getAttendanceById(String empId) {

    	Optional<List<Attendance>> attendance = attendanceRepo.findAllByEmployee_EmployeeId(empId);

        if(attendance.isEmpty()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Attendance not found with id : " + empId);
        }

        return ResponseEntity.ok(attendance.get());
    }


   
    @Transactional
    public ResponseEntity<?> deleteAttendanceById(String empId) {

    	Optional<List<Attendance>> attendance = attendanceRepo.findAllByEmployee_EmployeeId(empId);

        if(attendance.isEmpty()) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Attendance not found with id : " + empId);
        }

        attendanceRepo. deleteByEmployee_EmployeeId(empId);

        return ResponseEntity.ok(
                "Attendance deleted successfully");
    }

    
    public ResponseEntity<?> updateAttendance(
            String empId,
            AttendanceRequestDTO request){

        LocalDate today = LocalDate.now();

        Optional<Attendance> attendanceOpt =
                attendanceRepo
                .findByEmployee_EmployeeIdAndAttendanceDate(
                        empId,
                        today);

        if(attendanceOpt.isEmpty()){

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Attendance not found");
        }

        Attendance attendance = attendanceOpt.get();

        
      
        if(request.getCheckIn() != null){

            attendance.setCheckIn(
                    request.getCheckIn());
        }

        
        
        if(request.getCheckOut() != null){

            attendance.setCheckOut(
                    request.getCheckOut());
        }

        
       
        if(attendance.getCheckIn() != null
                && attendance.getCheckOut() != null){

            Duration duration =
                    Duration.between(
                            attendance.getCheckIn(),
                            attendance.getCheckOut());

            attendance.setTotalWorkingHours(
                    duration.toHours());
        }

        
        
        if(request.getStatus() != null){

            attendance.setStatus(
                    request.getStatus());
        }

        attendanceRepo.save(attendance);

        return ResponseEntity.ok(
                "Attendance updated successfully");
    }
	
	
	
	
	
	
	
	
	
	
	
}
