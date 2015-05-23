package com.example.tabwithicon.checkinpage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import com.example.tabwithicon.GlobalVariable;
import com.example.tabwithicon.R;
import com.example.tabwithicon.utility.Utilities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class CheckInActivity extends Activity{
	private static final String TAG = "letsBasket.CheckInActivity";
	ArrayList<HashMap<String, String>> mCheckInList = new ArrayList<HashMap<String, String>>();
	Intent i = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "CheckInActivity S");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.checkin_activity);
		//listviewObject infObj = (listviewObject)getIntent().getSerializableExtra("infObj");

		//TextView tv = (TextView) findViewById(R.id.courtName);
		//tv.setText(courtName);
		
		EditText editText = (EditText) findViewById(R.id.input);
		editText.setText("請輸入");

		//upload button
		Button btn = (Button)findViewById(R.id.CKIbutton);
		//upload logic
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				i = getIntent();
				Integer position = Integer.valueOf(i.getStringExtra("absPosition"));
				mCheckInList = ((GlobalVariable) getApplication()).getBCheckInList();
				
				JSONObject jsonObject = new JSONObject();
				try{
					jsonObject.put("idx", mCheckInList.get(position).get("idx"));//必要か？？？
					jsonObject.put("id", "anonymous");
					jsonObject.put("courtname", URLEncoder.encode(mCheckInList.get(position).get("courtname"), "UTF-8"));
					jsonObject.put("address", URLEncoder.encode(mCheckInList.get(position).get("address"),"UTF-8"));
					jsonObject.put("lat", mCheckInList.get(position).get("lat"));
					jsonObject.put("lng", mCheckInList.get(position).get("lng"));
					jsonObject.put("checkin_time", Utilities.getSystemTime());
					jsonObject.put("msg", URLEncoder.encode("單挑阿","UTF-8"));
					//jsonObject.put("valid_flag", "true");
					
					AsyncUploadTask upload = new AsyncUploadTask((Activity)v.getContext(), i, jsonObject);
					upload.execute();
				}catch(UnsupportedEncodingException e){
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	// BACKボタンが押された時の処理
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			i = getIntent();
			CheckInActivity.this.setResult(Activity.RESULT_OK, i);
			CheckInActivity.this.finish();
			return true;
		}
		return false;
	}
}
