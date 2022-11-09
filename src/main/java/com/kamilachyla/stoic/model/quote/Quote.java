package com.kamilachyla.stoic.model.quote;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kamilachyla.stoic.model.thought.Thought;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;


@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uc_quote_text",
        columnNames = {"text"}))
public class Quote {
    public static final String GENERATOR_NAME = "mygenerator";
    public static final String SEQUENCE_NAME = "hibernate_sequence";
    @OneToMany(mappedBy = "quote", fetch = FetchType.EAGER)
    @JsonIgnore
    List<Thought> thoughts;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    // see changeset kamila:5
    @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 50)
    @JsonAlias("ID")
    private Long id;
    @NotNull
    private String author;
    @Column(length = 2048)
    @NotNull
    private String text;

    public Quote() {

    }

    // TODO create static factory
    public Quote(Author author, Text text) {
        this.author = author.name();
        this.text = text.value();
        this.thoughts = List.of();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void update(String author, String text) {
        setAuthor(author);
        setText(text);
    }

    @JsonProperty
    public int thoughtsCount() {
        return thoughts == null ? 0 : thoughts.size();
    }

    @Override
    public String toString() {
        return "[ID: %d, Author: %s, Quote: %s, Thoughts count: %d]".formatted(id, author, text, thoughtsCount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quote quote = (Quote) o;
        return Objects.equals(author, quote.author) && Objects.equals(text, quote.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, text);
    }

    public record Author(String name) {
    }

    public record Text(String value) {
    }
}
