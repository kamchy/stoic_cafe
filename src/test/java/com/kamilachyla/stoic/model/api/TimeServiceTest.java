package com.kamilachyla.stoic.model.api;

import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class TimeServiceTest {
    @Test
    void timeService() {
        Clock c = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        var ts = new TimeService(c);
        LocalDateTime d1 = ts.get();
        LocalDateTime d2 = ts.get();
        assertEquals(0, Duration.between(d1, d2).toNanos());
    }

    class TickingByMinutes implements Supplier<LocalDateTime> {
        private final ZoneId zoneId = ZoneId.systemDefault();
        private final int byMinutes;
        private  Clock clock = Clock.tickMinutes(zoneId);
        TickingByMinutes(int byMinutes) {
            this.byMinutes = byMinutes;
            clock = Clock.tick(clock, Duration.ofMinutes(byMinutes));
        }
        @Override
        public LocalDateTime get() {
            print("current get: %s".formatted(LocalDateTime.now(clock)));
            clock = Clock.fixed(clock.instant().plus(byMinutes, ChronoUnit.MINUTES), zoneId);
            print("New clock: %s".formatted(LocalDateTime.now(clock)));
            return LocalDateTime.now(clock);
        }

    }

    void print(String s) {
        System.out.println(s);
    }
    @Test
    void timeServiceByMinutes() {
        final var minutesCountBeforeGet = 10;
        var ts = new TickingByMinutes(minutesCountBeforeGet);
        LocalDateTime d1 = ts.get();
        LocalDateTime d2 = ts.get();
        assertEquals(minutesCountBeforeGet, Duration.between(d1, d2).toMinutes());
    }
}