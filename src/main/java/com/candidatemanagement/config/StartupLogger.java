package com.candidatemanagement.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

@Component
public class StartupLogger {
    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady(ApplicationReadyEvent event) {
        int port = -1;
        try {
            var web = (ServletWebServerApplicationContext) event.getApplicationContext();
            port = web.getWebServer().getPort();
        } catch (Exception ignored) {
        }
        String host = "localhost";
        String backendUrl = port > 0 ? String.format("http://%s:%d", host, port) : "http://localhost";
        String apiBase = backendUrl + "/api";
        String swaggerUrl = backendUrl + "/swagger-ui.html";

        log.info("=== Application URLs ===");
        log.info("Frontend URL: {}", frontendUrl);
        log.info("Backend URL: {} (API base: {})", backendUrl, apiBase);
        log.info("Backend Swagger: {}", swaggerUrl);
        log.info("========================");
    }
}
