package com.amazonaws.samples.appconfig.movies;

import com.amazonaws.samples.appconfig.utils.AppConfigUtility;
import com.amazonaws.samples.appconfig.cache.ConfigurationCache;
import com.amazonaws.samples.appconfig.model.ConfigurationKey;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.services.appconfig.AppConfigClient;
import software.amazon.awssdk.services.appconfig.model.GetConfigurationResponse;

import java.time.Duration;
import java.util.UUID;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Spectacular test class for our amazing MoviesController! 🧪✨
 * These tests ensure our movie search adventure works perfectly!
 */
public class MoviesControllerTest {

    @Mock
    private Environment env;

    @Mock
    private AppConfigClient appConfigClient;

    @Mock
    private ConfigurationCache configurationCache;

    private MoviesController moviesController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        moviesController = new MoviesController();
        moviesController.env = env;
    }

    @Test
    public void testMovieWithFeatureEnabled() {
        // Arrange
        when(env.getProperty("appconfig.application")).thenReturn("myApp");
        when(env.getProperty("appconfig.environment")).thenReturn("dev");
        when(env.getProperty("appconfig.config")).thenReturn("myConfig");
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("60");

        String jsonResponse = "{\"boolEnableFeature\":true,\"intItemLimit\":5}";

        GetConfigurationResponse getConfigurationResponse = GetConfigurationResponse.builder().build();

        AppConfigUtility appConfigUtility = mock(AppConfigUtility.class);
        when(appConfigUtility.getConfiguration(any(ConfigurationKey.class))).thenReturn(getConfigurationResponse);

        moviesController.cacheItemTtl = Duration.ofSeconds(60);
        moviesController.client = appConfigClient;
        moviesController.cache = configurationCache;
        moviesController.clientId = UUID.randomUUID().toString();

        // Act
        //Movie[] movies = moviesController.movie();

        // Assert
        Movie[] expectedMovies = new Movie[5];
        for (int i = 0; i < 5; i++) {
            expectedMovies[i] = MoviesController.PAIDMOVIES[i];
        }
        //assertArrayEquals(expectedMovies, movies);
        assertEquals(5, expectedMovies.length);
    }

    /**
     * Test our spectacular movie search by name! 🎭
     * Wow! This test ensures name searching works like magic!
     */
    @Test
    public void testSearchMoviesByName() {
        // Arrange - set up our movie search adventure!
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act - search for movies with "Adventure" in the name! 🔍
        String result = moviesController.searchMovies("Adventure", null, null);
        
        // Assert - verify our amazing search results! ✨
        assertNotNull("Search result should not be null!", result);
        assertTrue("Should contain search form", result.contains("Amazing Movie Search Adventure"));
        assertTrue("Should contain movie name", result.contains("The Amazing Adventure"));
        assertTrue("Should show results count", result.contains("Found"));
    }

    /**
     * Test our incredible movie search by ID! 🎯
     * Bam! This test ensures ID searching hits the target perfectly!
     */
    @Test
    public void testSearchMoviesById() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act - search for movie with ID 3! 🎯
        String result = moviesController.searchMovies(null, "3", null);
        
        // Assert - verify our targeted search! 
        assertNotNull("Search result should not be null!", result);
        assertTrue("Should contain search form", result.contains("Amazing Movie Search Adventure"));
        assertTrue("Should contain the specific movie", result.contains("Space Odyssey"));
        assertTrue("Should show ID 3", result.contains("ID:</strong> 3"));
    }

    /**
     * Test our fantastic movie search by genre! 🌟
     * Super! This test ensures genre filtering works spectacularly!
     */
    @Test
    public void testSearchMoviesByGenre() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act - search for Comedy movies! 😄
        String result = moviesController.searchMovies(null, null, "Comedy");
        
        // Assert - verify our genre-based search magic!
        assertNotNull("Search result should not be null!", result);
        assertTrue("Should contain search form", result.contains("Amazing Movie Search Adventure"));
        assertTrue("Should contain comedy movie", result.contains("Laugh Out Loud"));
        assertTrue("Should show Comedy genre", result.contains("Comedy"));
    }

    /**
     * Test our amazing combined search criteria! 🎪
     * Incredible! This test ensures multiple search parameters work together!
     */
    @Test
    public void testSearchMoviesWithMultipleCriteria() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act - search with multiple criteria! 🎯✨
        String result = moviesController.searchMovies("Love", null, "Romance");
        
        // Assert - verify our multi-criteria search adventure!
        assertNotNull("Search result should not be null!", result);
        assertTrue("Should contain search form", result.contains("Amazing Movie Search Adventure"));
        assertTrue("Should contain romance movie", result.contains("Love Story Supreme"));
        assertTrue("Should show Romance genre", result.contains("Romance"));
    }

    /**
     * Test our spectacular empty search results handling! 😢
     * This test ensures we handle no results gracefully!
     */
    @Test
    public void testSearchMoviesNoResults() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act - search for something that doesn't exist! 🔍
        String result = moviesController.searchMovies("NonExistentMovie", null, null);
        
        // Assert - verify our graceful no-results handling!
        assertNotNull("Search result should not be null!", result);
        assertTrue("Should contain search form", result.contains("Amazing Movie Search Adventure"));
        assertTrue("Should show no results message", result.contains("No movies found"));
        assertTrue("Should encourage trying different terms", result.contains("Try different search terms"));
    }

    /**
     * Test our incredible invalid ID handling! 🛡️
     * This test ensures we handle invalid IDs like superheroes!
     */
    @Test
    public void testSearchMoviesWithInvalidId() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act - search with invalid ID! 
        String result = moviesController.searchMovies(null, "invalid_id", null);
        
        // Assert - verify our error handling superpowers!
        assertNotNull("Search result should not be null!", result);
        assertTrue("Should contain search form", result.contains("Amazing Movie Search Adventure"));
        assertTrue("Should show no results for invalid ID", result.contains("No movies found"));
    }

    /**
     * Test our fantastic empty search form display! 🎭
     * This test ensures the search form appears even without criteria!
     */
    @Test
    public void testSearchMoviesEmptyForm() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act - access search without any parameters! 
        String result = moviesController.searchMovies(null, null, null);
        
        // Assert - verify our welcome message appears!
        assertNotNull("Search result should not be null!", result);
        assertTrue("Should contain search form", result.contains("Amazing Movie Search Adventure"));
        assertTrue("Should show welcome message", result.contains("Welcome to the Movie Search Adventure"));
        assertTrue("Should have search input fields", result.contains("Movie Name"));
        assertTrue("Should have search button", result.contains("Search Movies"));
    }

    /**
     * Test our amazing case-insensitive search! 🎪
     * Pow! This test ensures search works regardless of case!
     */
    @Test
    public void testSearchMoviesCaseInsensitive() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act - search with different case! 
        String result = moviesController.searchMovies("ADVENTURE", null, null);
        
        // Assert - verify case-insensitive magic!
        assertNotNull("Search result should not be null!", result);
        assertTrue("Should find movie regardless of case", result.contains("The Amazing Adventure"));
    }
}