package com.kamilachyla.stoic.model.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamilachyla.stoic.model.quote.Quote;
import com.kamilachyla.stoic.model.thought.Thought;
import org.hamcrest.core.StringEndsWith;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ThoughtControllerTest {

    @MockBean
    StoicService service;

    @Autowired
    MockMvc mvc;

    @Value("${stoic.datetime.format}")
    private String dateTimePattern;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ThoughtController ctrl;

    @Test
    void controllerNotNull() {
        assertNotNull(ctrl);
    }

    @Test
    void checkControllerGetsAllThoughtsWhenEmpty() throws Exception {
        Mockito.when(service.getAllThoughts()).thenReturn(List.of());
        mvc.perform(get("/thought/")).andExpect(status().isOk()).andExpect(content().json("[]"));
        Mockito.verify(service).getAllThoughts();

    }

    @Test
    void checkControllerGetsAllThoughtsWhenSingle() throws Exception {
        String text = "test";
        LocalDateTime date = LocalDateTime.of(2022, 12, 03, 15, 23, 11);
        String author = "me";
        String qtext = "bla";
        final var quoteId = 1000;
        Quote quote = TestUtils.withQuoteId(new Quote(new Quote.Author(author), new Quote.Text(qtext)), quoteId);
        final var thoughtId = 123;
        final var expectedObject = List.of(TestUtils.withThoughtId(Thought.of(text, date, quote), thoughtId));
        Mockito.when(service.getAllThoughts()).thenReturn(expectedObject);
        String expectedContent = objectMapper.writeValueAsString(expectedObject);
        mvc.perform(get("/thought/"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedContent));
        Mockito.verify(service).getAllThoughts();

    }
    @Test
    void checkAddingThoughtForNonExistingQuote() throws Exception {
        Mockito.when(service.getQuoteById(Mockito.anyLong())).thenReturn(Optional.empty());

        mvc.perform(
                post("/thought/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"bla", "quoteId":30}
                                """)
                        ).andExpect(
                                status().isNotFound());

        Mockito.verify(service).getQuoteById(Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void checkAddingThoughtForExistingQuote() throws Exception {
        final var thought = new Thought();
        Quote quote = new Quote(new Quote.Author("Foo"), new Quote.Text("Bar"));
        thought.setText("bla");
        long thoughtId = 123;
        thought.setQuote(quote);
       // LocalDateTime nowTime = LocalDateTime.now();
        //thought.setDateTime(nowTime);
        // Why this test passes? verification of timeservice mock should be invalid!
        //Mockito.when(timeService.get()).thenReturn(nowTime);
        Mockito.when(service.getQuoteById(Mockito.anyLong())).thenReturn(Optional.of(quote));
        Mockito.when(service.saveThought(Mockito.any(Thought.class))).thenReturn(TestUtils.withThoughtId(thought, thoughtId));

        var response = mvc.perform(
                post("/thought/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"bla", "quoteId":30}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"))
                .andExpect(header().string("location", StringEndsWith.endsWith(Long.toString(thoughtId))))
                .andReturn().getResponse();

        Mockito.verify(service).getQuoteById(Mockito.anyLong());
        Mockito.verify(service).saveThought(Mockito.any(Thought.class));
    }

    /**
     *  This test tests:
     *  - if date format used in Thought Entity renders the same date as
     *  the pattern read from app context (@Value property)
     *  - if Thought's STRING_FMT formatting string is used for toString
     *  */
    @Test
    void toStringTest() throws JsonProcessingException {
        String text = "damole";
        LocalDateTime dt = LocalDateTime.now();
        String qauthor = "wqwe";
        String qtext = "qt";
        Quote quote= new Quote(
                new Quote.Author(qauthor),
                new Quote.Text(qtext));
        long thId = 24;
        Thought th = TestUtils.withThoughtId(Thought.of(text, dt, quote), thId);
        String thString = th.toString();
        final var formattedDtae = dt.format(DateTimeFormatter.ofPattern(dateTimePattern));
        String expectedString = Thought.STRING_FMT.formatted(
                formattedDtae,
                text,
                quote);
        assertEquals(expectedString, thString);
    }
}