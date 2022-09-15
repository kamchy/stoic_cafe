package com.kamilachyla.stoic.model.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kamilachyla.stoic.model.quote.Quote;
import com.kamilachyla.stoic.model.thought.Thought;

@RestController
@RequestMapping(path = "/quote")
public class QuoteController {
    private final StoicService service;
    private final Logger log = LoggerFactory.getLogger(QuoteController.class);

    QuoteController(StoicService service) {
        this.service = service;
    }

    public static record ClientQuote(String author, String text) {
    }

    @PostMapping("/")
    public ResponseEntity<Quote> addQuote(@RequestBody ClientQuote q) {
        var savedQuote = service.SaveQuote(new Quote(new Quote.Author(q.author), new Quote.Text(q.text)));
        if (savedQuote == null) {
            return ResponseEntity.notFound().build();
        } else {
            var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedQuote.getId())
                    .toUri();

            return ResponseEntity.created(uri).location(uri)
                    .body(savedQuote);
        }

    }

    @GetMapping("/ms")
    public String getManagementString() {
        return "Tu kamila";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quote> getQuote(@PathVariable("id") int id) {
        return service.getQuoteById(id).map(q -> ResponseEntity.ok(q)).orElse(ResponseEntity.notFound().build());
    }

    public record SimpleThought(Long id, String text) {
    }

    public record QuoteThoughts(Quote quote, List<SimpleThought> thought) {
    }

    @GetMapping("/{id}/thoughts")
    public ResponseEntity<QuoteThoughts> getThoughtsForQote(@PathVariable("id") Long id) {
        var quote = service.getQuoteById(id).orElse(null);
        if (quote == null) {
            return ResponseEntity.notFound().header("Message", "Quote not found").build();
        }
        List<SimpleThought> li = new ArrayList<>();
        var ths = service.getThougthsWhereQuoteId(id);
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

    record ErrorResponse(String comment, String message, LocalDateTime date) {
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        log.error("Exception handler", e);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Exception handler", e.getMessage(), LocalDateTime.now()));

    }

}
