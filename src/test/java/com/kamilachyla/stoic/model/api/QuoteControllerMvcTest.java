package com.kamilachyla.stoic.model.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamilachyla.stoic.model.quote.Quote;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.eq;

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

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addQuote() throws Exception {
        final var quote = new Quote(new Quote.Author("Kamila"), new Quote.Text("asd"));
        final var id = 123L;
        quote.setId(id);
        Mockito.when(stoicService.saveQuote(Mockito.any(Quote.class))).thenReturn(quote);
        mvc
                .perform(MockMvcRequestBuilders
                        .post("/quote/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new QuoteController.ClientQuote("kamila", "hello"))))
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
                            .content(asJsonString(new QuoteController.ClientQuote(null, "hello"))))
                    .andExpect(MockMvcResultMatchers.jsonPath("message").exists())
                    .andExpect(MockMvcResultMatchers.jsonPath("comment").value("Exception handler"))
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
                            .content(asJsonString(new QuoteController.ClientQuote("kamila", null))))
                    .andExpect(MockMvcResultMatchers.jsonPath("message").exists())
                    .andExpect(MockMvcResultMatchers.jsonPath("comment").value("Exception handler"))
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
                    .andExpect(MockMvcResultMatchers.jsonPath("message").exists())
                    .andExpect(MockMvcResultMatchers.jsonPath("comment").value("Exception handler"))
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
                            .content(asJsonString(new QuoteController.ClientQuote("kamila", "quote me"))))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        } catch (Exception e) {
            System.out.printf("exception class is %s%n message is: %s", e.getClass(), e.getMessage());
        }
        Mockito.verify(stoicService).saveQuote(Mockito.any(Quote.class));
    }

    @Test
    void updateQuoteUsingPut() {
        final QuoteController.ClientQuote input = new QuoteController.ClientQuote("Marcus", "good job");
        final QuoteController.ClientQuote expected = new QuoteController.ClientQuote("Marcus", "Good job, girl!");
        final var quoteId = 1;
        var quote = new Quote(new Quote.Author("Marcus"), new Quote.Text("Good job, girl!"));
        quote.setId(quoteId);

        //Mockito.when(stoicService.update(eq(quoteId), eq(input))).thenReturn(Optional.of(quote));

        var req = MockMvcRequestBuilders.put("/quote/" + quoteId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(input));
        try {

            mvc.perform(req).andExpect(result ->  asJsonString(expected));
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e);
        }
    }

    public static void main(String[] args) {
        System.out.printf(asJsonString(new QuoteController.ClientQuote("Kamila", "foo")));
    }
 }