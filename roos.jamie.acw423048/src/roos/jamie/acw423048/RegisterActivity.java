package roos.jamie.acw423048;

import org.json.JSONException;
import org.json.JSONObject;

import roos.jamie.acw423048.HTTP.OnRetrieveHttpData;
import roos.jamie.acw423048.HTTP.PerformHttpGET;
import roos.jamie.acw423048.HTTP.WordsearchURIBuilder;
import roos.jamie.acw423048.Resources.Utility;
import roos.jamie.acw423048.SharedPrefs.SharedPreferencesWrapper;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnRetrieveHttpData{
	
	private String _username = null;
	private String _firstName = null;
	private String _lastName = null;
	private String _password = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
	}
	
	public void RegisterButtonSendDataToServer(View v)
	{
		_username = ((EditText)findViewById(R.id.edit_username)).getText().toString().trim();
		_firstName = ((EditText)findViewById(R.id.edit_firstname)).getText().toString().trim();
		_lastName = ((EditText)findViewById(R.id.edit_lastname)).getText().toString().trim();
		_password = ((EditText)findViewById(R.id.edit_password)).getText().toString().trim();

		WordsearchURIBuilder builder = new WordsearchURIBuilder(SharedPreferencesWrapper.getFromPrefs(this, "server_url", Utility.defaultServerURL));
		String url = builder.RegisterUser(_firstName, _lastName, _username, _password);
		
		// do the server request
		PerformHttpGET retrieveData = new PerformHttpGET(this);
		retrieveData.execute(url);
	}

	@Override
	public void onTaskCompleted(String httpData) {
		// end the activity
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(httpData);
		} catch (JSONException e) {
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
				if(jsonObject.getString("Result").equalsIgnoreCase("OK"))
				{
					SharedPreferencesWrapper.saveToPrefs(this, "username", _username);
					SharedPreferencesWrapper.saveToPrefs(this, "first_name", _firstName);
					SharedPreferencesWrapper.saveToPrefs(this, "last_name", _lastName);
					SharedPreferencesWrapper.saveToPrefs(this, "password", _password);
					SharedPreferencesWrapper.saveToPrefs(this, "registered", "true");
					Toast.makeText(this, "User registered!", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			finish();
		}
	}
}
