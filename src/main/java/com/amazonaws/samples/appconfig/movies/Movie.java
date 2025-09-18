package com.amazonaws.samples.appconfig.movies;

/**
 * Movie model representing a movie with id, name, and genre
 * Arrr! This be our treasure map for movie data, matey!
 */
public class Movie {

    private long id;
    private final String movieName;
    private final String genre;

    /**
     * Constructor for Movie with id, name, and genre
     * @param id The unique identifier for the movie
     * @param movieName The name of the movie
     * @param genre The genre of the movie
     */
    public Movie(Long id, String movieName, String genre) {
        this.id = id;
        this.movieName = movieName;
        this.genre = genre != null ? genre : "Unknown";
    }

    /**
     * Backward compatible constructor for existing code
     * @param id The unique identifier for the movie
     * @param movieName The name of the movie
     */
    public Movie(Long id, String movieName) {
        this(id, movieName, "Unknown");
    }

    public long getId() {
        return this.id;
    }

    public long setId(int movieId){
        return this.id = movieId;
    }
    
    public String getMovieName() {
        return this.movieName;
    }
    
    /**
     * Gets the genre of the movie
     * @return The genre of the movie
     */
    public String getGenre() {
        return this.genre;
    }
}