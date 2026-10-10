package com.example.batch.entity;
import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employees")
public class Employee {
	@Id
	private Long id;
	@Column(name = "employee_number", nullable = false, unique = true)
	private String name;
	@Column(name = "email", nullable = false, unique = true)
	private String email;
	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal salary;

}
