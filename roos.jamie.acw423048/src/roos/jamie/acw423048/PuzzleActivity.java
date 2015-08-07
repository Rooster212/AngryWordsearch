package roos.jamie.acw423048;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import roos.jamie.acw423048.CustomView.PuzzleGridView;
import roos.jamie.acw423048.HTTP.OnRetrieveHttpData;
import roos.jamie.acw423048.HTTP.PerformHttpPOST;
import roos.jamie.acw423048.HTTP.WordsearchURIBuilder;
import roos.jamie.acw423048.Puzzle.OnWordFoundListener;
import roos.jamie.acw423048.Puzzle.PuzzleStorage;
import roos.jamie.acw423048.Puzzle.SolutionWord;
import roos.jamie.acw423048.Puzzle.Wordsearch;
import roos.jamie.acw423048.Resources.Utility;
import roos.jamie.acw423048.SharedPrefs.SharedPreferencesWrapper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PuzzleActivity extends Activity implements OnRetrieveHttpData, OnWordFoundListener {
	PuzzleGridView grid;
	PuzzleActivity _thisActivity = this;
	static Wordsearch _wordsearch;
	int numberOfColumns;
	int numberOfWordsInPuzzle;
	int numberOfSolvedWords = 0;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_puzzle);
	    grid = (PuzzleGridView) findViewById(R.id.puzzleGridView);
	    
	    PuzzleStorage storage = new PuzzleStorage();
		try {
			if(getIntent().hasExtra("wordsearch_date"))
			{
				_wordsearch = storage.GetPuzzle(getIntent().getStringExtra("wordsearch_date"), this);
			}
			else 
			{
				_wordsearch = storage.GetPuzzle(savedInstanceState.getString("wordsearch_date"), this);
			}
		} catch (Exception e2)
		{
			Toast.makeText(this, "Error occurred getting puzzle - Message: \""+e2.getMessage()+"\"", Toast.LENGTH_LONG).show();
		}
		
		if(_wordsearch == null)
		{
			Toast.makeText(this, "For some reason, the wordsearch was null!", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		Vector<String> wordVector = _wordsearch.getWords();
		numberOfWordsInPuzzle = wordVector.size();
		numberOfSolvedWords = _wordsearch.getUserFoundWords().size();
		if(numberOfWordsInPuzzle == numberOfSolvedWords)
		{
			Toast.makeText(this, "Wordsearch complete!", Toast.LENGTH_SHORT).show();
			Button submitButton = (Button)findViewById(R.id.puzzleSubmitButton);
			submitButton.setEnabled(true);
		}
		if(_wordsearch.getFullSolutionStored())
		{
			View v = findViewById(R.id.puzzlePopulateSolution);
			v.setEnabled(true);
		}
    	initGrid(_wordsearch);
	    initWordList(wordVector);
	    initSolutionWords(_wordsearch.getUserFoundWords());
	}
	
	private void initSolutionWords(Vector<SolutionWord> solvedWords) {
		if(solvedWords == null) return;
		grid.solutionWords = new Vector<SolutionWord>(); 
		for(int i = 0; i < solvedWords.size(); i++)
		{
			SolutionWord word = solvedWords.get(i);
			LinearLayout wordsLayout = (LinearLayout)findViewById(R.id.puzzleWordList);
			for(int c = 0; c < wordsLayout.getChildCount(); c++)
			{
				TextView thisTextArea = (TextView)wordsLayout.getChildAt(c);
				if(thisTextArea.getText().toString().toUpperCase().equals(word.Word.toUpperCase()))
				{
					// to undo thisTextArea.setPaintFlags(thisTextArea.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
					thisTextArea.setPaintFlags(thisTextArea.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					
					grid.solutionWords.add(word);
					break;
				}
			}
		}
	}

	public void initGrid(Wordsearch wordsearch) throws NullPointerException
	{
		Vector<String> gridInOrigFormat = wordsearch.getGrid();
	    
	    numberOfColumns = gridInOrigFormat.get(0).length();
	    
	    grid.setNumColumns(numberOfColumns);
	    
	    StringBuilder fullGrid = new StringBuilder();
	    
	    // load bottom to top
	    for(int i = gridInOrigFormat.size()-1; i >= 0; i--)
	    {
	    	fullGrid.append(gridInOrigFormat.get(i));
	    }
	    
	 // could have used regex but it is slower than this solution
	 // split("") also does it but puts a whitespace character at the front of the array
	    char[] gridArray = fullGrid.toString().toCharArray(); 
	    GridItem[] gridItems = new GridItem[gridArray.length];
	    int gridItemSize = SizeGridSquare();
	    for(int j = 0; j < gridArray.length; j++)
	    {
	    	GridItem newGridItem = new GridItem(String.valueOf(gridArray[j]),gridItemSize);
	    	gridItems[j] = newGridItem;
	    }
	    
	    PuzzleViewAdapter adapter = new PuzzleViewAdapter(this, R.layout.grid_item, gridItems);
	    grid.setAdapter(adapter);
	}

	public class GridItem
	{
		public String letter;
		public int fontSize;
		
		public GridItem()
		{
			super();
		}
		
		public GridItem(String letter, int fontSize)
		{
			super();
			this.letter = letter;
			this.fontSize = fontSize;
		}
	}
	
	public class PuzzleViewAdapter extends ArrayAdapter<GridItem>
	{
		Context context;
		int layoutResourceId;
		GridItem data[] = null;
		
		public PuzzleViewAdapter(Context context, int layoutResourceId, GridItem[] data)
		{
			super(context, layoutResourceId, data);
			this.context = context;
			this.data = data;
		}
		
		@Override
	    public View getView(int position, View pView, ViewGroup parent) {
			
	        TextView thisTextView; 
	        
	        if(pView != null)
	        	thisTextView =(TextView)pView;
	        else
	        {
	        	LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	            thisTextView = (TextView) inflater.inflate(R.layout.grid_item, parent, false);	        
	        }
            
	        thisTextView.setTextSize(data[position].fontSize);
	        thisTextView.setText(data[position].letter);
	        return thisTextView;
	    }
		
		
	}
	
	private int SizeGridSquare()
	{
    	int orientation = getResources().getConfiguration().orientation;
    	int modifier = 0;
    	if(orientation == Configuration.ORIENTATION_LANDSCAPE)
    		modifier = -8;
    	if(numberOfColumns <= 10)
    		return 30 + modifier;
    	else if(numberOfColumns == 11)
    		return 28 + modifier;
    	else if(numberOfColumns == 12)
    		return 26 + modifier;
    	else if(numberOfColumns == 13)
    		return 24 + modifier;
    	else if(numberOfColumns == 14)
    		return 22 + modifier;
    	else // if(numberOfColumns >= 15)
    		return 21 + modifier;
	}
	
	
	
	public void initWordList(Vector<String> words)
	{
		LinearLayout wordList = (LinearLayout)findViewById(R.id.puzzleWordList);
		wordList.removeAllViews();
		for(int i = 0; i < words.size();i++)
		{
			TextView wordView = (TextView)getLayoutInflater().inflate(R.layout.word_item, null);
			
			wordView.setText(words.get(i));
			wordList.addView(wordView);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putAll(grid.getSavedState());
		savedInstanceState.putString("wordsearch_date", _wordsearch.date());
		PuzzleStorage.SaveWordsearch(this, _wordsearch);
	}
	
	@Override
	public void onPause() 
	{
		super.onPause();
		PuzzleStorage.SaveWordsearch(this, _wordsearch);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		grid.restoreSavedState(savedInstanceState);
	}
	
	// -------------------------- BUTTONS --------------------------
	public void MenuButtonClicked(View v)
	{
		PuzzleStorage.SaveWordsearch(this, _wordsearch);
		finish();
	}
	
	public void SubmitButtonClicked(View v)
	{
		v.setEnabled(false);
		
		PuzzleGridView view = (PuzzleGridView)findViewById(R.id.puzzleGridView);
		JSONObject solutionObject = new JSONObject();
		JSONArray solutionWordsArray = new JSONArray();
		for(int i = 0; i < view.solutionWords.size(); i++)
		{
			SolutionWord thisSolutionWord = view.solutionWords.get(i);
			JSONObject thisSolutionWordJSONObj = new JSONObject();
			try {
				thisSolutionWordJSONObj.put("Word", thisSolutionWord.Word);
				// mistakes were made early on... so having row as column and vice versa works FINE....
				thisSolutionWordJSONObj.put("Row", thisSolutionWord.Column);
				thisSolutionWordJSONObj.put("Column", thisSolutionWord.Row);
				thisSolutionWordJSONObj.put("Direction", thisSolutionWord.Direction);
				solutionWordsArray.put(thisSolutionWordJSONObj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		JSONObject submissionObject = new JSONObject();
		JSONObject wrapperObject = new JSONObject();
		try {
			solutionObject.put("Id", _wordsearch.getId());
			solutionObject.put("SolutionWords",solutionWordsArray);
			submissionObject.put("Solution", solutionObject);
			submissionObject.put("Username",SharedPreferencesWrapper.getFromPrefs(this, "username", ""));
			submissionObject.put("Password",SharedPreferencesWrapper.getFromPrefs(this, "password", ""));
			wrapperObject.put("Submission", submissionObject);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		PerformHttpPOST data = new PerformHttpPOST(this);
		
		WordsearchURIBuilder builder = new WordsearchURIBuilder(SharedPreferencesWrapper.getFromPrefs(this, "server_url", Utility.defaultServerURL));
		data.execute(builder.PostSolutionToServer(), wrapperObject.toString());
	}
	
	public void PopulateSolutionButtonClicked(View v)
	{
		_wordsearch.setSolved();
		Vector<SolutionWord> sol = _wordsearch.getSolution();
		_wordsearch.setUserSolvedWords(sol);
		PuzzleStorage.SaveWordsearch(this, _wordsearch);
		Intent thisIntent = new Intent(this, PuzzleActivity.class);
		thisIntent.putExtra("wordsearch_date", _wordsearch.date());
		finish();
		startActivity(thisIntent);
	}

	@Override
	public void onTaskCompleted(String httpData) {
		JSONObject response;
		try {
			response = new JSONObject(httpData);
			if(response.has("Error"))
			{
				JSONObject obj = response.getJSONObject("Error");
				Toast.makeText(this, obj.getString("Type")+ " - " + obj.getString("Message"), Toast.LENGTH_LONG).show();
				View submitButton =findViewById(R.id.puzzleSubmitButton);
				submitButton.setEnabled(true);
			}
			else
			{
				// we need to get the score from the server and populate the wordsearch object with the score
				// and set it as submitted
				finish();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onWordFound(View v, SolutionWord word) {
		// check if the user has already found the word
		if(_wordsearch.hasWordAlreadyBeenFound(word.Word))
		{
			return;
		}
		numberOfSolvedWords++;
		_wordsearch.addSolvedWord(word);
		if(numberOfSolvedWords >= numberOfWordsInPuzzle)
		{
			Toast.makeText(this, "Wordsearch complete!", Toast.LENGTH_SHORT).show();
			_wordsearch.setSolved();
			Button submitButton = (Button)findViewById(R.id.puzzleSubmitButton);
			submitButton.setEnabled(true);
		}
		else
		{
			Toast.makeText(this, "Word found: "+word.Word, Toast.LENGTH_SHORT).show();
		}
		PuzzleStorage.SaveWordsearch(this, _wordsearch);
	}
}