package es.ibrands.popularmoviesstage1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity
{
    public static final String EXTRA_PARAM_ID = "es.ibrands.popularmoviesstage1.extra.ID";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setTitle(R.string.activity_detail_title);

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
     * @param movies The results to be presented to the user
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
                ThemoviedbAdapter.THUMB_BASE_URL + "/" + ThemoviedbAdapter.THUMB_SIZE + movie.getPosterPath()
            ).into(thumbView);
        }
    }
}
