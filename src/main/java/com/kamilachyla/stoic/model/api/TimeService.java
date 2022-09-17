package com.kamilachyla.stoic.model.api;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.function.Supplier;

@Service
public class TimeService implements Supplier<LocalDateTime> {
    @Override
    public LocalDateTime get() {
        return LocalDateTime.now();
    }
}
