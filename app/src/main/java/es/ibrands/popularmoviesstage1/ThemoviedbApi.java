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
    /* TODO: Populate API_KEY with your own api key */
    private static final String API_KEY = "c38c9232ef963058f15a2aba19f1a8a6";
    private static final String API_URL = "http://api.themoviedb.org/3/movie/";

    public Movie getMovie(String movieRef)
    {
        Movie movie = null;
        InputStream inputStream = null;

        try {
            HttpURLConnection urlConnection = query(movieRef);

            inputStream = urlConnection.getInputStream();

            movie = parseMovie(stringify(inputStream));
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

    private String stringify(InputStream stream) throws IOException
    {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);

        return bufferedReader.readLine();
    }
}
