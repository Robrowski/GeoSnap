package edu.cs430x.fuschia.geosnap.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import edu.cs430x.fuschia.geosnap.R;
import edu.cs430x.fuschia.geosnap.data.DiscoveredProjection;
import edu.cs430x.fuschia.geosnap.network.geocloud.Discoverability;

/**
 * Created by Matt on 4/9/2015.
 */
public class DiscoveredAdapter extends RecyclerView.Adapter<DiscoveredAdapter.DiscoveredViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIdColumn;
    private DataSetObserver mDataSetObserver;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;

    public DiscoveredAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
        mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
        mImageLoader = ImageLoader.getInstance();
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
        String id = Integer.toString(mCursor.getInt(mRowIdColumn));
        String imageUrl = mCursor.getString(DiscoveredProjection.COL_PHOTO_URL);
        String discoverability = mCursor.getString(DiscoveredProjection.COL_DISCOVER);
        String timeStamp = mCursor.getString(DiscoveredProjection.COL_TIMESTAMP);
        mImageLoader.displayImage(imageUrl,discoveredViewHolder.imageView);
        discoveredViewHolder.discoverText.setText(discoverability);
        discoveredViewHolder.timeLeft.setText(timeStamp);

        switch (discoverability){
            case Discoverability.DISC_SECRET:
                discoveredViewHolder.iconView.setImageResource(R.drawable.secret_icon_white);
                break;
            case Discoverability.DISC_MEDIUM:
                discoveredViewHolder.iconView.setImageResource(R.drawable.medium_icon_white);
                break;
            case Discoverability.DISC_FAR:
                discoveredViewHolder.iconView.setImageResource(R.drawable.far_icon_white);
                break;
        }

//        discoveredViewHolder.imageView.
//        discoveredViewHolder.textView.setText(id);
    }


    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     *
     * @param cursor The new cursor to be used
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     *
     * @param newCursor The new cursor to be used.
     * @return Returns the previously set Cursor, or null if there wasa not one.
     * If the given new Cursor is the same instance is the previously set
     * Cursor, null is also returned.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
            if (mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (newCursor != null) {
            if (mDataSetObserver != null) newCursor.registerDataSetObserver(mDataSetObserver);
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            // notify the observers about the lack of a data set
            // notifyDataSetInvalidated();
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
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
        TextView discoverText;
        SelectableRoundedImageView imageView;
        ImageView iconView;
        TextView timeLeft;

        DiscoveredViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_view);
            imageView = (SelectableRoundedImageView) itemView.findViewById(R.id.image);
            discoverText = (TextView) itemView.findViewById(R.id.discoverText);
            iconView = (ImageView) itemView.findViewById(R.id.discoverIcon);
            timeLeft = (TextView) itemView.findViewById(R.id.timeLeft);
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
