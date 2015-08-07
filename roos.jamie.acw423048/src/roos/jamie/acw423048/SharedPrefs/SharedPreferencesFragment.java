package roos.jamie.acw423048.SharedPrefs;

import roos.jamie.acw423048.R;
import roos.jamie.acw423048.R.xml;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;

public class SharedPreferencesFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        // get the shared preferences
        SharedPreferences sharedPref = getPreferenceScreen().getSharedPreferences();
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        
        // set the summary of the items on start
        initSummary(getPreferenceScreen());
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference pref = findPreference(key);
		initSummary(pref);
	}
	
	private void initSummary(Preference p)
	{
		if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
	}
	
	private void updatePrefSummary(Preference p)
	{
		 if (p instanceof ListPreference) {
	            ListPreference listPref = (ListPreference) p;
	            p.setSummary(listPref.getEntry());
	        }
		 else if (p instanceof EditTextPreference) {
			EditTextPreference editTextPref = (EditTextPreference) p;
			if (p.getTitle().toString().contains("assword"))
			{
			    p.setSummary(
			    		new String(
			    				new char[((EditTextPreference) p).getEditText().length()]).replace('\0', '*')
		    				);
			} else {
			    p.setSummary(editTextPref.getText());
			}
		 }
		 else if (p instanceof MultiSelectListPreference) {
		     EditTextPreference editTextPref = (EditTextPreference) p;
		     p.setSummary(editTextPref.getText());
		 }
	}
}
