package com.example.batch.dto;

import jakarta.persistence.Id;
import lombok.Data;

@Data
public class EmployeeRequestDto {
	@Id
	private Long id;
	private String name;
	private String email;
	private String salary;

}
