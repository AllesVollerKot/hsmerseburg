package com.example.hsmerseburg;

import net.saik0.android.unifiedpreference.UnifiedPreferenceFragment;
import net.saik0.android.unifiedpreference.UnifiedSherlockPreferenceActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

public class PreferenceActivity_Preferences extends UnifiedSherlockPreferenceActivity {

	public static final String UPDATE_PREF = "updatePref";
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHeaderRes(R.xml.pref_headers);
		setSharedPreferencesMode(Context.MODE_PRIVATE);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(getResources().getString(R.color.news))));
	}

	public static class FachbereichePreferenceFragment extends UnifiedPreferenceFragment {
	}

	
}
