package info.androidhive.tabsswipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import info.androidhive.tabsswipe.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment_2 extends Fragment {
	
	private static final String DEBUG_TAG="HttpExample";
	MainActivity ma;
	String result;
	private static TextView textview;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String text="File text here...";
		View rootView = inflater.inflate(R.layout.fragment_2, container, false);
	    textview=(TextView)rootView.findViewById(R.id.web_text);
		textview.setText(text);
		return rootView;
	}
	
	public void changeText(String text){
		textview.setText(text);
	}

	//Call AsyncTask to create a new, background network thread which is separate from the UI thread
	public class DownloadWebpageTask extends AsyncTask<String,String,String>{
		
		MainActivity ma;
		
		DownloadWebpageTask(MainActivity ma){
			this.ma=ma;
		}

		@Override
		protected String doInBackground(String... urls) {
			try{
				result=downloadUrl(urls[0]);
			}catch(Exception e){
				e.printStackTrace();
				result="Exception while reading the text";
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result){
			ma.displayText(result);
		}
		//downloadUrl fetches and processes the web page content. After processing, it returns a result string.
		private String downloadUrl(String myurl) throws IOException{
			InputStream is=null;
			String str=null;
			
			try{
				URL url=new URL(myurl);
				Log.d("URL:",url.toString());
				HttpURLConnection conn=(HttpURLConnection) url.openConnection();
				conn.setReadTimeout(20000);
				conn.setConnectTimeout(30000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				Log.d("TRY DOWNLOAD","Inside try block of downloadUrl");
				//Start the query
				conn.connect();
				int response=conn.getResponseCode();
				Log.d(DEBUG_TAG,"The response is : " + response);
				BufferedReader in=new BufferedReader(new InputStreamReader(url.openStream()));
				StringBuilder sb=new StringBuilder();
				while((str=in.readLine())!=null){
					sb.append(str);
				}
				return sb.toString();
			}finally{
				//input stream should be closed after the app has finished using it
				if(is!=null){
					is.close();
				}
			}	
		}
	}
}
