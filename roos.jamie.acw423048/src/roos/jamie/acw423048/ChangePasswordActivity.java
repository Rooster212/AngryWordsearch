package roos.jamie.acw423048;

import org.json.JSONException;
import org.json.JSONObject;

import roos.jamie.acw423048.HTTP.OnRetrieveHttpData;
import roos.jamie.acw423048.HTTP.PerformHttpGET;
import roos.jamie.acw423048.HTTP.WordsearchURIBuilder;
import roos.jamie.acw423048.Resources.Utility;
import roos.jamie.acw423048.SharedPrefs.SharedPreferencesWrapper;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePasswordActivity extends ActionBarActivity implements OnRetrieveHttpData {
	
	private String _newPassword = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		
		// do toolbar
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		Resources r = getResources();
		float elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());
		actionBar.setElevation(elevation);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Change Password");
	}
	
	public void ChangePasswordSendToServer(View v)
	{
		String oldPassword = ((EditText)findViewById(R.id.edit_oldpass)).getText().toString();
		
		String newPassword = ((EditText)findViewById(R.id.edit_newpassword)).getText().toString();
		
		String username = SharedPreferencesWrapper.getFromPrefs(this, "username", "");
		
		String oldPassFromSettings = SharedPreferencesWrapper.getFromPrefs(this, "password", "");
		
		if(oldPassFromSettings.equals(oldPassword))
		{
			WordsearchURIBuilder builder = new WordsearchURIBuilder(SharedPreferencesWrapper.getFromPrefs(this, "server_url", Utility.defaultServerURL));
			
			// do the server request
			String url = builder.ChangePassword(username, oldPassword, newPassword);
			PerformHttpGET retrieveData = new PerformHttpGET(this);
			retrieveData.execute(url);
		}
		else
		{
			Toast.makeText(this, "The old password does not match the one stored on this device", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onTaskCompleted(String httpData) {

		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(httpData);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(jsonObject.has("Error"))
		{
			// we have an error response from the server
			JSONObject errorJson;
			String typeOfError = null;
			String message = null;
			try {
				errorJson = jsonObject.getJSONObject("Error");
				typeOfError = errorJson.getString("Type");
				message = errorJson.getString("Message");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			AlertDialog dialog = Utility.createErrorDialog(typeOfError, message, this);
			dialog.show();
		}
		else
		{
			try {
				if(jsonObject.getString("Result").equalsIgnoreCase("OK"));
				{
					SharedPreferencesWrapper.saveToPrefs(this, "password", _newPassword);
					Toast.makeText(this, "Password Changed!", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			finish();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		finish();
		return true;
	}
}
