package com.onpositive.musket.data.core.filters;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A word tokenizer based on the java.text.BreakIterator, which supports
 * multiple natural languages (selected by locale setting).
 *
 * @author Haifeng Li
 */
public class BreakIteratorTokenizer {

    /**
     * The working horse for splitting words.
     */
    private BreakIterator boundary;

    /**
     * Constructor for the default locale.
     */
    public BreakIteratorTokenizer() {
        boundary = BreakIterator.getWordInstance();
    }

    /**
     * Constructor for the given locale.
     */
    public BreakIteratorTokenizer(Locale locale) {
        boundary = BreakIterator.getWordInstance(locale);
    }

    
    public String[] split(String text) {
        boundary.setText(text);
        ArrayList<String> words = new ArrayList<>();
        int start = boundary.first();
        int end = boundary.next();

        while (end != BreakIterator.DONE) {
            String word = text.substring(start, end).trim();
            if (!word.isEmpty()) {
                words.add(word);
            }
            start = end;
            end = boundary.next();
        }

        String[] array = new String[words.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = words.get(i);
        }

        return array;
    }
}