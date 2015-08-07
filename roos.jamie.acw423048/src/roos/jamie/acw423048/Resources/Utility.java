package roos.jamie.acw423048.Resources;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Utility {

	public final static String defaultServerURL = "08309.net.dcs.hull.ac.uk";
	
	public Utility() {
		// TODO Auto-generated constructor stub
	}

	public static enum RequestTypes
	{
		Register,
		Unregister,
		ChangePassword,
		Details,
		Score,
		ScoreWithDate,
		CurrentPuzzle,
		Solution,
		GetPuzzleByDate,
		Submit
	}
	
	public static String getFormattedTodaysDate()
	{
        // get todays date 
    	Calendar cal = Calendar.getInstance();
    	Date today = cal.getTime();
    	// get the format
    	SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
    	// get the string in the desired format
    	String formattedTodaysDate = dayFormat.format(today);
    	return formattedTodaysDate;
	}
	
	public static AlertDialog createErrorDialog(String errorType, String errorMessage, Activity context)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(errorType);
		alertDialog.setMessage(errorMessage);		
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// kill the dialog when the button is pressed
				dialog.cancel();
			}
		});
		return alertDialog;
	}
}
