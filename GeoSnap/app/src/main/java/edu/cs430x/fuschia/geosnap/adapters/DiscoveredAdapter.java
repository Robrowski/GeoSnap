package edu.cs430x.fuschia.geosnap.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.cs430x.fuschia.geosnap.R;

/**
 * Created by Matt on 4/9/2015.
 */
public class DiscoveredAdapter extends RecyclerView.Adapter<DiscoveredAdapter.DiscoveredViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIdColumn;
    private DataSetObserver mDataSetObserver;

    public DiscoveredAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
        mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
    }
    @Override
    public DiscoveredViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.discovered_photo_cardview, viewGroup, false);
        DiscoveredViewHolder pvh = new DiscoveredViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(DiscoveredViewHolder discoveredViewHolder, int i) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(i)) {
            throw new IllegalStateException("couldn't move cursor to position " + i);
        }
        int id = mCursor.getInt(mRowIdColumn);
        discoveredViewHolder.textView.setText(id);
    }


    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * The viewholder keeps all of the references to the views we need to populate for each row,
     * in order to avoid having to find the views by Id every single time, which is expensive.
     */
    public static class DiscoveredViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView textView;

        DiscoveredViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_view);
            textView = (TextView)itemView.findViewById(R.id.info_text);
        }
    }

    /**
     * This class is attached to the cursor, and is called whenever new data is added
     * to our database.  When this happens, we notify our recycler view, so the view can be updated.
     */
    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }
}
