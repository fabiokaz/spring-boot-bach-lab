package com.kaz.spring_boot_batch.config;

import com.kaz.spring_boot_batch.model.JokeData;
import com.kaz.spring_boot_batch.model.JokerData;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Future;

@Configuration
public class BatchConfig {

    private JobRepository jobRepository;
    private PlatformTransactionManager transactionManager;
    private RestTemplate restTemplate;

    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.restTemplate = new RestTemplate();
    }

    @Bean
    public Job importJokersJob(JobRepository jobRepository, Step importJokersStep) {
        return new JobBuilder("importJokersJob", jobRepository)
                .start(importJokersStep)
                .build();
    }

    @Bean
    public Step importJokerStep(ItemReader<JokerData> reader, ItemProcessor<JokerData, Future<JokerData>> processor, ItemWriter<Future<JokerData>> writer) {
        return new StepBuilder("importJokerStep", jobRepository)
                .<JokerData, Future<JokerData>> chunk(100, transactionManager) //1000
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public ItemReader<JokerData> reader() {
        return new FlatFileItemReaderBuilder<JokerData>()
                .name("jokersFileReader")
                .resource(new FileSystemResource("path/to/jokers.csv"))
                .delimited()
                .delimiter(";")
                .names("name", "email", "birth")// name, email, birth,
                .addComment("##")
                .fieldSetMapper((FieldSet fieldSet) -> {
                    return new JokerData(fieldSet.readString("name"),
                            fieldSet.readString("email"),
                            fieldSet.readString("birth"),
                            null);
                })
                .build();
    }

    @Bean
    public ItemProcessor<JokerData, Future<JokerData>> asyncProcessor(ItemProcessor<JokerData, JokerData> itemProcessor, TaskExecutor taskExecutor) {
        var asyncProcessor = new AsyncItemProcessor<JokerData, JokerData>();
        asyncProcessor.setTaskExecutor(taskExecutor);
        asyncProcessor.setDelegate(itemProcessor);
        return asyncProcessor;
    }

    @Bean
    public ItemProcessor<JokerData, JokerData> processor() {
        return jokerData -> {
            var uri = "http://localhost:3005/jokes/random";
            var joke = restTemplate.getForObject(uri, JokeData.class);
            return new JokerData(jokerData.name(), jokerData.email(), jokerData.birth(), joke);
        };
    }

    @Bean
    public ItemWriter<Future<JokerData>> asyncWriter(ItemWriter<JokerData> writer) {
        var asyncWriter = new AsyncItemWriter<JokerData>();
        asyncWriter.setDelegate(writer);
        return asyncWriter;
    }

    @Bean
    public ItemWriter<JokerData> writer() {
        return System.out::println;
    }

}
