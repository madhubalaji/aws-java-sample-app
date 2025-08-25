package com.amazonaws.samples.appconfig.movies;

import com.amazonaws.samples.appconfig.utils.HTMLBuilder;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HTMLBuilderTest {

    private HTMLBuilder htmlBuilder;
    private Movie[] testMovies;

    @Before
    public void setUp() {
        htmlBuilder = new HTMLBuilder();
        testMovies = new Movie[]{
            new Movie(1L, "Test Movie 1", "Action"),
            new Movie(2L, "Test Movie 2", "Comedy"),
            new Movie(3L, "Test Movie 3", "Drama")
        };
    }

    @Test
    public void testGetMoviesHtml() {
        // Act
        String result = htmlBuilder.getMoviesHtml(testMovies);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("FREE Movie List for this Month"));
        assertTrue(result.contains("Test Movie 1"));
        assertTrue(result.contains("Test Movie 2"));
        assertTrue(result.contains("Test Movie 3"));
        assertTrue(result.contains("ID: 1"));
        assertTrue(result.contains("ID: 2"));
        assertTrue(result.contains("ID: 3"));
    }

    @Test
    public void testGetSearchPageHtmlWithResults() {
        // Act
        String result = htmlBuilder.getSearchPageHtml(testMovies, "Test", null, null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Movie Search"));
        assertTrue(result.contains("Found 3 movie(s)"));
        assertTrue(result.contains("Test Movie 1"));
        assertTrue(result.contains("Action"));
        assertTrue(result.contains("Comedy"));
        assertTrue(result.contains("Drama"));
        assertTrue(result.contains("value='Test'"));
    }

    @Test
    public void testGetSearchPageHtmlWithNoResults() {
        // Act
        String result = htmlBuilder.getSearchPageHtml(new Movie[0], "NonExistent", null, null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Movie Search"));
        assertTrue(result.contains("No movies found matching your search criteria"));
        assertTrue(result.contains("value='NonExistent'"));
    }

    @Test
    public void testGetSearchPageHtmlWithNoSearchParams() {
        // Act
        String result = htmlBuilder.getSearchPageHtml(new Movie[0], null, null, null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Movie Search"));
        assertTrue(result.contains("Enter search criteria above to find movies"));
    }

    @Test
    public void testGetSearchPageHtmlWithAllSearchParams() {
        // Act
        String result = htmlBuilder.getSearchPageHtml(testMovies, "Test Movie", 1L, "Action");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Movie Search"));
        assertTrue(result.contains("value='Test Movie'"));
        assertTrue(result.contains("value='1'"));
        assertTrue(result.contains("value='Action'"));
    }

    @Test
    public void testSearchFormContainsRequiredElements() {
        // Act
        String result = htmlBuilder.getSearchPageHtml(testMovies, null, null, null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("form"));
        assertTrue(result.contains("name='name'"));
        assertTrue(result.contains("name='id'"));
        assertTrue(result.contains("name='genre'"));
        assertTrue(result.contains("Search Movies"));
        assertTrue(result.contains("Clear"));
        assertTrue(result.contains("action='/movies/search'"));
    }

    @Test
    public void testHtmlEscaping() {
        // Arrange
        Movie[] moviesWithSpecialChars = new Movie[]{
            new Movie(1L, "Movie <script>alert('xss')</script>", "Action & Adventure")
        };
        
        // Act
        String result = htmlBuilder.getSearchPageHtml(moviesWithSpecialChars, "<script>", null, null);
        
        // Assert
        assertNotNull(result);
        assertFalse(result.contains("<script>alert('xss')</script>"));
        assertTrue(result.contains("&lt;script&gt;"));
        assertTrue(result.contains("Action &amp; Adventure"));
    }

    @Test
    public void testEmptyMovieArray() {
        // Act
        String result = htmlBuilder.getMoviesHtml(new Movie[0]);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("FREE Movie List for this Month"));
    }

    @Test
    public void testMovieWithNullGenre() {
        // Arrange
        Movie[] moviesWithNullGenre = new Movie[]{
            new Movie(1L, "Test Movie", null)
        };
        
        // Act
        String result = htmlBuilder.getSearchPageHtml(moviesWithNullGenre, null, null, null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Test Movie"));
        assertTrue(result.contains("Unknown")); // Should show default genre
    }
}