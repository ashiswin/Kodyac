package com.ashiswin.kodyac;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Jing Yun on 6/4/2018.
 */
public class UtilTest {
    @Test
    public void date_isCorrect() throws Exception {
        assertEquals("Oct 01, 1997", Util.prettyDate("1997-10-01"));
    }

    @Test
    public void date_isCorrect1() throws Exception {
        assertEquals("Apr 24, 2018", Util.prettyDate("2018-04-24"));
    }

    @Test
    public void input_isWrong() throws Exception {
        try{
            String result = Util.prettyDate("2018 april 24");
            assertTrue(false);
        }catch(Exception e){
            assertTrue(true);
        }
    }

    @Test
    public void input_isWrong1() throws Exception {
        try{
            String result = Util.prettyDate("");
            assertTrue(false);
        }catch(Exception e){
            assertTrue(true);
        }
    }

    @Test
    public void input_isWrong2() throws Exception {
        try{
            String result = Util.prettyDate("2018 04 28");
            assertTrue(false);
        }catch(Exception e){
            assertTrue(true);
        }
    }

    @Test
    public void date_isWrong2() throws Exception {
        try{
            String result = Util.prettyDate("2018-13-01");
            assertTrue(false);
        }catch(Exception e){
            assertTrue(true);
        }
    }

}