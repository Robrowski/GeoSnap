package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {
    CrimeFragment cf;

    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent()
                .getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        cf = CrimeFragment.newInstance(crimeId);
        return cf;
    }

}
