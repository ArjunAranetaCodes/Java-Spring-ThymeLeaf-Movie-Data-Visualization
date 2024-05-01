package com.example.java.spring.data.vis.controller;

import com.example.java.spring.data.vis.model.Movie;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Controller
public class MovieController {

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    @GetMapping("/")
    public String index(Model model) {
        // Fetch the most popular movies
        List<Movie> popularMovies = fetchPopularMovies();

        // Prepare the data for the charts
        List<String> movieTitles = popularMovies.stream()
                .map(Movie::getTitle)
                .collect(Collectors.toList());
        List<Double> moviePopularities = popularMovies.stream()
                .map(Movie::getPopularity)
                .collect(Collectors.toList());

        // Add the data to the model
        model.addAttribute("movieTitles", movieTitles);
        model.addAttribute("moviePopularities", moviePopularities);

        // Fetch the top-rated movies
        List<Movie> topRatedMovies = fetchTopRatedMovies();
        List<String> topRatedMovieTitles = topRatedMovies.stream()
                .map(Movie::getTitle)
                .collect(Collectors.toList());
        List<Double> topRatedMovieVoteAverages = topRatedMovies.stream()
                .map(Movie::getVoteAverage)
                .collect(Collectors.toList());

        model.addAttribute("topRatedMovieTitles", topRatedMovieTitles);
        model.addAttribute("topRatedMovieVoteAverages", topRatedMovieVoteAverages);

        // Fetch the upcoming movies
        List<Movie> upcomingMovies = fetchUpcomingMovies();
        List<String> upcomingMovieTitles = upcomingMovies.stream()
                .map(Movie::getTitle)
                .collect(Collectors.toList());
        List<String> upcomingMovieDates = upcomingMovies.stream()
                .map(Movie::getReleaseDate)
                .collect(Collectors.toList());

        model.addAttribute("upcomingMovieTitles", upcomingMovieTitles);
        model.addAttribute("upcomingMovieDates", upcomingMovieDates);


        // Return the view name
        return "index";
    }

    @GetMapping("/popular-movies")
    public String getPopularMovies(Model model) {
        // Fetch the most popular movies from the TMDb API
        List<Movie> popularMovies = fetchPopularMovies();

        // Add the movies to the model
        model.addAttribute("movies", popularMovies);

        // Return the view name
        return "popular-movies";
    }

    private List<Movie> fetchPopularMovies() {
        List<Movie> popularMovies = new ArrayList<>();

        try {
            // Construct the TMDb API URL for fetching the most popular movies
            String apiUrl = "https://api.themoviedb.org/3/movie/popular?api_key=" + tmdbApiKey + "&language=en-US&page=1";

            // Open the connection to the TMDb API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response from the API
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }

                // Parse the JSON response and create Movie objects
                popularMovies = parseMoviesFromResponse(response.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return popularMovies;
    }



    @GetMapping("/top-rated")
    public String getTopRatedMovies(Model model) {
        // Fetch the top-rated movies from the TMDb API
        List<Movie> topRatedMovies = fetchTopRatedMovies();

        // Add the movies to the model
        model.addAttribute("movies", topRatedMovies);

        // Return the view name
        return "top-rated";
    }

    private List<Movie> fetchTopRatedMovies() {
        List<Movie> topRatedMovies = new ArrayList<>();

        try {
            // Construct the TMDb API URL for fetching the top-rated movies
            String apiUrl = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + tmdbApiKey + "&language=en-US&page=1";

            // Open the connection to the TMDb API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response from the API
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }

                // Parse the JSON response and create Movie objects
                topRatedMovies = parseMoviesFromResponse(response.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return topRatedMovies;
    }



    @GetMapping("/upcoming")
    public String getUpcomingMovies(Model model) {
        // Fetch the upcoming movies from the TMDb API
        List<Movie> upcomingMovies = fetchUpcomingMovies();

        // Add the movies to the model
        model.addAttribute("movies", upcomingMovies);

        // Return the view name
        return "upcoming";
    }

    private List<Movie> fetchUpcomingMovies() {
        List<Movie> upcomingMovies = new ArrayList<>();

        try {
            // Construct the TMDb API URL for fetching the upcoming movies
            String apiUrl = "https://api.themoviedb.org/3/movie/upcoming?api_key=" + tmdbApiKey + "&language=en-US&page=1";

            // Open the connection to the TMDb API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response from the API
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }

                // Parse the JSON response and create Movie objects
                upcomingMovies = parseMoviesFromResponse(response.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return upcomingMovies;
    }

    private List<Movie> parseMoviesFromResponse(String response) {
        List<Movie> movies = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode resultsNode = rootNode.get("results");

            for (JsonNode movieNode : resultsNode) {
                Long id = movieNode.get("id").asLong();
                String title = movieNode.get("title").asText();
                String overview = movieNode.get("overview").asText();
                String releaseDate = movieNode.get("release_date").asText();
                String posterPath = movieNode.get("poster_path").asText();
                double popularity = movieNode.get("popularity").asDouble();
                double voteAverage = movieNode.get("vote_average").asDouble();

                Movie movie = new Movie(id, title, overview, releaseDate, popularity, posterPath, voteAverage);
                movies.add(movie);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movies;
    }
}

