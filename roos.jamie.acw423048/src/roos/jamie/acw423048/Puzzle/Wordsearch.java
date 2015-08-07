package roos.jamie.acw423048.Puzzle;

import java.io.Serializable;
import java.util.Random;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;

public class Wordsearch implements Serializable{
	private static final long serialVersionUID = -4131822296489497205L;
	// members
	private Vector<String> _wordsearchGrid = null;
	private Vector<String> _wordsearchWords = null;
	private Vector<SolutionWord> _wordsearchSolution = null;
	private Vector<SolutionWord> _userFoundWords = null;
	private boolean _fullSolutionStored = false;
	private String _id = null;
	private String _date = null; // yyyy-MM-dd
	private boolean _submittable = false;
	private float _score = 0.0f;
	private boolean _solved = false;
	private boolean _scoreRetrieved = false;
	
	// constructor
	public Wordsearch(Vector<String> wordsearchGrid, Vector<String> wordsearchWords, String id, String dateOfWordsearch, boolean submittable) {
		_wordsearchGrid = wordsearchGrid;
		_wordsearchWords = wordsearchWords;
		_date = dateOfWordsearch;
		_id = id;
		_submittable = submittable;
		_userFoundWords = new Vector<SolutionWord>();
	}

	// getters
	public Vector<String> getGrid()
	{
		return _wordsearchGrid;
	}
	
	public Vector<String> getWords()
	{
		return _wordsearchWords;
	}
	
	public Vector<SolutionWord> getSolution()
	{
		return _wordsearchSolution;
	}
	
	public boolean getScoreHasBeenRetrieved()
	{
		return _scoreRetrieved;
	}
	
	public String getId()
	{
		return _id;
	}
	
	public boolean getSubmittable()
	{
		return _submittable;
	}
	
	public boolean getSolved()
	{
		return _solved;
	}
	
	public boolean getFullSolutionStored()
	{
		return _fullSolutionStored;
	}
	
	public float getScore()
	{
		return _score;
	}
	
	public Vector<SolutionWord> getUserFoundWords()
	{
		return _userFoundWords;
	}
	
	public boolean hasWordAlreadyBeenFound(String word)
	{
		boolean userFound = false;
		for(int i = 0; i < _userFoundWords.size();i++)
		{
			if(_userFoundWords.get(i).Word.equals(word))
				userFound = true;
		}
		return userFound;
	}
	
	// setters
	public void setSolution(Vector<SolutionWord> wordsearchSolution)
	{
		_wordsearchSolution = wordsearchSolution;
	}
	
	public void setScore(float score)
	{
		_score = score;
	}
	
	public void setSolved()
	{
		_solved = true;
	}
	
	public void addSolvedWord(SolutionWord word)
	{
		_userFoundWords.add(word);
	}
	
	public void setUserSolvedWords(Vector<SolutionWord> words)
	{
		_userFoundWords = words;
	}
	
	// static getters
	public static Vector<SolutionWord> GetSolution(String jsonString) throws JSONException
	{
		JSONObject solutionObj = new JSONObject(jsonString);
		return getSolutionFromJsonObject(solutionObj);
	}
	
	public static Wordsearch DeserializeWordsearchJSON(String jsonString, String dateOfWordsearch)
	{
		Vector<String> wordsearchGrid = new Vector<String>();
		Vector<String> wordsearchWords = new Vector<String>();
		
		try {
			JSONObject jsonObj = new JSONObject(jsonString);
			if(jsonObj.has("Puzzle"))
			{
				JSONObject puzzleObj = jsonObj.getJSONObject("Puzzle");
				String id = puzzleObj.getString("Id");
				
				JSONArray grid = puzzleObj.getJSONArray("Grid");
				JSONArray words = puzzleObj.getJSONArray("Words");
				
				for(int i = 0; i < grid.length(); i++)
				{
					wordsearchGrid.addElement(grid.getString(i));
				}
				for(int j = 0; j < words.length(); j++)
				{
					wordsearchWords.addElement(words.getString(j));
				}
				return new Wordsearch(wordsearchGrid, wordsearchWords, id, dateOfWordsearch, true);
			}
			else //if(jsonObj.has("PuzzleAndSolution"))
			{
				Vector<SolutionWord> wordsearchSolution = new Vector<SolutionWord>();
				
				JSONObject puzzleAndSolutionObj = jsonObj.getJSONObject("PuzzleAndSolution");
				JSONObject puzzleObj = puzzleAndSolutionObj.getJSONObject("Puzzle");
				
				// ------------------------- puzzle -------------------------
				String id = puzzleObj.getString("Id");
				JSONArray grid = puzzleObj.getJSONArray("Grid");
				JSONArray words = puzzleObj.getJSONArray("Words");
				
				for(int i = 0; i < grid.length(); i++)
				{
					wordsearchGrid.addElement(grid.getString(i));
				}
				for(int j = 0; j < words.length(); j++)
				{
					wordsearchWords.addElement(words.getString(j));
				}
				
				// ------------------------- solution -------------------------

				JSONObject solutionObj = puzzleAndSolutionObj.getJSONObject("Solution");
				wordsearchSolution = getSolutionFromJsonObject(solutionObj);
				// final step
				Wordsearch wrdsrch = new Wordsearch(wordsearchGrid, wordsearchWords, id, dateOfWordsearch, false);
				wrdsrch.setSolution(wordsearchSolution);
				wrdsrch._fullSolutionStored = true;
				return wrdsrch;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private static Vector<SolutionWord> getSolutionFromJsonObject(JSONObject obj) throws JSONException
	{
		Random rnd = new Random();
		Vector<SolutionWord> wordsearchSolution = new Vector<SolutionWord>();
		
		JSONArray solutionArray = obj.getJSONArray("SolutionWords");
		
		for(int i = 0; i < solutionArray.length(); i++)
		{
			JSONObject newSolutionItem = solutionArray.getJSONObject(i);
			SolutionWord solWord = new SolutionWord();
			// populate object
			solWord.Word = newSolutionItem.getString("Word");
			solWord.Column = newSolutionItem.getInt("Row"); // yes they are the wrong way around.. my bad
			solWord.Row = newSolutionItem.getInt("Column");
			solWord.Direction = newSolutionItem.getInt("Direction");
			solWord.colorToDraw = Color.argb(60, rnd.nextInt(256), rnd.nextInt(256),rnd.nextInt(256));
			wordsearchSolution.addElement(solWord);
		}
		return wordsearchSolution;
	}
	
	public String date()
	{
		return _date;
	}
}
