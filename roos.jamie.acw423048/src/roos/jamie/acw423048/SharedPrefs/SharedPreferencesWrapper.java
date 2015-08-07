package roos.jamie.acw423048.SharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesWrapper {	
	public static void saveToPrefs(Context context, String key, String value)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		final SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void removeFromPrefs(Context context, String key)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		final SharedPreferences.Editor editor = prefs.edit();
		editor.remove(key);
		editor.commit();
	}
	
	public static String getFromPrefs(Context context, String key, String defaultValue)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		try
		{
			return prefs.getString(key, defaultValue);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return defaultValue;
		}
	}

	
}
