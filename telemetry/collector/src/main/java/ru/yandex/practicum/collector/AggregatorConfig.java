package ru.yandex.practicum.collector;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.avro.AvroModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class AggregatorConfig {

    @Bean
    public JsonMapper jsonMapper() {
        JsonMapper result = new JsonMapper();
        result.registerModule(new JavaTimeModule());
        result.registerModule(new AvroModule());
        return result;
    }

    public static Properties getConsumerProperties() {
        Properties config = new Properties();
        return config;
    }

    public static Properties getProducerProperties() {
        Properties config = new Properties();
        return config;
    }
}
