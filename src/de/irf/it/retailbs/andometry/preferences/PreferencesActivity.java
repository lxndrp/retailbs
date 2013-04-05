package de.irf.it.retailbs.andometry.preferences;


import de.irf.it.retailbs.andometry.R;
import de.irf.it.retailbs.andometry.R.layout;
import android.os.Bundle;
import android.preference.PreferenceActivity;


public class PreferencesActivity extends PreferenceActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.layout.preferences);
	}
}
