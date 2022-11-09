package com.kamilachyla.stoic.model.api;

import com.kamilachyla.stoic.config.GlobalExceptionHandler;
import com.kamilachyla.stoic.model.quote.Quote;
import com.kamilachyla.stoic.model.thought.Thought;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/quote")
public class QuoteController {
    public static final String MANAGEMENT_STRING = "Hello, Java";
    private final StoicService service;
    private final Logger log = LoggerFactory.getLogger(QuoteController.class);

    QuoteController(StoicService service) {
        this.service = service;
    }

    @PostMapping("/")
    public ResponseEntity<Quote> addQuote(@RequestBody @Valid ClientQuote q) {
        var savedQuote = service.saveQuote(new Quote(new Quote.Author(q.author), new Quote.Text(q.text)));
        if (savedQuote == null) {
            return ResponseEntity.notFound().build();
        } else {
            var uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                    .path("/{id}")
                    .buildAndExpand(savedQuote.getId())
                    .toUri();

            return ResponseEntity.created(uri).location(uri)
                    .body(savedQuote);
        }

    }
    @PutMapping("/{id}")
    public ResponseEntity<Quote> updateQuote(@PathVariable("id") long id, @RequestBody @Valid ClientQuote q) {
        var quote = service.getQuoteById(id);
        if (quote.isPresent()) {
            var updated = service.update(id, q);
            return ResponseEntity.ok(updated.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/ms")
    public String getManagementString() {
        return MANAGEMENT_STRING;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quote> getQuote(@PathVariable("id") long id) {
        return service.getQuoteById(id).map(q -> ResponseEntity.ok(q)).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/thoughts")
    public ResponseEntity<QuoteThoughts> getThoughtsForQuote(@PathVariable("id") Long id) {
        var quote = service.getQuoteById(id).orElse(null);
        if (quote == null) {
            return ResponseEntity.notFound().header("Message", "Quote not found").build();
        }
        List<SimpleThought> li = new ArrayList<>();
        var ths = service.getThoughtsWhereQuoteId(id);
        for (Thought th : ths) {
            li.add(new SimpleThought(th.getId(), th.getText()));
        }
        return ResponseEntity.ok(new QuoteThoughts(quote, li));
    }

    @DeleteMapping("/{id}")
    public void deleteQuote(@PathVariable("id") Long id) {
        service.deleteQuoteById(id);
    }

    @GetMapping("/")
    public Iterable<Quote> getQuotesBy() {
        return service.getAllQuotes();
    }

    public static record ClientQuote(@NotNull String author, @NotNull String text) {
    }

    public record SimpleThought(Long id, String text) {
    }

    public record QuoteThoughts(Quote quote, List<SimpleThought> thought) {
    }

}
