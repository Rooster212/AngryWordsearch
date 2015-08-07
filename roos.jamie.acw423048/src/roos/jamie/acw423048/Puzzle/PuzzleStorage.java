package roos.jamie.acw423048.Puzzle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import roos.jamie.acw423048.HTTP.OnRetrieveHttpData;
import roos.jamie.acw423048.HTTP.PerformHttpGET;
import roos.jamie.acw423048.HTTP.WordsearchURIBuilder;
import roos.jamie.acw423048.Resources.Utility;
import roos.jamie.acw423048.SharedPrefs.SharedPreferencesWrapper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

public class PuzzleStorage implements OnRetrieveHttpData{
	private static final String filename = "PuzzleStorage.ser";
	public static TreeMap<String, Wordsearch> puzzleDatabase = new TreeMap<String, Wordsearch>();
	
	public static void StorePuzzlesToStorage(Context context)
	{
		FileOutputStream fout = null;
		ObjectOutputStream oos = null;
		try {
			fout = context.openFileOutput(filename, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(puzzleDatabase);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try
			{
				fout.close();
				oos.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void SaveWordsearch(Context context, Wordsearch wordsearch)
	{
		if(puzzleDatabase.containsKey(wordsearch.date()))
			puzzleDatabase.remove(wordsearch.date());
		puzzleDatabase.put(wordsearch.date(), wordsearch);
		StorePuzzlesToStorage(context);
	}
	
	public static TreeMap<String, Wordsearch> GetStorage(Context context)
	{
		GetPuzzlesFromStorage(context);
		return puzzleDatabase;
	}
	
	public static void GetPuzzlesFromStorage(Context context)
	{
		FileInputStream streamIn;
		try {
			
			streamIn = context.openFileInput(filename);
			// open stream
			ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
			@SuppressWarnings("unchecked")
			TreeMap<String, Wordsearch> data = (TreeMap<String, Wordsearch>)objectinputstream.readObject();
			
			// close streams
			streamIn.close();
			objectinputstream.close();
			
			// assign data to variable
			puzzleDatabase = data;
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public Wordsearch GetPuzzle(String date, Context ctxt) throws InterruptedException, ExecutionException
	{
		GetPuzzlesFromStorage(ctxt);
		Wordsearch currentWordsearch = null;
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		Date todaysDate = new Date();
	
		String formattedTodaysDate = fmt.format(todaysDate);
		
		if(puzzleDatabase.containsKey(date) && puzzleDatabase.get(date) != null)
		{
			currentWordsearch = puzzleDatabase.get(date);
			if(currentWordsearch == null || currentWordsearch.getGrid() == null || currentWordsearch.getWords() == null)
			{
				return GetWordsearchFromServer(date, ctxt, formattedTodaysDate);
			}
			else
			{
				Toast.makeText(ctxt, "Wordsearch loaded from storage!", Toast.LENGTH_SHORT).show();
				return currentWordsearch;
			}
		}
		else
		{
			return GetWordsearchFromServer(date, ctxt, formattedTodaysDate);
		}		
	}

	private Wordsearch GetWordsearchFromServer(String date, Context ctxt,
			String formattedTodaysDate) throws InterruptedException,
			ExecutionException {
		// go to the server and get the wordsearch
		WordsearchURIBuilder builder = new WordsearchURIBuilder(SharedPreferencesWrapper.getFromPrefs(ctxt, "server_url", Utility.defaultServerURL));
		String username = SharedPreferencesWrapper.getFromPrefs(ctxt, "username", "");
		String password = SharedPreferencesWrapper.getFromPrefs(ctxt, "password", "");
		
		String url = null;
		if(date.equals(formattedTodaysDate))
		{
			url = builder.GetTodaysPuzzle(username, password);
		}
		else
		{
			url = builder.GetOldPuzzle(date);
		}
		
		PerformHttpGET httpData = new PerformHttpGET(this);
		String jsonString = httpData.execute(url).get();
		Toast.makeText(ctxt, "Wordsearch loaded from server", Toast.LENGTH_SHORT).show();
		if(date.equals(formattedTodaysDate))
		{
			Wordsearch wsch = Wordsearch.DeserializeWordsearchJSON(jsonString, formattedTodaysDate);
			puzzleDatabase.put(formattedTodaysDate, wsch);
			StorePuzzlesToStorage(ctxt);
			return wsch;
		}
		else
		{
			Wordsearch wsch = Wordsearch.DeserializeWordsearchJSON(jsonString, date);
			puzzleDatabase.put(date, wsch);
			StorePuzzlesToStorage(ctxt);
			return wsch;
		}
	}

	public static boolean DeletePuzzle(Context context, String date)
	{
		GetPuzzlesFromStorage(context);
		if(puzzleDatabase.containsKey(date) && puzzleDatabase.get(date) != null)
		{
			puzzleDatabase.remove(date);
			StorePuzzlesToStorage(context);
			return true;
		}
		return false;
	}
	
	public static boolean ClearStorage(Context context)
	{
		context.deleteFile(filename);
		puzzleDatabase = new TreeMap<String, Wordsearch>();
		return true;
	}
	
	@Override
	public void onTaskCompleted(String httpData) {
	}
	
}
