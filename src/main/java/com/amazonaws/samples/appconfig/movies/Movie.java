package com.amazonaws.samples.appconfig.movies;

public class Movie {

    private long id;
    private final String movieName;
    private final String genre;

    public Movie(Long id, String movieName) {
        this.id = id;
        this.movieName = movieName;
        this.genre = "Unknown"; // Default genre for backward compatibility
    }

    public Movie(Long id, String movieName, String genre) {
        this.id = id;
        this.movieName = movieName;
        this.genre = genre != null ? genre : "Unknown";
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
    
    public String getGenre() {
        return this.genre;
    }
}