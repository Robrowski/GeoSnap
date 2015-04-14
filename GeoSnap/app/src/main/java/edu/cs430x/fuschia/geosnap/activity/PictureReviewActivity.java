package edu.cs430x.fuschia.geosnap.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import edu.cs430x.fuschia.geosnap.R;
import edu.cs430x.fuschia.geosnap.network.geocloud.InsertPhoto;
import edu.cs430x.fuschia.geosnap.network.imgur.model.ImageResponse;
import edu.cs430x.fuschia.geosnap.network.imgur.model.Upload;
import edu.cs430x.fuschia.geosnap.network.imgur.services.OnImgurResponseListener;
import edu.cs430x.fuschia.geosnap.network.imgur.services.UploadService;
import edu.cs430x.fuschia.geosnap.service.LocationService;


/**
 * This is written as an activity because the amount of interaction between the fragment and activity was way stupid
 *
 * Imgur upload process taken from https://github.com/AKiniyalocts/imgur-android
 *
 */
public class PictureReviewActivity extends ActionBarActivity implements OnImgurResponseListener {
    public final static String TAG = "PictureReviewActivity";

    private InsertPhoto insertPhotoTask;

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

        // Set up our geocloud server upload task
        insertPhotoTask = new InsertPhoto();

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

    /** Whe the accept button is pressed, start trying to upload the image to the servers */
    public void onAccept(View v) {
        /*     Start upload     */
        new UploadService(createUpload(), this).execute();
        Log.i(TAG, "Started upload to imgur");
    }

    /** When the reject button is pressed, the activity backs out to previous and
     * no image is sent to any server  */
    public void onReject(View v){
        NavUtils.navigateUpFromSameTask(this);
        Toast toast = Toast.makeText(this, "Image rejected", Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    protected void onDestroy() {
        // Trash the image on exit
        String file_path = getIntent().getStringExtra(MainActivity.INTENT_FILE_PATH);
        new File(file_path).deleteOnExit();
        Log.i(TAG, "Image file deleted on image review exit");

        super.onDestroy();
    }


    /** Package up the current picture to be uploaded to imgur */
    private Upload createUpload(){
        Upload imgur_upload = new Upload();

        imgur_upload.image = new File(getIntent().getStringExtra(MainActivity.INTENT_FILE_PATH));

        // TODO set these strings to real values... something useful like GPS? time?
        imgur_upload.title = "Anonymous title";
        imgur_upload.description = "poop";
        return imgur_upload;
    }



    @Override
    public void onImgurResponse(ImageResponse response) {
        // This is called when we get a successful response from imgur.
        if(!response.success) {
            // If uploaded to imgur failed, try again! forever!
            Toast toast = Toast.makeText(this, "Imgur upload failed. Trying again...", Toast.LENGTH_SHORT);
            toast.show();
            this.onAccept(null);
        }

        // Show stuff on screen
        String imgur_image_id = response.data.id;
        TextView txt = (TextView) findViewById(R.id.imgur_response);
        txt.setText("imgur.com/"+ imgur_image_id);

        // Send crap to the GeoCloud server
        // TODO send crap to the GeoCloud Server...
        Toast.makeText(this,"lat: " + LocationService.location_latitude + " lon: " + LocationService.location_longitude,Toast.LENGTH_SHORT).show();
        Pair<Context,String> args = new Pair<Context,String>(getApplicationContext(),imgur_image_id);
        insertPhotoTask.execute(args);
        // TODO parcel up Lat + Long for GeoCloud too
        Intent i = getIntent();
        float latitude = i.getFloatExtra(MainActivity.INTENT_LATITUDE, 0);
        float longitude = i.getFloatExtra(MainActivity.INTENT_LONGITUDE, 0);

    }

}
