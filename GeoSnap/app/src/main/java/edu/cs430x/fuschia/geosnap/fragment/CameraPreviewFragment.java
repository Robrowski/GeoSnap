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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.software.shell.fab.ActionButton;

import edu.cs430x.fuschia.geosnap.R;
import edu.cs430x.fuschia.geosnap.activity.PictureReviewActivity;
import edu.cs430x.fuschia.geosnap.camera.CameraPreview;
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

    private ActionButton take_picture_button, swap_camera_button;
    private FrameLayout mLayout;
    private CameraPreview mPreview;

    private static int FRONT = 0, BACK = 1;// Probably not right for ALL phones
    private int mCameraId = FRONT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {

        View view = inflater.inflate(R.layout.camera_preview_fragment, container, false);
        mLayout = (FrameLayout) view.findViewById(R.id.camera_preview_frame);

        // get the floating action camera button
        take_picture_button = (ActionButton) view.findViewById(R.id.camera_button);
        take_picture_button.playShowAnimation();
        take_picture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get an image from the camera
                Log.d(TAG, "Taking a picture!");
                take_picture_button.setClickable(false); // Safety precaution
                mPreview.takePicture(mPictureCallback);
            }
        });

        swap_camera_button = (ActionButton) view.findViewById(R.id.switch_camera);

        // Check to make sure there is a face-facing camera!
        if (Camera.getNumberOfCameras() <= 1){
            swap_camera_button.setClickable(false);
            swap_camera_button.setVisibility(View.INVISIBLE);
        } else {
            swap_camera_button.playShowAnimation();
            swap_camera_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get an image from the camera
                    Log.d(TAG, "Swapping cameras!");
                    swap_camera_button.setClickable(false); // Safety precaution

                    stopCameraPreview();
                    mCameraId = (mCameraId + 1) % 2;
                    startCameraPreview(mCameraId);

                    swap_camera_button.setClickable(true); // Safety precaution
                }
            });
        }

        Log.d(TAG, "View created");
        return view;
    }


    @Override
    public void onResume() {
        Log.d(TAG, "Resumed");
        super.onResume();
        startCameraPreview(mCameraId);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "paused");
        super.onStop();
        stopCameraPreview();
    }

    /** Start a camera preview, including a new camera reference */
    private void startCameraPreview(int cam_id){
        mPreview = new CameraPreview(getActivity(), cam_id, CameraPreview.LayoutMode.FitToParent);
        LayoutParams previewLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mLayout.addView(mPreview, 0, previewLayoutParams);
    }

    /** Stop the camera preview, remove the view and release the camera */
    private void stopCameraPreview(){
        mPreview.stop();
        mLayout.removeView(mPreview); // This is necessary.
        mPreview = null;
    }


    private PictureCallback mPictureCallback = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap oldBitmap = bitmap;



            // In order to properly fix the rotation of our image, we create
            // a new bitmap with a 90 degree rotation transformation.
            Matrix matrix = new Matrix();
            if (mCameraId == FRONT){
                matrix.postRotate(90);
            } else if (mCameraId == BACK){
                matrix.preScale(1, -1); // Mirror image
                matrix.postRotate(-90); // Rotate to make vertical
            }

            bitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    matrix,
                    false
            );
            Log.i(TAG, "w: " + String.valueOf(bitmap.getWidth()) + "  h: " + String.valueOf(bitmap.getHeight()));

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
