package com.kamilachyla.stoic.model.thought;

import com.kamilachyla.stoic.model.quote.Quote;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "Thought", uniqueConstraints = {
        @UniqueConstraint(name = "uc_thought_text", columnNames = {"text"})
})
public class Thought {
    @Id
    @GeneratedValue
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
        return "[Date: %s, Th: %s, Q: %s]".formatted(dateTime.format(DateTimeFormatter.ISO_DATE_TIME), text, quote.toString());
    }

    public static Thought of(String text, LocalDateTime now, Quote quote) {
        var th = new Thought();
        th.text = text;
        th.dateTime = LocalDateTime.now();
        th.quote = quote;
        return th;
    }
}