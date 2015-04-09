package edu.cs430x.fuschia.geosnap.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import edu.cs430x.fuschia.geosnap.R;


/**
 * This is written as an activity because the amount of interaction between the fragment and activity was way stupid
 */
public class PictureReviewActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_review);

        // Get file path to picture
        String file_path = getIntent().getStringExtra(MainActivity.INTENT_FILE_PATH);

        // Put it in the image view
        ImageView imageView = (ImageView) findViewById(R.id.imageReview);
        imageView.setImageBitmap(BitmapFactory.decodeFile(file_path));
        imageView.setRotation(90);

        // Not sure why this has to be commented out...
//        getActionBar().setDisplayHomeAsUpEnabled(true);

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
        switch (id) {
            case R.id.action_settings:
                return true;

            case android.R.id.home:
                onReject(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        onReject(null);
    }

    public void onAccept(View v) {
        // Send picture to server, etc.


        // Toast

        // Return to picture view
    }

    /**
     * This also needs to happen when the back button is pressed
     */
    public void onReject(View v) {
        // return to picture view
        NavUtils.navigateUpFromSameTask(this);

        // Trash the image
        String file_path = getIntent().getStringExtra(MainActivity.INTENT_FILE_PATH);
        new File(file_path).deleteOnExit();

        Toast toast = Toast.makeText(this, "Image rejected and file deleted", Toast.LENGTH_SHORT);
        toast.show();
    }
}
