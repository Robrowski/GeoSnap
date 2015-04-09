package edu.cs430x.fuschia.geosnap.activity;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

import edu.cs430x.fuschia.geosnap.R;


/** This is written as an activity because the amount of interaction between the fragment and activity was way stupid */
public class PictureReviewActivity extends ActionBarActivity  {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_review);

        // Get file path to picture
        String file_path = getIntent().getStringExtra(MainActivity.INTENT_FILE_PATH);

        // Open the file
        File f = new File(file_path);


        // Put it in the image view
        ImageView imageView = (ImageView) findViewById(R.id.imageReview);
        imageView.setImageBitmap(BitmapFactory.decodeFile(file_path));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_picture_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onAccept(Uri u) {
        // Send picture to server, etc.

        // Return to picture view

        // Toast
    }

    /** This also needs to happen when the back button is pressed */
    public void onReject(Uri u) {
    // Trash the image
    // return to picture view

    // Toast file deleted

    }
}
