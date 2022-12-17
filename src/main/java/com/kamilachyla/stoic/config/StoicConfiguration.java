package com.kamilachyla.stoic.config;

import java.time.Clock;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class StoicConfiguration {

    private final Logger LOGGER = LoggerFactory.getLogger(StoicConfiguration.class);
    @Value("${stoic.date.format}")
    private String dateFormat;
    @Value("${stoic.datetime.format}")
    private String dateTimeFormat;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.simpleDateFormat(dateTimeFormat);
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
        };
    }

    @Bean
    public WebMvcConfigurer corsConfigurer(Environment env) {
        final int guiPort = env.getProperty("gui.port", Integer.class, 8081);
        final int serverPort = env.getProperty("server.port", Integer.class, 5000);
        LOGGER.info("corsConfigurer found gui.port=%d and server.port=%d".formatted(guiPort, serverPort));
        final String host = "http://localhost:%s";

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(host.formatted(guiPort), host.formatted(serverPort))
                        .allowedMethods("GET", "POST", "DELETE", "PUT");
            }
        };
    }

    @Bean
    Clock getClock() {
        return Clock.systemDefaultZone();
    }
}