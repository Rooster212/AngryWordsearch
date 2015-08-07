package roos.jamie.acw423048;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailsActivity extends ActionBarActivity {
	HashMap<String, String> details = new HashMap<String, String>();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		details.clear();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		
		// do toolbar
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		Resources r = getResources();
		float elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());
		actionBar.setElevation(elevation);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("User Details");
		
		String httpData = getIntent().getExtras().getString("httpData");
		try {
			JSONObject jsonObject = new JSONObject(httpData);
			Iterator<String> allKeys = jsonObject.keys();
			while(allKeys.hasNext())
			{
				String nextKey = allKeys.next();
				String value = jsonObject.getString(nextKey);
				details.put(nextKey, value);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PopulateDetails();
	}
	
	private void PopulateDetails()
	{
		LinearLayout rootLayout = (LinearLayout)findViewById(R.id.rootLayout);
		for(Map.Entry<String, String> entry : details.entrySet())
		{
			StringBuilder builder = new StringBuilder();
			builder.append(entry.getKey());
			builder.append(": ");
			builder.append(entry.getValue());
			rootLayout.addView(createTextView(builder.toString()));
		}
	}
	
	private TextView createTextView(String entryData)
	{
		TextView entryView = new TextView(this);
		entryView.setTextSize(20);
		entryView.setText(entryData);
		entryView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		return entryView;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		finish();
		return true;
	}
}
