package com.kamilachyla.stoic.model.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class StoicServiceIntegrationTest {
    @Autowired StoicService service;

    @Test
    public void checkCount() {
        var th = service.getAllThoughts();
        th.forEach(System.out::println);
        // because I have added two thoughts to liquibase migration
        assertEquals(th.size(), 2);

    }
}
