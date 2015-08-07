package roos.jamie.acw423048;

import roos.jamie.acw423048.HTTP.OnRetrieveHttpData;
import roos.jamie.acw423048.HTTP.PerformHttpGET;
import roos.jamie.acw423048.HTTP.WordsearchURIBuilder;
import roos.jamie.acw423048.Resources.Utility;
import roos.jamie.acw423048.Resources.Utility.RequestTypes;
import roos.jamie.acw423048.SharedPrefs.SharedPreferencesWrapper;
import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JSONActivity extends Activity implements OnRetrieveHttpData {

	private final String defaultServerURL = "08309.net.dcs.hull.ac.uk";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String httpUrlIntent = SharedPreferencesWrapper.getFromPrefs(this, "server_url", defaultServerURL).trim();
        WordsearchURIBuilder uriBuilder = new WordsearchURIBuilder(httpUrlIntent);
        
        // get type of request
        Boolean testing = getIntent().getExtras().getBoolean("Testing");
        RequestTypes requestType = (RequestTypes) getIntent().getExtras().getSerializable("ServerRequestType");
        // get the stuff we need
        String username = SharedPreferencesWrapper.getFromPrefs(this, "username", "").trim();
        String password = SharedPreferencesWrapper.getFromPrefs(this, "password", "").trim();
        String firstName = SharedPreferencesWrapper.getFromPrefs(this, "first_name", "").trim();
        String lastName = SharedPreferencesWrapper.getFromPrefs(this, "last_name", "").trim();
        String todayDate = Utility.getFormattedTodaysDate();
        
        String urlToUse = "";
        switch(requestType)
        {
        	case Register:
        		urlToUse = uriBuilder.RegisterUser(firstName, lastName, username, password);
        		break;
        	case Unregister: 
        		urlToUse = uriBuilder.UnregisterUser(username, password);
        		break;
        	case ChangePassword:
        		String oldPassword = getIntent().getExtras().getString("oldPassword");
        		String newPassword = getIntent().getExtras().getString("newPassword");
        		urlToUse = uriBuilder.ChangePassword(username, oldPassword, newPassword);
        		break;
        	case Details:
        		urlToUse = uriBuilder.GetDetails(username, password);
        		break;
        	case Score: 
        		urlToUse = uriBuilder.GetUserScore(username, password);
        		break;
        	case ScoreWithDate:
        		String date = getIntent().getExtras().getString("scoreDate");
        		urlToUse = uriBuilder.GetUserScore(username, password, date);
        		break;
        	case CurrentPuzzle: 
        		urlToUse = uriBuilder.GetTodaysPuzzle(username, password);
    			break;
        	case Solution:
        		String puzzleGuid = getIntent().getExtras().getString("puzzleGuid");
        		urlToUse = uriBuilder.GetSolutionToPuzzle(puzzleGuid);
        		break;
        	case GetPuzzleByDate:
        		String puzzleDate = getIntent().getExtras().getString("puzzleDate");
        		urlToUse = uriBuilder.GetOldPuzzle(puzzleDate);
        		break;
        	case Submit:
        		urlToUse = uriBuilder.PostSolutionToServer();
        		break;
        }
        if(testing)
        {
        	// do something here
        	try
            {
    	        retrieveJsonDataFromServer(urlToUse);
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            }
        }
        else
        {
        	
        }
        
        setContentView(R.layout.activity_json);
    }
	
	private void retrieveJsonDataFromServer(String httpUrlIntent)
	{
		PerformHttpGET retrieveData = new PerformHttpGET(this);
		retrieveData.execute(httpUrlIntent);
	}

	@Override
	public void onTaskCompleted(String httpData) {
		LinearLayout rootLayout = (LinearLayout)findViewById(R.id.rootLayout);
		rootLayout.addView(createTextView(httpData));
	}

	private TextView createTextView(String entryData)
	{
		TextView entryView = new TextView(this);
		entryView.setText(entryData);
		entryView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		return entryView;
	}
}
