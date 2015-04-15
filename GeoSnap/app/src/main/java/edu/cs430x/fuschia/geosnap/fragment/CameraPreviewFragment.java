package edu.cs430x.fuschia.geosnap.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.software.shell.fab.ActionButton;

import edu.cs430x.fuschia.geosnap.R;
import edu.cs430x.fuschia.geosnap.camera.DynamicSizeCameraPreview;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraPreviewFragment} interface
 * to handle interaction events.
 * Use the {@link CameraPreviewFragment} factory method to
 * create an instance of this fragment.
 */
public class CameraPreviewFragment extends Fragment {
    private static final String cTAG = "CameraDebug";

    private Camera mCamera;
    private SurfaceHolder mHolder;
    private DynamicSizeCameraPreview mSurfaceCallback;


    private OnCameraFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an instance of Camera
        mCamera = getCameraInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCameraFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCameraFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle b) {

        View view = inflater.inflate(R.layout.camera_preview_fragment, container, false);
        SurfaceView preview = (SurfaceView) view.findViewById(R.id.cpPreview);

        // Get the holder from our SurfaceView, and set its callback to our own CameraPreview
        mHolder = preview.getHolder();
        mSurfaceCallback = new DynamicSizeCameraPreview(getActivity(),mCamera, DynamicSizeCameraPreview.LayoutMode.NoBlank);
        mHolder.addCallback(mSurfaceCallback);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // get the floating action camera button
        ActionButton fab = (ActionButton) view.findViewById(R.id.camera_button);
        fab.playShowAnimation();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get an image from the camera
                mCamera.takePicture(null, null, mPicture);
            }
        });

        return view;
    }

    /**
     * Get an instance of the phone's camera
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e(cTAG, "Camera is not available");
        }
        return c; // returns null if camera is unavailable
    }

    private PictureCallback mPicture = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap oldBitmap = bitmap;

            // In order to properly fix the rotation of our image, we create
            // a new bitmap with a 90 degree rotation transformation.
            Matrix matrix = new Matrix();
            matrix.postRotate(90);

            bitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    matrix,
                    false
            );

            oldBitmap.recycle();

            // Get new activity started
            mListener.onPictureTaken(bitmap);

        }
    };
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
    public interface OnCameraFragmentInteractionListener {
        public void onPictureTaken(Bitmap data);
    }

}
