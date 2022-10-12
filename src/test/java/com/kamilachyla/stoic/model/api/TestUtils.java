package com.kamilachyla.stoic.model.api;

import com.kamilachyla.stoic.model.quote.Quote;
import com.kamilachyla.stoic.model.thought.Thought;

public final class TestUtils {
    private TestUtils(){
        // no extension
    }

    static Quote withQuoteId(Quote q, int i) {
        Quote qwid  = new Quote();
        qwid.setText(q.getText());
        qwid.setAuthor(q.getAuthor());
        qwid.setId(i);
        return qwid;
    }

    static Thought withThoughtId(Thought th, Long thoughtId) {
        var res = Thought.of(th.getText(), th.getDateTime(), th.getQuote());
        res.setId(thoughtId);
        return res;
    }

    public static Quote createQuoteWithId(String expectedText, String expectedAuthor, long expectedId) {
        Quote q = new Quote();
        q.setText(expectedText);
        q.setAuthor(expectedAuthor);
        q.setId(expectedId);
        return q;
    }
}
