package com.example.tabwithicon.mainpage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.example.tabwithicon.GlobalVariable;
import com.example.tabwithicon.R;
import com.example.tabwithicon.utility.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainPage implements OnConnectionFailedListener, LocationListener, ConnectionCallbacks,
								OnMapClickListener, OnMapLongClickListener, OnMarkerClickListener{
	
	private static final String TAG = "letsBasket.MainPage";
	
	private String NLocation;
	private static SupportMapFragment mMapFragment;
	ArrayList<HashMap<String, String>> mbCourtList = new ArrayList<HashMap<String, String>>();
	boolean markerClicked;
	private int absPosition = 0;
	//************下拉更新選單*************
	private PullToRefreshListView mPullRefreshListView;
	private static ArrayList<Marker> mMarkList = new ArrayList<Marker>(); 
	
	ListView MPlistView = null;
	private static Activity tmpActivity = null;
	static GoogleMap mMap = null;
	
	
	public View createMainPage(LayoutInflater inflater, final FragmentActivity mActivity){
		tmpActivity = mActivity;
		//tmpActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		View mixedView = inflater.inflate(R.layout.main_page, null);
		mPullRefreshListView = (PullToRefreshListView) mixedView.findViewById(R.id.ListView1);
		MPlistView = mPullRefreshListView.getRefreshableView();
		// 下拉選單時更新球場資訊
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				//刷新球場資訊
				AsyncMyTask task= new AsyncMyTask(mActivity, mPullRefreshListView, mMap, NLocation);
				task.execute(2);
			}
		});
		//get the fragment object
		mMapFragment = ((SupportMapFragment) mActivity.getSupportFragmentManager().findFragmentById(R.id.map));
		//google map
		//get the layout information(screen size) in dp
		int xDPI = (int)Utilities.getScreenDpiX(mActivity);
		int yDPI = (int)Utilities.getScreenDpiY(mActivity);
		
		//get the layout setting object
		ViewGroup.LayoutParams LayOutparams = mMapFragment.getView().getLayoutParams();
		//convert screen size from dp to pixel
		yDPI = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, yDPI , mActivity.getResources().getDisplayMetrics());
		xDPI = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, xDPI , mActivity.getResources().getDisplayMetrics());
		//setting the screen size in pixels
		LayOutparams.height = yDPI/3;
		LayOutparams.width = xDPI;
		mMapFragment.getView().setLayoutParams(LayOutparams);
		mMap = mMapFragment.getMap();
		mMap.setOnMapClickListener(this);
		mMap.setOnMarkerClickListener(this);
		markerClicked = false;
		
		LocationManager locationManager = (LocationManager)tmpActivity.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria=new Criteria();
		String provider =locationManager.getBestProvider(criteria, true);
		Location location =locationManager.getLastKnownLocation(provider);
		Log.i(TAG, "LastKnownLocation = " + location);
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
		//初始化,從server取得球場資訊
		AsyncMyTask task= new AsyncMyTask(mActivity, mPullRefreshListView, mMap, NLocation);
		task.execute(1);

		MPlistView.setClickable(true);
		/*
		 * 選擇清單上球場時,地圖會跟著移到該地點的處理
		 * */
		MPlistView.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3){
				
				mbCourtList = ((GlobalVariable) tmpActivity.getApplication()).getBCourtList();
				absPosition = position - 1;
				String SelectName = mbCourtList.get(absPosition).get(Utilities.getCourtName());
				String SelectLat = mbCourtList.get(absPosition).get(Utilities.getLAT());
				String SelectLng = mbCourtList.get(absPosition).get(Utilities.getLNG());
				Location CurrentLocation = new Location(SelectName);
				CurrentLocation.setLatitude(Double.parseDouble(SelectLat));
				CurrentLocation.setLongitude(Double.parseDouble(SelectLng));
				
				onLocationChanged(CurrentLocation);
			}
		});
		return mixedView;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {}
	@Override
	public boolean onMarkerClick(Marker arg0) {return false;}
	@Override
	public void onMapLongClick(LatLng arg0) {}
	@Override
	public void onMapClick(LatLng arg0) {}
	@Override
	public void onConnected(Bundle arg0) {}
	@Override
	public void onDisconnected() {}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
		CameraPosition cameraPos = new CameraPosition.Builder()
		.target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(14.0f)
		.bearing(0).build();
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
		
		mMarkList = ((GlobalVariable) tmpActivity.getApplication()).getMarkList();
		Marker marker = (Marker) mMarkList.get(absPosition);
		if(marker != null){
			marker.showInfoWindow();
		}
		
	}
}
