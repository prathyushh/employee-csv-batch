package com.example.batch.batch;

import org.springframework.batch.core.listener.SkipListener;
import org.springframework.stereotype.Component;
import com.example.batch.dto.EmployeeRequestDto;
import com.example.batch.entity.Employee;
import com.example.batch.entity.ErrorTable;
import com.example.batch.repository.ErrorTableRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmployeeSkipListener implements SkipListener<EmployeeRequestDto, Employee> {
	private final ErrorTableRepository errorTableRepository;

	@Override
	public void onSkipInProcess(EmployeeRequestDto item, Throwable throwable) {
		ErrorTable errorRecord = new ErrorTable(null, item.getId(), item.getName(), item.getEmail(), item.getSalary(),
				throwable.getMessage());
		errorTableRepository.save(errorRecord);
	}
}