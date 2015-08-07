package roos.jamie.acw423048.HTTP;

import java.util.HashMap;
import java.util.Map;

import android.net.Uri;

public class WordsearchURIBuilder {
	
	private String _url;
	private final String _api = "api";
	private final String _admin = "admin";
	private final String _wordsearch = "wordsearch";
	/*
	 * Default address
	 * http://08309.net.dcs.hull.ac.uk/
	 * 
	 * Api
	 * http://08309.net.dcs.hull.ac.uk/api/
	 * 
	 * Admin
	 * http://08309.net.dcs.hull.ac.uk/api/admin/
	 * 
	 * Puzzles
	 * http://08309.net.dcs.hull.ac.uk/api/wordsearch
	 * 
	 * */
	
	public WordsearchURIBuilder(String URL) {
		_url = URL;
	}

	private String getURL(String type, String request, HashMap<String, String> params)
	{
		Uri.Builder builder = new Uri.Builder();
		// do build
		builder.scheme("http")
			.authority(_url)
			.appendPath(_api)
			.appendPath(type)
			.appendPath(request);
		
		
		if(!params.isEmpty()) // if we have params
		{
			for(Map.Entry<String, String> entry : params.entrySet())
			{
				builder.appendQueryParameter(entry.getKey(), entry.getValue());
			}
		}
		return builder.build().toString();
	}
	
	public String RegisterUser(String firstName, String surname, String username, String password)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("firstname", firstName);
		params.put("Surname", surname);
		params.put("username",username);
		params.put("password", password);
		String url = getURL(_admin,"register",params);
		return url;
	}
	
	public String UnregisterUser(String username, String password)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username",username);
		params.put("password", password);
		String url = getURL(_admin,"unregister",params);
		return url;
	}
	
	public String ChangePassword(String username, String oldPassword, String newPassword)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username",username);
		params.put("oldpassword", oldPassword);
		params.put("newpassword", newPassword);
		String url = getURL(_admin,"change",params);
		return url;
	}
	
	public String GetDetails(String username, String password)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username",username);
		params.put("password",password);
		String url = getURL(_admin,"details",params);
		return url;
	}
	
	public String GetUserScore(String username, String password)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username",username);
		params.put("password", password);
		String url = getURL(_admin,"score",params);
		return url;
	}
	
	public String GetUserScore(String username, String password, String date)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username",username);
		params.put("password", password);
		params.put("date", date); // this must be in format YYYY-MM-DD
		String url = getURL(_admin,"score",params);
		return url;
	}
	
	public String GetTodaysPuzzle(String username, String password)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username",username);
		params.put("password", password);
		String url = getURL(_wordsearch,"current",params);
		return url;
	}
	
	public String GetSolutionToPuzzle(String puzzleGuid)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id",puzzleGuid);
		String url = getURL(_wordsearch,"solution",params);
		return url;
	}
	
	public String GetOldPuzzle(String puzzleDate)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("date",puzzleDate);
		String url = getURL(_wordsearch,"solution",params);
		return url;
	}
	
	public String PostSolutionToServer()
	{
		HashMap<String, String> params = new HashMap<String, String>();
		String url = getURL(_wordsearch,"submit",params);
		return url;
	}
}
