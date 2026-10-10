package com.example.batch.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import com.example.batch.exception.InvalidCsvFileException;

@Component
public class FileValidationTasklet implements Tasklet {
	private static final String FILE_PATH = "input/employees.csv";
	private static final List<String> EXPECTED_HEADERS = List.of("id", "name", "email", "salary");

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		ClassPathResource resource = new ClassPathResource(FILE_PATH);
		if (!resource.exists()) {
			throw new InvalidCsvFileException("CSV file not found.");
		}
		if (!FILE_PATH.endsWith(".csv")) {
			throw new InvalidCsvFileException("File must be a CSV.");
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
			String header = reader.readLine();
			if (header == null || header.isBlank()) {
				throw new InvalidCsvFileException("CSV file is empty.");
			}
			List<String> actualHeaders = List.of(header.split(","));
			if (!EXPECTED_HEADERS.equals(actualHeaders)) {
				throw new InvalidCsvFileException("Invalid CSV header.");
			}
		} catch (IOException e) {
			throw new InvalidCsvFileException("Unable to read CSV file.");
		}
		return RepeatStatus.FINISHED;
	}
}
