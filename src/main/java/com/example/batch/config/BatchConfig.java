package com.example.batch.config;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batch.batch.EmployeeSkipListener;
import com.example.batch.batch.FileValidationTasklet;
import com.example.batch.dto.EmployeeRequestDto;
import com.example.batch.entity.Employee;
import com.example.batch.exception.InvalidEmployeeException;

@Configuration
public class BatchConfig {

	@Bean
	public Step fileValidationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			FileValidationTasklet fileValidationTasklet) {
		return new StepBuilder("fileValidationStep", jobRepository).tasklet(fileValidationTasklet, transactionManager)
				.build();
	}

	@Bean
	public Step employeeProcessingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			FlatFileItemReader<EmployeeRequestDto> employeeReader,
			ItemProcessor<EmployeeRequestDto, Employee> employeeProcessor, ItemWriter<Employee> employeeWriter,
			EmployeeSkipListener employeeSkipListener) {
		return new StepBuilder("employeeProcessingStep", jobRepository).<EmployeeRequestDto, Employee>chunk(10)
				.transactionManager(transactionManager).reader(employeeReader).processor(employeeProcessor)
				.writer(employeeWriter).faultTolerant().skip(InvalidEmployeeException.class).skipLimit(50)
				.listener(employeeSkipListener).build();
	}

	@Bean
	public Job employeeProcessingJob(JobRepository jobRepository, Step employeeProcessingStep,
			Step fileValidationStep) {
		return new JobBuilder("employeeProcessingJob", jobRepository).start(fileValidationStep)
				.next(employeeProcessingStep).build();
	}
}