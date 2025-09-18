package com.amazonaws.samples.appconfig.movies;

import com.amazonaws.samples.appconfig.utils.MovieUtils;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Arrays;
import com.amazonaws.samples.appconfig.utils.AppConfigUtility;
import com.amazonaws.samples.appconfig.cache.ConfigurationCache;
import com.amazonaws.samples.appconfig.model.ConfigurationKey;
import com.amazonaws.samples.appconfig.utils.HTMLBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.appconfig.AppConfigClient;
import software.amazon.awssdk.services.appconfig.model.GetConfigurationResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    /**
     * Static Movie Array containing all the list of Movies with genres.
     * Arrr! This be our treasure chest of movies, matey!
     */
    static final Movie[] PAIDMOVIES = {
        new Movie(1L, "The Pirate's Adventure", "Adventure"),
        new Movie(2L, "Treasure Island Quest", "Adventure"),
        new Movie(3L, "Comedy of the Seven Seas", "Comedy"),
        new Movie(4L, "Drama on the High Seas", "Drama"),
        new Movie(5L, "Fantasy Voyage", "Fantasy"),
        new Movie(6L, "Horror of the Deep", "Horror"),
        new Movie(7L, "Romance Under the Stars", "Romance"),
        new Movie(8L, "Sci-Fi Space Pirates", "Sci-Fi"),
        new Movie(9L, "Thriller on the Ocean", "Thriller"),
        new Movie(10L, "Action Heroes Unite", "Action")
    };
    public Duration cacheItemTtl = Duration.ofSeconds(30);
    private Boolean boolEnableFeature;
    private int intItemLimit;
    AppConfigClient client;
    String clientId;
    ConfigurationCache cache;

    @Autowired
    Environment env;

    /**
     * REST API method to get all the Movies based on AWS App Config parameter.
     *
     * @return List of Movies
     */
    @GetMapping("/movies/getMovies")
    public String movie() {
        logger.info("Fetching movies from AWS App Config");
        try {


        cacheItemTtl = Duration.ofSeconds(Long.parseLong(env.getProperty("appconfig.cacheTtlInSeconds")));

        final AppConfigUtility appConfigUtility = new AppConfigUtility(getOrDefault(this::getClient, this::getDefaultClient),
                getOrDefault(this::getConfigurationCache, ConfigurationCache::new),
                getOrDefault(this::getCacheItemTtl, () -> cacheItemTtl),
                getOrDefault(this::getClientId, this::getDefaultClientId));

            final String application = env.getProperty("appconfig.application");
            final String environment = env.getProperty("appconfig.environment");
            final String config = env.getProperty("appconfig.config");
        final GetConfigurationResponse response = appConfigUtility.getConfiguration(new ConfigurationKey(application, environment, config));
        final String appConfigResponse = response.content().asUtf8String();

        final JSONObject jsonResponseObject = new JSONObject(appConfigResponse);
        System.out.println("json is "+jsonResponseObject);

        JSONArray moviesArray = jsonResponseObject.getJSONArray("movies");
        System.out.println("movies array is "+moviesArray);
        List<Movie> movieList = new ArrayList<>();
        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieObj = moviesArray.getJSONObject(i);
            long id = movieObj.getLong("id");
            String movieName = movieObj.getString("movieName");
            String genre = movieObj.optString("genre", "Unknown"); // Handle missing genre gracefully
            Movie movie = new Movie(id, movieName, genre);
            movieList.add(movie);
        }
        Movie[] movies = movieList.toArray(new Movie[movieList.size()]);
        HTMLBuilder htmlBuilder = new HTMLBuilder();
        String moviesHtml = htmlBuilder.getMoviesHtml(movies);

        return moviesHtml;
        } catch (Exception e) {
            logger.error("Error fetching movies from AWS App Config", e);
            HTMLBuilder htmlBuilder = new HTMLBuilder();
            String moviesHtml = htmlBuilder.getMoviesHtml(PAIDMOVIES);
            return moviesHtml;
        }
    }

    /**
     * REST API method to search movies based on query parameters.
     * Arrr! This be our treasure hunting method, matey!
     *
     * @param name Optional movie name to search for (partial match)
     * @param id Optional movie ID to search for (exact match)
     * @param genre Optional genre to filter by (exact match)
     * @return HTML response with search results
     */
    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("Searching movies with parameters - name: {}, id: {}, genre: {}", name, id, genre);
        
        try {
            // Validate input parameters
            if (name != null && !name.trim().isEmpty() && !MovieUtils.isValidMovieName(name)) {
                logger.warn("Invalid movie name provided: {}", name);
                HTMLBuilder htmlBuilder = new HTMLBuilder();
                return htmlBuilder.getSearchResultsHtml(new Movie[0], true);
            }
            
            if (id != null && !MovieUtils.isValidMovieId(id)) {
                logger.warn("Invalid movie ID provided: {}", id);
                HTMLBuilder htmlBuilder = new HTMLBuilder();
                return htmlBuilder.getSearchResultsHtml(new Movie[0], true);
            }
            
            if (genre != null && !genre.trim().isEmpty() && !MovieUtils.isValidGenre(genre)) {
                logger.warn("Invalid genre provided: {}", genre);
                HTMLBuilder htmlBuilder = new HTMLBuilder();
                return htmlBuilder.getSearchResultsHtml(new Movie[0], true);
            }

            // Check if any search parameters were provided
            boolean hasSearchParams = (name != null && !name.trim().isEmpty()) || 
                                    (id != null) || 
                                    (genre != null && !genre.trim().isEmpty());
            
            if (!hasSearchParams) {
                // No search parameters provided, show search form
                HTMLBuilder htmlBuilder = new HTMLBuilder();
                return htmlBuilder.getSearchResultsHtml(new Movie[0], false);
            }

            // Get movies from AppConfig or fallback to static data
            Movie[] allMovies = getAllMovies();
            
            // Filter movies based on search criteria
            List<Movie> filteredMovies = Arrays.stream(allMovies)
                .filter(movie -> matchesSearchCriteria(movie, name, id, genre))
                .collect(Collectors.toList());

            Movie[] results = filteredMovies.toArray(new Movie[0]);
            
            logger.info("Search completed. Found {} movies matching criteria", results.length);
            
            HTMLBuilder htmlBuilder = new HTMLBuilder();
            return htmlBuilder.getSearchResultsHtml(results, true);
            
        } catch (Exception e) {
            logger.error("Error during movie search", e);
            HTMLBuilder htmlBuilder = new HTMLBuilder();
            return htmlBuilder.getSearchResultsHtml(new Movie[0], true);
        }
    }

    /**
     * Helper method to get all movies from AppConfig or fallback to static data
     * @return Array of all available movies
     */
    private Movie[] getAllMovies() {
        try {
            cacheItemTtl = Duration.ofSeconds(Long.parseLong(env.getProperty("appconfig.cacheTtlInSeconds")));

            final AppConfigUtility appConfigUtility = new AppConfigUtility(
                getOrDefault(this::getClient, this::getDefaultClient),
                getOrDefault(this::getConfigurationCache, ConfigurationCache::new),
                getOrDefault(this::getCacheItemTtl, () -> cacheItemTtl),
                getOrDefault(this::getClientId, this::getDefaultClientId));

            final String application = env.getProperty("appconfig.application");
            final String environment = env.getProperty("appconfig.environment");
            final String config = env.getProperty("appconfig.config");
            
            final GetConfigurationResponse response = appConfigUtility.getConfiguration(
                new ConfigurationKey(application, environment, config));
            final String appConfigResponse = response.content().asUtf8String();

            final JSONObject jsonResponseObject = new JSONObject(appConfigResponse);
            JSONArray moviesArray = jsonResponseObject.getJSONArray("movies");
            
            List<Movie> movieList = new ArrayList<>();
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObj = moviesArray.getJSONObject(i);
                long movieId = movieObj.getLong("id");
                String movieName = movieObj.getString("movieName");
                String movieGenre = movieObj.optString("genre", "Unknown");
                Movie movie = new Movie(movieId, movieName, movieGenre);
                movieList.add(movie);
            }
            
            return movieList.toArray(new Movie[0]);
            
        } catch (Exception e) {
            logger.warn("Failed to fetch movies from AppConfig, using static data", e);
            return PAIDMOVIES;
        }
    }

    /**
     * Helper method to check if a movie matches the search criteria
     * @param movie The movie to check
     * @param name Name search term (partial match)
     * @param id ID search term (exact match)
     * @param genre Genre search term (exact match)
     * @return true if the movie matches all provided criteria
     */
    private boolean matchesSearchCriteria(Movie movie, String name, Long id, String genre) {
        // Check name criteria (partial match, case-insensitive)
        if (name != null && !name.trim().isEmpty()) {
            if (!MovieUtils.matchesMovieName(movie.getMovieName(), name)) {
                return false;
            }
        }
        
        // Check ID criteria (exact match)
        if (id != null) {
            if (movie.getId() != id) {
                return false;
            }
        }
        
        // Check genre criteria (exact match, case-insensitive)
        if (genre != null && !genre.trim().isEmpty()) {
            if (!MovieUtils.matchesGenre(movie.getGenre(), genre)) {
                return false;
            }
        }
        
        return true;
    }

    @RequestMapping(value = "/movies/{movie}/edit", method = POST)
    public String processUpdateMovie(@Valid Movie movie, BindingResult result, @PathVariable("movieId") int movieId) {
        if (!MovieUtils.isValidMovieName(movie.getMovieName())) {
            result.rejectValue("name", "error.name", "Invalid movie name");
            return "editMovieForm";
        }
        final AppConfigUtility appConfigUtility = new AppConfigUtility(getOrDefault(this::getClient, this::getDefaultClient),
                getOrDefault(this::getConfigurationCache, ConfigurationCache::new),
                getOrDefault(this::getCacheItemTtl, () -> cacheItemTtl),
                getOrDefault(this::getClientId, this::getDefaultClientId));


        final String application = env.getProperty("appconfig.application");
        final String environment = env.getProperty("appconfig.environment");
        final String config = env.getProperty("appconfig.config");

        final GetConfigurationResponse response = appConfigUtility.updateConfiguration(new ConfigurationKey(application, environment, config),movie.toString());
        final String appConfigResponse = response.content().asUtf8String();

        final JSONObject jsonResponseObject = new JSONObject(appConfigResponse);
        System.out.println("json is "+jsonResponseObject);

        JSONArray moviesArray = jsonResponseObject.getJSONArray("movies");
        System.out.println("movies array is "+moviesArray);
        List<Movie> movieList = new ArrayList<>();
        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieObj = moviesArray.getJSONObject(i);
            long id = movieObj.getLong("id");
            String movieName = movieObj.getString("movieName");
            // Extract other fields as needed
            movieList.add(movie);
        }
        Movie[] movies = movieList.toArray(new Movie[movieList.size()]);
        HTMLBuilder htmlBuilder = new HTMLBuilder();
        String moviesHtml = htmlBuilder.getMoviesHtml(movies);

        return moviesHtml;

    }

    private <T> T getOrDefault(final Supplier<T> optionalGetter, final Supplier<T> defaultGetter) {
        return Optional.ofNullable(optionalGetter.get()).orElseGet(defaultGetter);
    }

    String getDefaultClientId() {
        return UUID.randomUUID().toString();
    }

    protected AppConfigClient getDefaultClient() {
        return AppConfigClient.create();
    }

    public ConfigurationCache getConfigurationCache() {
        return cache;
    }


    public AppConfigClient getClient() {
        return client;
    }


    public Duration getCacheItemTtl() {
        return cacheItemTtl;
    }

    public String getClientId() {
        return clientId;
    }


}