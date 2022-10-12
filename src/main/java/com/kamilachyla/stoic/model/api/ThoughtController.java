package com.kamilachyla.stoic.model.api;

import com.kamilachyla.stoic.model.thought.Thought;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/thought/")
public class ThoughtController {
    private final StoicService service;
    private final TimeService timeService;

    ThoughtController(StoicService service, TimeService timeService) {
        this.service = service;
        this.timeService = timeService;
    }

    @GetMapping
    public Iterable<Thought> getAll() {
        return service.getAllThoughts();
    }

    @GetMapping("/{id}")
    public Optional<Thought> byId(@PathVariable Long id) {
        return service.getThoughtById(id);
    }


    public record ClientThought(String text, Long quoteId) {
    }

    @PostMapping
    public ResponseEntity<Thought> postThought(@RequestBody ClientThought th) {
        final var quoteById = service.getQuoteById(th.quoteId());

        var thought = Thought.of(th.text(), timeService.get(), quoteById.orElse(null));
        var savedThought = service.saveThought(thought);
        if (savedThought == null) {
            return ResponseEntity.notFound().build();
        } else {
            var uri = uriWithId(Optional.ofNullable(savedThought.getId()).orElse(-1L));

            return ResponseEntity.created(uri).location(uri)
                    .body(savedThought);
        }

    }


    @DeleteMapping("/{id}")
    public void deleteQuote(@PathVariable("id") Long id) {
        service.deleteThought(id);
    }

    static URI uriWithId(long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
