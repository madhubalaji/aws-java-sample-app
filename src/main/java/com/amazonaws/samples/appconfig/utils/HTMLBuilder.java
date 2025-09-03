package com.amazonaws.samples.appconfig.utils;
import com.amazonaws.samples.appconfig.movies.Movie;
public class HTMLBuilder {

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

    public String getSearchResultsHtml(Movie[] movies, String searchQuery) {
        String htmlBuilder = "<div id='movies-container'>"
                + getSearchFormHtml()
                + "<h1>Search Results" + (searchQuery != null && !searchQuery.trim().isEmpty() ? " for: " + searchQuery : "") + "</h1>"
                + (movies.length > 0 ? getMovieItemsHtml(movies) : getNoResultsHtml())
                + "<hr>"
                + "<hr width=\"100%\" size=\"2\" color=\"blue\" noshade>"
                + "</div>";
        return htmlBuilder;
    }

    private static String getSearchFormHtml() {
        return "<div id='search-form' style='background-color: #f0f0f0; padding: 20px; margin-bottom: 20px; border-radius: 5px;'>"
                + "<h2>Search Movies</h2>"
                + "<form action='/movies/search' method='get' style='display: flex; flex-wrap: wrap; gap: 10px; align-items: end;'>"
                + "<div style='display: flex; flex-direction: column;'>"
                + "<label for='name' style='margin-bottom: 5px; font-weight: bold;'>Movie Name:</label>"
                + "<input type='text' id='name' name='name' placeholder='Enter movie name' style='padding: 8px; border: 1px solid #ccc; border-radius: 3px;'>"
                + "</div>"
                + "<div style='display: flex; flex-direction: column;'>"
                + "<label for='id' style='margin-bottom: 5px; font-weight: bold;'>Movie ID:</label>"
                + "<input type='number' id='id' name='id' placeholder='Enter movie ID' style='padding: 8px; border: 1px solid #ccc; border-radius: 3px;'>"
                + "</div>"
                + "<div style='display: flex; flex-direction: column;'>"
                + "<label for='genre' style='margin-bottom: 5px; font-weight: bold;'>Genre:</label>"
                + "<input type='text' id='genre' name='genre' placeholder='Enter genre' style='padding: 8px; border: 1px solid #ccc; border-radius: 3px;'>"
                + "</div>"
                + "<button type='submit' style='padding: 8px 16px; background-color: #007bff; color: white; border: none; border-radius: 3px; cursor: pointer;'>Search</button>"
                + "<button type='button' onclick='clearForm()' style='padding: 8px 16px; background-color: #6c757d; color: white; border: none; border-radius: 3px; cursor: pointer;'>Clear</button>"
                + "</form>"
                + "<script>"
                + "function clearForm() {"
                + "  document.getElementById('name').value = '';"
                + "  document.getElementById('id').value = '';"
                + "  document.getElementById('genre').value = '';"
                + "}"
                + "</script>"
                + "</div>";
    }

    private static String getMovieItemsHtml(Movie[] movies) {
        StringBuilder movieItemsHtml = new StringBuilder();
        for (Movie movie : movies) {
            movieItemsHtml.append("<div class='movie-item' style='border: 1px solid #ddd; padding: 15px; margin: 10px 0; border-radius: 5px; background-color: #fafafa;'>"
                    + "<p><strong>ID:</strong> ").append(movie.getId()).append("</p>"
                    + "<h3 style='color: #333; margin: 10px 0;'>").append(movie.getMovieName()).append("</h3>"
                    + "<p><strong>Genre:</strong> ").append(movie.getGenre()).append("</p>"
                    + "<hr width=\"100%\" size=\"1\" color=\"#ddd\" noshade>"
                    + "</div>");
        }
        return movieItemsHtml.toString();
    }

    private static String getNoResultsHtml() {
        return "<div style='text-align: center; padding: 40px; background-color: #f8f9fa; border-radius: 5px; margin: 20px 0;'>"
                + "<h3 style='color: #6c757d;'>No movies found</h3>"
                + "<p style='color: #6c757d;'>Try adjusting your search criteria or browse all movies.</p>"
                + "<a href='/movies/getMovies' style='color: #007bff; text-decoration: none;'>View All Movies</a>"
                + "</div>";
    }
}
