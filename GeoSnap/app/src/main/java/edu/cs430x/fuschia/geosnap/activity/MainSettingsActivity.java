package edu.cs430x.fuschia.geosnap.activity;

import android.app.Activity;
import android.os.Bundle;

import edu.cs430x.fuschia.geosnap.fragment.settings.MainSettingsFragment;

public class MainSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MainSettingsFragment())
                .commit();
    }
}
