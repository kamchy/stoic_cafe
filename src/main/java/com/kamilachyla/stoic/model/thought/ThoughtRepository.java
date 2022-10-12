
package com.kamilachyla.stoic.model.thought;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ThoughtRepository extends JpaRepository<Thought, Long> {
    @Query("select t from Thought t join t.quote q where q.id = ?1")
    Iterable<Thought> findByQuoteId(Long id);

}
