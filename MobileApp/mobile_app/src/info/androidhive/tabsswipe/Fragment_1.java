package info.androidhive.tabsswipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.*;
import org.achartengine.chart.*;
import org.achartengine.model.*;
import org.achartengine.renderer.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.*;

import info.androidhive.tabsswipe.R;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.Activity;
import android.content.ContextWrapper;



public class Fragment_1 extends Fragment {
	
	public static GraphicalView mChart;
	private View rootView;
	public static XYMultipleSeriesRenderer mRenderer;
	public static XYMultipleSeriesDataset dataset;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_1, container, false);	
		DatePicker picker1= (DatePicker) rootView.findViewById(R.id.Date1);
		DatePicker picker2= (DatePicker) rootView.findViewById(R.id.Date2);
		try {
		Field f1[] =picker1.getClass().getDeclaredFields();
		Field f2[] =picker2.getClass().getDeclaredFields();
        for (Field field : f1) {
            if (field.getName().equals("mYearPicker") || field.getName().equals("mYearSpinner")) {
                field.setAccessible(true);
                Object yearPicker = new Object();
				yearPicker = field.get(picker1);
				((View) yearPicker).setVisibility(View.GONE);
				}
        }
        for (Field field : f2) {
            if (field.getName().equals("mYearPicker") || field.getName().equals("mYearSpinner")) {
                field.setAccessible(true);
                Object yearPicker = new Object();
				yearPicker = field.get(picker2);
				((View) yearPicker).setVisibility(View.GONE);
				}
        }
		}
            catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
            catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
		OpenChartempty();
		return rootView;
	}
	
	 public void OpenChartempty()
	    {
	     // Define the number of elements you want in the chart.
	     // Create a Dataset to hold the XSeries.
	     
	     dataset=new XYMultipleSeriesDataset();

	     mRenderer=new XYMultipleSeriesRenderer();
	     
	     mRenderer.setChartTitle("Mean temperature in Aalto for selected period");
	     mRenderer.setXTitle("Days");
	     mRenderer.setYTitle("Temperature");
	     mRenderer.setZoomButtonsVisible(true);
	     mRenderer.setXLabels(0);
	     mRenderer.setPanEnabled(false);
	   
	   
	     mRenderer.setShowGrid(true);
	 
	     mRenderer.setClickEnabled(true);

	      // Adding the XSeriesRenderer to the MultipleRenderer. 
	     
	     LinearLayout chart_container=(LinearLayout)rootView.findViewById(R.id.Chart_layout);

	   // Creating an intent to plot line chart using dataset and multipleRenderer
	     
	     mChart=(GraphicalView)ChartFactory.getLineChartView(((ContextWrapper)rootView.getContext()).getBaseContext(), dataset, mRenderer);
	     
	     //  Adding click event to the Line Chart.
	// Add the graphical view mChart object into the Linear layout .
	     chart_container.addView(mChart);
	}
	
	public void OpenChart(String[] DataArray)
    {
     // Define the number of elements you want in the chart.
     int day[] = new int[DataArray.length];  
     for(int i=0;i<DataArray.length;i++){
    	 day[i]=i+1;
     }
    
      // Create XY Series for X Series.
     XYSeries xSeries=new XYSeries("Days");
     
     //  Adding data to the X Series.
     for(int i=0;i<DataArray.length;i++)
     {
      xSeries.add(day[i],Float.parseFloat(DataArray[i]));
     }

      // Add X series to the Dataset.   
     dataset.clear();
     dataset.addSeries(xSeries);
     
     
      // Create XYSeriesRenderer to customize XSeries

     XYSeriesRenderer Xrenderer=new XYSeriesRenderer();
     Xrenderer.setColor(Color.GREEN);
     Xrenderer.setPointStyle(PointStyle.DIAMOND);
     Xrenderer.setDisplayChartValues(true);
     Xrenderer.setLineWidth(2);
     Xrenderer.setFillPoints(true);
     
     // Create XYMultipleSeriesRenderer to customize the whole chart
     
       // Adding the XSeriesRenderer to the MultipleRenderer. 
     mRenderer.addSeriesRenderer(Xrenderer);
     mChart.repaint();
     
 }



	public class DownloadData extends AsyncTask<String,Void,String[]>{

	MainActivity ma;
	
    
	
		DownloadData(MainActivity ma,View rootView){
			this.ma=ma;
		}
		
		
		//Call AsyncTask to create a background thread which is different from the UI thread
		
		public List<String> getDatafromUrl(String param1, String param2, String param3, String param4) {
		
				DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
				HttpGet httpget = new HttpGet("http://playground.cs.hut.fi/t-110.5140/visualizer/daily/average/json");
				InputStream inputStream = null;
				String result = null;
				try {
					HttpResponse response = httpclient.execute(httpget);
				    
				    Log.d("HTTP response","The code returned is " + Integer.toString(response.getStatusLine().getStatusCode()));
				    HttpEntity entity = response.getEntity();
	
				    inputStream = entity.getContent();
				    // json is UTF-8 by default
				    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
				    StringBuilder sb = new StringBuilder();
	
				    String line = null;
				    while ((line = reader.readLine()) != null)
				    {	
				    	Log.d("data received:",line);
				        sb.append(line + "\n");
				    }
				    result = sb.toString();
				} catch (Exception e) { 
					Log.d("HTTP GET error", e.toString());
				}
				finally {
				    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
				}
				
				JSONObject jObject; 
				JSONArray jArray;
				List<String> temperature = new ArrayList<String>();
				if (result != null){
					try {  
						jObject = new JSONObject(result);
						jArray = jObject.getJSONArray("data");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						jArray=null;
					}
				
	
				long d1; 
				long d2;
		
				switch (Integer.parseInt(param2)){
				case 0: d1=0; break;
				case 1: d1=31*86400; break;
				case 2: d1=59*86400; break;
				case 3: d1=90*86400; break;
				default: d1=99999*86400;
				}
				switch (Integer.parseInt(param4)){
				case 0: d2=0; break;
				case 1: d2=31*86400; break;
				case 2: d2=59*86400; break;
				case 3: d2=90*86400; break;
				default: d2=99999*86400;
				}
				try {
				for (int i=0; i < jArray.length(); i++)
				{
				    
				        JSONObject oneObject = jArray.getJSONObject(i);
				        // Pulling items from the array
				        String timestamp = oneObject.getString("timestamp");
				        
				        if((1293840000+Integer.parseInt(param1)*86400+d1<=Integer.parseInt(timestamp)) && (Integer.parseInt(timestamp)<=1293840000+Integer.parseInt(param3)*86400+d2)){
				        	temperature.add(oneObject.getString("temperature"));
				        }
				    }
				} catch (JSONException e) {
				        // Oops
			
					}
				}
				else
				{
				Log.d("NO VALID DATA","no corresponding data");
				}
				return temperature;
		}
	
		@Override
		protected String[] doInBackground(String... param) {
			List<String> Datalist = getDatafromUrl(param[0],param[1],param[2],param[3]);
			String[] DataArray = new String[ Datalist.size() ];
			Datalist.toArray( DataArray );
			return DataArray ;
		}
		
		@Override
		protected void onPostExecute(String[] DataArray){
			ma.displaychart(DataArray);
		}
	
	}
}
