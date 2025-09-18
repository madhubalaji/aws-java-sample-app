package com.amazonaws.samples.appconfig.utils;
import com.amazonaws.samples.appconfig.movies.Movie;

/**
 * HTML Builder utility for generating movie-related HTML content
 * Arrr! This be our ship's carpenter, building fine HTML structures, matey!
 */
public class HTMLBuilder {

    /**
     * Generates HTML for displaying movies with search form
     * @param movies Array of movies to display
     * @return Complete HTML string with search form and movie list
     */
    public String getMoviesHtml(Movie[] movies) {
        String htmlBuilder = "<div id='movies-container'>"
                + getSearchFormHtml()
                + "<h1> FREE Movie List for this Month</h1>"
                + getMovieItemsHtml(movies)
                + "<hr>"
                + "<hr width=\"100%\" size=\"2\" color=\"blue\" noshade>"
                + "</div>";
        return htmlBuilder;
    }

    /**
     * Generates HTML for search results with search form
     * @param movies Array of movies to display
     * @param searchPerformed Whether a search was performed
     * @return Complete HTML string with search form and results
     */
    public String getSearchResultsHtml(Movie[] movies, boolean searchPerformed) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<div id='movies-container'>");
        htmlBuilder.append(getSearchFormHtml());
        
        if (searchPerformed) {
            if (movies.length > 0) {
                htmlBuilder.append("<h1>Search Results (").append(movies.length).append(" movies found)</h1>");
                htmlBuilder.append(getMovieItemsHtml(movies));
            } else {
                htmlBuilder.append("<h1>Search Results</h1>");
                htmlBuilder.append("<div class='no-results'>");
                htmlBuilder.append("<p><strong>Ahoy! No movies found matching your search criteria, matey!</strong></p>");
                htmlBuilder.append("<p>Try adjusting your search terms and sail again!</p>");
                htmlBuilder.append("</div>");
            }
        } else {
            htmlBuilder.append("<h1>Movie Search</h1>");
            htmlBuilder.append("<p>Use the search form above to find your treasure movies!</p>");
        }
        
        htmlBuilder.append("<hr>");
        htmlBuilder.append("<hr width=\"100%\" size=\"2\" color=\"blue\" noshade>");
        htmlBuilder.append("</div>");
        return htmlBuilder.toString();
    }

    /**
     * Generates the search form HTML
     * @return HTML string for the search form
     */
    private String getSearchFormHtml() {
        return "<div class='search-form-container' style='background-color: #f0f8ff; padding: 20px; margin-bottom: 20px; border: 2px solid #4169e1; border-radius: 10px;'>"
                + "<h2 style='color: #4169e1; margin-top: 0;'>🎬 Movie Search Treasure Hunt 🏴‍☠️</h2>"
                + "<form action='/movies/search' method='get' style='display: flex; flex-wrap: wrap; gap: 15px; align-items: end;'>"
                + "<div style='flex: 1; min-width: 200px;'>"
                + "<label for='name' style='display: block; font-weight: bold; color: #2c3e50; margin-bottom: 5px;'>Movie Name:</label>"
                + "<input type='text' id='name' name='name' placeholder='Enter movie name...' "
                + "style='width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px;'>"
                + "</div>"
                + "<div style='flex: 0 0 120px;'>"
                + "<label for='id' style='display: block; font-weight: bold; color: #2c3e50; margin-bottom: 5px;'>Movie ID:</label>"
                + "<input type='number' id='id' name='id' placeholder='ID' min='1' "
                + "style='width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px;'>"
                + "</div>"
                + "<div style='flex: 1; min-width: 150px;'>"
                + "<label for='genre' style='display: block; font-weight: bold; color: #2c3e50; margin-bottom: 5px;'>Genre:</label>"
                + "<select id='genre' name='genre' "
                + "style='width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 14px;'>"
                + "<option value=''>All Genres</option>"
                + "<option value='Action'>Action</option>"
                + "<option value='Adventure'>Adventure</option>"
                + "<option value='Comedy'>Comedy</option>"
                + "<option value='Drama'>Drama</option>"
                + "<option value='Fantasy'>Fantasy</option>"
                + "<option value='Horror'>Horror</option>"
                + "<option value='Romance'>Romance</option>"
                + "<option value='Sci-Fi'>Sci-Fi</option>"
                + "<option value='Thriller'>Thriller</option>"
                + "<option value='Unknown'>Unknown</option>"
                + "</select>"
                + "</div>"
                + "<div style='flex: 0 0 auto;'>"
                + "<button type='submit' "
                + "style='background-color: #4169e1; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; font-size: 14px; font-weight: bold;'>"
                + "🔍 Search Movies"
                + "</button>"
                + "</div>"
                + "<div style='flex: 0 0 auto;'>"
                + "<a href='/movies/getMovies' "
                + "style='background-color: #28a745; color: white; padding: 10px 20px; border: none; border-radius: 4px; text-decoration: none; font-size: 14px; font-weight: bold; display: inline-block;'>"
                + "📋 View All Movies"
                + "</a>"
                + "</div>"
                + "</form>"
                + "</div>";
    }

    /**
     * Generates HTML for individual movie items
     * @param movies Array of movies to display
     * @return HTML string for movie items
     */
    private static String getMovieItemsHtml(Movie[] movies) {
        StringBuilder movieItemsHtml = new StringBuilder();
        for (Movie movie : movies) {
            movieItemsHtml.append("<div class='movie-item' style='background-color: #f9f9f9; padding: 15px; margin-bottom: 10px; border-left: 4px solid #4169e1; border-radius: 5px;'>"
                    + "<p style='margin: 0 0 5px 0; color: #666;'><strong>ID:</strong> ").append(movie.getId()).append("</p>"
                    + "<h3 style='margin: 0 0 5px 0; color: #2c3e50;'>").append(movie.getMovieName()).append("</h3>"
                    + "<p style='margin: 0; color: #4169e1;'><strong>Genre:</strong> ").append(movie.getGenre()).append("</p>"
                    + "<hr width=\"100%\" size=\"1\" color=\"#ddd\" noshade style='margin: 10px 0;'>"
                    + "</div>");
        }
        return movieItemsHtml.toString();
    }
}
