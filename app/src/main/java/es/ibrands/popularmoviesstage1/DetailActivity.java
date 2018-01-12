package es.ibrands.popularmoviesstage1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.ibrands.popularmoviesstage1.data.FavoriteMovieContract;
import es.ibrands.popularmoviesstage1.data.FavoriteMovieDbHelper;

public class DetailActivity extends AppCompatActivity
{
    public static final String EXTRA_PARAM_ID = "es.ibrands.popularmoviesstage1.extra.ID";

    private SQLiteDatabase mDb;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setTitle(R.string.activity_detail_title);

        FavoriteMovieDbHelper favoriteMovieDbHelper = new FavoriteMovieDbHelper(this);
        mDb = favoriteMovieDbHelper.getWritableDatabase();

        /* Check if the NetworkConnection is active and connected */
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new Api().execute();
        } else {
            Log.d("network", "No internet connection");
        }

        // Get a support ActionBar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();

        // Enable the Up button
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button favoriteButton = (Button) findViewById(R.id.movie_default_mark_as_favorite_button);
        favoriteButton.setOnClickListener(onFavoriteButtonClick);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        Intent listActivityTest = new Intent(this, ListActivity.class);
        startActivity(listActivityTest);
        finish();
    }

    private class Api extends AsyncTask
    {
        @Override
        protected void onPostExecute(Object result)
        {
            updateViewWithResults((Movie) result);
        }

        @Override
        protected Movie doInBackground(Object...params)
        {
            String movieId = getIntent().getStringExtra(EXTRA_PARAM_ID);

            ThemoviedbApi themoviedbApi = new ThemoviedbApi();
            return themoviedbApi.getMovie(movieId);
        }
    }

    /* Updates the View with the results. This is called asynchronously when the results are ready.
     * @param movie The results to be presented to the user
     */
    private void updateViewWithResults(Movie movie)
    {
        if (movie instanceof Movie) {
            TextView title = (TextView) findViewById(R.id.movie_detail_title);

            title.setText(movie.getTitle());

            TextView releaseDate = (TextView) findViewById(R.id.movie_detail_release_date);
            releaseDate.setText(movie.getReleaseAt().split("-")[0]);

            TextView runtime = (TextView) findViewById(R.id.movie_detail_runtime);
            runtime.setText(getString(R.string.movie_runtime_text, movie.getRuntime()));

            TextView averageScore = (TextView) findViewById(R.id.movie_detail_vote_average);
            averageScore.setText(getString(R.string.movie_vote_average_text, movie.getAverageScore()));

            TextView overview = (TextView) findViewById(R.id.movie_detail_overview);
            overview.setText(movie.getOverview());

            Context context = DetailActivity.this;
            ImageView thumbView = (ImageView) findViewById(R.id.movie_detail_thumb);
            Picasso.with(context).load(
                MovieListAdapter.THUMB_BASE_URL + "/" + MovieListAdapter.THUMB_SIZE + movie.getPosterPath()
            ).into(thumbView);

            createTrailerListView(movie);

            createReviewListView(movie);

            changeFavoriteMovieButtonBgColor(movie.getId());
        }
    }

    private void createTrailerListView(Movie movie)
    {
        ArrayList<Trailer> trailers = movie.getTrailers();
        TrailerListAdapter trailerListAdapter = new TrailerListAdapter(this, trailers);

        ListView trailerView = (ListView) findViewById(R.id.trailer_list_view);
        trailerView.setAdapter(trailerListAdapter);
        trailerView.setOnItemClickListener(onTrailerClick);

        int totalHeight = 0;
        for (int i = 0; i < trailerListAdapter.getCount(); i++) {
            View listItem = trailerListAdapter.getView(i, null, trailerView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = trailerView.getLayoutParams();
        params.height = totalHeight + (trailerView.getDividerHeight() * (trailerListAdapter.getCount() - 1));
        trailerView.setLayoutParams(params);
        trailerView.requestLayout();
    }

    /**
     * This method is called when the Open Youtube link is clicked. It will open the
     * a youtube to the video represented by the variable youtubeId using implicit Intents.
     *
     * @param v Link that was clicked
     */
    private AdapterView.OnItemClickListener onTrailerClick = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Trailer trailer = (Trailer) parent.getItemAtPosition(position);

            String youtubeId = trailer.getKey();

            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeId));

            if (appIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(appIntent);
            } else {
                Intent webIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + youtubeId)
                );

                startActivity(webIntent);
            }
        }
    };

    private void createReviewListView(Movie movie)
    {
        ArrayList<Review> reviews = movie.getReviews();
        ReviewListAdapter reviewListAdapter = new ReviewListAdapter(this, reviews);

        ListView reviewView = (ListView) findViewById(R.id.review_list_view);
        reviewView.setAdapter(reviewListAdapter);
        reviewView.setOnItemClickListener(onReviewClick);

        int totalHeight = 0;
        for (int i = 0; i < reviewListAdapter.getCount(); i++) {
            View listItem = reviewListAdapter.getView(i, null, reviewView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = reviewView.getLayoutParams();
        params.height = totalHeight + (reviewView.getDividerHeight() * (reviewListAdapter.getCount() - 1));
        reviewView.setLayoutParams(params);
        reviewView.requestLayout();
    }

    /**
     * This method is called when the Open Youtube link is clicked. It will open the
     * a youtube to the video represented by the variable youtubeId using implicit Intents.
     *
     * @param v Link that was clicked
     */
    private AdapterView.OnItemClickListener onReviewClick = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Review review = (Review) parent.getItemAtPosition(position);

            String url = review.getUrl();

            Intent webIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            );

            startActivity(webIntent);
        }
    };

    private void changeFavoriteMovieButtonBgColor(String movieId)
    {
        Button favoriteButton = (Button) findViewById(R.id.movie_default_mark_as_favorite_button);

        Boolean isFound = isFavoriteMovieFound(movieId);

        Integer textResourceId = R.string.mark_as_favorite_button_text;
        Integer colorResourceId = R.color.bgFavoriteButton;

        if (isFound) {
            textResourceId = R.string.marked_as_favorite_button_text;
            colorResourceId = R.color.bgMarkedFavoriteButton;
        }

        favoriteButton.setText(textResourceId);
        favoriteButton.setBackgroundColor(getResources().getColor(colorResourceId));
    }

    private Boolean isFavoriteMovieFound(String movieId)
    {
        boolean isFound = false;

        String columnId = FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID;
        String[] fields = new String[] {columnId};
        String[] criteria = new String[] {movieId};

        Cursor mCursor = getContentResolver().query(
            FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
            fields,
            columnId + "=?",
            criteria,
            FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TIMESTAMP
        );

        if (mCursor.getCount() > 0) {
            isFound = true;
        }

        return isFound;
    }

    /**
     * This method is called when the Open Youtube link is clicked. It will open the
     * a youtube to the video represented by the variable youtubeId using implicit Intents.
     *
     * @param v Link that was clicked
     */
    private Button.OnClickListener onFavoriteButtonClick = new Button.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String movieId = getIntent().getStringExtra(EXTRA_PARAM_ID);

            Boolean isFound = isFavoriteMovieFound(movieId);

            if (isFound) {
                removeFavoriteMovie(movieId);
            } else if(!isFound) {
                addFavoriteMovie(movieId);
            }

            changeFavoriteMovieButtonBgColor(movieId);
        }

        private void addFavoriteMovie(String movieId)
        {
            // to pass the values onto the insert query
            ContentValues contentValues = new ContentValues();

            TextView titleTextView = (TextView) findViewById(R.id.movie_detail_title);
            String title = titleTextView.getText().toString();

            contentValues.put(
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, Integer.parseInt(movieId)
            );

            contentValues.put(
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TITLE, title
            );

            Uri uri = getContentResolver().insert(
                FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                contentValues
            );
        }

        private void removeFavoriteMovie(String movieId)
        {
            Uri uri = FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(movieId).build();

            getContentResolver().delete(uri, null, null);
        }
    };
}
