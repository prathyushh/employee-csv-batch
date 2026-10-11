package com.example.batch.batch;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.batch.dto.EmployeeRequestDto;
import com.example.batch.entity.Employee;
import com.example.batch.exception.InvalidEmployeeException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmployeeAsyncDelegateProcessor implements ItemProcessor<EmployeeRequestDto, EmployeeProcessingResult> {
	private final EmployeeItemProcessor employeeItemProcessor;

	@Override
	public EmployeeProcessingResult process(EmployeeRequestDto input) {
		try {
			Employee employee = employeeItemProcessor.process(input);
			return new EmployeeProcessingResult(input, employee, null);
		} catch (InvalidEmployeeException ex) {
			return new EmployeeProcessingResult(input, null, ex.getMessage());
		}
	}
}