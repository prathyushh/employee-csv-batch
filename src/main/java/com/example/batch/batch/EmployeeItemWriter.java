package com.example.batch.batch;

import com.example.batch.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EmployeeItemWriter implements ItemWriter<Employee> {
	private static final Logger log = LoggerFactory.getLogger(EmployeeItemWriter.class);
	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final JdbcBatchItemWriter<Employee> jdbcEmployeeWriter;

	@Override
	public void write(Chunk<? extends Employee> chunk) throws Exception {
		if (chunk.isEmpty()) {
			return;
		}
		// Step 1: Collect employee IDs and normalized emails.
		Set<Long> ids = new HashSet<>();
		Set<String> emails = new HashSet<>();
		for (Employee employee : chunk.getItems()) {
			ids.add(employee.getId());
			emails.add(normalizeEmail(employee.getEmail()));
		}
		// Step 2: Find employees that already exist in the database.
		String lookupSql = """
				SELECT id, email
				FROM employees
				WHERE id IN (:ids)
				   OR LOWER(email) IN (:emails)
				""";
		MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("ids", ids).addValue("emails", emails);
		List<EmployeeKey> existingEmployees = jdbcTemplate.query(lookupSql, parameters,
				(rs, rowNum) -> new EmployeeKey(rs.getLong("id"), normalizeEmail(rs.getString("email"))));
		// Step 3: Store existing IDs and emails.
		Set<Long> existingIds = new HashSet<>();
		Set<String> existingEmails = new HashSet<>();
		for (EmployeeKey existing : existingEmployees) {
			existingIds.add(existing.id());
			existingEmails.add(existing.email());
		}
		// Step 4: Filter out records already in the database.
		List<Employee> employeesToInsert = new ArrayList<>();
		for (Employee employee : chunk.getItems()) {
			boolean duplicateId = existingIds.contains(employee.getId());
			boolean duplicateEmail = existingEmails.contains(normalizeEmail(employee.getEmail()));
			if (duplicateId || duplicateEmail) {
				log.info("Skipping employee already in database. ID: {}, email: {}", employee.getId(),
						employee.getEmail());
				continue;
			}
			employeesToInsert.add(employee);
		}
		// Step 5: Batch-insert the remaining employees.
		if (!employeesToInsert.isEmpty()) {
			jdbcEmployeeWriter.write(new Chunk<>(employeesToInsert));
			log.info("Submitted {} employees for batch insertion.", employeesToInsert.size());
		}
	}

	private String normalizeEmail(String email) {
		return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
	}

	private record EmployeeKey(Long id, String email) {
	}
}
