package com.example.batch.config;

import java.util.concurrent.Future;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batch.batch.EmployeeAsyncDelegateProcessor;
import com.example.batch.batch.EmployeeProcessingResult;
import com.example.batch.batch.EmployeeProcessingResultWriter;
import com.example.batch.batch.FileValidationTasklet;
import com.example.batch.dto.EmployeeRequestDto;

@Configuration
public class BatchConfig {
	@Bean
	public Step fileValidationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			FileValidationTasklet fileValidationTasklet) {
		return new StepBuilder("fileValidationStep", jobRepository).tasklet(fileValidationTasklet, transactionManager)
				.allowStartIfComplete(true).build();
	}

	@Bean
	public TaskExecutor employeeProcessingExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(4);
		executor.setQueueCapacity(20);
		executor.setThreadNamePrefix("employee-processor-");
		return executor;
	}

	@Bean
	public AsyncItemProcessor<EmployeeRequestDto, EmployeeProcessingResult> asyncEmployeeProcessor(
			EmployeeAsyncDelegateProcessor delegate, TaskExecutor employeeProcessingExecutor) {
		AsyncItemProcessor<EmployeeRequestDto, EmployeeProcessingResult> processor = new AsyncItemProcessor<>(delegate);
		processor.setTaskExecutor(employeeProcessingExecutor);
		return processor;
	}

	@Bean
	public AsyncItemWriter<EmployeeProcessingResult> asyncEmployeeWriter(EmployeeProcessingResultWriter delegate) {
		return new AsyncItemWriter<>(delegate);
	}

	@Bean
	public Step employeeProcessingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			FlatFileItemReader<EmployeeRequestDto> employeeReader,
			AsyncItemProcessor<EmployeeRequestDto, EmployeeProcessingResult> asyncEmployeeProcessor,
			AsyncItemWriter<EmployeeProcessingResult> asyncEmployeeWriter) {
		return new StepBuilder("employeeProcessingStep", jobRepository)
				.<EmployeeRequestDto, Future<EmployeeProcessingResult>>chunk(10).transactionManager(transactionManager)
				.reader(employeeReader).processor(asyncEmployeeProcessor).writer(asyncEmployeeWriter).build();
	}

	@Bean
	public Job employeeProcessingJob(JobRepository jobRepository, Step employeeProcessingStep,
			Step fileValidationStep) {
		return new JobBuilder("employeeProcessingJob", jobRepository).start(fileValidationStep)
				.next(employeeProcessingStep).build();
	}
}