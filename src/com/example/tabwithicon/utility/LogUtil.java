package com.example.tabwithicon.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class LogUtil {
	private static String TAG ="letsBasket.LogOutput";

	private static String filePath = null;					// 出力ファイルパス
	private static String logFileName = "letsBasket";		// ログファイル名
	private static int logFileSize = 1024;					// ログファイルサイズ
	private static int logFileRotationCount = 0;			// ログローテーション数
	
	private static long logSize = 0;							// n-1時点のファイルサイズ
	private static Activity mActivity;
	
	public LogUtil(Activity a){
		mActivity = a;
	}
	
	static public void LogOutput(){
		setFilePath("");
		File logpath = new File(getFilePath());
		try{
			if(!logpath.exists()){
	            //create new folder if there are null
	            logpath.mkdirs();
	            Log.i(TAG, "setLogCat() create logpath!");
	        }
			/*
			String cmd2 = "logcat -v time -r " + logFileSize +
            " -n " + logFileRotationCount +
            " -f " + filePath + "/" + logFileName + ".log";
            */
			String cmd2 = "logcat -v time" + 
		            " -f " + filePath + "/" + logFileName + ".log";
	        Process proc2 = Runtime.getRuntime().exec(cmd2);
	        //save the pid of logcat
            Log.i(TAG, "setLogCat() mproc = " + proc2.toString());
		}catch(Exception e){
			Log.e(TAG, "ERROR in initializing Logcat" + e.toString());
		}
	}
	
	static public String getFilePath(){
		return filePath;
	}
	static public void setFilePath(String newFilePath){
        String filepathTemp = Environment.getExternalStorageDirectory().getAbsolutePath();
        filepathTemp = filepathTemp + "/log/"+ newFilePath;
        Log.i(TAG, "setLogCat() filepath = " + filepathTemp);

		filePath = filepathTemp;
	}

	public static void LogCopy() {
		Timer timer = new Timer(false);
		timer.schedule(new TimerTask(){
			public void run(){
				// log rotation per 30 seconds
				Log.i(TAG, "LogCopy S!");

				File mfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/log/letsbasket.log");
				long size = mfile.length();
				Log.i(TAG, "old logfile size : " + LogUtil.getLogSize());
				
				if(size != LogUtil.getLogSize()){
					LogUtil.setLogSize(size);
					Log.i(TAG, "new logfile size : " + size);
					Log.e(TAG, "copy file for rotating");
					try {
		                FileInputStream in = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/log/letsbasket.log");
		                FileOutputStream out = new FileOutputStream(
		                	Environment.getExternalStorageDirectory().getAbsolutePath() + 
		                	"/log/logrotate/letsbasket.log");
		                
		                byte[] buf = new byte[1024];
		                int len;
		                while ((len = in.read(buf)) > 0) {
		                    out.write(buf, 0, len);
		                }
		                in.close();
		                out.close();
		                
	                } catch (FileNotFoundException e) {
		                e.printStackTrace();
		                Log.e(TAG,"FileNotFoundException");
	                } catch (IOException e) {
		                e.printStackTrace();
		                Log.e(TAG,"IOException");
	                }
				}
			}
		}, 15000, 2000);
	    
    }

	
	// rotation practice
	public static void Logrotation() {
		Timer timer = new Timer(false);
		timer.schedule(new TimerTask(){
			public void run(){
				Log.i(TAG, "log rotate S!");
				File mfile = new File(
						Environment.getExternalStorageDirectory().getAbsolutePath() + 
						"/log/logrotate/letsbasket.log");
				long size = mfile.length();
				
				if(size > 1024*1000){
					// rotate
					try {
						FileInputStream in = new FileInputStream(mfile);
		                
		                byte[] buf = new byte[1024];
		                int len;
		                int i = 0;
		                while ((len = in.read(buf)) > 0 && i<2) {
			                FileOutputStream out = new FileOutputStream(
				                	Environment.getExternalStorageDirectory().getAbsolutePath() + 
				                	"/log/logrotate/letsbasket.log." + i);
		                    out.write(buf, 0, len);
		                    out.close();
		                    i++;
		                }
		                in.close();
	                    
                    } catch (FileNotFoundException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
                    } catch (IOException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
                    }
					
					
				}else{
					
				}
				// get all file name in this folder
				
				// get total size of this folder
				
				// if size over the total rotation size
				// start the rotation process
				
				// monitoring /log/logrotate folder 
				
				
				
			}
		}, 30000, 5000);
    }
	
	public static long getLogSize() {
		return logSize;
	}

	public static void setLogSize(long logSize) {
		LogUtil.logSize = logSize;
	}
}

/*
            // test
            InputStream is = proc2.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null){
                sb.append(line);
                sb.append("\n");
            }
            */
