package com.amazonaws.samples.appconfig.movies;

/**
 * Movie model representing a movie with id, name, and genre.
 * This fantastic character in our movie adventure story! 🎬
 */
public class Movie {

    private long id;
    private final String movieName;
    private final String genre;

    /**
     * Constructor for Movie with id and movieName (for backward compatibility)
     */
    public Movie(Long id, String movieName) {
        this.id = id;
        this.movieName = movieName;
        this.genre = "Unknown"; // Default genre for existing movies
    }

    /**
     * Constructor for Movie with id, movieName, and genre
     * Wow! Now our movie characters have genres too! 🎭
     */
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

    /**
     * Get the genre of this spectacular movie! 🌟
     */
    public String getGenre() {
        return this.genre;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", movieName='" + movieName + '\'' +
                ", genre='" + genre + '\'' +
                '}';
    }
}