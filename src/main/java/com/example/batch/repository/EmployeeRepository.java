package com.example.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.batch.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
