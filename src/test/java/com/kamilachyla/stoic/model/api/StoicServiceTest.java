package com.kamilachyla.stoic.model.api;

import com.kamilachyla.stoic.model.quote.Quote;
import com.kamilachyla.stoic.model.quote.QuoteRepository;
import com.kamilachyla.stoic.model.thought.Thought;
import com.kamilachyla.stoic.model.thought.ThoughtRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StoicServiceTest {
    QuoteRepository qr;
    ThoughtRepository tr;
    StoicService stoicService;
    final static long expectedId = 10;
    final static String expectedText = "sample";
    final static String expectedAuthor= "wer";

    @BeforeEach
    void before() {
        tr = Mockito.mock(ThoughtRepository.class);
        qr = Mockito.mock(QuoteRepository.class);
        stoicService = new StoicService(qr, tr);
    }

    @Test
    void testSaveQuote() {
        final var q = TestUtils.createQuoteWithId(expectedText, expectedAuthor, expectedId);
        Mockito.when(qr.save(Mockito.any(Quote.class))).thenReturn(q);
        Quote res = stoicService.saveQuote(q);
        assertEquals(expectedId, res.getId());
        assertEquals(expectedText, res.getText());
        assertEquals(expectedAuthor, res.getAuthor());
        Mockito.verify(qr).save(q);
        Mockito.verifyNoInteractions(tr);
    }

    @Test
    void getQuoteById() {
        final var q = TestUtils.createQuoteWithId(expectedText, expectedAuthor, expectedId);
        Mockito.when(qr.findById(expectedId)).thenReturn(Optional.of(q));
        var res = stoicService.getQuoteById(expectedId);
        Mockito.verify(qr).findById(expectedId);
        Mockito.verifyNoMoreInteractions(qr);
        assertTrue(res.isPresent());
        assertEquals(res.get(), q);
    }

    @Test
    void deleteQuoteById() {
        final var q = TestUtils.createQuoteWithId(expectedText, expectedAuthor, expectedId);
        stoicService.deleteQuoteById(expectedId);
        Mockito.verify(qr).deleteById(expectedId);
        Mockito.verifyNoMoreInteractions(qr);
    }

    @Test
    void getAllQuotes() {
        final var t = List.of(newQuote("Me", "One"), newQuote("me", "two"));
        Mockito.when(qr.findAll()).thenReturn(t);
        Assertions.assertThat(stoicService.getAllQuotes()).containsAll(t);
        Mockito.verify(qr).findAll();
        Mockito.verifyNoInteractions(tr);
    }

    @Test
    void saveThought() {
        Thought th = Thought.of("text", LocalDateTime.now(), new Quote());
        Mockito.when(tr.save(Mockito.any(Thought.class))).thenReturn(TestUtils.withThoughtId(th, 123));
    }

    private Quote newQuote(String auth, String text) {
        var q = new Quote();
        q.setAuthor(auth);
        q.setText(text);
        return q;
    }

}