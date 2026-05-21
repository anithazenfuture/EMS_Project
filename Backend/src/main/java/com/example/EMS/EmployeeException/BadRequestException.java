package com.example.EMS.EmployeeException;



public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}