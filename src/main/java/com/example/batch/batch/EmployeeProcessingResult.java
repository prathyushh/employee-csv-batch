package com.example.batch.batch;

import com.example.batch.dto.EmployeeRequestDto;
import com.example.batch.entity.Employee;

public record EmployeeProcessingResult(EmployeeRequestDto input, Employee employee, String errorMessage) {
	public boolean isValid() {
		return employee != null;
	}
}