package com.amazonaws.samples.appconfig.utils;

public class MovieUtils {
    
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
     * Validates if a movie name and ID meet the required criteria
     * @param movieName The name of the movie to validate
     * @param movieId The ID of the movie to validate
     * @return true if both movie name and ID are valid, false otherwise
     */
    public static boolean isValidMovie(String movieName, int movieId) {
        if (!isValidMovieName(movieName)) {
            return false;
        }

        if(movieId <= 0){
            return false;
        }
        return true;
    }
}