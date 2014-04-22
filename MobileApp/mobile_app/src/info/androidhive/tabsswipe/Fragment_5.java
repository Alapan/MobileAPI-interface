package info.androidhive.tabsswipe;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.*;




public class Fragment_5 extends Fragment{
	
	private static final String TAG="Fragment 5";
	private UiLifecycleHelper uiHelper;
	private TextView userInfoTextView;
	private static TextView locdetails;
	private Button viewloc;
	private Session session;
	private static String accesstoken;
	
	class MyLocation{
		
		String formatted_address;
		String locationkind;
		String latitude;
		String longitude;
		String locationtype;
		
		public String toString(){
			String s;
			s="Current location: " + "\n"
			+ "Address - " + formatted_address + "\n"
			+ "Location Type - " + locationkind + "\n"
			+ "Latitude - " + latitude + "\n"
			+ "Longitude - " + longitude + "\n"
			+ "Location type - " + locationtype;
			
			return s;
		}
	}
	

	// Login code referred from https://developers.facebook.com/docs/android/login-with-facebook/	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_5, container, false);		
		LoginButton authButton=(LoginButton)rootView.findViewById(R.id.authButton);
		userInfoTextView = (TextView) rootView.findViewById(R.id.userInfoTextView);
		locdetails=(TextView)rootView.findViewById(R.id.locdetails);
		viewloc=(Button)rootView.findViewById(R.id.locbutton);
		viewloc.setVisibility(View.INVISIBLE);
		authButton.setFragment(this);
		authButton.setReadPermissions(Arrays.asList("user_location"));
		return rootView;
	}
	
	public void locationDetails(String locationresult){
		locdetails.setText(locationresult);
	}
	
	private String buildUserInfoDisplay(GraphUser user) {
	    StringBuilder userInfo = new StringBuilder("");
	    // Example: typed access (name)
	    // - no special permissions required
	    userInfo.append(String.format("Name: %s\n\n", 
	        user.getName()));

	    // Example: partially typed access, to location field,
	    // name key (location)
	    // - requires user_location permission
	    userInfo.append(String.format("Location: %s\n\n", 
	        user.getLocation().getProperty("name")));

	    return userInfo.toString();
	}
	
	//Respond to session state changes by making textview visible when user is logged in
	@SuppressWarnings("deprecation")
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	        userInfoTextView.setVisibility(View.VISIBLE);
	        viewloc.setVisibility(View.VISIBLE);
	     // Request user data and show the results
	        Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					userInfoTextView.setText(buildUserInfoDisplay(user));
				}
	        });
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	        userInfoTextView.setVisibility(View.INVISIBLE);
	        viewloc.setVisibility(View.INVISIBLE);
	        locdetails.setVisibility(View.INVISIBLE);
	    }
	}
	
	//Listen for session state changes
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	//Override the fragment lifecycle methods to create sessions
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	    	accesstoken=session.getAccessToken();
	    	Log.d("ACCESS TOKEN : ",accesstoken);
	        onSessionStateChange(session, session.getState(), null);
	    }
	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	public class sendUserDetails extends AsyncTask<String,Void,String>{
		
		MainActivity ma;
		sendUserDetails(MainActivity ma){
			this.ma=ma;
		}

		@Override
		protected String doInBackground(String... params) {
			String result=formConnection(params[0],params[1]);
			Log.d("RETURNED RESULT",result);
			return result;
		}
		
		@Override
		protected void onPostExecute(String result){
			Log.d("INSIDE onPOSTEXECUTE", "test");
			String locationresult=new sendUserDetails(this.ma).parseJSON(result).toString();
			ma.locationData(locationresult);
		}
		
		private MyLocation parseJSON(String input){
			MyLocation mylocation=new MyLocation();
			try {
				JSONObject json=new JSONObject(input);
				mylocation.formatted_address=json.getJSONArray("results").getJSONObject(0).getString("formatted_address");
				Log.d("Address : ",mylocation.formatted_address);
				mylocation.locationkind=json.getJSONArray("results").getJSONObject(0).getJSONArray("types").getString(0);
				Log.d("Location Kind : ",mylocation.locationkind);
				mylocation.latitude=json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lat");
				Log.d("Latitude : ",mylocation.latitude);
				mylocation.longitude=json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lng");
				Log.d("Longitude : ",mylocation.longitude);
				mylocation.locationtype=json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getString("location_type");
				Log.d("Type : ",mylocation.locationtype);
			} catch (JSONException e) {
				mylocation.formatted_address="empty";
				mylocation.locationkind="empty";
				mylocation.latitude="empty";
				mylocation.longitude="empty";
				mylocation.locationtype="empty";
			}
			return mylocation;
		}
		
		//Send URL and read returned data into a string
		
		private String formConnection(String latitude,String longitude){
			String inputurl="http://group02.naf.cs.hut.fi/fbrest.php";
			Log.d("INSIDE FRAGMENT 5 WITH LOCATION:",latitude + " " + longitude);
			Log.d("ACCESSTOKEN INSIDE formConnection: ",accesstoken);
			String requesturl=inputurl + "?accesstoken=" + accesstoken + "&latitude=" + latitude + "&longitude=" + longitude;
			String result=null;
			try{
				//Send GET request
				URL obj=new URL(requesturl);
				HttpURLConnection con=(HttpURLConnection)obj.openConnection();
				con.setRequestMethod("GET");
				int responsecode=con.getResponseCode();
				BufferedReader in=new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputline;
				StringBuilder response=new StringBuilder();
				while((inputline=in.readLine())!=null){
					response.append(inputline);
				}
				in.close();
				return response.toString();
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
	}

}
