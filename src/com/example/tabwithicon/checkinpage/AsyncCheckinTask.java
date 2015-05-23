package com.example.tabwithicon.checkinpage;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.ParseException;

import com.example.tabwithicon.GlobalVariable;
import com.example.tabwithicon.recordboard.AsyRecordUpload;
import com.example.tabwithicon.utility.JasonParser;
import com.example.tabwithicon.utility.Utilities;
import com.example.tabwithicon.utility.XMLParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

public class AsyncCheckinTask extends AsyncTask<Integer, Integer, Integer> {
	
	private static final String TAG = "letsBasket.AsyncCheckinTask";
	private static final String CLASS_NAME = "checkinpage";
	
	//private static final String URL = "http://192.168.42.56:8080/letsBasket/jsp/bktInfoListBL.do";
	private static final String URL = "http://192.168.11.4/shop/fetchentry/";
	//private static final String URL = "http://192.168.42.42/shop/fetchentry/";
	//private static final String URL = "http://192.168.11.3:8080/letsBasket/jsp/bktInfoListBL.do";
	ArrayList<HashMap<String, String>> checkinlist = new ArrayList<HashMap<String, String>>();
	private CheckInPageListAdapter list_adapter = null;
	ListView listView1 = null;
	private ProgressDialog progressDialog;				//與server連結處理時顯示進度的視窗
	private static Activity CIActivity;					//avtivity物件,儲存許多資訊
	private String location;							//開啟app當下的地點,要傳到server端以取得該地區的球場資訊
	
	/**
	 * Constructor
	 */
	public AsyncCheckinTask(Activity mActivity, ListView mlistview, String NLocation) {
		super();
		AsyncCheckinTask.CIActivity = mActivity;
		this.listView1 = mlistview;
		this.location = NLocation;
	}
	@Override
	protected void onPreExecute(){
		Log.i(TAG, "onPreExecute");
		super.onPreExecute();
		progressDialog = new ProgressDialog(CIActivity);
		progressDialog.setCancelable(true);
		progressDialog.setMessage("Loading...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setProgress(0);
		progressDialog.show();
	}
	/**
	 * @params 從execute()傳進來的變數
	 * @return 為onPostExecute的引數
	 * */
	@Override
	protected Integer doInBackground(Integer... params) {
		Log.i(TAG, "doInBackground");
		//obtain data from server
		try {
			// code when using php server
			//Http-Post method
			String resp = Utilities.postData(CIActivity, location, URL, CLASS_NAME);
			if(resp != null){
				JasonParser JParser = new JasonParser(CIActivity.getAssets(), CIActivity);
				checkinlist = JParser.CheckInListparser(resp);
				((GlobalVariable)CIActivity.getApplication()).setBCheckInList(checkinlist);
			}
			
			/* code when using servlet server
			//Http-Post method
			String resp = Utilities.makeRequest(CIActivity, location, URL);
			if(resp != null){
				XMLParser OParser = new XMLParser(CIActivity.getAssets(), CIActivity);
				checkinlist = OParser.InfoListparser(resp);
				//為了給選擇checkin地點而儲存為全域變數
				((GlobalVariable)CIActivity.getApplication()).setBCheckInList(checkinlist);
			}
			*/
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//data from server
		return 1;
	}
	/**
	 * @result doInBackground的return
	 * finish background process and update the data to UI thread
	 */
	@Override
	protected void onPostExecute(Integer result) {
		Log.i(TAG, "onPostExecute");
        list_adapter = new CheckInPageListAdapter(CIActivity, checkinlist);
        //list_adapter.notifyDataSetChanged();
        listView1.setAdapter(list_adapter);
        
        listView1.setClickable(true);
		progressDialog.dismiss();
	}
	

}
