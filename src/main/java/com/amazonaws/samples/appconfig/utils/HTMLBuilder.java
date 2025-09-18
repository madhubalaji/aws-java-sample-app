package com.amazonaws.samples.appconfig.utils;
import com.amazonaws.samples.appconfig.movies.Movie;

/**
 * Amazing HTML Builder for our spectacular movie adventures! 🎬✨
 * This fantastic utility creates incredible HTML responses!
 */
public class HTMLBuilder {

    /**
     * Create spectacular HTML for movie list display! 🎭
     */
    public String getMoviesHtml(Movie[] movies) {
        String htmlBuilder = "<div id='movies-container'>"
                + "<h1>🎬 FREE Movie List for this Month! 🎬</h1>"
                + getMovieItemsHtml(movies)
                + "<hr>"
                + "<hr width=\"100%\" size=\"2\" color=\"blue\" noshade>"
                + "</div>";
        return htmlBuilder;
    }

    /**
     * Create incredible HTML for movie search with form and results! 🔍✨
     * This is where the magic happens - search form plus amazing results!
     */
    public String getSearchHtml(Movie[] movies, String searchName, String searchId, String searchGenre) {
        StringBuilder htmlBuilder = new StringBuilder();
        
        // Add spectacular CSS styling! 🎨
        htmlBuilder.append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }")
                .append(".search-container { background: rgba(255,255,255,0.1); padding: 20px; border-radius: 15px; margin-bottom: 20px; backdrop-filter: blur(10px); }")
                .append(".search-form { display: flex; flex-wrap: wrap; gap: 15px; align-items: center; }")
                .append(".search-input { padding: 10px; border: none; border-radius: 8px; background: rgba(255,255,255,0.9); color: #333; }")
                .append(".search-button { padding: 12px 25px; background: #ff6b6b; color: white; border: none; border-radius: 8px; cursor: pointer; font-weight: bold; transition: all 0.3s; }")
                .append(".search-button:hover { background: #ff5252; transform: translateY(-2px); box-shadow: 0 4px 8px rgba(0,0,0,0.2); }")
                .append(".movie-item { background: rgba(255,255,255,0.1); margin: 10px 0; padding: 15px; border-radius: 10px; backdrop-filter: blur(5px); }")
                .append(".movie-title { color: #ffd700; font-size: 1.2em; font-weight: bold; }")
                .append(".movie-genre { color: #98fb98; font-style: italic; }")
                .append(".no-results { text-align: center; padding: 40px; font-size: 1.2em; color: #ffcccb; }")
                .append(".results-count { background: rgba(255,255,255,0.2); padding: 10px; border-radius: 8px; margin-bottom: 20px; text-align: center; }")
                .append("</style>");
        
        // Start the main container
        htmlBuilder.append("<div id='search-container'>");
        
        // Add amazing search form! 🎯
        htmlBuilder.append("<div class='search-container'>")
                .append("<h1>🔍 Amazing Movie Search Adventure! 🎬</h1>")
                .append("<form class='search-form' method='GET' action='/movies/search'>")
                .append("<input type='text' name='name' class='search-input' placeholder='🎭 Movie Name...' value='")
                .append(searchName != null ? escapeHtml(searchName) : "").append("'>")
                .append("<input type='text' name='id' class='search-input' placeholder='🎯 Movie ID...' value='")
                .append(searchId != null ? escapeHtml(searchId) : "").append("'>")
                .append("<input type='text' name='genre' class='search-input' placeholder='🌟 Genre...' value='")
                .append(searchGenre != null ? escapeHtml(searchGenre) : "").append("'>")
                .append("<button type='submit' class='search-button'>🚀 Search Movies!</button>")
                .append("</form>")
                .append("</div>");
        
        // Show search results count and criteria
        if (hasSearchCriteria(searchName, searchId, searchGenre)) {
            htmlBuilder.append("<div class='results-count'>")
                    .append("🎉 Found ").append(movies.length).append(" amazing movies")
                    .append(getSearchCriteriaText(searchName, searchId, searchGenre))
                    .append("!</div>");
        }
        
        // Display movie results or no results message
        if (movies.length > 0) {
            htmlBuilder.append(getSearchMovieItemsHtml(movies));
        } else if (hasSearchCriteria(searchName, searchId, searchGenre)) {
            htmlBuilder.append("<div class='no-results'>")
                    .append("😢 Oh no! No movies found matching your search criteria.<br>")
                    .append("🎬 Try different search terms for more spectacular results!")
                    .append("</div>");
        } else {
            htmlBuilder.append("<div class='no-results'>")
                    .append("🎭 Welcome to the Movie Search Adventure!<br>")
                    .append("✨ Enter your search criteria above to find amazing movies!")
                    .append("</div>");
        }
        
        htmlBuilder.append("</div>");
        return htmlBuilder.toString();
    }

    /**
     * Generate movie items HTML for search results with genre info! 🎬
     */
    private String getSearchMovieItemsHtml(Movie[] movies) {
        StringBuilder movieItemsHtml = new StringBuilder();
        for (Movie movie : movies) {
            movieItemsHtml.append("<div class='movie-item'>")
                    .append("<div class='movie-title'>🎬 ").append(escapeHtml(movie.getMovieName())).append("</div>")
                    .append("<p><strong>🎯 ID:</strong> ").append(movie.getId()).append("</p>")
                    .append("<p><strong>🌟 Genre:</strong> <span class='movie-genre'>").append(escapeHtml(movie.getGenre())).append("</span></p>")
                    .append("</div>");
        }
        return movieItemsHtml.toString();
    }

    /**
     * Generate movie items HTML for regular movie list display
     */
    private static String getMovieItemsHtml(Movie[] movies) {
        StringBuilder movieItemsHtml = new StringBuilder();
        for (Movie movie : movies) {
            movieItemsHtml.append("<div class='movie-item'>"
                    + "<p>ID: ").append(movie.getId()).append("</p>"
                    + "<h3>").append(movie.getMovieName()).append("</h3>");
            
            // Add genre info if available
            if (movie.getGenre() != null && !movie.getGenre().equals("Unknown")) {
                movieItemsHtml.append("<p><em>Genre: ").append(movie.getGenre()).append("</em></p>");
            }
            
            movieItemsHtml.append("<hr width=\"100%\" size=\"2\" color=\"blue\" noshade>"
                    + "</div>");
        }
        return movieItemsHtml.toString();
    }

    /**
     * Check if any search criteria is provided
     */
    private boolean hasSearchCriteria(String name, String id, String genre) {
        return (name != null && !name.trim().isEmpty()) ||
               (id != null && !id.trim().isEmpty()) ||
               (genre != null && !genre.trim().isEmpty());
    }

    /**
     * Generate search criteria text for display
     */
    private String getSearchCriteriaText(String name, String id, String genre) {
        StringBuilder criteria = new StringBuilder();
        boolean hasAny = false;
        
        if (name != null && !name.trim().isEmpty()) {
            criteria.append(" with name containing '").append(escapeHtml(name)).append("'");
            hasAny = true;
        }
        
        if (id != null && !id.trim().isEmpty()) {
            if (hasAny) criteria.append(" and");
            criteria.append(" with ID '").append(escapeHtml(id)).append("'");
            hasAny = true;
        }
        
        if (genre != null && !genre.trim().isEmpty()) {
            if (hasAny) criteria.append(" and");
            criteria.append(" in genre '").append(escapeHtml(genre)).append("'");
        }
        
        return criteria.toString();
    }

    /**
     * Escape HTML characters to prevent XSS - our security superhero! 🛡️
     */
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }
}
