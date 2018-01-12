package es.ibrands.popularmoviesstage1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavoriteMovieContentProvider extends ContentProvider
{
    public static final int FAVORITE_MOVIES = 100;
    public static final int FAVORITE_MOVIES_ID = 101;

    // Declare a static variable for the Uri matcher that you construct
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private FavoriteMovieDbHelper favoriteMovieDbHelper;


    // Define a static buildUriMatcher method that associates URI's with their int match
    public static UriMatcher buildUriMatcher()
    {
        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY, FavoriteMovieContract.FAVORITE_MOVIE_PATH, FAVORITE_MOVIES);
        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY, FavoriteMovieContract.FAVORITE_MOVIE_PATH + "/#", FAVORITE_MOVIES_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate()
    {
        Context context = getContext();
        favoriteMovieDbHelper = new FavoriteMovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = favoriteMovieDbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        // Query for the tasks directory and write a default case
        switch (match) {
            // Query for the tasks directory
            case FAVORITE_MOVIES:
                retCursor = db.query(
                    FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                );
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        // Get access to the task database (to write new data to)
        final SQLiteDatabase db = favoriteMovieDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the tasks directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case FAVORITE_MOVIES:
                // Insert new values into the database
                // Inserting values into tasks table
                long id = db.insert(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME, null, values);

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        final SQLiteDatabase db = favoriteMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted; // starts as 0

        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case FAVORITE_MOVIES_ID:
                // Get the task ID from the URI path
                String movieId = uri.getPathSegments().get(1);

                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(
                    FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                    FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + "=?",
                    new String[]{movieId}
                );

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
