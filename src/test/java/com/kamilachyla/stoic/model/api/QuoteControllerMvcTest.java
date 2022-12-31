package com.kamilachyla.stoic.model.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamilachyla.stoic.model.quote.Quote;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@WebMvcTest
class QuoteControllerMvcTest {

    @MockBean
    StoicService stoicService;
    @MockBean
    TimeService timeService;
    @Autowired
    QuoteController quoteController;
    @Autowired
    private MockMvc mvc;
    final Long quoteId = 1L;
    final QuoteController.ClientQuote sampleClientQuote = new QuoteController.ClientQuote("Marcus", "good job", quoteId);

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addQuote() throws Exception {
        final var quote = new Quote(new Quote.Author("Kamila"), new Quote.Text("Smart text"));
        final var id = 123L;
        quote.setId(id);
        Mockito.when(stoicService.saveQuote(Mockito.any(Quote.class))).thenReturn(quote);
        mvc
                .perform(MockMvcRequestBuilders
                        .post("/quote/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new QuoteController.ClientQuote("kamila", "hello", 354l))))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("location"))
                .andExpect(MockMvcResultMatchers.header().string("location", "http://localhost/quote/%d".formatted(id)));
    }

    @Test
    void addQuoteWithNullAuthor() {
        try {
            mvc
                    .perform(MockMvcRequestBuilders
                            .post("/quote/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(asJsonString(new QuoteController.ClientQuote(null, "hello", null))))
                    .andExpect(MockMvcResultMatchers.jsonPath("title").value("Bad Request"))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        } catch (Exception e) {
            System.out.printf("exception class is %s%n message is: %s", e.getClass(), e.getMessage());
        }
    }


    @Test
    void addQuoteWithNullText() {
        try {
            mvc
                    .perform(MockMvcRequestBuilders
                            .post("/quote/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(asJsonString(new QuoteController.ClientQuote("kamila", null, null))))
                    .andExpect(MockMvcResultMatchers.jsonPath("detail").exists())
                    .andExpect(MockMvcResultMatchers.jsonPath("title").value("Bad Request"))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        } catch (Exception e) {
            System.out.printf("exception class is %s%n message is: %s", e.getClass(), e.getMessage());
        }
    }

    @Test
    void addQuoteWithNullParam() {
        try {
            mvc
                    .perform(MockMvcRequestBuilders
                            .post("/quote/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(asJsonString(null)))
                    .andExpect(MockMvcResultMatchers.jsonPath("title").value("Bad Request"))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        } catch (Exception e) {
            System.out.printf("exception class is %s%n message is: %s", e.getClass(), e.getMessage());
        }
    }

    @Test
    void addQuoteWhenServiceReturnsNull() {
        Mockito.when(stoicService.saveQuote(Mockito.any(Quote.class))).thenReturn(null);
        try {
            mvc
                    .perform(MockMvcRequestBuilders
                            .post("/quote/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(asJsonString(new QuoteController.ClientQuote("kamila", "quote me", null))))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        } catch (Exception e) {
            System.out.printf("exception class is %s%n message is: %s", e.getClass(), e.getMessage());
        }
        Mockito.verify(stoicService).saveQuote(Mockito.any(Quote.class));
    }

    @Test
    @Tag("put")
    void putQuoteExistingShouldResultOk() {

        var req = MockMvcRequestBuilders
                .put("/quote/" + quoteId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(sampleClientQuote));
        Quote quote = createQuoteFromClientQuoteAndId(sampleClientQuote, sampleClientQuote.id());
        Mockito.when(stoicService.update(quoteId, sampleClientQuote)).thenReturn(Optional.of(quote));
        try {
            mvc.perform(req)
                    .andExpect(result -> asJsonString(sampleClientQuote))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e);
        }
    }

    private Quote createQuoteFromClientQuoteAndId(QuoteController.ClientQuote input, Long id) {
        Quote quote = new Quote(new Quote.Author(input.author()), new Quote.Text(input.text()));
        quote.setId(id);
        return quote;
    }


    @Test
    @Tag("put")
    void putQuoteNotExistingShouldResultNotFoud() {
        Mockito.when(stoicService.update(quoteId, sampleClientQuote)).thenReturn(Optional.empty());
        var req = MockMvcRequestBuilders
                .put("/quote/" + quoteId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(sampleClientQuote));
        try {

            mvc.perform(req)
                    .andExpect(MockMvcResultMatchers.status().isNotFound());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e);
        }
    }

    @Test
    void getQuoteWithNonExitingId() {
        long quoteId = 1234L;
        var req = MockMvcRequestBuilders.get("/quote/" + quoteId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        Mockito.when(stoicService.getQuoteById(Mockito.eq(quoteId))).thenReturn(Optional.empty());
        try {

            mvc.perform(req)
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e);
        }
    }

    public static void main(String[] args) {
        System.out.printf(asJsonString(new QuoteController.ClientQuote("Kamila", "foo", null)));
    }
}