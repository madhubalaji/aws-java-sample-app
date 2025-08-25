package com.amazonaws.samples.appconfig.movies;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.amazonaws.samples.appconfig.movies.MoviesController;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MovieTest {

    @Test
    public void contextLoads() {
    }

    @Test
    public void testMovieConstructorWithoutGenre() {
        // Arrange & Act
        Movie movie = new Movie(1L, "Test Movie");
        
        // Assert
        assertEquals(1L, movie.getId());
        assertEquals("Test Movie", movie.getMovieName());
        assertEquals("Unknown", movie.getGenre()); // Default genre
    }

    @Test
    public void testMovieConstructorWithGenre() {
        // Arrange & Act
        Movie movie = new Movie(1L, "Test Movie", "Action");
        
        // Assert
        assertEquals(1L, movie.getId());
        assertEquals("Test Movie", movie.getMovieName());
        assertEquals("Action", movie.getGenre());
    }

    @Test
    public void testMovieConstructorWithNullGenre() {
        // Arrange & Act
        Movie movie = new Movie(1L, "Test Movie", null);
        
        // Assert
        assertEquals(1L, movie.getId());
        assertEquals("Test Movie", movie.getMovieName());
        assertEquals("Unknown", movie.getGenre()); // Should default to "Unknown"
    }

    @Test
    public void testSetId() {
        // Arrange
        Movie movie = new Movie(1L, "Test Movie", "Action");
        
        // Act
        long newId = movie.setId(5);
        
        // Assert
        assertEquals(5L, newId);
        assertEquals(5L, movie.getId());
    }

    @Test
    public void testMovieWithEmptyGenre() {
        // Arrange & Act
        Movie movie = new Movie(1L, "Test Movie", "");
        
        // Assert
        assertEquals(1L, movie.getId());
        assertEquals("Test Movie", movie.getMovieName());
        assertEquals("", movie.getGenre()); // Empty string should be preserved
    }

    @Test
    public void testMovieWithSpecialCharactersInName() {
        // Arrange & Act
        Movie movie = new Movie(1L, "Test Movie: The Sequel & More!", "Sci-Fi");
        
        // Assert
        assertEquals(1L, movie.getId());
        assertEquals("Test Movie: The Sequel & More!", movie.getMovieName());
        assertEquals("Sci-Fi", movie.getGenre());
    }
}
