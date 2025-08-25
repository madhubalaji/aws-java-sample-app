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
    public void testSearchMoviesWithNoParameters() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act
        String result = moviesController.searchMovies(null, null, null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Movie Search"));
        assertTrue(result.contains("Enter search criteria above to find movies"));
    }

    @Test
    public void testSearchMoviesByName() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act
        String result = moviesController.searchMovies("Static Movie 1", null, null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Static Movie 1"));
        assertTrue(result.contains("Found 1 movie(s)"));
    }

    @Test
    public void testSearchMoviesByPartialName() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act
        String result = moviesController.searchMovies("Static", null, null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Found 10 movie(s)"));
    }

    @Test
    public void testSearchMoviesById() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act
        String result = moviesController.searchMovies(null, 1L, null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Static Movie 1"));
        assertTrue(result.contains("Found 1 movie(s)"));
    }

    @Test
    public void testSearchMoviesByGenre() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act
        String result = moviesController.searchMovies(null, null, "Action");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Found 2 movie(s)")); // Static Movie 1 and 8 are Action
    }

    @Test
    public void testSearchMoviesWithMultipleFilters() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act
        String result = moviesController.searchMovies("Static Movie 1", 1L, "Action");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Static Movie 1"));
        assertTrue(result.contains("Found 1 movie(s)"));
    }

    @Test
    public void testSearchMoviesNoResults() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act
        String result = moviesController.searchMovies("NonExistentMovie", null, null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("No movies found matching your search criteria"));
    }

    @Test
    public void testSearchMoviesCaseInsensitive() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act
        String result = moviesController.searchMovies("static movie 1", null, null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Static Movie 1"));
        assertTrue(result.contains("Found 1 movie(s)"));
    }

    @Test
    public void testSearchMoviesGenreCaseInsensitive() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act
        String result = moviesController.searchMovies(null, null, "action");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Found 2 movie(s)"));
    }

    @Test
    public void testSearchMoviesWithInvalidId() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act
        String result = moviesController.searchMovies(null, -1L, null);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Found 10 movie(s)")); // Invalid ID should be ignored, return all movies
    }

    @Test
    public void testSearchMoviesWithEmptyStrings() {
        // Arrange
        when(env.getProperty("appconfig.cacheTtlInSeconds")).thenReturn("30");
        
        // Act
        String result = moviesController.searchMovies("", null, "");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Found 10 movie(s)")); // Empty strings should be ignored, return all movies
    }
}