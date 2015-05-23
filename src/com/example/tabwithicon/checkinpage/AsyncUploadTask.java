package com.example.tabwithicon.checkinpage;

import org.apache.http.ParseException;
import org.json.JSONObject;

import com.example.tabwithicon.utility.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * for checkin page's location info upload 
 * */
public class AsyncUploadTask extends AsyncTask<String, String, String>{
	
	//private static final String URL = "http://192.168.42.56:8080/letsBasket/jsp/bktCheckinBL.do";
	//private static final String URL = "http://192.168.11.3:8080/letsBasket/jsp/bktCheckinBL.do";
	private static final String URL = "http://192.168.11.4/shop/uploadentry";
	//private static final String URL = "http://192.168.42.42/shop/uploadentry";
	private static final String CLASS_NAME = "location_info";
	
	private static final String TAG = "letsBasket.AsyncUploadTask";
	private static final String msg = "資料上傳中...";
	
	private ProgressDialog progressDialog;				//與server連結處理時顯示進度的視窗
	public Activity tmpActivity;
	public Intent tmpI;
	public JSONObject mJSONO;
	
	public AsyncUploadTask(Activity a, Intent i, JSONObject mJ)
	{
		this.tmpActivity = a;
		this.tmpI = i;
		this.mJSONO = mJ;
	}
	@Override
	protected void onPreExecute(){
		Log.i(TAG, "onPreExecute");
		super.onPreExecute();
		progressDialog = new ProgressDialog(tmpActivity);
		progressDialog.setCancelable(true);
		progressDialog.setMessage(msg);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setProgress(0);
		progressDialog.show();
	}
	/**
	 * @params 從execute()傳進來的值
	 * upload the checkin message to server
	 * */
	@Override
	protected String doInBackground(String... params) {
		String resp = "";
		try {
			resp = Utilities.postData(mJSONO, URL, CLASS_NAME);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return resp;
	}
	/**
	 * finish background process and update the data to UI thread
	 * 這裡需要修改!!!!!!!
	 */
	@Override
	protected void onPostExecute(String result) {
		Log.i(TAG, "onPostExecute");
		Bundle bundle = new Bundle();
		bundle.putString("key.StringData", "test");
		bundle.putInt("key.intData", 123456789);
		
		if(result == "1"){//upload successed
			Log.i(TAG, "upload successed");
			//set the return value
			tmpI.putExtras(bundle);
			tmpActivity.setResult(tmpActivity.RESULT_OK, tmpI);
			tmpActivity.finish();
		}else{//upload failed
			Log.i(TAG, "upload failed");
			new AlertDialog.Builder(tmpActivity)
				.setTitle("上傳失敗")
				.setMessage("伺服器忙碌中,請稍後再試")
				.setPositiveButton("了解了", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 
					// continue with retry
					}
				})
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
		}
		progressDialog.dismiss();
	}
}
