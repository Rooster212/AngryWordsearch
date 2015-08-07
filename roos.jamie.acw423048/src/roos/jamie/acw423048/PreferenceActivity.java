package roos.jamie.acw423048;

import roos.jamie.acw423048.SharedPrefs.SharedPreferencesFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;

public class PreferenceActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
		
		setContentView(R.layout.activity_preferences);

		// do toolbar
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		Resources r = getResources();
		float elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());
		actionBar.setElevation(elevation);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Settings");

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SharedPreferencesFragment()).commit();
	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		finish();
		return true;
	}
}
