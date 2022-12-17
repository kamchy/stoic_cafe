package com.kamilachyla.stoic.model.api;

import com.kamilachyla.stoic.model.quote.Quote;
import com.kamilachyla.stoic.model.quote.QuoteRepository;
import com.kamilachyla.stoic.model.thought.Thought;
import com.kamilachyla.stoic.model.thought.ThoughtRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class StoicService {
    private final QuoteRepository repo;
    private final Logger logger = LoggerFactory.getLogger(StoicService.class);
    private final ThoughtRepository thRepo;

    public StoicService(QuoteRepository repo, ThoughtRepository thRepo) {
        this.repo = repo;
        this.thRepo = thRepo;
    }


    public Quote saveQuote(Quote q) {
        Quote res = repo.save(q);
        logger.info("Saved {}", res);
        return res;
    }

    public Optional<Quote> getQuoteById(Long id) {
        return Optional.ofNullable(id).flatMap(repo::findById);
    }

    public List<Quote> getAllQuotes() {
        return repo.findAll();
    }

    public void deleteQuoteById(Long id) {
        repo.deleteById(id);
    }


    public Thought saveThought(Thought th) {
        var savedTh = thRepo.save(th);
        logger.info("{}", savedTh);
        return savedTh;
    }

    public List<Thought> getAllThoughts() {
        return thRepo.findAll();
    }

    public Optional<Thought> getThoughtById(Long id) {
        return thRepo.findById(id);
    }

    public void deleteThought(Long id) {
        thRepo.deleteById(id);
    }


    public List<Thought> getThoughtsWhereQuoteId(Long id) {
        var l = new ArrayList<Thought>();
        thRepo.findByQuoteId(id).forEach(l::add);
        return l;
    }

    Optional<Quote> update(long id, QuoteController.ClientQuote q) {
        return repo.findById(id).map(quote -> {
            quote.setAuthor(q.author());
            quote.setText(q.text());
            repo.save(quote);
            return quote;
        });
    }
}