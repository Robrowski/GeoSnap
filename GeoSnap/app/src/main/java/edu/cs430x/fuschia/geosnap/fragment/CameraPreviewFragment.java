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

    private ActionButton fab;
    private FrameLayout mLayout;
    private CameraPreview mPreview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {

        View view = inflater.inflate(R.layout.camera_preview_fragment, container, false);
        mLayout = (FrameLayout) view.findViewById(R.id.camera_preview_frame);

        // get the floating action camera button
        fab = (ActionButton) view.findViewById(R.id.camera_button);
        fab.playShowAnimation();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get an image from the camera
                Log.d(TAG, "Taking a picture!");
                fab.setClickable(false); // Safety precaution
                mPreview.takePicture(mPictureCallback);
            }
        });

        Log.d(TAG, "View created");
        return view;
    }


    @Override
    public void onResume() {
        Log.d(TAG, "Resumed");
        super.onStart();
        mPreview = new CameraPreview(getActivity(), 0, CameraPreview.LayoutMode.FitToParent);
        LayoutParams previewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        mLayout.addView(mPreview, 0, previewLayoutParams);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "paused");
        super.onStop();
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
