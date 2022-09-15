package com.kamilachyla.stoic.model.api;

import com.kamilachyla.stoic.model.thought.Thought;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/thought")
public class ThoughtController {
    private final StoicService service;

    ThoughtController(StoicService service) {
        this.service = service;
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
        var thought = Thought.of(th.text(), LocalDateTime.now(), service.getQuoteById(th.quoteId()).orElse(null));
        var savedThought = service.saveThought(thought);
        if (savedThought == null) {
            return ResponseEntity.notFound().build();
        } else {
            var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedThought.getId())
                    .toUri();

            return ResponseEntity.created(uri).location(uri)
                    .body(savedThought);
        }

    }
}
