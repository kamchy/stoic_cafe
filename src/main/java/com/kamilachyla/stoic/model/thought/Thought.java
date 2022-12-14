package com.kamilachyla.stoic.model.thought;

import com.kamilachyla.stoic.model.quote.Quote;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "Thought", uniqueConstraints = {
        @UniqueConstraint(name = "uc_thought_text", columnNames = {"text"})
})
public class Thought {
    public static final String STRING_FMT = "[Date: %s, Th: %s, Q: %s]";
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = Quote.GENERATOR_NAME)
    @SequenceGenerator(name = Quote.GENERATOR_NAME, sequenceName = Quote.SEQUENCE_NAME , allocationSize = 50)
    private Long id;
    private String text;
    private LocalDateTime dateTime;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", columnDefinition = "quote_Id")
    private Quote quote;

    public String getText() {
        return text;
    }


    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public Quote getQuote() {
        return quote;
    }

    @Override
    public String toString() {
        return STRING_FMT.formatted(
                dateTime.format(DATE_TIME_FORMAT),
                text,
                quote == null ? "Null quote!" : quote.toString());
    }

    public static Thought of(String text, LocalDateTime now, Quote quote) {
        var th = new Thought();
        th.text = text;
        th.dateTime = now;
        th.quote = quote;
        return th;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Thought thought = (Thought) o;
        return text.equals(thought.text) && dateTime.equals(thought.dateTime) && Objects.equals(quote, thought.quote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, dateTime, quote);
    }
}
