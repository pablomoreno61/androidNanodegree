package es.ibrands.popularmoviesstage1;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class MovieListRecyclerView extends RecyclerView
{
    private int mScrollPosition;

    public MovieListRecyclerView(Context context)
    {
        super(context);
    }

    public MovieListRecyclerView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MovieListRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected Parcelable onSaveInstanceState()
    {
        Parcelable superState = super.onSaveInstanceState();
        LayoutManager layoutManager = getLayoutManager();

        if(layoutManager != null && layoutManager instanceof GridLayoutManager){
            mScrollPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
        }

        SavedState newState = new SavedState(superState);
        newState.mScrollPosition = mScrollPosition;

        return newState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        super.onRestoreInstanceState(state);

        if (state != null && state instanceof SavedState) {

            mScrollPosition = ((SavedState) state).mScrollPosition;
            LayoutManager layoutManager = getLayoutManager();

            if (layoutManager != null) {
                int count = layoutManager.getChildCount();

                if (mScrollPosition != RecyclerView.NO_POSITION && mScrollPosition < count) {
                    layoutManager.scrollToPosition(mScrollPosition);
                }
            }
        }
    }

    static class SavedState extends android.view.View.BaseSavedState
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
