package com.kamilachyla.stoic.model.quote;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kamilachyla.stoic.model.thought.Thought;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;


@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uc_quote_text",
        columnNames = {"text"}))
public class Quote {
    @OneToMany(mappedBy = "quote")
    @JsonIgnore
    List<Thought> thoughts;
    @Id
    @GeneratedValue
    @JsonAlias("ID")
    private Long id;
    @NotNull
    private String author;
    @Column(length = 2048)
    @NotNull
    private String text;

    public Quote() {

    }

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

    @JsonProperty
    public int thoughtsCount() {
        return thoughts == null ? 0 : thoughts.size();
    }

    @Override
    public String toString() {
        return "[ID: %d, Author: %s, Quote: %s, Thoughts count: %d]".formatted(id, author, text, thoughtsCount());
    }

    public record Author(String name) {
    }

    public record Text(String value) {
    }
}
