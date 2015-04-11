package edu.cs430x.fuschia.geosnap.fragment;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import edu.cs430x.fuschia.geosnap.R;
import edu.cs430x.fuschia.geosnap.network.imgur.model.ImageResponse;
import edu.cs430x.fuschia.geosnap.network.imgur.services.GetService;
import edu.cs430x.fuschia.geosnap.network.imgur.services.OnImageResponseListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SnapViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SnapViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SnapViewFragment extends Fragment implements OnImageResponseListener{
    private static final String TAG = "SnapViewFragment", SNAP_ID = "SnapID";

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SnapViewFragment.
     */
    public static SnapViewFragment newInstance(String id) {
        SnapViewFragment fragment = new SnapViewFragment();
        Bundle args = new Bundle();

        // TODO The main thing to store here the imgur id, so that the file path can be guessed... or
        // a reference into the local DB and do stuff that way
        args.putString(SNAP_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public SnapViewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO images should already be loaded somewhere else
        Log.w(TAG, "Requesting image from imgur: " + String.valueOf(getArguments().getString(SNAP_ID)));
        new GetService(String.valueOf(getArguments().getString(SNAP_ID)), this, getActivity()).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_snap_view, container, false);

        // TODO If image is already loaded, this is what SHOULD happen
//        ImageView imageView = (ImageView) getView().findViewById(R.id.snapView);
//        imageView.setImageBitmap(BitmapFactory.decodeFile(getActivity()
//                .getFileStreamPath(String.valueOf(getArguments().getString(SNAP_ID)) + ".png")
//                .getAbsolutePath()));

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

    @Override
    public void onImageResponse(ImageResponse response) {
        // TODO Images shouldn't actually be handled here...

        // Put the image in the image view
        Log.w(TAG, "Loading the image from imgur");
        ImageView imageView = (ImageView) getView().findViewById(R.id.snapView);

        imageView.setImageBitmap(BitmapFactory.decodeFile(getActivity()
                .getFileStreamPath(String.valueOf(getArguments().getString(SNAP_ID)) + ".png")
                .getAbsolutePath()));
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
