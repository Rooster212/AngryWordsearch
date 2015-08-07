package roos.jamie.acw423048;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import roos.jamie.acw423048.HTTP.OnRetrieveHttpData;
import roos.jamie.acw423048.HTTP.PerformHttpGET;
import roos.jamie.acw423048.HTTP.WordsearchURIBuilder;
import roos.jamie.acw423048.Puzzle.PuzzleStorage;
import roos.jamie.acw423048.Puzzle.Wordsearch;
import roos.jamie.acw423048.Resources.Utility;
import roos.jamie.acw423048.SharedPrefs.SharedPreferencesWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StorageActivity extends ActionBarActivity implements OnRetrieveHttpData{
	
	Wordsearch workingWordsearch;
	Vector<Wordsearch> getScoreVector = new Vector<Wordsearch>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_storage);
		
		// do toolbar
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar(); 
		actionBar.setDisplayHomeAsUpEnabled(true);
		Resources r = getResources();
		float elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());
		actionBar.setElevation(elevation);
		actionBar.setTitle("Stored Puzzles");
		initTable();
	}
	
	private void initTable()
	{
		TreeMap<String, Wordsearch> map = PuzzleStorage.GetStorage(this);
		LinearLayout table = (LinearLayout)findViewById(R.id.storeTable);
		if(map.isEmpty())
		{
			TextView noPuzzlesView = new TextView(this);
			noPuzzlesView.setText("No puzzles stored");
			noPuzzlesView.setGravity(Gravity.CENTER);
			noPuzzlesView.setTextSize(24);
			table.addView(noPuzzlesView);
			return;
		}
		for(Map.Entry<String, Wordsearch> entry : map.entrySet())
		{
			LinearLayout view = (LinearLayout)getLayoutInflater()
					.inflate(R.layout.storage_row, (ViewGroup)findViewById(R.id.storeTable),false);
			Wordsearch thisWordsearch = entry.getValue();
			
			String date = (String)entry.getKey();
			
			
			
			view.setTag(date);
			TextView headView = (TextView)view.findViewById(R.id.storageRowHeader);
			headView.setText(date);
			TextView storedView = (TextView)view.findViewById(R.id.solutionStored);
			if(thisWordsearch.getFullSolutionStored())
			{
				storedView.setText("Solution Stored");
			}
			else
			{
				storedView.setText("No Solution Stored");
				storedView.setTextColor(Color.RED);
			}
			
			TextView solvedView = (TextView)view.findViewById(R.id.solved);
			TextView scoreView = (TextView)view.findViewById(R.id.score);
					
			if(thisWordsearch.getSolved())
			{
				solvedView.setText("Solved");
				solvedView.setTextColor(Color.argb(250, 46, 92, 0));
				// get score from server
				if(thisWordsearch.getScoreHasBeenRetrieved())
				{
					scoreView.setText("Score: "+thisWordsearch.getScore());
				}
				else
				{
					getScoreVector.add(thisWordsearch);
				}
			}
			else
			{
				solvedView.setText("Not Solved");
				solvedView.setTextColor(Color.RED);
				scoreView.setText("No Score Yet");
			}
			table.addView(view);
		}
		if(getScoreVector.size() > 0)
		{
			WordsearchURIBuilder builder = new WordsearchURIBuilder(SharedPreferencesWrapper.getFromPrefs(this, "server_url", Utility.defaultServerURL));
			String username = SharedPreferencesWrapper.getFromPrefs(this, "username", "");
			String password = SharedPreferencesWrapper.getFromPrefs(this, "password", "");
			workingWordsearch = getScoreVector.get(0);
			getScoreVector.remove(0);
			PerformHttpGET httpDataNext = new PerformHttpGET(this);
			String urlToUse;
			if(!Utility.getFormattedTodaysDate().equals(workingWordsearch.date()))
				urlToUse = builder.GetUserScore(username, password, workingWordsearch.date());
			else
				urlToUse = builder.GetUserScore(username, password);
			httpDataNext.execute(urlToUse);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// this will get fired when we press the back button
		finish();
		return true;
	}
	
	@Override
	public void onTaskCompleted(String httpData) {
		
		View parentView = findViewById(R.id.storeTable).findViewWithTag(workingWordsearch.date());
		
		TextView scoreView = (TextView)parentView.findViewById(R.id.score);
		
		
		JSONObject obj;
		try {
			obj = new JSONObject(httpData);
			if(obj.has("ScoreData"))
			{
				String score = obj.getJSONObject("ScoreData").getString("Score");
				scoreView.setText("Score: "+score);
				workingWordsearch.setScore(Float.parseFloat(score));
				PuzzleStorage.SaveWordsearch(this, workingWordsearch);
			}
			else if(obj.has("Error"))
			{
				String errorMessage = obj.getJSONObject("Error").getString("Message"); 
				scoreView.setText(errorMessage);
			}
			
			if(getScoreVector.size() > 0)
			{
				// do server request
				WordsearchURIBuilder builder = new WordsearchURIBuilder(SharedPreferencesWrapper.getFromPrefs(this, "server_url", Utility.defaultServerURL));
				String username = SharedPreferencesWrapper.getFromPrefs(this, "username", "");
				String password = SharedPreferencesWrapper.getFromPrefs(this, "password", "");
				workingWordsearch = getScoreVector.get(0);
				getScoreVector.remove(0);
				PerformHttpGET httpDataNext = new PerformHttpGET(this);
				String urlToUse;
				if(!Utility.getFormattedTodaysDate().equals(workingWordsearch.date()))
					urlToUse = builder.GetUserScore(username, password, workingWordsearch.date());
				else
					urlToUse = builder.GetUserScore(username, password);
				httpDataNext.execute(urlToUse);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void PlayPuzzleClicked(View view)
	{
		Button thisButton = (Button)view;
		LinearLayout parent = (LinearLayout)thisButton.getParent().getParent();
		String puzzleDate = (String)parent.getTag();
		Intent playPuzzleIntent = new Intent(this, PuzzleActivity.class);
		playPuzzleIntent.putExtra("wordsearch_date", puzzleDate);
		startActivity(playPuzzleIntent);
	}
	
	public void DeletePuzzleClicked(View view)
	{
		Button thisButton = (Button)view;
		LinearLayout parent = (LinearLayout)thisButton.getParent().getParent();
		String puzzleDate = (String)parent.getTag();
		PuzzleStorage.DeletePuzzle(this, puzzleDate);
		Intent thisIntent = new Intent(this, StorageActivity.class);
		finish();
		startActivity(thisIntent);
	}
}
