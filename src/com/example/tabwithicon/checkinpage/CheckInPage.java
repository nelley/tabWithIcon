package com.example.tabwithicon.checkinpage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.tabwithicon.GlobalVariable;
import com.example.tabwithicon.R;
import com.example.tabwithicon.utility.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CheckInPage implements OnConnectionFailedListener, LocationListener, 
								ConnectionCallbacks,OnMapClickListener, OnMapLongClickListener, 
								OnMarkerClickListener, OnMarkerDragListener, TextWatcher{
	
	private static final String TAG = "letsBasket.CheckinPage";
	
	boolean changed = false;
	private String NLocation;
	private CheckInPageListAdapter list_adapter = null;
	private SupportMapFragment mMapFragment;
	private GoogleMap mMap = null;
	//have to change
	static final String REALTIME_INFO = "output.xml";
	
	ArrayList<HashMap<String, String>> mCheckInList = new ArrayList<HashMap<String, String>>();
	Activity tmpActivity = null;
	Button NewCourtBtn = null;
	Button ReportBtn = null;
	
	public View createCheckinPage(LayoutInflater inflater, final FragmentActivity mActivity){
		tmpActivity = mActivity;
		//tmpActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		final View mixedView = inflater.inflate(R.layout.checkin_page, null);
		final ListView listViewCKI = (ListView) mixedView.findViewById(R.id.ListView1);
		
		EditText SearchEditText = (EditText) mixedView.findViewById(R.id.CheckInEditTextR);
		NewCourtBtn = (Button) mixedView.findViewById(R.id.CheckInBtnC);
		ReportBtn = (Button) mixedView.findViewById(R.id.CheckInBtnL);
		ReportBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//v是imagebutton
				changed = (changed == true)? false:true;
				//call getView() to redraw the layout
				list_adapter.notifyDataSetChanged();
				/*
				ViewGroup PView = (ViewGroup) v.getParent();							//get parent view from imagebutton
				int currentV = PView.indexOfChild(PView.findViewById(R.id.ListView1));	//get position number of listview1 from parent view
				View ChildView = PView.getChildAt(currentV);							//get view of the listview1
				PView.removeView(ChildView);											//remove it!!
				PView.addView(v,currentV);												//add new view
				*/
			}
		});
		//initialize the setting for buttons
		NewCourtBtn.setClickable(false);
		//set the hint in the editText
		SearchEditText.setHint("搜尋最近的球場");
		SearchEditText.addTextChangedListener(this);
		//set listener
		
		
		//when checkin candidate is selected
		listViewCKI.setClickable(true);
		listViewCKI.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> adapter, View v,int position, long id){
				Intent intent = new Intent(tmpActivity, CheckInActivity.class);
				// requestCode : startActivityForResult の第二引数で指定した値が渡される
				int requestCode = 999;
				intent.putExtra("absPosition", String.valueOf(position));
				tmpActivity.startActivityForResult(intent, requestCode);
			}
		});

		//get the fragment object
	    mMapFragment = ((SupportMapFragment) mActivity.getSupportFragmentManager().findFragmentById(R.id.map1));
	    mMap = mMapFragment.getMap();
	    
	    /*顯示現在地點的按鈕,按了可自動轉到現在地點
		if (mMap != null) {
			mMap.setMyLocationEnabled(true);
		}*/
		//google map:get the layout information(screen size) in dp
	    int xDPI = (int)Utilities.getScreenDpiX(mActivity);
		int yDPI = (int)Utilities.getScreenDpiY(mActivity);
		
	    //get the layout setting object in google map fragment
	    ViewGroup.LayoutParams LayOutparams = mMapFragment.getView().getLayoutParams();
	    //convert screen size from dp to pixel
	    yDPI = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, yDPI , mActivity.getResources().getDisplayMetrics());
	    xDPI = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, xDPI , mActivity.getResources().getDisplayMetrics());
	    //setting the screen size in pixels
	    LayOutparams.height = yDPI/3;
	    LayOutparams.width = xDPI;
	    mMapFragment.getView().setLayoutParams(LayOutparams);
	    //obtain current location
	    
	    LocationManager locationManager = (LocationManager)mActivity.getSystemService(Context.LOCATION_SERVICE);
	    Criteria criteria=new Criteria();
	    String provider =locationManager.getBestProvider(criteria, true);
	    Location location=locationManager.getLastKnownLocation(provider);
	    if(location != null){
	    	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12));
	    }
	    mMap.addMarker( new MarkerOptions()
	        .position( new LatLng(location.getLatitude(), location.getLongitude()) )
	        .title("球場位置")
	        .draggable(true)
	        .snippet("長按可拖移地標")).showInfoWindow();
	    
	    /*
	    Geocoder geocoder= new Geocoder(tmpActivity, Locale.JAPAN);
	    try {
			List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
			if(addresses != null) {
                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();
                for(int i=0; i<=fetchedAddress.getMaxAddressLineIndex(); i++) {
                      strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
                }
                Toast.makeText(tmpActivity, strAddress.toString(), Toast.LENGTH_SHORT).show();
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    */
	    Geocoder geocoder =new Geocoder(tmpActivity, Locale.TAIWAN);
		//===code test ===從經緯度轉換成地址 ,for告訴server要丟回哪個區的球場資訊
		try {
			//在這裡替換lat,lng.台北文山區24.987685,121.567123, location.getLatitude(), location.getLongitude()
			List<Address> addresses = geocoder.getFromLocation(24.987685,121.567123, 1);
			if(addresses != null) {
				Address fetchedAddress = addresses.get(0);
				//只取區,捨棄其他資訊
				NLocation = fetchedAddress.getLocality();
				Log.i(TAG, "location = " + NLocation);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//===code test end===
		AsyncCheckinTask task= new AsyncCheckinTask(mActivity, listViewCKI, NLocation);
		task.execute(1);

		mMap.setOnMapClickListener(this);
		mMap.setOnMarkerClickListener(this);
		mMap.setOnMapLongClickListener(this);
		mMap.setOnMarkerDragListener(this);

		return mixedView;
	}
	//interface of OnConnectionFailedListener, LocationListener, ConnectionCallbacks,OnMapClickListener, OnMapLongClickListener, OnMarkerClickListener
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}	
	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12));
		
		
		
	}

	//interface of TextWatcher
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		Toast.makeText(tmpActivity, "text changed", Toast.LENGTH_SHORT).show();
		//send a query the server
		//update the listview 
		//if the court not existed in the DB
		if(s.length() > 3){
			//Toast.makeText(tmpActivity, "bigger than 3 char", Toast.LENGTH_SHORT).show();
			NewCourtBtn.setClickable(true);
			NewCourtBtn.setTextColor(Color.RED);
			//NewCourtBtn.setImageResource(R.drawable.basketicon1);

		}else{
			//if the court found in the DB, focus the target row
			//Toast.makeText(tmpActivity, "smaller than 3 char", Toast.LENGTH_SHORT).show();
			NewCourtBtn.setClickable(false);
			NewCourtBtn.setTextColor(Color.BLACK);
			//NewCourtBtn.setImageResource(R.drawable.basketicon);
		}
	}
	//OnMarkerDragListener
	@Override
	public void onMarkerDrag(Marker position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMarkerDragEnd(Marker position) {
		// TODO Auto-generated method stub
		LatLng dragPosition = position.getPosition();
        double dragLat = dragPosition.latitude;
        double dragLong = dragPosition.longitude;
        Toast.makeText(tmpActivity, "Marker Dragged..!", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onMarkerDragStart(Marker position) {
		// TODO Auto-generated method stub

	}
	
}