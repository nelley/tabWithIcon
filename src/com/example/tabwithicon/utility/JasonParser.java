package com.example.tabwithicon.utility;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetManager;
import android.support.v4.app.Fragment;
import android.util.Log;

@SuppressLint("ValidFragment")
public class JasonParser extends Fragment{
	private static final String TAG = "letsBasket.JasonParser";
	
	Activity mActivity = null;
	ArrayList<HashMap<String, String>> bktList = new ArrayList<HashMap<String, String>>();
	AssetManager massetmanager;
	
	/**
	 * constructor
	 * */
	@SuppressLint("ValidFragment")
    public JasonParser(AssetManager a, Activity b){
        massetmanager = a;
        mActivity = b;
    }
	
	/**
	 * parser for main page
	 * */
    public ArrayList<HashMap<String, String>> InfoListparser(String bktInfoList){
        bktList.clear();
        try {
            JSONArray jsonarray = new JSONArray(bktInfoList);
            for (int i=0; i < jsonarray.length(); i++){
                HashMap<String, String> map= new HashMap<String, String>();
                JSONObject oneObject = jsonarray.getJSONObject(i);
                // Pulling items from the array
                map.put("courtname", oneObject.getString("COURTNAME"));
                map.put("address", oneObject.getString("ADDRESS"));
                map.put("lat", oneObject.getString("LAT"));
                map.put("lng", oneObject.getString("LNG"));
                map.put("cur_people", oneObject.getString("CUR_PEOPLE"));
                bktList.add(map);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException");
            e.printStackTrace();
        }
        return bktList;
    }
    
    /**
     * parser for check in page
     * */
    public ArrayList<HashMap<String, String>> CheckInListparser(String CheckInList) {
        bktList.clear();
        try {
            JSONArray jsonarray = new JSONArray(CheckInList);
            for (int i=0; i < jsonarray.length(); i++){
                HashMap<String, String> map= new HashMap<String, String>();
                JSONObject oneObject = jsonarray.getJSONObject(i);
                // Pulling items from the array
                map.put("courtname", oneObject.getString("COURTNAME"));
                map.put("address", oneObject.getString("ADDRESS"));
                map.put("distance", oneObject.getString("DISTANCE"));
                map.put("lat", oneObject.getString("LAT"));
                map.put("lng", oneObject.getString("LNG"));
                map.put("idx", oneObject.getString("IDX"));
                bktList.add(map);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException");
            e.printStackTrace();
        }
        return bktList;
    }
}
