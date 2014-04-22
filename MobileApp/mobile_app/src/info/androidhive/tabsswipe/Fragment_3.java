package info.androidhive.tabsswipe;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import info.androidhive.tabsswipe.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class Fragment_3 extends Fragment {

	private static ImageView imageview;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_3, container, false);
		imageview=(ImageView)rootView.findViewById(R.id.imgview);
		return rootView;
	}
	
	public void changeImage(Bitmap bmp){
		imageview.setImageBitmap(bmp);
	}
	
	public class DownloadImage extends AsyncTask<String,Void,Bitmap>{
		
		private Bitmap bmp;
		MainActivity ma;
		
		DownloadImage(MainActivity ma){
			this.ma=ma;
		}
		
		
		//Call AsyncTask to create a background thread which is different from the UI thread
		
		public Bitmap getBitmapfromUrl(String inputurl) {
			try{
				URL url=new URL(inputurl);
				HttpURLConnection conn=(HttpURLConnection)url.openConnection();
				conn.setDoInput(true);
				conn.connect();
				int code=conn.getResponseCode();
				Log.d("HTTP response","The code returned is " + code);
				InputStream input=conn.getInputStream();
				Bitmap img=BitmapFactory.decodeStream(input);
				return img;
			}catch(IOException e){
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected Bitmap doInBackground(String... inputurl) {
			Log.d("RECEIVED URL",inputurl[0]);
			bmp=getBitmapfromUrl(inputurl[0]);
			return bmp;
		}
		
		@Override
		protected void onPostExecute(Bitmap result){
			ma.displayImage(result);
		}

	}

}
