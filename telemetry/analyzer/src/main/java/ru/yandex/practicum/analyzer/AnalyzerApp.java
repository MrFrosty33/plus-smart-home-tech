package ru.yandex.practicum.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.analyzer.service.AnalyzerStarter;

@SpringBootApplication
@ConfigurationProperties
public class AnalyzerApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AnalyzerApp.class, args);

        AnalyzerStarter analyzer = context.getBean(AnalyzerStarter.class);
        analyzer.run();
    }
}
