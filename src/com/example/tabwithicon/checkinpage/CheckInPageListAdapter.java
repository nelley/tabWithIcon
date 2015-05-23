package com.example.tabwithicon.checkinpage;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.tabwithicon.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CheckInPageListAdapter extends BaseAdapter{
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private LayoutInflater mInflater;
    //for report correction
    boolean changed = false;
    
    public CheckInPageListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        super();
        activity = a;
        data = d;
        this.mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    	if(changed == false){
            // layout of initialization
            convertView = this.mInflater.inflate(R.layout.list_item_chkin, null);
            
            TextView title = (TextView)convertView.findViewById(R.id.title); 		// courtname
            TextView artist = (TextView)convertView.findViewById(R.id.artist); 		// distance
            TextView duration = (TextView)convertView.findViewById(R.id.duration); 	// address
            
            HashMap<String, String> bBasketCourtList = new HashMap<String, String>(); 
            bBasketCourtList = data.get(position);
            title.setText(bBasketCourtList.get("courtname"));
            artist.setText(bBasketCourtList.get("distance"));
            duration.setText(bBasketCourtList.get("address"));
    	}
    	else{
            // layout of report correction
            convertView = this.mInflater.inflate(R.layout.list_item_chkin2, null);
            
            TextView title = (TextView)convertView.findViewById(R.id.title); // title
            TextView artist = (TextView)convertView.findViewById(R.id.artist); // artist name
            
            HashMap<String, String> bBasketCourtList = new HashMap<String, String>(); 
            bBasketCourtList = data.get(position);
            title.setText(bBasketCourtList.get("courtname"));
            artist.setText(bBasketCourtList.get("address"));
    	}
		
        return convertView;
    }
	/*
	 * 如果getCount為零的話getView()就不會被呼叫,畫面也就不會被刷新!!
	 * */
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

}
