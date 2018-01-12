package es.ibrands.popularmoviesstage1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    private static final String MOST_POPULAR_SORT_METHOD = "popular";
    private static final String TOP_RATED_SORT_METHOD = "top_rated";
    private static final String FAVORITE_MOVIES_METHOD = "favorite";

    private final String STATE_SORT_METHOD = "sortMethod";
    private String mCurrentSortMethod;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        getSupportActionBar().setTitle(R.string.activity_list_title);

        /* Check if the NetworkConnection is active and connected */
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new Api().execute();
        } else {
            Log.d("network", getString(R.string.no_internet_connection));
        }
    }

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
            new Api().execute(TOP_RATED_SORT_METHOD);
        } else if (mCurrentSortMethod == MOST_POPULAR_SORT_METHOD) {
            new Api().execute(MOST_POPULAR_SORT_METHOD);
        } else if (mCurrentSortMethod == FAVORITE_MOVIES_METHOD) {
            Intent intent = new Intent(this, FavoriteActivity.class);
            startActivity(intent);
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
        GridView thumbView = new GridView(this);
        thumbView.setNumColumns(2);
        thumbView.setPadding(0, 0, 0, 0);
        thumbView.setHorizontalSpacing(0);
        thumbView.setVerticalSpacing(0);
        thumbView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        thumbView.setSaveEnabled(true);
        thumbView.setOnItemClickListener(this);

        MovieListAdapter movieListAdapter = new MovieListAdapter(this, movies);
        thumbView.setAdapter(movieListAdapter);

        setContentView(thumbView);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Movie movie = (Movie) parent.getItemAtPosition(position);

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_PARAM_ID, movie.getId());

        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        // Save the user's current game state
        savedInstanceState.putString(STATE_SORT_METHOD, mCurrentSortMethod);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        mCurrentSortMethod = savedInstanceState.getString(STATE_SORT_METHOD);

        doAction();
    }

    public static class SavedState extends android.view.View.BaseSavedState
    {
        public int mScrollPosition;

        SavedState(Parcel in)
        {
            super(in);
            mScrollPosition = in.readInt();
        }

        SavedState(Parcelable superState)
        {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            super.writeToParcel(dest, flags);
            dest.writeInt(mScrollPosition);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>()
        {
            @Override
            public SavedState createFromParcel(Parcel in)
            {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size)
            {
                return new SavedState[size];
            }
        };
    }
}
