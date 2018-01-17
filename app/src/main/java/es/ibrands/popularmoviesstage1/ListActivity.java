package es.ibrands.popularmoviesstage1;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import es.ibrands.popularmoviesstage1.data.FavoriteMovieContract;

public class ListActivity extends AppCompatActivity implements MovieListAdapter.MovieListAdapterOnClickHandler
{
    private static final String MOST_POPULAR_SORT_METHOD = "popular";
    private static final String TOP_RATED_SORT_METHOD = "top_rated";
    private static final String FAVORITE_MOVIES_METHOD = "favorite";

    private final String SORT_STATE = "sortState";
    private final String POSITION_STATE = "positionState";

    // default sort method
    private String mCurrentSortMethod;

    private MovieListRecyclerView thumbView;
    // private static Bundle mBundleMovieListRecyclerViewState;
    private static Parcelable mLayoutManagerSavedState;

    private MovieListAdapter movieListAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.top_rated_sort_method) {
            mCurrentSortMethod = TOP_RATED_SORT_METHOD;
        } else if (id == R.id.most_popular_sort_method) {
            mCurrentSortMethod = MOST_POPULAR_SORT_METHOD;
        } else if (id == R.id.favorite_movie_method) {
            mCurrentSortMethod = FAVORITE_MOVIES_METHOD;
        }

        doAction();

        return super.onOptionsItemSelected(item);
    }

    private void doAction()
    {
        new Api().execute(mCurrentSortMethod);
    }

    private class Api extends AsyncTask<String, Object, Object>
    {
        @Override
        protected void onPostExecute(Object result)
        {
            updateViewWithResults((ArrayList<Movie>) result);

            updateSupportBarTitle();
        }

        @Override
        protected ArrayList<Movie> doInBackground(String...params)
        {
            ArrayList<Movie> movies;

            String sortMethod = MOST_POPULAR_SORT_METHOD;
            if (params.length > 0 && params[0] != null) {
                sortMethod = params[0];
            }

            Log.i("listActivity", "doInBackground: " + sortMethod);
            mCurrentSortMethod = sortMethod;

            if (sortMethod == FAVORITE_MOVIES_METHOD) {
                movies = getFavorites();
            } else {
                ThemoviedbApi themoviedbApi = new ThemoviedbApi();
                movies = themoviedbApi.getMovies(sortMethod);
            }

            return movies;
        }
    }

    private ArrayList<Movie> getFavorites()
    {
        ArrayList<Movie> movies = new ArrayList<>();

        Cursor mCursor = getContentResolver().query(
            FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
            null,
            null,
            null,
            FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TIMESTAMP
        );

        if (mCursor.getCount() > 0) {
            ArrayList<String> favorites = new ArrayList<String>();

            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                Movie movie = new Movie(
                    "0",
                    mCursor.getString(mCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID)),
                    mCursor.getString(mCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TITLE)),
                    "",
                    mCursor.getString(mCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH)),
                    ""
                );

                movies.add(movie);
            }
        }

        Log.i("listActivity", String.valueOf(movies.size()));

        return movies;
    }

    /* Updates the View with the results. This is called asynchronously when the results are ready.
     * @param movies The results to be presented to the user
     */
    private void updateViewWithResults(ArrayList<Movie> movies)
    {
        movieListAdapter.setData(movies);
        if (mLayoutManagerSavedState != null) {
            Log.i("listActivity", "updateViewWithResults onRestoreInstanceState");
            thumbView.getLayoutManager().onRestoreInstanceState(mLayoutManagerSavedState);
        }
    }

    @Override
    public void onClick(Movie movie)
    {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_PARAM_ID, movie.getId());

        startActivity(intent);
    }

    @Override
    protected void onPause()
    {
        Log.i("listActivity", "onPause: " + mCurrentSortMethod);

        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.i("listActivity", "onSaveInstanceState: " + mCurrentSortMethod);

        outState.putParcelable(POSITION_STATE, thumbView.getLayoutManager().onSaveInstanceState());

        // Save the user's current sort state
        outState.putString(SORT_STATE, mCurrentSortMethod);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i("listActivity", "onCreate: " + mCurrentSortMethod);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        thumbView = (MovieListRecyclerView) findViewById(R.id.recyclerview_movies);

        int noOfColumns = calculateGridNoOfColumns();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, noOfColumns);
        thumbView.setLayoutManager(gridLayoutManager);
        thumbView.setHasFixedSize(true);

        movieListAdapter = new MovieListAdapter(this, this);

        Log.i("listActivity", "onCreate setAdapter");
        thumbView.setAdapter(movieListAdapter);
    }

    private int calculateGridNoOfColumns()
    {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        return (int) (dpWidth / scalingFactor);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.i("listActivity", "onRestoreInstanceState: " + mCurrentSortMethod);

        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        mCurrentSortMethod = savedInstanceState.getString(SORT_STATE);

        mLayoutManagerSavedState = savedInstanceState.getParcelable(POSITION_STATE);
    }

    @Override
    protected void onResume()
    {
        Log.i("listActivity", "onResume: " + mCurrentSortMethod);

        super.onResume();

        if (Utility.isConnected(this)) {
            doAction();
        }
    }

    private void updateSupportBarTitle()
    {
        Integer titleResource = getApplicationContext().getResources().getIdentifier(
            mCurrentSortMethod + "_sort_method",
            "string",
            getApplicationContext().getPackageName()
        );

        getSupportActionBar().setTitle(titleResource);
    }
}
