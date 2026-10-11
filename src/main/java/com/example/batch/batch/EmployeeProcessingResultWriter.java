package com.example.batch.batch;

import java.util.ArrayList;
import java.util.List;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;
import com.example.batch.entity.Employee;
import com.example.batch.entity.ErrorTable;
import com.example.batch.repository.ErrorTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeProcessingResultWriter implements ItemWriter<EmployeeProcessingResult> {
	private final EmployeeItemWriter employeeItemWriter;
	private final ErrorTableRepository errorTableRepository;

	@Override
	public void write(Chunk<? extends EmployeeProcessingResult> chunk) throws Exception {
		List<Employee> validEmployees = new ArrayList<>();
		for (EmployeeProcessingResult result : chunk.getItems()) {
			if (result.isValid()) {
				validEmployees.add(result.employee());
			} else {
				var input = result.input();
				ErrorTable errorRecord = new ErrorTable(null, input.getId(), input.getName(), input.getEmail(),
						input.getSalary(), result.errorMessage());
				errorTableRepository.save(errorRecord);
				log.warn("Invalid employee ID {}: {}", input.getId(), result.errorMessage());
			}
		}
		if (!validEmployees.isEmpty()) {
			employeeItemWriter.write(new Chunk<>(validEmployees));
		}
	}
}