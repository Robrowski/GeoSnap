package edu.cs430x.fuschia.geosnap.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.Locale;

import edu.cs430x.fuschia.geosnap.R;
import edu.cs430x.fuschia.geosnap.activity.settings.MainSettingsActivity;
import edu.cs430x.fuschia.geosnap.data.DiscoveredContract;
import edu.cs430x.fuschia.geosnap.data.DiscoveredSnapsDBHelper;
import edu.cs430x.fuschia.geosnap.data.ImageParcelable;
import edu.cs430x.fuschia.geosnap.fragment.CameraPreviewFragment;
import edu.cs430x.fuschia.geosnap.fragment.DiscoveredSnapsFragment;
import edu.cs430x.fuschia.geosnap.network.geocloud.QueryPhotos;
import edu.cs430x.fuschia.geosnap.network.imgur.utils.NetworkListener;
import edu.cs430x.fuschia.geosnap.network.imgur.utils.NetworkUtils;
import edu.cs430x.fuschia.geosnap.service.GoogleApiLocationService;
import edu.cs430x.fuschia.geosnap.service.receivers.LocationReceiver;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener,
        DiscoveredSnapsFragment.OnFragmentInteractionListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String INTENT_SNAP_ID = "SNAP_ID", INTENT_FILE_PATH = "IMAGE_FILE_PATH", TAG = "MainActivity";
    public static final String INTENT_IMAGE_BYTE_ARRAY="IMAGE_BYTE_ARRAY";
    public static final String INTENT_LATITUDE = "INTENT_LATITUDE", INTENT_LONGITUDE = "INTENT_LONGITUDE";

    public static final String INTENT_IMG_URL="IMAGE_URL";

    private static final int DISCOVERED_PAGE = 0;
    private static final int CAMERA_PAGE = 1;


    private  Intent start_location_service_intent;

    private NetworkListener mNetworkListener;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private View mInternetConnectivityWarning, mLocationConnectivityWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Register shared preference listener
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        // Set up warning bars
        mInternetConnectivityWarning = findViewById(R.id.internet_connectivity_warning);
        mLocationConnectivityWarning = findViewById(R.id.location_services_warning);

        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        // If this activity is launched from a notification, load the discovered page first
        if (getIntent().getBooleanExtra(QueryPhotos.SNAPS_DISCOVERED, false)){
            mViewPager.setCurrentItem(DISCOVERED_PAGE);
        } else {
            mViewPager.setCurrentItem(CAMERA_PAGE);
        }

        start_location_service_intent = new Intent(getBaseContext(), GoogleApiLocationService.class);

        // TODO should this really be here?
        if (!isMyServiceRunning(GoogleApiLocationService.class)) {
            startService(start_location_service_intent);
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)

                .build();

        File cacheDir = StorageUtils.getCacheDirectory(this);

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.MAX_PRIORITY)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .defaultDisplayImageOptions(options)
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }

    /** Check to see if a given service is running already */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d(TAG, "Service is already running");
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart(){
        super.onStart();
        setNetworkConnectivityWarning(NetworkUtils.isConnected(this)); // Force UI update
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mNetworkListener = new NetworkListener(this);
            mNetworkListener.start();
        } /// Else it won't update until onStart()

        // Update location warning 
        LocationManager lm = null;
        boolean gps_enabled = false,network_enabled = false;
        if(lm==null)
            lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex){}
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex){}

        Log.i(TAG, "GPS: "  + gps_enabled + "   Net: " + network_enabled);

        if (!network_enabled && !gps_enabled){
            mLocationConnectivityWarning.setVisibility(View.VISIBLE);
        } else {
            mLocationConnectivityWarning.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mNetworkListener.stop();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String mock_id = "mrl7Jl4";
        switch (id){
            case R.id.action_settings:
                startActivity(new Intent(this, MainSettingsActivity.class));
                return true;


            case R.id.test_location:
                String txt = "lat: " + LocationReceiver.location_latitude + " lon: " + LocationReceiver.location_longitude;
                Log.w(TAG, txt);
                Toast t = Toast.makeText(this,txt,Toast.LENGTH_SHORT);
                t.show();
                return true;

            // TODO Remove (Matt wants this until he finishes the material design of the notifications)
            case R.id.test_discovered_snaps_notification:
                Log.i(TAG, "Testing discovered snaps notification");
                Intent query_intent = new Intent(this, QueryPhotos.class);
                query_intent.putExtra("com.google.android.location.LOCATION",LocationReceiver.location);
                query_intent.putExtra("DEBUG", true);
                LocationReceiver.forceLocationUpdate();
                startService(query_intent);

                return true;

            case R.id.test_delete_db:
                DiscoveredSnapsDBHelper dbHelper = new DiscoveredSnapsDBHelper(this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete(DiscoveredContract.DiscoveredEntry.TABLE_NAME,null,null);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public ComponentName startService(Intent service) {
        ComponentName cn = super.startService(service);
        if (cn == null){  Log.e(TAG, "Service could not be started"); }
        return cn;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }


    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onFragmentInteraction(String id) {
        Log.i(TAG, "Launching the snap viewing activity");

        // Make intent to start new activity
        Intent view_snap_intent = new Intent(this, SnapViewActivity.class);

        // Put the ID in it
        view_snap_intent.putExtra(INTENT_SNAP_ID, id);

        // send it
        startActivity(view_snap_intent);
    }

    @Override
    public void onFragmentInteraction(Cursor c){
        Log.i(TAG, "Launching the snap viewing activity");
        // Make intent to start new activity
        Intent view_snap_intent = new Intent(this, SnapViewActivity.class);

        ImageParcelable image = new ImageParcelable(c);
        view_snap_intent.putExtra(INTENT_IMG_URL,image);
        startActivity(view_snap_intent);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        switch (key){
            case NetworkListener.PREF_NETWORK_STATE:
                setNetworkConnectivityWarning(
                        sharedPref.getBoolean(NetworkListener.PREF_NETWORK_STATE, false));
                return;
            default:
                return;
        }
    }

    public void setNetworkConnectivityWarning(boolean connected){
        if (connected)
            mInternetConnectivityWarning.setVisibility(View.INVISIBLE);
        else
            mInternetConnectivityWarning.setVisibility(View.VISIBLE);
        return;
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case DISCOVERED_PAGE:
                    return new DiscoveredSnapsFragment();
                case CAMERA_PAGE:
                    return new CameraPreviewFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case DISCOVERED_PAGE:
                    return getString(R.string.title_discovered_snap_list).toUpperCase(l);
                case CAMERA_PAGE:
                    return getString(R.string.title_take_picture).toUpperCase(l);
            }
            return null;
        }
    }
}
