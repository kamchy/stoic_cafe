package com.kamilachyla.stoic.model.api;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.function.Supplier;

@Service
public class TimeService implements Supplier<LocalDateTime> {
    private final Clock clock;

    TimeService(Clock c) {
        this.clock = c;
    }
    @Override
    public LocalDateTime get() {
        return LocalDateTime.now(clock);
    }
}
