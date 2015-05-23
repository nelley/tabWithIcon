package com.example.tabwithicon.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

public class Utilities {
	
	private static final String TAG = "letsBasket.Utilities";
	// コネクションタイマアウト時間10秒（ms単位）
	public static final int HTTP_CONNECTION_TIMEOUT = 10000;
	// 通信読み込みタイマアウト設定時間10秒（ms単位）
	public static final int HTTP_DATAREAD_TIMEOUT = 10000;
	
	/************ 処理結果定義 ******************************************************/
	// 0:成功
	// 1:通信エラー
	// 2:プロセスエラー
	// 3:処理中断エラー
	// 4:送信データ作成エラー
	// 5:送信回答エラー
	public static final int UPLOAD_RET_SUCCESS = 0;
	public static final int UPLOAD_RET_COMMUNICATIONERROR = 1;
	public static final int UPLOAD_RET_PROCESS = 2;
	public static final int UPLOAD_RET_CANCELLED = 3;
	public static final int UPLOAD_RET_DATAERROR = 4;
	public static final int UPLOAD_RET_RESPONSEERROR = 5;

	private static final String KEY_COURTNAME = "court_name";
	private static final String KEY_ID = "id";
	private static final String KEY_LAT = "lat";
	private static final String KEY_LNG = "lng";
	private static final String KEY_NAME = "name";
	private static final String KEY_TYPE = "type";
	//Check-in page address	
	static public String getCourtName(){
		return KEY_COURTNAME;
	}
	static public String getID(){
		return KEY_ID;
	}
	static public String getLAT(){
		return KEY_LAT;
	}
	static public String getLNG(){
		return KEY_LNG;
	}
	static public String getNAME(){
		return KEY_NAME;
	}
	static public String getTYPE(){
		return KEY_TYPE;
	}
	//int SCREEN_DPI  = metrics.densityDpi;
	static public double getScreenDpiX(Context mcontext){
		//get the screen size
		DisplayMetrics metrics = mcontext.getResources().getDisplayMetrics();
		int scale = (int)(metrics.density);
		return (metrics.widthPixels/scale);
	}
	static public double getScreenDpiY(Context mcontext){
		DisplayMetrics metrics = mcontext.getResources().getDisplayMetrics();
		int scale = (int)(metrics.density);
		return (metrics.heightPixels/scale);
	}

	
    /**
     * HTTP-Post
     * send the address to the server for obtaining the nearest bktcourt information 
     */
	/*
	static public String makeRequest(Activity UActivity, String addr, String URL) {
		String responseBody = null;
		HttpClient httpClient = new HttpClient();
		CookieManager cookieManager = CookieManager.getInstance();
		try{
			//String userAgent = System.getProperty("http.agent");
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(HTTP_CONNECTION_TIMEOUT);
			httpClient.getHttpConnectionManager().getParams().setSoTimeout(HTTP_DATAREAD_TIMEOUT);
			
			PostMethod pm = null;
			int responseCode;
			// 端末IMEI
			StringPart part_imei = new StringPart("imei", getIMEI(UActivity));
			// 端末のMACアドレス
			StringPart part_macAddress = new StringPart("macAddress", getMacIPAddress(UActivity));
			// 送信位置
			StringPart part_addr = new StringPart("addr", URLEncoder.encode(addr, "UTF-8"));
			// 送信日時
			StringPart part_sendDate = new StringPart("sendDate", getSystemTime());
			
			// HTTP POSTクラスの生成
			pm = new PostMethod(URL);
			Part[] parts = {part_imei,
							part_macAddress,
							part_addr,
							part_sendDate};
			MultipartRequestEntity reqEntity = new MultipartRequestEntity(parts, pm.getParams());
			// HTTP要素の設定
			pm.setRequestEntity(reqEntity);
			//Header設定的方法是hashMap,所以server端也要用相對應的key取出
			pm.setRequestHeader("User-Agent", System.getProperty("http.agent"));
			//設定session,取出sessionid傳至server進行檢測
			String JSessionID = cookieManager.getCookie(URL);
			if(JSessionID != null){
				//set sessionid to header
				pm.setRequestHeader("Cookie", JSessionID);
			}
			// HTTP POST送信
			responseCode = httpClient.executeMethod(pm);
			
			if(responseCode == 200) {
			// 正常
			//--------------------------------
			// レスポンスの取得と設定
			//--------------------------------
				//header strings split
				Header[] cookie = pm.getResponseHeaders("Set-Cookie");
				int L = cookie.length;
				if(cookie.length != 0){
					String[] cookietmp = cookie[cookie.length - 1].toString().split(";");
					String[] session = cookietmp[0].split(":");
					//設定session,取出sessionid傳至server進行檢測
					cookieManager.setCookie(URL, session[1]);
				}
				responseBody = pm.getResponseBodyAsString();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return responseBody;
	}
	*/
	/**
	 * @checkinObject 包含球場名,經緯度等要上傳伺服器的資訊
	 * 
	 * */
	/*
	static public Integer makeRequest(Part[] checkinObject, String URL){
		int retValue = -1;
		HttpClient httpClient = new HttpClient();
		try{
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(HTTP_CONNECTION_TIMEOUT);
			httpClient.getHttpConnectionManager().getParams().setSoTimeout(HTTP_DATAREAD_TIMEOUT);
			
			// HTTP POSTクラスの生成
			PostMethod pm = null;
			pm = new PostMethod(URL);
			Log.i(TAG, "connectServerForsendData() parts = " + checkinObject.toString());
			MultipartRequestEntity reqEntity = new MultipartRequestEntity(checkinObject, pm.getParams());
			// HTTP要素の設定
			pm.setRequestEntity(reqEntity);
			// HTTP POST送信
			int responseCode = httpClient.executeMethod(pm);
			if(responseCode == 200) {
				Log.i(TAG, "connect to server successed");
				//--------------------------------
				// レスポンスの取得と設定
				//--------------------------------
				Header[] headers = pm.getResponseHeaders();
				for (int i = 0; i < headers.length; i++) {
					Log.i(TAG, "### headers[" + i + "] = " + headers[i]);
				}
				String responseBody = pm.getResponseBodyAsString();
				retValue = getResponseResult(responseBody);
			} else {
				Log.e(TAG, "responseCode = " + responseCode);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return retValue;
	}
	*/
	/**
	 * post request to php server
	 * */
    static public String postData(Activity UActivity, String addr, String URL, String className) {
        // Create a new HttpClient and Post Header
        JSONObject json = new JSONObject();
        StringBuilder sb = new StringBuilder();
        String line = null;
        
        try {
            // create JSON data to send to server
            json.put("className", className);
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,3000);
            HttpConnectionParams.setSoTimeout(httpParams, 3000);
            
            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost request = new HttpPost(URL);
            request.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
            request.setHeader("json", json.toString());
            HttpResponse response = client.execute(request);

            if(response != null){
                InputStream is = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //retrieve the jason data from response
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }catch (ClientProtocolException e) {
            Log.e(TAG, "ClientProtocolException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IOException");
            e.printStackTrace();
        } catch (JSONException e) {
        	Log.e(TAG, "JSONException");
            e.printStackTrace();
        }
        //response body
        return sb.toString();
    }
    /**
     * post request to php server(checkin activity)
     * */
    static public String postData(JSONObject mJSONO, String URL, String className) {
        // Create a new HttpClient and Post Header
        StringBuilder sb = new StringBuilder();
        String line = null;
        
        try {
            // create JSON data to send to server
            mJSONO.put("className", className);
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,3000);
            HttpConnectionParams.setSoTimeout(httpParams, 3000);
            
            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost request = new HttpPost(URL);
            request.setEntity(new ByteArrayEntity(mJSONO.toString().getBytes("UTF8")));
            request.setHeader("json", mJSONO.toString());
            HttpResponse response = client.execute(request);

            if(response != null){
                InputStream is = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //retrieve the jason data from response
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }catch (ClientProtocolException e) {
            Log.e(TAG, "ClientProtocolException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IOException");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception");
            e.printStackTrace();
        }
        //response body
        return sb.toString();
    }
	/**
	 * IMEI取得
	 * */
	public static String getIMEI(Activity tUActivity) {
		String imei = "";
		
		TelephonyManager telephonyManager = (TelephonyManager)tUActivity.getSystemService(Context.TELEPHONY_SERVICE);
		imei = telephonyManager.getDeviceId();
		if((imei == null) || (imei.length() == 0)) {
			return imei = "";
		}
		Log.i(TAG, "getIMEI() IMEI = " + imei);
		return imei;
	}
	/**
	 * MACアドレス取得
	 * */
	public static String getMacIPAddress(Activity tUActivity) {
		String macIP = "";
	
		WifiManager wifiManager = (WifiManager) tUActivity.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		macIP = wifiInfo.getMacAddress();
		if((macIP == null) || (macIP.length() == 0)) {
			return macIP = "";
		}
		Log.i(TAG, "getMacIPAddress() macIP = " + macIP);
		
		return macIP;
	}
	/**
	 * "yyyyMMddhhmmss"のフォーマットで現在時間を取得
	 * */
	public static String getSystemTime() {
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		Log.i(TAG, "getSystemTime() systime = " + str);
	
		return str;
	}
	/**
	 * サーバーレスポンスから応答コードを取得し、通信処理結果コードを返す
	 */
	private static int getResponseResult(String response) {
		Log.i(TAG, "getResponseResult()");
		int retValue = UPLOAD_RET_SUCCESS;
		String replacement = "";
		String regex = "\r\n|<.+?>";
	try{
		int len = 0;
		if(response != null) {
			len = response.length();
			response = response.replaceAll(regex, replacement);
		}
		if(len > 0) {
			JSONObject jsonData = new JSONObject(response);
			int resultResponse = jsonData.getInt("resultStatus");
			Log.i(TAG, "getResponseResult() Response = " + resultResponse);
			if(resultResponse == 0) {
				return UPLOAD_RET_SUCCESS;
			} else {
				return UPLOAD_RET_RESPONSEERROR;
			}
		} else {
			return UPLOAD_RET_COMMUNICATIONERROR;
		}
	}catch (JSONException e) {
		Log.e(TAG, "getResponseResult() JSONException happened!" + e.toString());
		retValue = UPLOAD_RET_COMMUNICATIONERROR;
	}catch(Exception e) {
		Log.e(TAG, "getResponseResult() Exception happened!" + e.toString());
		retValue = UPLOAD_RET_PROCESS;
	}
		return retValue;
	}

}
