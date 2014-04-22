package info.androidhive.tabsswipe;

import java.text.DateFormat;

import info.androidhive.tabsswipe.adapter.TabsPagerAdapter;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	private String stringurl="https://playground.cs.hut.fi/t-110.5140/hello.txt";
	
	//Picking date in fragment 1
	private DatePicker Date1;
	private DatePicker Date2;
	 
	//Needed for reading text file in fragment 2
	private TextView textview;
	
	//Needed for retrieving image from URL in fragment 3
	private EditText edittext;
	
	//Needed for displaying weather details in fragment 4
	private EditText woeid;
	
	// Tab titles
	private String[] tabs = { "Introduction", "Access the network", "Display images", "Weather around the world","View your location" };
	
	
	// OnClick method for displaying message from file in fragment 2
	public void myClickHandler(View view){
		ConnectivityManager connmngr=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo networkinfo=connmngr.getActiveNetworkInfo();
		MainActivity ma=new MainActivity();
		if(networkinfo!=null && networkinfo.isConnected()){
			new Fragment_2().new DownloadWebpageTask(ma).execute(stringurl);
		}else{
			textview.setText("No network connection available");
		}
	}
	
	//Called by the onPostExecute() method of fragment 2
	public void displayText(String result){
		DateFormat df=DateFormat.getDateInstance(DateFormat.LONG);
		java.util.Date d=new java.util.Date();
		String display=result + " " + df.format(d);
		Log.d("DOWNLOAD",display);
		new Fragment_2().changeText(display);
	}
	
	// OnClick method for displaying image from file in fragment 3
	public void showImage(View view){
		ConnectivityManager connmngr=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo=connmngr.getActiveNetworkInfo();
		
		MainActivity ma=new MainActivity();
		if(networkinfo!=null && networkinfo.isConnected()){
			edittext=(EditText)findViewById(R.id.url_input);
			new Fragment_3().new DownloadImage(ma).execute(edittext.getText().toString());	
		}else{
			Toast.makeText(getApplicationContext(), "Error! Network connectivity could not be established!", Toast.LENGTH_LONG).show();
		}
	}
	
	//Called by the onPostExecute() method of fragment 3
	public void displayImage(Bitmap bmp){
		new Fragment_3().changeImage(bmp);
	}
	public void displaychart(String[] DataArray){
		new Fragment_1().OpenChart(DataArray);
	}
	
	//OnClick method for displaying weather details of fragment 4
	public void searchWoeid(View view){
		woeid=(EditText)findViewById(R.id.woeid);
		MainActivity ma=new MainActivity();
		Log.d("ENTERED WOEID", woeid.getText().toString());
		new Fragment_4().new DownloadWeatherData(ma).execute(woeid.getText().toString());
	}

	//called by the onPostExecute method of fragment 4
	public void weatherData(String weatherdata){
		new Fragment_4().changeWeather(weatherdata);
	}
	
	//OnClick method for displaying user location details in fragment 5 (code for Android's Network Location Provider obtained from http://developer.android.com/guide/topics/location/strategies.html)
	public void view_location(View view){
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			// Called when a new location is found by the network location provider.
		    public void onLocationChanged(Location location) {
		    	MainActivity ma=new MainActivity();
		    	Log.d("LOCATION : ",String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()) );
		    	new Fragment_5().new sendUserDetails(ma).execute(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()));
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}
	
	// Called by the onPostExecute method of fragment 5
	public void locationData(String locationresult){
		new Fragment_5().locationDetails(locationresult);
	}
	
	//OnClick method for displaying temperatures details in fragment 1
	public void ShowData(View view){
		Date1=(DatePicker)findViewById(R.id.Date1);
		Date2=(DatePicker)findViewById(R.id.Date2);
		MainActivity ma=new MainActivity();
		Log.d("ENTERED DATE1", (Integer.toString(Date1.getDayOfMonth())+"/"+Integer.toString(Date1.getMonth())));
		Log.d("ENTERED DATE2", (Integer.toString(Date2.getDayOfMonth())+"/"+Integer.toString(Date2.getMonth())));
		new Fragment_1().new DownloadData(ma,view).execute(Integer.toString(Date1.getDayOfMonth()),Integer.toString(Date1.getMonth()),Integer.toString(Date2.getDayOfMonth()),Integer.toString(Date2.getMonth()));
	}
	
	//Construct and send URL to server
	//public void sendURL(View view){
		
	//}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		
		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}
		
		// on swiping the viewpager make respective tab selected
		 
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}


}
