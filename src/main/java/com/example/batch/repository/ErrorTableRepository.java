package com.example.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.batch.entity.ErrorTable;

public interface ErrorTableRepository extends JpaRepository<ErrorTable, Long>{

}
