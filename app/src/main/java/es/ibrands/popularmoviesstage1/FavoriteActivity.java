package es.ibrands.popularmoviesstage1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import es.ibrands.popularmoviesstage1.data.FavoriteMovieContract;
import es.ibrands.popularmoviesstage1.data.FavoriteMovieDbHelper;

public class FavoriteActivity extends AppCompatActivity
{
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        getSupportActionBar().setTitle(R.string.activity_favorite_title);

        FavoriteMovieDbHelper favoriteMovieDbHelper = new FavoriteMovieDbHelper(getApplicationContext());
        mDb = favoriteMovieDbHelper.getWritableDatabase();

        new Api().execute();

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

    private class Api extends AsyncTask<String, Object, Object>
    {
        @Override
        protected void onPostExecute(Object result)
        {
            updateViewWithResults((Cursor) result);
        }

        @Override
        protected Cursor doInBackground(String...params)
        {
            Cursor mCursor = mDb.query(
                FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TIMESTAMP
            );

            return mCursor;
        }
    }

    private void updateViewWithResults(Cursor mCursor)
    {
        if (mCursor.getCount() > 0) {
            ArrayList<String> favorites = new ArrayList<String>();

            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                favorites.add(mCursor.getString(mCursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TITLE)));
            }

            FavoriteListAdapter favoriteListAdapter = new FavoriteListAdapter(this, favorites);

            ListView favoriteView = (ListView) findViewById(R.id.favorite_list_view);
            favoriteView.setAdapter(favoriteListAdapter);

            int totalHeight = 0;
            for (int i = 0; i < favoriteListAdapter.getCount(); i++) {
                View listItem = favoriteListAdapter.getView(i, null, favoriteView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = favoriteView.getLayoutParams();
            params.height = totalHeight + (favoriteView.getDividerHeight() * (favoriteListAdapter.getCount() - 1));
            favoriteView.setLayoutParams(params);
            favoriteView.requestLayout();
        }
    }
}
