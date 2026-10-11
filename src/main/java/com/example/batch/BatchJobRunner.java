package com.example.batch;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchJobRunner implements CommandLineRunner {
	private final JobOperator jobOperator;
	private final Job employeeProcessingJob;
	@Value("${app.batch.run}")
	private boolean runJob;

	@Override
	public void run(String... args) throws Exception {
		if (!runJob) {
			log.warn("Batch job execution is disabled.");
			return;
		}
		// Manually provide the long job parameter here
		long runId = 1003L;
		JobParameters jobParameters = new JobParametersBuilder().addLong("run.id", runId).toJobParameters();
		JobExecution execution = jobOperator.start(employeeProcessingJob, jobParameters);
		log.info("Job execution status: " + execution.getStatus());
	}
}
