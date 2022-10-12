package com.kamilachyla.stoic.model.thought;

import com.kamilachyla.stoic.model.quote.Quote;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThoughtTest {


    static Object[] thoughtOf() {
        Iterator<LocalDateTime> dt = Stream.iterate(LocalDateTime.now(), d -> d.plusDays(1)).iterator();
        Quote.Author kam = new Quote.Author("kam");
        final var quoteText = new Quote.Text("quote");
        final var quote = new Quote(kam, quoteText);
        var count = 10;
        var res = new Object[count];
        for (int i = 0; i < count; i++) {
            final var nextDate = dt.next();
            final var text = String.format("Thought %s", i);
            final var expectedThought = Thought.of(text, nextDate, quote);
            res[i] = new Object[]{expectedThought.getText(), expectedThought.getDateTime(), expectedThought.getQuote(), expectedThought};
        };
        return res;
    }

    @ParameterizedTest
    @MethodSource("thoughtOf")
    public void thoughtOf(String name, LocalDateTime date, Quote quote, Thought expected) {
        Thought actual = Thought.of(name, date, quote);
        assertEquals(expected, actual);

    }
}