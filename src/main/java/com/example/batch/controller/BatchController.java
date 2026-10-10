package com.example.batch.controller;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class BatchController {
	private final JobOperator jobOperator;
	private final Job employeeProcessingJob;

	@PostMapping("/process")
	public String processEmployees() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder().addLong("run.id", System.currentTimeMillis())
				.toJobParameters();
		JobExecution execution = jobOperator.start(employeeProcessingJob, jobParameters);
		return "Batch completed with status: " + execution.getStatus();
	}
}