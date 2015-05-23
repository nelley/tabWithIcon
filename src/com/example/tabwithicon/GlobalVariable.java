package com.example.tabwithicon;

import java.util.ArrayList;
import java.util.HashMap;




import com.example.tabwithicon.mainpage.MainPageListAdapter;
import com.google.android.gms.maps.model.Marker;

import android.app.Application;

public class GlobalVariable extends Application{
	private int pageLocation = 0;
	private ArrayList<Marker> markList = new ArrayList<Marker>(); 
	ArrayList<HashMap<String, String>> bCourtList = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> bCheckInList = new ArrayList<HashMap<String, String>>();
	
	private MainPageListAdapter globList_Adapter = null;
	/**
	 * SERVER端傳來的即時球場資訊
	 */
	public ArrayList<HashMap<String, String>> getBCourtList(){
		return bCourtList;
	}
	public void setBCourtList(ArrayList<HashMap<String, String>> bCourtList){
		this.bCourtList = bCourtList;
	}

	/**
	 * 頁數定位用
	 * @return
	 */
	public int getpageLocation(){
		return pageLocation;
	}
	public void setPageLocation(int pageLocation){
		this.pageLocation = pageLocation;
	}
	
	/**
	 * googlemap上的marker位置陣列
	 * @return
	 */
	public ArrayList<Marker> getMarkList(){
		return markList;
	}
	public void setMarkList(ArrayList<Marker> markList){
		this.markList = markList;
	}
	public void resetMarkList(){
		this.markList = new ArrayList<Marker>();
	}
	/**
	 * checkin地點清單設置
	 */
	public ArrayList<HashMap<String, String>> getBCheckInList(){
		return bCheckInList;
	}
	public void setBCheckInList(ArrayList<HashMap<String, String>> bCheckInList){
		this.bCheckInList = bCheckInList;
	}
	public void resetCheckInList(){
		this.bCheckInList = new ArrayList<HashMap<String, String>>();
	}
	/**
	 * mainpage layout資訊,在初始化時存入,刷新時取出clear
	 */
	public MainPageListAdapter getGlobList_Adapter(){
		return globList_Adapter;
	}
	public void setGlobList_Adapter(MainPageListAdapter globList_Adapter){
		this.globList_Adapter = globList_Adapter;
	}
}
