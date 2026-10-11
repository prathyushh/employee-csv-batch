package com.example.batch.batch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;
import com.example.batch.dto.EmployeeRequestDto;
import com.example.batch.entity.Employee;
import com.example.batch.exception.InvalidEmployeeException;

@Component
public class EmployeeItemProcessor implements ItemProcessor<EmployeeRequestDto, Employee> {
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	@Override
	public Employee process(EmployeeRequestDto input) {
		/*
		 * // simulate a failure at employee ID 15 if (input.getId() != null &&
		 * input.getId() == 15L) { throw new
		 * RuntimeException("Simulated failure for restart testing"); }
		 */
		List<String> errors = new ArrayList<>();
		if (input.getName() == null || input.getName().isBlank()) {
			errors.add("Name is mandatory");
		}
		if (input.getId() == null) {
		    errors.add("Employee ID is mandatory");
		}
		if (input.getEmail() == null || input.getEmail().isBlank()) {
			errors.add("Email is mandatory");
		} else if (!EMAIL_PATTERN.matcher(input.getEmail()).matches()) {
			errors.add("Invalid email format");
		}
		BigDecimal salary;
		try {
			salary = new BigDecimal(input.getSalary());
			if (salary.compareTo(BigDecimal.ZERO) <= 0) {
				errors.add("Salary must be positive");
			}
		} catch (NumberFormatException | NullPointerException e) {
			errors.add("Salary must be a valid number");
		}
		if (!errors.isEmpty()) {
			throw new InvalidEmployeeException(String.join(", ", errors));
		}
		return Employee.builder().id(input.getId()).name(input.getName()).email(input.getEmail())
				.salary(new BigDecimal(input.getSalary())).build();
	}
}