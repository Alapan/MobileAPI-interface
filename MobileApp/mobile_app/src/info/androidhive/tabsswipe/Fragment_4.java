package info.androidhive.tabsswipe;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment_4 extends Fragment{
	
	private static TextView weather;
	
	//Code referred to from "http://android-er.blogspot.fi/2012/10/search-woeid-and-query-yahoo-weather.html"

	class MyWeather{
		String description;
		String city;
		String region;
		String country;
		
		String windchill;
		String winddirection;
		String windspeed;
		
		String sunrise;
		String sunset;
		
		String conditiontext;
		String conditiondate;
		String conditiontemp;
		
		public String toString(){
			String s;
			s = description + " -\n\n" + "city: " + city + "\n"
		     + "region: " + region + "\n"
		     + "country: " + country + "\n\n"
		     + "Wind\n"
		     + "chill: " + windchill + "\n"
		     + "direction: " + winddirection + "\n"
		     + "speed: " + windspeed + "\n\n"
		     + "Sunrise: " + sunrise + "\n"
		     + "Sunset: " + sunset + "\n\n"
		     + "Condition: " + conditiontext + "\n"
		     + conditiondate +"\n";
			
			return s;
		}
	}
	
	final String yahooapisloc="http://query.yahooapis.com/v1/public/yql?q=select*from%20geo.places%20where%20text=";
	final String yahooformat="&format=xml";
	String yahooapisquery;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_4, container, false);	
		weather=(TextView)rootView.findViewById(R.id.weather_data);
		return rootView;
	}
	
	public void changeWeather(String weatherdata){
		weather.setText(weatherdata);
	}
	
	public class DownloadWeatherData extends AsyncTask<String,Void,String>{
		
		String qresult="";
		String weatherresult;
		String weatherstring;
		MainActivity ma;
		
		DownloadWeatherData(MainActivity ma){
			this.ma=ma;
		}
		
		private String queryYahooWeather(String woeid){
			String querystring="http://weather.yahooapis.com/forecastrss?w=" + woeid;
			
			HttpClient httpclient=new DefaultHttpClient();
			HttpGet httpget=new HttpGet(querystring);
			try{
				HttpEntity httpentity=httpclient.execute(httpget).getEntity();
				if(httpentity!=null){
					BufferedReader br=new BufferedReader(new InputStreamReader(httpentity.getContent()));
					StringBuilder sb=new StringBuilder();
					String str=null;
					while((str=br.readLine())!=null){
						sb.append(str + "\n");
					}
					qresult=sb.toString();
				}
			}catch(ClientProtocolException cpe){
				cpe.printStackTrace();
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
			return qresult;
		}

		private Document convertStringToDocument(String datastring) {
			Document dest=null;
			DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
			DocumentBuilder parser;
			try{
				parser=dbf.newDocumentBuilder();
				dest=parser.parse(new ByteArrayInputStream(datastring.getBytes()));
			}catch(ParserConfigurationException pce){
				pce.printStackTrace();
			}catch(SAXException sx){
				sx.printStackTrace();
			}catch(IOException io){
				io.printStackTrace();
			}
			return dest;
		}

		@Override
		protected String doInBackground(String... woeid) {
			Log.d("RECEIVED WOEID",woeid[0]);
			weatherstring=queryYahooWeather(woeid[0]);
			Log.d("Returned by queryYahooWeather",weatherstring);
			Document weatherdoc=convertStringToDocument(weatherstring);
			
			if(weatherdoc!=null){
				weatherresult=parseWeather(weatherdoc).toString();
			}else{
				weatherresult="Cannot convert string to document";
			}
			return weatherresult;
		}

		@Override
		protected void onPostExecute(String result){
			ma.weatherData(result);
		}

		private MyWeather parseWeather(Document weatherdoc) {
			
			MyWeather myweather=new MyWeather();
			
			//Description
			NodeList descnode=weatherdoc.getElementsByTagName("description");
			if(descnode!=null && descnode.getLength()>0){
				myweather.description=descnode.item(0).getTextContent();
			}else{
				myweather.description="empty";
			}
			
			//Location
			NodeList locnode=weatherdoc.getElementsByTagName("yweather:location");
			if(locnode!=null && locnode.getLength()>0){
				Node location=locnode.item(0);
				NamedNodeMap locmap=location.getAttributes();
				
				myweather.city=locmap.getNamedItem("city").getNodeValue().toString();
				myweather.region=locmap.getNamedItem("region").getNodeValue().toString();
				myweather.country=locmap.getNamedItem("country").getNodeValue().toString();
			}else{
				myweather.city="empty";
				myweather.region="empty";
				myweather.country="empty";
			}
			
			//Wind speed
			NodeList windnode=weatherdoc.getElementsByTagName("yweather:wind");
			if(windnode!=null && windnode.getLength()>0){
				Node wind=windnode.item(0);
				NamedNodeMap windmap=wind.getAttributes();
				
				myweather.windchill=windmap.getNamedItem("chill").getNodeValue().toString() + " °F";
				myweather.winddirection=windmap.getNamedItem("direction").getNodeValue().toString();
				myweather.windspeed=windmap.getNamedItem("speed").getNodeValue().toString() + " mph";
			}else{
				myweather.windchill="empty";
				myweather.winddirection="empty";
				myweather.windspeed="empty";
			}
			
			//Sunrise and sunset
			NodeList sundetnode=weatherdoc.getElementsByTagName("yweather:astronomy");
			if(sundetnode!=null && sundetnode.getLength()>0){
				Node sun=sundetnode.item(0);
				NamedNodeMap sunmap=sun.getAttributes();
				
				myweather.sunrise=sunmap.getNamedItem("sunrise").getNodeValue().toString();
				myweather.sunset=sunmap.getNamedItem("sunset").getNodeValue().toString();
			}else{
				myweather.sunrise="empty";
				myweather.sunset="empty";
			}
			
			//Weather condition
			
			NodeList conditionnode=weatherdoc.getElementsByTagName("yweather:condition");
			if(conditionnode!=null && conditionnode.getLength()>0){
				Node condition=conditionnode.item(0);
				NamedNodeMap conmap=condition.getAttributes();
				
				myweather.conditiontext=conmap.getNamedItem("text").getNodeName().toString();
				myweather.conditiondate=conmap.getNamedItem("date").getNodeName().toString();
				myweather.conditiontemp=conmap.getNamedItem("temp").getNodeName().toString();
			}else{
				myweather.conditiontext="empty";
				myweather.conditiondate="empty";
				myweather.conditiontemp="empty";
			}
			return myweather;
		}
		
	}
	
}
