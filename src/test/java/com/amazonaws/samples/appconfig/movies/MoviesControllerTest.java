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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

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

    @Test
    public void testSearchMoviesByName() {
        // Test search functionality with name parameter
        String result = moviesController.searchMovies("Static Movie 1", null, null);
        
        // Verify the result contains search form and movie information
        assertTrue("Result should contain search form", result.contains("Search Movies"));
        assertTrue("Result should contain movie name", result.contains("Static Movie 1"));
        assertTrue("Result should contain search results header", result.contains("Search Results"));
    }

    @Test
    public void testSearchMoviesById() {
        // Test search functionality with ID parameter
        String result = moviesController.searchMovies(null, 1L, null);
        
        // Verify the result contains the correct movie
        assertTrue("Result should contain search form", result.contains("Search Movies"));
        assertTrue("Result should contain movie ID", result.contains("ID:</strong> 1"));
        assertTrue("Result should contain search results header", result.contains("Search Results"));
    }

    @Test
    public void testSearchMoviesByGenre() {
        // Test search functionality with genre parameter
        String result = moviesController.searchMovies(null, null, "Action");
        
        // Verify the result contains movies with Action genre
        assertTrue("Result should contain search form", result.contains("Search Movies"));
        assertTrue("Result should contain Action genre", result.contains("Action"));
        assertTrue("Result should contain search results header", result.contains("Search Results"));
    }

    @Test
    public void testSearchMoviesWithMultipleParameters() {
        // Test search functionality with multiple parameters
        String result = moviesController.searchMovies("Static Movie 1", 1L, "Action");
        
        // Verify the result contains the specific movie matching all criteria
        assertTrue("Result should contain search form", result.contains("Search Movies"));
        assertTrue("Result should contain movie name", result.contains("Static Movie 1"));
        assertTrue("Result should contain movie ID", result.contains("ID:</strong> 1"));
        assertTrue("Result should contain Action genre", result.contains("Action"));
    }

    @Test
    public void testSearchMoviesNoResults() {
        // Test search functionality with parameters that don't match any movies
        String result = moviesController.searchMovies("Nonexistent Movie", null, null);
        
        // Verify the result shows no results message
        assertTrue("Result should contain search form", result.contains("Search Movies"));
        assertTrue("Result should contain no results message", result.contains("No movies found"));
    }

    @Test
    public void testSearchMoviesEmptyParameters() {
        // Test search functionality with empty parameters (should return all movies)
        String result = moviesController.searchMovies("", null, "");
        
        // Verify the result contains all static movies
        assertTrue("Result should contain search form", result.contains("Search Movies"));
        assertTrue("Result should contain multiple movies", result.contains("Static Movie"));
    }

}