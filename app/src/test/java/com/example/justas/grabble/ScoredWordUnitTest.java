package com.example.justas.grabble;

import com.example.justas.grabble.helper.ScoredWord;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ScoredWordUnitTest {

    @Test
    public void print_isCorrect() throws Exception {
        ScoredWord scoredWord = new ScoredWord("ABCD", 10);
        assertEquals(scoredWord.prettyPrint(), "ABCD (10)");
    }


    @Test
    public void print_worksWithMissingInfo() throws Exception {
        ScoredWord scoredWord = new ScoredWord(null, 5);
        assertEquals(scoredWord.prettyPrint(), "-");
    }
}