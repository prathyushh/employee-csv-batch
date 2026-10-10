package com.example.batch.batch;

import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import com.example.batch.dto.EmployeeRequestDto;

@Configuration
public class EmployeeItemReader {
	@Bean
	public FlatFileItemReader<EmployeeRequestDto> employeeReader() {
		return new FlatFileItemReaderBuilder<EmployeeRequestDto>().name("EmployeeReader")
				.resource(new ClassPathResource("input/employees.csv")).linesToSkip(1).delimited()
				.names("id", "name", "email", "salary").targetType(EmployeeRequestDto.class).build();
	}
}