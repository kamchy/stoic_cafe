package com.kamilachyla.stoic.model.api;

import com.kamilachyla.stoic.model.quote.Quote;
import com.kamilachyla.stoic.model.thought.Thought;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/quote")
public class QuoteController {
    private final StoicService service;
    private final Logger LOGGER = LoggerFactory.getLogger(QuoteController.class);

    QuoteController(StoicService service) {
        this.service = service;
    }

    @PostMapping("/")
    public ResponseEntity<ClientQuote> addQuote(@RequestBody @Valid ClientQuote q) {
        LOGGER.info("Post {}", q);
        var savedQuote = service.saveQuote(new Quote(new Quote.Author(q.author), new Quote.Text(q.text)));
        var clientQuote = ClientQuote.from(Optional.ofNullable(savedQuote));
        return clientQuote
                .map(quote -> ResponseEntity.created(createUri(quote.id)).body(quote))
                .orElseGet(()-> ResponseEntity.notFound().build());

    }

    private static URI createUri(Long id) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientQuote> updateQuote(@PathVariable("id") long id, @RequestBody @Valid ClientQuote q) {
        var updated = ClientQuote.from(service.update(id, q));
        return updated
                .map(ResponseEntity.ok()::body)
                .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientQuote> getQuote(@PathVariable("id") long id) {
        var quote = ClientQuote.from(service.getQuoteById(id));
        return quote.map(ResponseEntity.ok()::body)
                .orElseGet(()->ResponseEntity.notFound().build());
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
    public Iterable<ClientQuote> getQuotesBy() {
        LOGGER.info("Get all quotes");
        return service.getAllQuotes().stream().map(q -> new ClientQuote(q.getAuthor(), q.getText(), q.getId())).toList();
    }
    public record ClientQuote(
            @NotNull(message = "Author cannot be empty") String author,
            @NotNull(message = "Text cannot be empty") String text, Long id) {
        public static Optional<ClientQuote> from(Optional<Quote> updated) {
            return updated.map(q -> new ClientQuote(q.getAuthor(), q.getText(), q.getId()));
        }
    }

    public record SimpleThought(Long id, String text) {
    }

    public record QuoteThoughts(Quote quote, List<SimpleThought> thought) {
    }

}
