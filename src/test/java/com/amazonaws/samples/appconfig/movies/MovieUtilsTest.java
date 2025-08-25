package com.amazonaws.samples.appconfig.movies;

import com.amazonaws.samples.appconfig.utils.MovieUtils;
import org.junit.Test;
import static org.junit.Assert.*;

public class MovieUtilsTest {

    @Test
    public void testIsValidMovieNameWithValidName() {
        // Test valid movie names
        assertTrue(MovieUtils.isValidMovieName("Valid Movie Name"));
        assertTrue(MovieUtils.isValidMovieName("A"));
        assertTrue(MovieUtils.isValidMovieName("Movie with Numbers 123"));
        assertTrue(MovieUtils.isValidMovieName("Movie: The Sequel"));
    }

    @Test
    public void testIsValidMovieNameWithInvalidName() {
        // Test null name
        assertFalse(MovieUtils.isValidMovieName(null));
        
        // Test empty name
        assertFalse(MovieUtils.isValidMovieName(""));
        
        // Test whitespace only name
        assertFalse(MovieUtils.isValidMovieName("   "));
        assertFalse(MovieUtils.isValidMovieName("\t\n"));
    }

    @Test
    public void testIsValidMovieNameWithLongName() {
        // Test name at the boundary (200 characters)
        StringBuilder longNameBuilder = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            longNameBuilder.append("A");
        }
        String longName = longNameBuilder.toString();
        assertTrue(MovieUtils.isValidMovieName(longName));
        
        // Test name over the limit (201 characters)
        String tooLongName = longName + "A";
        assertFalse(MovieUtils.isValidMovieName(tooLongName));
    }

    @Test
    public void testIsValidMovieNameWithWhitespace() {
        // Test name with leading/trailing whitespace
        assertTrue(MovieUtils.isValidMovieName("  Valid Movie  "));
        assertTrue(MovieUtils.isValidMovieName("\tValid Movie\n"));
    }

    @Test
    public void testIsValidMovieWithValidInputs() {
        // Test valid movie with valid ID
        assertTrue(MovieUtils.isValidMovie("Valid Movie", 1));
        assertTrue(MovieUtils.isValidMovie("Another Movie", 100));
    }

    @Test
    public void testIsValidMovieWithInvalidId() {
        // Test valid movie name but invalid ID
        assertFalse(MovieUtils.isValidMovie("Valid Movie", 0));
        assertFalse(MovieUtils.isValidMovie("Valid Movie", -1));
        assertFalse(MovieUtils.isValidMovie("Valid Movie", -100));
    }

    @Test
    public void testIsValidMovieWithInvalidName() {
        // Test invalid movie name with valid ID
        assertFalse(MovieUtils.isValidMovie(null, 1));
        assertFalse(MovieUtils.isValidMovie("", 1));
        assertFalse(MovieUtils.isValidMovie("   ", 1));
    }

    @Test
    public void testIsValidMovieWithBothInvalid() {
        // Test both invalid name and ID
        assertFalse(MovieUtils.isValidMovie(null, 0));
        assertFalse(MovieUtils.isValidMovie("", -1));
    }
}