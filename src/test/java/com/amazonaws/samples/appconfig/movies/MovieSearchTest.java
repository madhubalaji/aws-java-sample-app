package com.amazonaws.samples.appconfig.movies;

import com.amazonaws.samples.appconfig.utils.MovieUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for movie search functionality
 * Arrr! These be our tests to ensure our treasure hunting works properly, matey!
 */
public class MovieSearchTest {

    @Mock
    private Environment env;

    private MoviesController moviesController;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        moviesController = new MoviesController();
        moviesController.env = env;
        
        // Mock environment properties
        when(env.getProperty("appconfig.application")).thenReturn("TestApp");
        when(env.getProperty("appconfig.environment")).thenReturn("test");
        when(env.getProperty("appconfig.config")).thenReturn("testConfig");
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        mockMvc = MockMvcBuilders.standaloneSetup(moviesController).build();
    }

    @Test
    public void testSearchMoviesByName() throws Exception {
        // Test searching by movie name
        mockMvc.perform(get("/movies/search")
                .param("name", "Pirate"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("The Pirate's Adventure")));
    }

    @Test
    public void testSearchMoviesById() throws Exception {
        // Test searching by movie ID
        mockMvc.perform(get("/movies/search")
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("The Pirate's Adventure")));
    }

    @Test
    public void testSearchMoviesByGenre() throws Exception {
        // Test searching by genre
        mockMvc.perform(get("/movies/search")
                .param("genre", "Adventure"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Adventure")));
    }

    @Test
    public void testSearchMoviesWithMultipleCriteria() throws Exception {
        // Test searching with multiple criteria
        mockMvc.perform(get("/movies/search")
                .param("name", "Treasure")
                .param("genre", "Adventure"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Treasure Island Quest")));
    }

    @Test
    public void testSearchMoviesNoResults() throws Exception {
        // Test searching with criteria that returns no results
        mockMvc.perform(get("/movies/search")
                .param("name", "NonExistentMovie"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("No movies found")));
    }

    @Test
    public void testSearchMoviesNoParameters() throws Exception {
        // Test accessing search endpoint without parameters
        mockMvc.perform(get("/movies/search"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Movie Search")));
    }

    @Test
    public void testSearchMoviesInvalidId() throws Exception {
        // Test searching with invalid ID
        mockMvc.perform(get("/movies/search")
                .param("id", "-1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("No movies found")));
    }

    @Test
    public void testMovieUtilsValidation() {
        // Test MovieUtils validation methods
        assertTrue("Valid movie name should pass", MovieUtils.isValidMovieName("Test Movie"));
        assertFalse("Null movie name should fail", MovieUtils.isValidMovieName(null));
        assertFalse("Empty movie name should fail", MovieUtils.isValidMovieName(""));
        assertFalse("Whitespace-only movie name should fail", MovieUtils.isValidMovieName("   "));
        
        assertTrue("Valid movie ID should pass", MovieUtils.isValidMovieId(1L));
        assertFalse("Zero movie ID should fail", MovieUtils.isValidMovieId(0L));
        assertFalse("Negative movie ID should fail", MovieUtils.isValidMovieId(-1L));
        
        assertTrue("Valid genre should pass", MovieUtils.isValidGenre("Action"));
        assertFalse("Null genre should fail", MovieUtils.isValidGenre(null));
        assertFalse("Empty genre should fail", MovieUtils.isValidGenre(""));
    }

    @Test
    public void testMovieUtilsMatching() {
        // Test MovieUtils matching methods
        assertTrue("Case-insensitive name matching should work", 
                   MovieUtils.matchesMovieName("The Great Movie", "great"));
        assertTrue("Partial name matching should work", 
                   MovieUtils.matchesMovieName("Adventure Time", "Adventure"));
        assertFalse("Non-matching name should return false", 
                    MovieUtils.matchesMovieName("Comedy Show", "Drama"));
        
        assertTrue("Case-insensitive genre matching should work", 
                   MovieUtils.matchesGenre("ACTION", "action"));
        assertTrue("Exact genre matching should work", 
                   MovieUtils.matchesGenre("Comedy", "Comedy"));
        assertFalse("Non-matching genre should return false", 
                    MovieUtils.matchesGenre("Action", "Comedy"));
    }

    @Test
    public void testMovieModelWithGenre() {
        // Test Movie model with genre
        Movie movie = new Movie(1L, "Test Movie", "Action");
        assertEquals("Movie ID should match", 1L, movie.getId());
        assertEquals("Movie name should match", "Test Movie", movie.getMovieName());
        assertEquals("Movie genre should match", "Action", movie.getGenre());
        
        // Test backward compatibility constructor
        Movie movieWithoutGenre = new Movie(2L, "Old Movie");
        assertEquals("Movie should have default genre", "Unknown", movieWithoutGenre.getGenre());
    }

    @Test
    public void testStaticMovieData() {
        // Test that static movie data includes genres
        Movie[] movies = MoviesController.PAIDMOVIES;
        assertTrue("Should have movies", movies.length > 0);
        
        for (Movie movie : movies) {
            assertNotNull("Movie should have a name", movie.getMovieName());
            assertNotNull("Movie should have a genre", movie.getGenre());
            assertNotEquals("Movie should not have Unknown genre in static data", "Unknown", movie.getGenre());
            assertTrue("Movie ID should be positive", movie.getId() > 0);
        }
    }
}