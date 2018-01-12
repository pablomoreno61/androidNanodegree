package es.ibrands.popularmoviesstage1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

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
    private static Bundle mBundleMovieListRecyclerViewState;

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
        if (mCurrentSortMethod == TOP_RATED_SORT_METHOD) {
            Log.i("listActivity", "doAction top rated");
            new Api().execute(TOP_RATED_SORT_METHOD);
        } else if (mCurrentSortMethod == FAVORITE_MOVIES_METHOD) {
            Log.i("listActivity", "doAction favorites");
            Intent intent = new Intent(this, FavoriteActivity.class);
            startActivity(intent);
        } else {
            Log.i("listActivity", "doAction popular");
            new Api().execute(MOST_POPULAR_SORT_METHOD);
        }
    }

    private class Api extends AsyncTask<String, Object, Object>
    {
        @Override
        protected void onPostExecute(Object result)
        {
            updateViewWithResults((ArrayList<Movie>) result);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String...params)
        {
            ThemoviedbApi themoviedbApi = new ThemoviedbApi();

            String sortMethod = MOST_POPULAR_SORT_METHOD;
            if (params.length > 0) {
                sortMethod = params[0];
            }

            return themoviedbApi.getMovies(sortMethod);
        }
    }

    /* Updates the View with the results. This is called asynchronously when the results are ready.
     * @param movies The results to be presented to the user
     */
    private void updateViewWithResults(ArrayList<Movie> movies)
    {
        movieListAdapter.setData(movies);
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

        // save RecyclerView state
        mBundleMovieListRecyclerViewState = new Bundle();
        Parcelable listState = thumbView.getLayoutManager().onSaveInstanceState();
        mBundleMovieListRecyclerViewState.putParcelable(POSITION_STATE, listState);

        mBundleMovieListRecyclerViewState.putString(SORT_STATE, mCurrentSortMethod);
    }
/*
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.i("listActivity", "onSaveInstanceState: " + mCurrentSortMethod);

        // Save the user's current sort state
        outState.putString(SORT_STATE, mCurrentSortMethod);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i("listActivity", "onCreate: " + mCurrentSortMethod);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        getSupportActionBar().setTitle(R.string.activity_list_title);

        thumbView = (MovieListRecyclerView) findViewById(R.id.recyclerview_movies);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        thumbView.setLayoutManager(gridLayoutManager);
        thumbView.setHasFixedSize(true);

        movieListAdapter = new MovieListAdapter(this, this);
        thumbView.setAdapter(movieListAdapter);
    }
/*
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.i("listActivity", "onRestoreInstanceState: " + mCurrentSortMethod);

        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        mCurrentSortMethod = savedInstanceState.getString(SORT_STATE);
    }
*/
    @Override
    protected void onResume()
    {
        Log.i("listActivity", "onResume: " + mCurrentSortMethod);

        super.onResume();

        /* Check if the NetworkConnection is active and connected */
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // restore RecyclerView state
        if (mBundleMovieListRecyclerViewState != null) {
            mCurrentSortMethod = mBundleMovieListRecyclerViewState.getString(SORT_STATE);

            Log.i("listActivity", "onResume mBundleMovieListRecyclerViewState: " + mCurrentSortMethod);
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            doAction();
        } else {
            Log.d("network", getString(R.string.no_internet_connection));
        }

        if (mBundleMovieListRecyclerViewState != null) {
            Parcelable listState = mBundleMovieListRecyclerViewState.getParcelable(POSITION_STATE);
            thumbView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }
}
