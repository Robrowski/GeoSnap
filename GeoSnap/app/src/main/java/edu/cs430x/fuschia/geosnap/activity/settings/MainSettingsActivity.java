package edu.cs430x.fuschia.geosnap.activity.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import edu.cs430x.fuschia.geosnap.R;

public class MainSettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.pref_main); // I don't care that its deprecated
    }
}
