package es.ibrands.popularmoviesstage1.data;

import android.provider.BaseColumns;

public class FavoriteMovieContract
{
    public static final class FavoriteMovieEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "favorite_movies";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
