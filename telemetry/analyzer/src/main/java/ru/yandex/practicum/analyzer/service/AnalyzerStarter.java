package ru.yandex.practicum.analyzer.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AnalyzerStarter implements CommandLineRunner, AutoCloseable {

    @Override
    public void run(String... args) {
        while (true) {
            // Logic to start the analyzer
        }
    }

    @Override
    public void close() throws Exception {

    }
}
