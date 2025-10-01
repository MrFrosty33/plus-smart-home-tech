package ru.yandex.practicum.analyzer.service;

import org.springframework.stereotype.Component;

@Component
public class AnalyzerStarter implements Runnable, AutoCloseable {

    @Override
    public void run() {
        while (true) {
            // Logic to start the analyzer
        }
    }

    @Override
    public void close() throws Exception {

    }
}
