package es.ibrands.popularmoviesstage1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import es.ibrands.popularmoviesstage1.data.FavoriteMovieContract.*;

public class FavoriteMovieDbHelper extends SQLiteOpenHelper
{
    // The database name
    private static final String DATABASE_NAME = "movies.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 2;

    public FavoriteMovieDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        // Create a table to hold waitlist data
        final String SQL_CREATE_FAVORITE_MOVIE_TABLE = "CREATE TABLE " + FavoriteMovieEntry.TABLE_NAME + " (" +
            FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FavoriteMovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
            FavoriteMovieEntry.COLUMN_TITLE + " INTEGER NOT NULL, " +
            FavoriteMovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
            FavoriteMovieEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
        "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        switch (oldVersion) {
            case 1:
                String tableName = FavoriteMovieEntry.TABLE_NAME;
                db.execSQL("ALTER TABLE " + tableName  + " ADD COLUMN poster_path TEXT NULL;");

                break;
            default:
                onCreate(db);
        }
    }
}
