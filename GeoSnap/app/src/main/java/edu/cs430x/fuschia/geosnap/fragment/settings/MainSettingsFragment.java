package edu.cs430x.fuschia.geosnap.fragment.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import edu.cs430x.fuschia.geosnap.R;

/** Basic main settings accessed from MainActivity */
public class MainSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
    }
}
