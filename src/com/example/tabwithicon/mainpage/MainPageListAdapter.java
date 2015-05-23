package com.example.tabwithicon.mainpage;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.tabwithicon.GlobalVariable;
import com.example.tabwithicon.R;
import com.example.tabwithicon.utility.ImageLoader;
import com.example.tabwithicon.utility.Utilities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainPageListAdapter extends BaseAdapter{
	private static ArrayList<Marker> mMarkList = new ArrayList<Marker>(); 
	static GoogleMap mMap = null;
	
	private static Activity activity;
	private static ArrayList<HashMap<String, String>> data;
	private LayoutInflater mInflater;
	public ImageLoader imageLoader; 
	
	public MainPageListAdapter(Activity a, ArrayList<HashMap<String, String>> d, GoogleMap m) {
		super();
		MainPageListAdapter.activity = a;
		MainPageListAdapter.data = d;
		MainPageListAdapter.mMap = m;
		imageLoader=new ImageLoader(activity.getApplicationContext());
		this.mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return data.size();
	}
	@Override
	public Object getItem(int position) {
		return position;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	/**
	 * set the content in each listview in the MainPage
	 * 
	 * */
	public View getView(final int position, View convertView, ViewGroup parent) {
			
		if(convertView == null) {
			//行のレイアウトを取得（TextViewをImageButtonのレイアウト）
			convertView = this.mInflater.inflate(R.layout.list_item, null);
		}
		TextView title = (TextView)convertView.findViewById(R.id.title); 					// title
		TextView artist = (TextView)convertView.findViewById(R.id.artist); 					// artist name
		TextView duration = (TextView)convertView.findViewById(R.id.duration); 				// duration
		ImageView thumb_image = (ImageView)convertView.findViewById(R.id.list_image); 		// thumb image
		
		HashMap<String, String> bBasketCourtList = new HashMap<String, String>(); 
		bBasketCourtList = data.get(position);
		
		// Setting all values in listview
		title.setText(bBasketCourtList.get("court_name"));
		artist.setText(bBasketCourtList.get("cur_people"));
		//String a = bBasketCourtList.get("photo");
		imageLoader.DisplayImage(bBasketCourtList.get("photo"), thumb_image);
		//duration.setText(bBasketCourtList.get(Utilities.getID()));

		return convertView;
	}
	/**
	 * put marker in googlemap method
	 */
	static public void refreshGooMap() {
		//reset markers on the google map
		((GlobalVariable)activity.getApplication()).resetMarkList();
		mMap.clear();
		//settings for GoogleMap, first get the bktCourt array
		//ArrayList<HashMap<String, String>> bktCourtList = ((GlobalVariable) activity.getApplication()).getBCourtList();
		mMarkList = ((GlobalVariable) activity.getApplication()).getMarkList();
		if(data.size() != 0){
			for(int i=0; i< data.size(); i++){
				//get the value of LAT,LNG,TITLE
				Double lat = Double.parseDouble(data.get(i).get(Utilities.getLAT()));
				Double lng = Double.parseDouble(data.get(i).get(Utilities.getLNG()));
				String MrkTitle = data.get(i).get(Utilities.getNAME());
				//put markers into googlemap
				Marker mark1 = mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(MrkTitle).icon(BitmapDescriptorFactory.fromResource(R.drawable.basketicon)));
				//save the information in each other for clicking event
				mMarkList.add(mark1);
			}
			((GlobalVariable) activity.getApplication()).setMarkList(mMarkList);
			//設定初始畫面的location
			
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
					new LatLng(Double.parseDouble(data.get(0).get(Utilities.getLAT())),
							Double.parseDouble(data.get(0).get(Utilities.getLNG()))) , 12));
		}
	}
	public void clear() {
		data.clear();
	}
	
	
	
	
	
}
