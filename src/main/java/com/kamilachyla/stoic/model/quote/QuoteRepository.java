package com.kamilachyla.stoic.model.quote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    Iterable<Quote> findByAuthorLike(String author);
    Iterable<Quote> findByTextLike(String text);

    
}
