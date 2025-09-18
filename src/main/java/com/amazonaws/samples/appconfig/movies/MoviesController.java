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
     * Static Movie Array containing all the list of Movies.
     * Wow! Our fantastic movie collection with amazing genres! 🎬✨
     */
    static final Movie[] PAIDMOVIES = {
        new Movie(1L, "The Amazing Adventure", "Action"),
        new Movie(2L, "Laugh Out Loud", "Comedy"),
        new Movie(3L, "Space Odyssey", "Sci-Fi"),
        new Movie(4L, "Love Story Supreme", "Romance"),
        new Movie(5L, "Mystery of the Lost Treasure", "Mystery"),
        new Movie(6L, "Horror House", "Horror"),
        new Movie(7L, "Epic Fantasy Quest", "Fantasy"),
        new Movie(8L, "Documentary Delight", "Documentary"),
        new Movie(9L, "Animated Wonder", "Animation"),
        new Movie(10L, "Thrilling Chase", "Thriller")
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
            // Create our fantastic movie character with all its amazing details! 🎭
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
     * Spectacular movie search endpoint! 🔍✨
     * Search for movies by name, id, or genre - it's like a treasure hunt for movies!
     * 
     * @param name Movie name to search for (optional)
     * @param id Movie ID to search for (optional) 
     * @param genre Movie genre to search for (optional)
     * @return HTML response with search results and search form
     */
    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("Pow! Starting movie search adventure with name: {}, id: {}, genre: {}", name, id, genre);
        
        try {
            // Get all movies first - our treasure chest of movies! 🎬
            Movie[] allMovies = getAllMoviesFromConfig();
            
            // Filter movies based on search criteria - the magic filtering spell! ✨
            List<Movie> filteredMovies = filterMovies(allMovies, name, id, genre);
            
            // Create spectacular HTML response with search form and results! 🎭
            HTMLBuilder htmlBuilder = new HTMLBuilder();
            String searchHtml = htmlBuilder.getSearchHtml(filteredMovies.toArray(new Movie[0]), name, id, genre);
            
            logger.info("Bam! Found {} amazing movies matching the search criteria!", filteredMovies.size());
            return searchHtml;
            
        } catch (Exception e) {
            logger.error("Oh no! Plot twist in our movie search adventure!", e);
            // Fallback to static movies for search
            List<Movie> filteredMovies = filterMovies(PAIDMOVIES, name, id, genre);
            HTMLBuilder htmlBuilder = new HTMLBuilder();
            return htmlBuilder.getSearchHtml(filteredMovies.toArray(new Movie[0]), name, id, genre);
        }
    }

    /**
     * Get all movies from AppConfig - our movie treasure retrieval mission! 🏴‍☠️
     */
    private Movie[] getAllMoviesFromConfig() throws Exception {
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
    }

    /**
     * Filter movies based on search criteria - our incredible filtering adventure! 🎯
     * This method is like a magical sieve that finds exactly what you're looking for!
     */
    private List<Movie> filterMovies(Movie[] movies, String name, String id, String genre) {
        List<Movie> movieList = Arrays.asList(movies);
        
        return movieList.stream()
                .filter(movie -> {
                    // Name filter - case insensitive search! Whoosh! 💨
                    if (name != null && !name.trim().isEmpty()) {
                        if (!movie.getMovieName().toLowerCase().contains(name.toLowerCase().trim())) {
                            return false;
                        }
                    }
                    
                    // ID filter - exact match for our movie ID treasure! 🎯
                    if (id != null && !id.trim().isEmpty()) {
                        try {
                            long searchId = Long.parseLong(id.trim());
                            if (movie.getId() != searchId) {
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            logger.warn("Invalid ID format in search: {}", id);
                            return false;
                        }
                    }
                    
                    // Genre filter - case insensitive genre matching! Super! 🌟
                    if (genre != null && !genre.trim().isEmpty()) {
                        if (!movie.getGenre().toLowerCase().contains(genre.toLowerCase().trim())) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
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