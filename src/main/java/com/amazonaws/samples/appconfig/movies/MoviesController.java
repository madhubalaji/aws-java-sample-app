package com.amazonaws.samples.appconfig.movies;

import com.amazonaws.samples.appconfig.utils.MovieUtils;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
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
     */
    static final Movie[] PAIDMOVIES = {
        new Movie(1L, "Static Movie 1", "Action"),
        new Movie(2L, "Static Movie 2", "Comedy"),
        new Movie(3L, "Static Movie 3", "Drama"),
        new Movie(4L, "Static Movie 4", "Horror"),
        new Movie(5L, "Static Movie 5", "Romance"),
        new Movie(6L, "Static Movie 6", "Thriller"),
        new Movie(7L, "Static Movie 7", "Sci-Fi"),
        new Movie(8L, "Static Movie 8", "Fantasy"),
        new Movie(9L, "Static Movie 9", "Adventure"),
        new Movie(10L, "Static Movie 10", "Mystery")
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
            String genre = movieObj.optString("genre", "Unknown"); // Use optString with default value
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

    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        // Validate and sanitize input parameters
        if (name != null) {
            name = name.trim();
            if (name.length() > 200) {
                return getErrorResponse("Movie name search term too long");
            }
        }
        
        if (id != null && id < 0) {
            return getErrorResponse("Invalid movie ID");
        }
        
        if (genre != null) {
            genre = genre.trim();
            if (genre.length() > 50) {
                return getErrorResponse("Genre search term too long");
            }
        }
        
        logger.info("Searching movies with parameters - name: {}, id: {}, genre: {}", name, id, genre);
        
        try {
            // Get movies from AppConfig
            List<Movie> allMovies = getAllMoviesFromConfig();
            
            // Filter movies based on search criteria
            List<Movie> filteredMovies = filterMovies(allMovies, name, id, genre);
            
            // Convert to array
            Movie[] moviesArray = filteredMovies.toArray(new Movie[0]);
            
            // Generate HTML response
            HTMLBuilder htmlBuilder = new HTMLBuilder();
            String searchQuery = buildSearchQuery(name, id, genre);
            return htmlBuilder.getSearchResultsHtml(moviesArray, searchQuery);
            
        } catch (Exception e) {
            logger.error("Error searching movies", e);
            // Fallback to static movies and filter them
            List<Movie> staticMoviesList = Arrays.asList(PAIDMOVIES);
            List<Movie> filteredMovies = filterMovies(staticMoviesList, name, id, genre);
            Movie[] moviesArray = filteredMovies.toArray(new Movie[0]);
            
            HTMLBuilder htmlBuilder = new HTMLBuilder();
            String searchQuery = buildSearchQuery(name, id, genre);
            return htmlBuilder.getSearchResultsHtml(moviesArray, searchQuery);
        }
    }

    /**
     * Helper method to get all movies from AppConfig
     */
    private List<Movie> getAllMoviesFromConfig() throws Exception {
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
        
        return movieList;
    }

    /**
     * Helper method to filter movies based on search criteria
     */
    private List<Movie> filterMovies(List<Movie> movies, String name, Long id, String genre) {
        return movies.stream()
                .filter(movie -> {
                    // Filter by name (case-insensitive partial match)
                    if (name != null && !name.trim().isEmpty()) {
                        if (!movie.getMovieName().toLowerCase().contains(name.toLowerCase().trim())) {
                            return false;
                        }
                    }
                    
                    // Filter by ID (exact match)
                    if (id != null && id > 0) {
                        if (movie.getId() != id) {
                            return false;
                        }
                    }
                    
                    // Filter by genre (case-insensitive partial match)
                    if (genre != null && !genre.trim().isEmpty()) {
                        if (!movie.getGenre().toLowerCase().contains(genre.toLowerCase().trim())) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * Helper method to build search query string for display
     */
    private String buildSearchQuery(String name, Long id, String genre) {
        List<String> queryParts = new ArrayList<>();
        
        if (name != null && !name.trim().isEmpty()) {
            queryParts.add("name: " + name.trim());
        }
        if (id != null && id > 0) {
            queryParts.add("id: " + id);
        }
        if (genre != null && !genre.trim().isEmpty()) {
            queryParts.add("genre: " + genre.trim());
        }
        
        return queryParts.isEmpty() ? "" : String.join(", ", queryParts);
    }

    /**
     * Helper method to generate error response HTML
     */
    private String getErrorResponse(String errorMessage) {
        return "<div style='color: red; padding: 20px; border: 1px solid red; margin: 20px; border-radius: 5px;'>"
                + "<h2>Error</h2>"
                + "<p>" + errorMessage + "</p>"
                + "<a href='/movies/getMovies'>Back to Movies</a>"
                + "</div>";
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
            String genre = movieObj.optString("genre", "Unknown");
            Movie movieFromJson = new Movie(id, movieName, genre);
            movieList.add(movieFromJson);
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