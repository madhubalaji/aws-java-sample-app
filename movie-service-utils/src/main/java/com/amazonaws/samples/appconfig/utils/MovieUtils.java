package com.amazonaws.samples.appconfig.utils;

/**
 * Utility class for movie validation and operations
 * Arrr! These be the crew members that help us validate our treasure, matey!
 */
public class MovieUtils {
    
    /**
     * Validates if a movie name meets the required criteria
     * @param movieName The name of the movie to validate
     * @param movieId The ID of the movie to validate
     * @return true if the movie is valid, false otherwise
     */
    public static boolean isValidMovie(String movieName, int movieId) {
        if (movieName == null || movieName.trim().isEmpty()) {
            return false;
        }

        if(movieId <= 0){
            return false;
        }
        // Movie name should be between 1 and 200 characters
        return movieName.trim().length() > 0 && movieName.trim().length() <= 200;
    }
    
    /**
     * Validates if a movie name meets the required criteria
     * @param movieName The name of the movie to validate
     * @return true if the movie name is valid, false otherwise
     */
    public static boolean isValidMovieName(String movieName) {
        if (movieName == null || movieName.trim().isEmpty()) {
            return false;
        }
        // Movie name should be between 1 and 200 characters
        return movieName.trim().length() > 0 && movieName.trim().length() <= 200;
    }
    
    /**
     * Validates if a movie ID is valid
     * @param movieId The ID to validate
     * @return true if the ID is valid, false otherwise
     */
    public static boolean isValidMovieId(long movieId) {
        return movieId > 0;
    }
    
    /**
     * Validates if a genre is valid
     * @param genre The genre to validate
     * @return true if the genre is valid, false otherwise
     */
    public static boolean isValidGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            return false;
        }
        // Genre should be between 1 and 50 characters
        return genre.trim().length() > 0 && genre.trim().length() <= 50;
    }
    
    /**
     * Performs case-insensitive string matching for movie names
     * @param movieName The movie name to check
     * @param searchTerm The search term to match against
     * @return true if the movie name contains the search term (case-insensitive)
     */
    public static boolean matchesMovieName(String movieName, String searchTerm) {
        if (movieName == null || searchTerm == null) {
            return false;
        }
        return movieName.toLowerCase().contains(searchTerm.toLowerCase().trim());
    }
    
    /**
     * Performs case-insensitive string matching for genres
     * @param genre The genre to check
     * @param searchTerm The search term to match against
     * @return true if the genre matches the search term (case-insensitive)
     */
    public static boolean matchesGenre(String genre, String searchTerm) {
        if (genre == null || searchTerm == null) {
            return false;
        }
        return genre.toLowerCase().equals(searchTerm.toLowerCase().trim());
    }
}