package com.spring.batch.springBatch.Configuration;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.batch.core.JobExecution;

@Configuration

// @Component
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private SimpleJobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Scheduled(cron = "*/5 * * * * *")
    public void run() {

        try {
            String dateParam = new Date().toString();
            JobParameters jobParameters = new JobParametersBuilder().addString("date", dateParam).toJobParameters();

            JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            System.out.println("Exit Status=" + jobExecution.getExitStatus());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    @Bean
    public ResourcelessTransactionManager transactionManagerSimple() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public MapJobRepositoryFactoryBean mapJobRepositoryFactory(ResourcelessTransactionManager txManager)
            throws Exception {

        MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean(txManager);

        factory.afterPropertiesSet();

        return factory;
    }

    @Bean
    public JobRepository jobRepositorySimple(MapJobRepositoryFactoryBean factory) throws Exception {

        return factory.getObject();

    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").tasklet(printTasklet()).build();
    }

    @Bean
    public Tasklet printTasklet() {
        Tasklet t = new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
                System.out.println("Print Tasklet executed!!");

                return RepeatStatus.FINISHED;
            }
        };
        return t;
    }

    @Bean
    public Job readFiles() {
        return jobBuilderFactory.get("readFiles").incrementer(new RunIdIncrementer()).flow(step1()).end().build();
    }

    @Bean
    public SimpleJobLauncher simpleJobLauncher(JobRepository jobRepository) {
        SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
        simpleJobLauncher.setJobRepository(jobRepository);
        return simpleJobLauncher;
    }
}
