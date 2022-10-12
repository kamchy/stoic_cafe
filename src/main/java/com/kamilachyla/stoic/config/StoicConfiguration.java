package com.kamilachyla.stoic.config;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Configuration
public class StoicConfiguration {

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
        final String host = "http://localhost:%s";


        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins(host.formatted(guiPort), host.formatted(serverPort))
                    .allowedMethods("GET", "POST", "DELETE");
            }
        };
    }

    @Bean
    Clock getClock() {
        return Clock.systemDefaultZone();
    }
}