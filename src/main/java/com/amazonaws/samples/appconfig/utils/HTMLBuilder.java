package com.amazonaws.samples.appconfig.utils;
import com.amazonaws.samples.appconfig.movies.Movie;
public class HTMLBuilder {

    public String getMoviesHtml(Movie[] movies) {
        String htmlBuilder = "<div id='movies-container'>"
                + "<h1> FREE Movie List for this Month</h1>"
                + getMovieItemsHtml(movies)
                + "<hr>"
                + "<hr width=\"100%\" size=\"2\" color=\"blue\" noshade>"
                + "</div>";
        return htmlBuilder;
    }

    /**
     * Generates HTML for the search page including search form and results
     */
    public String getSearchPageHtml(Movie[] movies, String searchName, Long searchId, String searchGenre) {
        StringBuilder htmlBuilder = new StringBuilder();
        
        // Add CSS styles
        htmlBuilder.append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 20px; }")
                .append(".search-form { background-color: #f5f5f5; padding: 20px; border-radius: 5px; margin-bottom: 20px; }")
                .append(".form-group { margin-bottom: 15px; }")
                .append(".form-group label { display: inline-block; width: 100px; font-weight: bold; }")
                .append(".form-group input { padding: 8px; border: 1px solid #ddd; border-radius: 3px; width: 200px; }")
                .append(".search-button { background-color: #007bff; color: white; padding: 10px 20px; border: none; border-radius: 3px; cursor: pointer; }")
                .append(".search-button:hover { background-color: #0056b3; }")
                .append(".clear-button { background-color: #6c757d; color: white; padding: 10px 20px; border: none; border-radius: 3px; cursor: pointer; margin-left: 10px; }")
                .append(".clear-button:hover { background-color: #545b62; }")
                .append(".movie-item { border: 1px solid #ddd; padding: 15px; margin-bottom: 10px; border-radius: 5px; }")
                .append(".movie-id { color: #666; font-size: 14px; }")
                .append(".movie-name { color: #333; margin: 5px 0; }")
                .append(".movie-genre { color: #007bff; font-style: italic; }")
                .append(".no-results { text-align: center; color: #666; font-style: italic; padding: 20px; }")
                .append(".results-count { color: #666; margin-bottom: 15px; }")
                .append("</style>");
        
        htmlBuilder.append("<div id='search-container'>");
        htmlBuilder.append("<h1>Movie Search</h1>");
        
        // Add search form
        htmlBuilder.append(getSearchFormHtml(searchName, searchId, searchGenre));
        
        // Add results section
        if (movies.length > 0) {
            htmlBuilder.append("<div class='results-count'>Found ").append(movies.length).append(" movie(s)</div>");
            htmlBuilder.append(getMovieItemsHtmlWithGenre(movies));
        } else {
            // Check if any search parameters were provided
            boolean hasSearchParams = (searchName != null && !searchName.trim().isEmpty()) ||
                                    (searchId != null && searchId > 0) ||
                                    (searchGenre != null && !searchGenre.trim().isEmpty());
            
            if (hasSearchParams) {
                htmlBuilder.append("<div class='no-results'>No movies found matching your search criteria.</div>");
            } else {
                htmlBuilder.append("<div class='no-results'>Enter search criteria above to find movies.</div>");
            }
        }
        
        htmlBuilder.append("</div>");
        return htmlBuilder.toString();
    }

    /**
     * Generates the search form HTML
     */
    private String getSearchFormHtml(String searchName, Long searchId, String searchGenre) {
        StringBuilder formBuilder = new StringBuilder();
        
        formBuilder.append("<form class='search-form' method='GET' action='/movies/search'>");
        
        formBuilder.append("<div class='form-group'>");
        formBuilder.append("<label for='name'>Movie Name:</label>");
        formBuilder.append("<input type='text' id='name' name='name' placeholder='Enter movie name...' value='")
                .append(searchName != null ? escapeHtml(searchName) : "").append("'>");
        formBuilder.append("</div>");
        
        formBuilder.append("<div class='form-group'>");
        formBuilder.append("<label for='id'>Movie ID:</label>");
        formBuilder.append("<input type='number' id='id' name='id' placeholder='Enter movie ID...' value='")
                .append(searchId != null ? searchId : "").append("'>");
        formBuilder.append("</div>");
        
        formBuilder.append("<div class='form-group'>");
        formBuilder.append("<label for='genre'>Genre:</label>");
        formBuilder.append("<input type='text' id='genre' name='genre' placeholder='Enter genre...' value='")
                .append(searchGenre != null ? escapeHtml(searchGenre) : "").append("'>");
        formBuilder.append("</div>");
        
        formBuilder.append("<div class='form-group'>");
        formBuilder.append("<button type='submit' class='search-button'>Search Movies</button>");
        formBuilder.append("<button type='button' class='clear-button' onclick='clearForm()'>Clear</button>");
        formBuilder.append("</div>");
        
        formBuilder.append("</form>");
        
        // Add JavaScript for clear functionality
        formBuilder.append("<script>");
        formBuilder.append("function clearForm() {");
        formBuilder.append("  document.getElementById('name').value = '';");
        formBuilder.append("  document.getElementById('id').value = '';");
        formBuilder.append("  document.getElementById('genre').value = '';");
        formBuilder.append("}");
        formBuilder.append("</script>");
        
        return formBuilder.toString();
    }

    private static String getMovieItemsHtml(Movie[] movies) {
        StringBuilder movieItemsHtml = new StringBuilder();
        for (Movie movie : movies) {
            movieItemsHtml.append("<div class='movie-item'>"
                    + "<p>ID: ").append(movie.getId()).append("</p>"
                    + "<h3>").append(movie.getMovieName()).append("</h3>"
                    + "<hr width=\"100%\" size=\"2\" color=\"blue\" noshade>"
                    + "</div>");
        }
        return movieItemsHtml.toString();
    }

    /**
     * Generates movie items HTML with genre information
     */
    private static String getMovieItemsHtmlWithGenre(Movie[] movies) {
        StringBuilder movieItemsHtml = new StringBuilder();
        for (Movie movie : movies) {
            movieItemsHtml.append("<div class='movie-item'>");
            movieItemsHtml.append("<div class='movie-id'>ID: ").append(movie.getId()).append("</div>");
            movieItemsHtml.append("<h3 class='movie-name'>").append(escapeHtml(movie.getMovieName())).append("</h3>");
            movieItemsHtml.append("<div class='movie-genre'>Genre: ").append(escapeHtml(movie.getGenre())).append("</div>");
            movieItemsHtml.append("</div>");
        }
        return movieItemsHtml.toString();
    }

    /**
     * Escapes HTML special characters to prevent XSS
     */
    private static String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }
}
