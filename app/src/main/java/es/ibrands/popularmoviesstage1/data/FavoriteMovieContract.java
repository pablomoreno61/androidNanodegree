package es.ibrands.popularmoviesstage1.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteMovieContract
{
    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "es.ibrands.popularmoviesstage1";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String FAVORITE_MOVIE_PATH = "favorite_movies";

    public static final class FavoriteMovieEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(FAVORITE_MOVIE_PATH).build();

        public static final String TABLE_NAME = "favorite_movies";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
