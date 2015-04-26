package edu.cs430x.fuschia.geosnap.fragment;

import android.content.Context;
import android.content.Intent;
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
import edu.cs430x.fuschia.geosnap.activity.PictureReviewActivity;
import edu.cs430x.fuschia.geosnap.camera.DynamicSizeCameraPreview;
import edu.cs430x.fuschia.geosnap.camera.ImageBitmap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraPreviewFragment} interface
 * to handle interaction events.
 * Use the {@link CameraPreviewFragment} factory method to
 * create an instance of this fragment.
 */
public class CameraPreviewFragment extends Fragment {
    private static final String TAG = "CameraPreviewFragment";

    public static Camera mCamera;
    private boolean camera_open = false;
    private SurfaceHolder mHolder;
    private DynamicSizeCameraPreview mSurfaceCallback;
    private ActionButton fab;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Created");
        mCamera = getCameraInstance();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "Started");
        super.onStart();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "paused");
        super.onStop();
        // Technically supposed to release the camera here... problem is I can't
        // figure out how to reconnect the camera to the SurfaceHolder view (the image freezes)
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "destroyed");
        releaseCamera();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle b) {

        view = inflater.inflate(R.layout.camera_preview_fragment, container, false);
        SurfaceView preview = (SurfaceView) view.findViewById(R.id.cpPreview);
        mHolder = preview.getHolder();

        mSurfaceCallback = new DynamicSizeCameraPreview(getActivity(),mCamera, DynamicSizeCameraPreview.LayoutMode.NoBlank);
        mHolder.addCallback(mSurfaceCallback);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        // get the floating action camera button
        fab = (ActionButton) view.findViewById(R.id.camera_button);
        fab.playShowAnimation();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get an image from the camera
                Log.d(TAG, "Taking a picture!");
                fab.setClickable(false); // Safety precaution
                mCamera.takePicture(null, null, mPicture);
            }
        });

        Log.d(TAG, "View created");
        return view;
    }

    /**
     * Get an instance of the phone's camera
     */
    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            camera_open = true;
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "Camera is not available");
        }
        return c; // returns null if camera is unavailable
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
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

            // Make intent to start activity to display the picture
            Context c = getActivity();
            Intent review_picture_intent = new Intent(c, PictureReviewActivity.class);

            // save the bitmap to a global static variable; raw bitmap is too big for intent.
            // Can change to pass byte[] around, at cost of more time on conversions.
            // We have to convert to bitmap on picture callback anyway in the first place to rotate,
            // so might as well keep it as a bitmap instead of re convert it.
            ImageBitmap.bm = bitmap;

            // start review picture activity
            c.startActivity(review_picture_intent);

            // Clean up
            oldBitmap.recycle();
        }
    };
}
