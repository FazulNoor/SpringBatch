package com.spring.batch.springBatch.Configuration;

import java.util.Date;

import javax.batch.runtime.JobExecution;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    public void run(){
        String dateParam = new Date().toString();
        JobParameters jobParameters = new JobParametersBuilder().addString("date", dateParam).toJobParameters();

        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
    }

}
