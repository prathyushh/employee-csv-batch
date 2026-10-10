package com.example.batch.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.example.batch.exception.InvalidCsvFileException;

@Service
public class FileValidationService {
	private static final String FILE_PATH = "input/employees.csv";
	private static final List<String> EXPECTED_HEADERS = List.of("id", "name", "email", "salary");

	public void validateEmployeeCsv() {
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
	}
}