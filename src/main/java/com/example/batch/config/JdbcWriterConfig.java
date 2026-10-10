package com.example.batch.config;

import com.example.batch.entity.Employee;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JdbcWriterConfig {

    @Bean
    public JdbcBatchItemWriter<Employee> jdbcEmployeeWriter(
            DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<Employee>()
                .dataSource(dataSource)
                .sql("""
                    INSERT INTO employees (id, name, email, salary)
                    VALUES (:id, :name, :email, :salary)
                    ON CONFLICT DO NOTHING
                    """)
                .beanMapped()
                .assertUpdates(false)
                .build();
    }
}