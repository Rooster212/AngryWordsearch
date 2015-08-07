package roos.jamie.acw423048;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import roos.jamie.acw423048.HTTP.OnRetrieveHttpData;
import roos.jamie.acw423048.HTTP.PerformHttpGET;
import roos.jamie.acw423048.HTTP.WordsearchURIBuilder;
import roos.jamie.acw423048.Puzzle.PuzzleStorage;
import roos.jamie.acw423048.Resources.Utility;
import roos.jamie.acw423048.SharedPrefs.SharedPreferencesWrapper;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

public class MenuActivity extends ActionBarActivity implements OnRetrieveHttpData{
	// Members
	
	private final int DATE_DIALOG_ID = 12222;
	
	int year;
	int month;
	int day;
	Button _button;
	
	private static enum RequestType {
		Details,
		Unregister
	}
	private static RequestType CurrentReqType = null;
	private String _username = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		// toolbar setup
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		Resources r = getResources();
		float elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());
		actionBar.setElevation(elevation);
		actionBar.setTitle("Angry Wordsearch ACW");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);
		addChangeDateButtonListener();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		onActivityStart(null);
		invalidateOptionsMenu();
	}
	
	public void RegisterMenuButtonClicked(View v)
	{
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}
	
	public void ViewScoresButtonClicked(View v)
	{
		Intent intent = new Intent(this, ScoresActivity.class);
		startActivity(intent);
	}
	
	public void TodayPuzzleButtonClicked(View v)
	{
		Intent intent = new Intent(this, PuzzleActivity.class);
		intent.putExtra("wordsearch_date",Utility.getFormattedTodaysDate());
		startActivity(intent);
	}
	
	private void onActivityStart(Bundle savedInstanceState)
	{
		View registerButton = findViewById(R.id.registerMenuButton);
		View viewScoresButton = findViewById(R.id.viewScoresButton);
		View playTodayButton = findViewById(R.id.playTodayPuzzleButton);
		View playPastButton = findViewById(R.id.playPastPuzzleButton);
		View viewStoredPuzzlesButton = findViewById(R.id.viewStoredPuzzlesButton);
		if(SharedPreferencesWrapper.getFromPrefs(this, "registered", "").equalsIgnoreCase("true"))
		{
			registerButton.setVisibility(View.GONE);
			viewScoresButton.setVisibility(View.VISIBLE);
			playTodayButton.setVisibility(View.VISIBLE);
			playPastButton.setVisibility(View.VISIBLE);
			viewStoredPuzzlesButton.setVisibility(View.VISIBLE);
		}
		else
		{
			registerButton.setVisibility(View.VISIBLE);
			viewScoresButton.setVisibility(View.GONE);
			playTodayButton.setVisibility(View.GONE);
			playPastButton.setVisibility(View.GONE);
			viewStoredPuzzlesButton.setVisibility(View.GONE);
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem unregisterMenuItem = menu.findItem(R.id.action_unregister);
		MenuItem changePasswordMenuItem = menu.findItem(R.id.password_change);
		MenuItem retrieveDetailsMenuItem = menu.findItem(R.id.retrieve_details);
		if(SharedPreferencesWrapper.getFromPrefs(this, "registered", "").equalsIgnoreCase("true"))
		{
			unregisterMenuItem.setVisible(true);
			changePasswordMenuItem.setVisible(true);
			retrieveDetailsMenuItem.setVisible(true);
		}
		else
		{
			unregisterMenuItem.setVisible(false);
			changePasswordMenuItem.setVisible(false);
			retrieveDetailsMenuItem.setVisible(false);
		}
        return true;
    }

	/// DATE DIALOG
	public void addChangeDateButtonListener()
	{
		_button = (Button) findViewById(R.id.playPastPuzzleButton);
		_button.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				showDialog(DATE_DIALOG_ID);
			}

		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DATE_DIALOG_ID:
		   return new DatePickerDialog(this, datePickerListener, year, month, day);
		}
		return null;
	}
	
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		@SuppressLint("SimpleDateFormat")
		@Override
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, selectedDay);
			cal.set(Calendar.YEAR, selectedYear);
			cal.set(Calendar.MONTH, selectedMonth);
		
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			PlayPastPuzzle(dateFormat.format(cal.getTime()));
		}
	};
	
	public void ViewStoredPuzzlesButton(View v)
	{
		Intent storageIntent = new Intent(this, StorageActivity.class);
		startActivity(storageIntent);
	}
	
	private void PlayPastPuzzle(String dateString)
	{
		Intent intent = new Intent(this,PuzzleActivity.class);
		intent.putExtra("wordsearch_date",dateString);
		startActivity(intent);
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        switch(id)
        {
        	case R.id.action_settings:
	        	Intent intent = new Intent(this, PreferenceActivity.class);
	        	startActivity(intent);
	        	return true;
        	case R.id.password_change:
        		Intent intent2 = new Intent(this, ChangePasswordActivity.class);
        		startActivity(intent2);
        		return true;
    		case R.id.retrieve_details:
    			GetDetails();
        		return false;
    		
    		case R.id.action_unregister:
    			// do a confirm box to unregister
    			AlertDialog dialog = createConfirmUnregisterDialog();
    			dialog.show();
    			return false;
        }
        return super.onOptionsItemSelected(item);
    }
    
	private AlertDialog createConfirmUnregisterDialog()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Are you sure you want to unregister?");
		
		_username = SharedPreferencesWrapper.getFromPrefs(this, "username", "");
		alertDialog.setMessage("Pressing ok will delete the user "+ _username+" from the server!");
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PerformUnregister();
				dialog.cancel();
			}
		});
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// kill the dialog when the button is pressed
				dialog.cancel();
			}
		});
		return alertDialog;
	}
	
	private void PerformUnregister()
	{
		String serverURL = SharedPreferencesWrapper.getFromPrefs(this, "server_url", Utility.defaultServerURL);
		
		WordsearchURIBuilder urlBuilder = new WordsearchURIBuilder(serverURL);
		_username = SharedPreferencesWrapper.getFromPrefs(this, "username", "");
		String password = SharedPreferencesWrapper.getFromPrefs(this, "password", "");
		String url = urlBuilder.UnregisterUser(_username, password);
		CurrentReqType = RequestType.Unregister;
		PerformHttpGET retrieveData = new PerformHttpGET(this);
		retrieveData.execute(url);
	}
	
	private void GetDetails()
	{
		String serverURL = SharedPreferencesWrapper.getFromPrefs(this, "server_url", Utility.defaultServerURL);
		
		CurrentReqType = RequestType.Details;
		WordsearchURIBuilder urlBuilder = new WordsearchURIBuilder(serverURL);
		_username = SharedPreferencesWrapper.getFromPrefs(this, "username", "");
		String password = SharedPreferencesWrapper.getFromPrefs(this, "password", "");
		String url = urlBuilder.GetDetails(_username, password);
		PerformHttpGET retrieveData = new PerformHttpGET(this);
		retrieveData.execute(url);
	}
	
	@Override
	public void onTaskCompleted(String httpData) {
		
		switch(CurrentReqType)
		{
			case Details:
				Intent intent = new Intent(this, DetailsActivity.class);
				intent.putExtra("httpData", httpData);
				CurrentReqType = null;
				startActivity(intent);
				break;
			case Unregister:
				try {
					JSONObject unregisterResponse = new JSONObject(httpData);
					if(unregisterResponse.has("Error"))
					{
						Toast.makeText(this, "User unregister failed. "+unregisterResponse.getJSONObject("Error").getString("Message"), Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(this, "User unregistered successfully.", Toast.LENGTH_SHORT).show();
						SharedPreferencesWrapper.saveToPrefs(this, "registered", "false");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Intent thisIntent = getIntent();
				PuzzleStorage.ClearStorage(this);
				finish();
				startActivity(thisIntent);
				CurrentReqType = null;
				break;
		}
	}
}
