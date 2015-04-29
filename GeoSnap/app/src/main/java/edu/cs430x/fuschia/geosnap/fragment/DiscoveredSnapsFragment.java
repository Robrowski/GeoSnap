package edu.cs430x.fuschia.geosnap.fragment;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import edu.cs430x.fuschia.geosnap.R;
import edu.cs430x.fuschia.geosnap.adapters.DiscoveredAdapter;
import edu.cs430x.fuschia.geosnap.data.DiscoveredContract;
import edu.cs430x.fuschia.geosnap.data.DiscoveredProjection;
import edu.cs430x.fuschia.geosnap.data.DiscoveredSnapsDBHelper;
import edu.cs430x.fuschia.geosnap.network.geocloud.QueryPhotos;
import edu.cs430x.fuschia.geosnap.service.receivers.LocationReceiver;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class DiscoveredSnapsFragment extends Fragment {

    private static final String TAG = "DiscoveredSnapsFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's recycler view.
     */
    private RecyclerView mRecyclerView;

    SQLiteDatabase mDB;
    private static DiscoveredSnapsDBHelper mDbHelper;

    Cursor mdiscoveredCursor;

    private static SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
//    private ListAdapter mAdapter;
    private static DiscoveredAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static DiscoveredSnapsFragment newInstance(String param1, String param2) {
        DiscoveredSnapsFragment fragment = new DiscoveredSnapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DiscoveredSnapsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mDbHelper = new DiscoveredSnapsDBHelper(getActivity());
        mDbHelper.RemoveExpiredSnaps(getActivity().getApplicationContext());

        // get the database for reading, and query it for our projection to
        mDB = mDbHelper.getReadableDatabase();

        // TODO: create selection string for selecting only photos not posted by user?
        mdiscoveredCursor = queryDB(mDB);


        mAdapter = new DiscoveredAdapter(getActivity(), mdiscoveredCursor);
    }

    private static Cursor queryDB(SQLiteDatabase db){
        return db.query(DiscoveredContract.DiscoveredEntry.TABLE_NAME,
                DiscoveredProjection.DISCOVERED_COLUMNS, // projection: what cols we want to retrieve
                null, // selection: query string, (select photos not posted by user?)
                null, // selection args: values for selection string
                null, // group by
                null, // having
                null); // order by
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.discovered_list);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(getActivity(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (mListener != null) {
                    Log.d(TAG, "onClick " + position);
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    if (!mdiscoveredCursor.moveToPosition(position)) {
                        throw new IllegalStateException("couldn't move cursor to position " + position);
                    }
                    mListener.onFragmentInteraction(mdiscoveredCursor);

                }
            }

            @Override
            public void onLongClick(View view, int position) {
                // TODO: maybe add material ripple effect here
            }
        }));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cancel notifications about new snaps, because the user is already here!
        ((NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE))
                .cancel(QueryPhotos.NOTIFICATION_ID);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void refreshContent(){
        Intent query_intent = new Intent(getActivity(), QueryPhotos.class);
        LocationReceiver.forceLocationUpdate();
        query_intent.putExtra("com.google.android.location.LOCATION",LocationReceiver.location);
        query_intent.putExtra("DEBUG", false);
        getActivity().startService(query_intent);

    }

    public static class RefreshReceiver extends BroadcastReceiver{


        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG,"refresh UI, query intent finished!");
            if (mAdapter != null && mSwipeRefreshLayout !=null){
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                Cursor updated = queryDB(db);
                mAdapter.changeCursor(updated);
                mAdapter.notifyDataSetChanged();
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);

        public void onFragmentInteraction(Cursor c);
        // TODO rename to something that reflects the fact that a new activity for snap viewing
        // should be opened
    }

    static class RecyclerOnTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerOnTouchListener(Context context, final RecyclerView rv, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    View child = rv.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onClick(child, rv.getChildPosition(child));
                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = rv.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, rv.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            return gestureDetector.onTouchEvent(e);
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

}
