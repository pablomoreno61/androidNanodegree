package es.ibrands.popularmoviesstage1;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ThemoviedbApi
{
    private static final String API_KEY = BuildConfig.API_KEY;

    private static final String API_URL = "http://api.themoviedb.org/3/movie/";

    public ArrayList<Movie> getMovies(String viewMode)
    {
        ArrayList<Movie> movies = new ArrayList<>();
        InputStream inputStream = null;

        try {
            HttpURLConnection urlConnection = query(viewMode);

            inputStream = urlConnection.getInputStream();

            movies = parseMovies(stringify(inputStream));
        } catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch(IOException e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        }

        return movies;
    }

    private ArrayList<Movie> parseMovies(String result)
    {
        ArrayList<Movie> movies = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray array = (JSONArray) jsonObject.get("results");

            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonMovieObject = array.getJSONObject(i);
                Movie movie = new Movie(
                        jsonMovieObject.getString("vote_average"),
                        jsonMovieObject.getString("id"),
                        jsonMovieObject.getString("title"),
                        jsonMovieObject.getString("overview"),
                        jsonMovieObject.getString("poster_path"),
                        jsonMovieObject.getString("release_date")
                );

                movies.add(movie);
            }
        } catch(JSONException e) {
            Log.d("ERROR", "Error parsing JSON. String was: " + result);
        }

        return movies;
    }

    private String stringify(InputStream stream) throws IOException
    {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);

        return bufferedReader.readLine();
    }

    public Movie getMovie(String movieRef)
    {
        Movie movie = null;
        InputStream inputStream = null;

        try {
            HttpURLConnection urlConnection = query(movieRef);

            inputStream = urlConnection.getInputStream();

            movie = parseMovie(stringify(inputStream));

            ArrayList<Trailer> trailers = getTrailers(movieRef + "/videos");
            movie.setTrailers(trailers);

            ArrayList<Review> reviews = getReviews(movieRef + "/reviews");
            movie.setReviews(reviews);
        } catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch(IOException e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        }

        return movie;
    }

    private Movie parseMovie(String result)
    {
        Movie movie = null;

        try {
            JSONObject jsonObject = new JSONObject(result);

            movie = new Movie(
                    jsonObject.getString("vote_average"),
                    jsonObject.getString("id"),
                    jsonObject.getString("title"),
                    jsonObject.getString("overview"),
                    jsonObject.getString("poster_path"),
                    jsonObject.getString("release_date")
            );

            movie.setRuntime(jsonObject.getString("runtime"));
        } catch(JSONException e) {
            Log.d("ERROR", "Error parsing JSON. String was: " + result);
        }

        return movie;
    }

    public ArrayList<Trailer> getTrailers(String viewMode)
    {
        ArrayList<Trailer> trailers = new ArrayList<>();
        InputStream inputStream = null;

        try {
            HttpURLConnection urlConnection = query(viewMode);

            inputStream = urlConnection.getInputStream();

            trailers = parseTrailers(stringify(inputStream));
        } catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch(IOException e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        }

        return trailers;
    }

    private ArrayList<Trailer> parseTrailers(String result)
    {
        ArrayList<Trailer> trailers = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray array = (JSONArray) jsonObject.get("results");

            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonMovieObject = array.getJSONObject(i);
                Trailer trailer = new Trailer(
                    jsonMovieObject.getString("id"),
                    jsonMovieObject.getString("name"),
                    jsonMovieObject.getString("key")
                );

                trailers.add(trailer);
            }
        } catch(JSONException e) {
            Log.d("ERROR", "Error parsing JSON. String was: " + result);
        }

        return trailers;
    }

    public ArrayList<Review> getReviews(String viewMode)
    {
        ArrayList<Review> reviews = new ArrayList<>();
        InputStream inputStream = null;

        try {
            HttpURLConnection urlConnection = query(viewMode);

            inputStream = urlConnection.getInputStream();

            reviews = parseReviews(stringify(inputStream));
        } catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch(IOException e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        }

        return reviews;
    }

    private ArrayList<Review> parseReviews(String result)
    {
        ArrayList<Review> reviews = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray array = (JSONArray) jsonObject.get("results");

            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonMovieObject = array.getJSONObject(i);
                Review review = new Review(
                    jsonMovieObject.getString("id"),
                    jsonMovieObject.getString("author"),
                    jsonMovieObject.getString("content"),
                    jsonMovieObject.getString("url")
                );

                reviews.add(review);
            }
        } catch(JSONException e) {
            Log.d("ERROR", "Error parsing JSON. String was: " + result);
        }

        return reviews;
    }

    /**
     * @param viewMode following values accepted (popular, top_rated, [movieId])
     * @return HttpURLConnection
     */
    private HttpURLConnection query(String viewMode)
    {
        HttpURLConnection urlConnection = null;
        URL url = null;

        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(API_URL);
            stringBuilder.append(viewMode);
            stringBuilder.append("?api_key=" + API_KEY);

            url = new URL(stringBuilder.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);  /* milliseconds */
            urlConnection.setConnectTimeout(15000);  /* milliseconds */
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("Accept", "application/json");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch(MalformedURLException e) {
            Log.d("url", e.getMessage());
        } catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return urlConnection;
    }
}
