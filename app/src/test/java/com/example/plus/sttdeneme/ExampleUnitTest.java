package com.example.plus.sttdeneme;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void parser_canFind() throws Exception {
        Parser p = new Parser("Set a meeting");
        boolean isFound = p.find("meet");
        assertTrue(isFound);
    }



}