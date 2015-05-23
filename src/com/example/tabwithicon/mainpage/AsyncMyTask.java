package com.example.tabwithicon.mainpage;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.ParseException;
import com.example.tabwithicon.GlobalVariable;
import com.example.tabwithicon.utility.JasonParser;
import com.example.tabwithicon.utility.Utilities;
import com.google.android.gms.maps.GoogleMap;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;


public class AsyncMyTask extends AsyncTask<Integer, Integer, Integer> {
	// 第一個關係到doInBackgroud的傳入變數,第二個,第三個關係到onPostExecute的傳入變數&doInBackground的回傳值
	private static final String URL = "http://192.168.11.4/shop/fetchentry/";
	//private static final String URL = "http://192.168.42.42/shop/fetchentry/";
	//private static String URL = "http://192.168.11.3:8080/letsBasket/jsp/bktCourtInfoBL.do";
	//private static String URL = "http://192.168.42.56:8080/letsBasket/jsp/bktCourtInfoBL.do";

	private static final String TAG = "letsBasket.AsyncMyTask";
	private static final String CLASS_NAME = "mainpage";
	
	
	private MainPageListAdapter list_adapter = null;
	private PullToRefreshListView mPullRefreshListView;		//下拉選單,下拉更新時會用到
	private ProgressDialog progressDialog;					//與server連結處理時顯示進度的視窗
	private static Activity tmpActivity;					//avtivity物件,儲存許多資訊
	private static GoogleMap mMap;							//google map object,添加marker時會用到
	private String location;								//開啟app當下的地點,要傳到server端以取得該地區的球場資訊
	//
	StringBuilder strAddress = null;
	ListView MPListview = null;
	static ArrayList<HashMap<String, String>> mbCourtList = new ArrayList<HashMap<String, String>>();
	MainPageListAdapter MPListAdapter = null;
	/**
	 * Constructor
	 */
	public AsyncMyTask(Activity mActivity, PullToRefreshListView pullToRefreshListView
			, GoogleMap m, String NLocation) {
		super();
		this.mPullRefreshListView = pullToRefreshListView;
		AsyncMyTask.tmpActivity = mActivity;
		AsyncMyTask.mMap = m;
		this.location = NLocation;
	}

	@Override
	protected void onPreExecute(){
		Log.i(TAG, "onPreExecute S");
		super.onPreExecute();
		mbCourtList = ((GlobalVariable)tmpActivity.getApplication()).getBCourtList();
		if(mbCourtList.size() == 0){
			progressDialog = new ProgressDialog(tmpActivity);
			progressDialog.setCancelable(true);
			progressDialog.setMessage("Loading...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setProgress(0);
			progressDialog.show();
		}
	}
	/**
	 * background process
	 */
	@Override
	protected Integer doInBackground(Integer... value) {
		Log.i(TAG, "doInBackground S");
		try {
			
			// code when using php server
			// Http-Post method
			String resp = Utilities.postData(tmpActivity, location, URL, CLASS_NAME);
			if(resp != null){
				JasonParser JParser = new JasonParser(tmpActivity.getAssets(), tmpActivity);
				mbCourtList = JParser.InfoListparser(resp);
				((GlobalVariable)tmpActivity.getApplication()).setBCheckInList(mbCourtList);
			}
			/*
			//Http-Post method
			String resp = Utilities.makeRequest(tmpActivity, location, URL);
			if(resp != null){
				XMLParser OParser = new XMLParser(tmpActivity.getAssets(), tmpActivity);
				mbCourtList = OParser.Geoparser(resp);
				//為了給googlemap上marker而儲存為全域變數
				((GlobalVariable)tmpActivity.getApplication()).setBCourtList(mbCourtList);	
			}*/
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(value[0] == 1){
			return 1;
		}else{
			return 2;
		}
	}
	/**
	 * finish background process and update the data to UI thread
	 */
	@Override
	protected void onPostExecute(Integer result) {
		Log.i(TAG, "onPostExecute S");
		MPListview = mPullRefreshListView.getRefreshableView();
		if(result == 1){
			list_adapter = new MainPageListAdapter(tmpActivity, mbCourtList, mMap);
			MPListview.setAdapter(list_adapter);
			MainPageListAdapter.refreshGooMap();
			
			if(progressDialog != null){
				progressDialog.dismiss();
			}
		}else{
			list_adapter= new MainPageListAdapter(tmpActivity, mbCourtList, mMap);
			MPListview.setAdapter(list_adapter);
			//notify the system that the listview was changed
			list_adapter.notifyDataSetChanged();
			// カスタムリストビューに完了を伝える
			mPullRefreshListView.onRefreshComplete();
			MainPageListAdapter.refreshGooMap();
			super.onPostExecute(result);
		}
		

	}
}
