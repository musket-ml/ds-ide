package com.onpositive.musket.data.core.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A concise dictionary of common terms in English.
 *
 * @author Haifeng Li
 */
public enum EnglishDictionary {
    /**
     * A concise dictionary of common terms in English.
     */
    CONCISE("/smile/nlp/dictionary/dictionary_en.txt");

    /**
     * A list of abbreviations.
     */
    private HashSet<String> dict;

    /**
     * Constructor.
     * @param resource the file name of dictionary. The file should be in plain
     * text, in which each line is a word.
     */
    private EnglishDictionary(String resource) {
        dict = new HashSet<>();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(resource)))) {
        
            String line = null;
            while ((line = input.readLine()) != null) {
                line = line.trim();
                // Remove blank line or single capital characters from dictionary.
                if (!line.isEmpty() && !line.matches("^[A-Z]$")) {
                    dict.add(line);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean contains(String s) {
        return dict.contains(s);
    }

    public int size() {
        return dict.size();
    }

    public Iterator<String> iterator() {
        return dict.iterator();
    }
}