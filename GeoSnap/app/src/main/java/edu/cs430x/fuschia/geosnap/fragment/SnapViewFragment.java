package edu.cs430x.fuschia.geosnap.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import edu.cs430x.fuschia.geosnap.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SnapViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SnapViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SnapViewFragment extends Fragment {
    private static final String TAG = "SnapViewFragment", SNAP_ID = "SnapID";

    public static final String INTENT_IMG_URL="IMAGE_URL";

    private OnFragmentInteractionListener mListener;

    private ImageLoader mImageLoader;
    private DisplayImageOptions options;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SnapViewFragment.
     */
    public static SnapViewFragment newInstance(String imgUrl) {
        SnapViewFragment fragment = new SnapViewFragment();
        Bundle args = new Bundle();

        // a reference into the local DB and do stuff that way?
        args.putString(INTENT_IMG_URL, imgUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public SnapViewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoader.getInstance();
//        options = new DisplayImageOptions.Builder()
//                .cacheOnDisk(false)
//                .cacheInMemory(true)
//                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.fragment_snap_view, container, false);

        Log.i(TAG, "Loading snap file into SnapImageView");
        ImageView imageView = (ImageView) inflated.findViewById(R.id.snapView);

        String url = getArguments().getString(INTENT_IMG_URL);
        Log.i(TAG,url);
        mImageLoader.displayImage(getArguments().getString(INTENT_IMG_URL),imageView);
//        imageView.setImageBitmap(BitmapFactory.decodeFile(getActivity()
//                .getFileStreamPath(String.valueOf(getArguments().getString(SNAP_ID)) + ".png")
//                .getAbsolutePath()));
        Log.i(TAG, "Done Loading snap file into SnapImageView");

        return inflated;
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
        public void onFragmentInteraction(Uri uri);
    }

}
