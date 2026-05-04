package com.bpm.bpm_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoAtlasConfig {

    @Bean
    public MongoClient mongoClient() {
        // ¡Le quitamos el volante a Spring Boot! Conectamos a Atlas directamente.
        return MongoClients.create("mongodb+srv://nicol:0910nicol@bpm.bqtmrs8.mongodb.net/bpm_db?appName=bpm");
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "bpm_db");
    }
}