package es.ibrands.popularmoviesstage1;

import android.content.ContentUris;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder>
{
    private final Context mContext;

    public static final String THUMB_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String THUMB_SIZE = "w185";

    private ArrayList<Movie> mMovies;

    /*
     * Below, we've defined an interface to handle clicks on items within this Adapter. In the
     * constructor of our ForecastAdapter, we receive an instance of a class that has implemented
     * said interface. We store that instance in this variable to call the onClick method whenever
     * an item is clicked in the list.
     */
    final private MovieListAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieListAdapterOnClickHandler
    {
        void onClick(Movie movie);
    }

    public MovieListAdapter(@NonNull Context context, MovieListAdapterOnClickHandler clickHandler)
    {
        mContext = context;
        mClickHandler = clickHandler;
    }

    public class MovieListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public final ImageView thumbView;

        public MovieListViewHolder(View view)
        {
            super(view);

            thumbView = (ImageView) view.findViewById(R.id.movie_list_thumb);

            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v)
        {
            int position = getAdapterPosition();

            Movie movie = mMovies.get(position);

            mClickHandler.onClick(movie);
        }
    }

    @Override
    public MovieListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieListViewHolder movieAdapterViewHolder, int position)
    {
        Movie movie = mMovies.get(position);

        Context context = movieAdapterViewHolder.thumbView.getContext();
        Picasso.with(context)
            .load(THUMB_BASE_URL + THUMB_SIZE + "/" + movie.getPosterPath())
            .placeholder(R.drawable.no_image)
            .error(R.drawable.no_image)
            .into(movieAdapterViewHolder.thumbView);
    }

    @Override
    public int getItemCount()
    {
        if (null == mMovies) return 0;
        return mMovies.size();
    }

    public void setData(ArrayList<Movie> movies)
    {
        mMovies = movies;
        notifyDataSetChanged();
    }
}
