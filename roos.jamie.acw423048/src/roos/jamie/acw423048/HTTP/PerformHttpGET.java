package roos.jamie.acw423048.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

@SuppressWarnings("deprecation")
public class PerformHttpGET extends AsyncTask<String, Void, String> implements OnRetrieveHttpData{

	private int StatusCodeOK = 200;
	
	private OnRetrieveHttpData listener;
	
	// Constructor
	public PerformHttpGET(OnRetrieveHttpData listener)
	{
		this.listener = listener;
	}
	
	@Override
	protected String doInBackground(String... params) {
		return getHttpData(params[0]);
	}
	
	private String getHttpData(String url)
	{
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.e("ClientProtocolException", "Client Protocol Exception thrown by httpClient.execute() in GET method");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("IOException", "IO exception thrown by httpClient.execute()");
			e.printStackTrace();
		} finally{
			httpClient.getConnectionManager().shutdown();
		}
		
		// setup string builder
		StringBuilder builder = new StringBuilder();
		
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if(statusCode == StatusCodeOK)
		{
			HttpEntity entity = response.getEntity();
			InputStream iStreamContent = null;
			try {
				iStreamContent = entity.getContent();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			InputStreamReader inReader = new InputStreamReader(iStreamContent, Charset.forName("UTF-8"));
			BufferedReader reader = new BufferedReader(inReader);
			
			// get the content from the stream
			String currentLine = null;
			try {
				while((currentLine=reader.readLine())!= null)
				{
					builder.append(currentLine);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			// TODO something else here
			Log.e("Error code", "Website returned code "+statusCode);
		}
		return builder.toString();
	}

	@Override
	public void onPostExecute(String httpData) {
		listener.onTaskCompleted(httpData);
	};
	
	@Override
	public void onTaskCompleted(String httpData) {	}
}
