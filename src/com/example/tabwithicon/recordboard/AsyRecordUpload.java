package com.example.tabwithicon.recordboard;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tabwithicon.utility.Utilities;

/**
 * class for upload team record
 * */
public class AsyRecordUpload extends AsyncTask<String, Integer, Integer> 
	implements DialogInterface.OnCancelListener{

    private static final String TAG = "letsBasket.AsyRecordUpload";
    
    private static final String URL = "http://192.168.11.4/shop/fetchentry/";
    //private static final String URL = "http://192.168.42.42/shop/fetchentry";
    
    private static final String lineEnd = "\r\n";
    
    
    private ProgressDialog progressDialog;					//與server連結處理時顯示進度的視窗
    private static Activity RecordActivity;					//avtivity物件,儲存許多資訊
    private File mFile;
    
    
	/**
	 * Constructor
	 */
	public AsyRecordUpload(Activity mActivity) {
		super();
		AsyRecordUpload.RecordActivity = mActivity;
		//test
		mFile = new File(Environment.getExternalStorageDirectory() + "/log/helpbuy.txt");
	}
	
    @Override
	protected void onPreExecute(){
		Log.i(TAG, "onPreExecute S");
		super.onPreExecute();
		progressDialog = new ProgressDialog(RecordActivity);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage("Uploading...");
		progressDialog.setCancelable(true);
		progressDialog.setMax((int) mFile.length());
		progressDialog.show();
	}
    
    
    @Override
    protected Integer doInBackground(String... params) {
    	
    	String lineEnd = "\r\n";
    	String response = null;
    	
    	HttpURLConnection connection = null;
    	HttpURLConnection.setFollowRedirects(false);
        String fileName = mFile.getName();
        
        String boundary = "--boundary";
        String tail = lineEnd + "--" + boundary + "--" + lineEnd;
        
        try {
            // Setup the request
            connection = (HttpURLConnection) new URL(URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(10000);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Connection", "close");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Charset", "UTF-8");
            
            String metadataPart1 = "--" + boundary + lineEnd 
                + "Content-Disposition: form-data; name=\"metadata\"" + lineEnd + lineEnd
                + "aaa" + lineEnd;

            String metadataPart2 =  "--" + boundary + lineEnd 
                    + "Content-Disposition: form-data; name=\"meta\"" + lineEnd + lineEnd
                    + "bbb" + lineEnd;
            
            String fileHeader1 = "--" + boundary + lineEnd
                + "Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                + fileName + "\"" + lineEnd
                + "Content-Type: application/octet-stream" + lineEnd
                + "Content-Transfer-Encoding: binary" + lineEnd;

            long fileLength = mFile.length() + tail.length();
            String fileHeader2 = "Content-length: " + fileLength + lineEnd;
            String fileHeader = fileHeader1 + fileHeader2 + lineEnd;
            String stringData = metadataPart1 + metadataPart2 + fileHeader;

            //long requestLength = stringData.length() + fileLength;
            //connection.setRequestProperty("Content-length", "" + requestLength);
            //connection.setFixedLengthStreamingMode((int) requestLength);
            connection.connect();
            
            // Start content wrapper:
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(stringData);
            out.flush();

            int progress = 0;
            int bytesRead = 0;
            byte buf[] = new byte[1024];
            BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(mFile));
            while ((bytesRead = bufInput.read(buf)) != -1) {
              // write output
              out.write(buf, 0, bytesRead);
              out.flush();
              progress += bytesRead;
              // update progress bar
              publishProgress(progress);
            }

             // Write closing boundary and close stream
            out.writeBytes(tail);
            out.flush();
            out.close();
            
            // Get response
            
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            	InputStream responseStream = new BufferedInputStream(connection.getInputStream());
            	BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
            	
                String line = "";
                StringBuilder builder = new StringBuilder();
                while((line = responseStreamReader.readLine()) != null) {
                  builder.append(line);
                }
                responseStreamReader.close();
                response = builder.toString();
            }
            
          } catch (Exception e) {
            e.printStackTrace();
            // Exception
          } finally {
            if (connection != null) {
                connection.disconnect();
            }
          }
        return null;
    }
    


	@Override
    protected void onProgressUpdate(Integer... progress) {
      progressDialog.setProgress((int) (progress[0]));
    }
    
    
    protected void onPostExecute(Integer result) {
    	super.onPostExecute(result);
    	progressDialog.dismiss();
    	
    }
	@Override
    public void onCancel(DialogInterface dialog) {
	    // TODO Auto-generated method stub
	    
    }
	/*
	    String response = null;
    	
    	HttpURLConnection connection = null;
    	HttpURLConnection.setFollowRedirects(false);
        String fileName = mFile.getName();
        
        String boundary = "---------------------------boundary";
        String tail = "\r\n--" + boundary + "--\r\n";
        
        
        try {
            // Setup the request
            connection = (HttpURLConnection) new URL(URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(10000);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            //connection.setDoOutput(true);

            connection.setRequestProperty("Connection", "close");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Charset", "UTF-8");
            
            String metadataPart = "--" + boundary + "\r\n" 
                + "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
                + "" + "\r\n";

            String fileHeader1 = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                + fileName + "\"\r\n"
                + "Content-Type: application/octet-stream\r\n"
                + "Content-Transfer-Encoding: binary\r\n";

            long fileLength = mFile.length() + tail.length();
            String fileHeader2 = "Content-length: " + fileLength + "\r\n";
            String fileHeader = fileHeader1 + fileHeader2 + "\r\n";
            String stringData = metadataPart + fileHeader;

            //long requestLength = stringData.length() + fileLength;
            //connection.setRequestProperty("Content-length", "" + requestLength);
            //connection.setFixedLengthStreamingMode((int) requestLength);
            connection.connect();
            
            // Start content wrapper:
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(stringData);
            out.flush();

            int progress = 0;
            int bytesRead = 0;
            byte buf[] = new byte[1024];
            BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(mFile));
            while ((bytesRead = bufInput.read(buf)) != -1) {
              // write output
              out.write(buf, 0, bytesRead);
              out.flush();
              progress += bytesRead;
              // update progress bar
              publishProgress(progress);
            }

             // Write closing boundary and close stream
            out.writeBytes(tail);
            out.flush();
            out.close();
            
            // Get response
            
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            	InputStream responseStream = new BufferedInputStream(connection.getInputStream());
            	BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
            	
                String line = "";
                StringBuilder builder = new StringBuilder();
                while((line = responseStreamReader.readLine()) != null) {
                  builder.append(line);
                }
                responseStreamReader.close();
                response = builder.toString();
            }
            
          } catch (Exception e) {
            e.printStackTrace();
            // Exception
          } finally {
            if (connection != null) {
                connection.disconnect();
            }
          }
          
        //Utilities.postData(null, null, URL, null);
	 * 
	 * */
}
