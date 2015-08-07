package roos.jamie.acw423048;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import roos.jamie.acw423048.HTTP.OnRetrieveHttpData;
import roos.jamie.acw423048.HTTP.PerformHttpGET;
import roos.jamie.acw423048.HTTP.WordsearchURIBuilder;
import roos.jamie.acw423048.Resources.Utility;
import roos.jamie.acw423048.SharedPrefs.SharedPreferencesWrapper;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class ScoresActivity extends ActionBarActivity implements OnRetrieveHttpData{
	
	static final int DATE_DIALOG_ID = 1;
	private TextView _text_date;
	private TextView _scoreView;
	private TextView _otherScoreView;
    private Button _button;

    private WordsearchURIBuilder _builder = null; 
    private int year;
    private int month;
    private int day;
    private RequestType reqType = RequestType.GetTodayScore;
	
    private enum RequestType{
    	GetTodayScore,
    	GetSpecificScore
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) 
    {
    	outState.putString("today_score",_scoreView.getText().toString());
    	outState.putString("other_date", _text_date.getText().toString());
    	outState.putString("other_score", _otherScoreView.getText().toString());
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) 
    {
    	_scoreView.setText(savedInstanceState.getString("today_score"));
    	_text_date.setText(savedInstanceState.getString("other_date"));
    	_otherScoreView.setText(savedInstanceState.getString("other_score"));
    }
    
	@Override 
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scores);
		setCurrentDate();
		addChangeDateButtonListener();
		
		// do toolbar
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Resources r = getResources();
		float elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());
		getSupportActionBar().setElevation(elevation);
		
		String url = SharedPreferencesWrapper.getFromPrefs(this, "server_url", Utility.defaultServerURL);
		_builder = new WordsearchURIBuilder(url);
		_scoreView = (TextView)findViewById(R.id.scoreText);
		_otherScoreView = (TextView)findViewById(R.id.otherScoreText);
		String username = SharedPreferencesWrapper.getFromPrefs(this, "username", "");
		String password = SharedPreferencesWrapper.getFromPrefs(this, "password", "");
		
		String scoreUrl = _builder.GetUserScore(username, password);
		PerformHttpGET httpData = new PerformHttpGET(this);
		httpData.execute(scoreUrl);
	}

	public void setCurrentDate() {

		_text_date = (TextView) findViewById(R.id.selectDateEditText);
		final Calendar calendar = Calendar.getInstance();

		// set the day to the day before 
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public void addChangeDateButtonListener()
	{
		_button = (Button) findViewById(R.id.changeDateForScoreButton);
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
		   return new DatePickerDialog(this, datePickerListener, year, month,day);
		}
		return null;
	}
	
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			String dateStringToShow = new StringBuilder().append(day) // starts at 0 so add one
					.append("/").append(month + 1).append("/").append(year).toString();
			
			String dateStringForRequest = new StringBuilder().append(year) // starts at 0 so add one
					.append("-").append(month + 1).append("-").append(day).toString();
			
			_text_date.setText(dateStringToShow);
			getScoreFromServer(dateStringForRequest);
		}
	};
	
	private void getScoreFromServer(String date)
	{
		WordsearchURIBuilder builder = new WordsearchURIBuilder(SharedPreferencesWrapper.getFromPrefs(this, "server_url", Utility.defaultServerURL));
		String username = SharedPreferencesWrapper.getFromPrefs(this, "username", "");
		String password = SharedPreferencesWrapper.getFromPrefs(this, "password", "");
		reqType = RequestType.GetSpecificScore;
		PerformHttpGET httpData = new PerformHttpGET(this);
		httpData.execute(builder.GetUserScore(username, password, date));
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getScoreForDate(Date date)
	{
		DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
		return dateFormat.format(date);
	}

	@Override
	public void onTaskCompleted(String httpData) {
		String puzzleId = null;
		String userScore = null;
		userScore = getScoreFromJSONString(httpData, userScore);
		switch(reqType)
		{
			case GetTodayScore:
				_scoreView.setText(userScore);
				break;
			case GetSpecificScore:
				_otherScoreView.setText(userScore);
				break;
		}
	}

	private String getScoreFromJSONString(String httpData, String userScore) {
		JSONObject scoreObject;
		try {
			scoreObject = new JSONObject(httpData);
			if(scoreObject.has("Error"))
			{
				JSONObject errorObj = scoreObject.getJSONObject("Error");
				userScore = errorObj.getString("Message");
			}
			else
			{
				return httpData; 
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userScore;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		finish();
		return true;
	}
}
