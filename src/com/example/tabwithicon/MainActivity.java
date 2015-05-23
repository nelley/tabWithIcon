package com.example.tabwithicon;

import java.io.IOException;

import com.example.tabwithicon.utility.LogUtil;
import com.example.tabwithicon.utility.MultiDevInit;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	
    private static final String TAG = "letsBasket.MainActivity";
	
    private static final String[] CONTENT = new String[] {"打卡", "記錄板", "比賽數據", "人氣鬥牛場"};//"人氣鬥牛場", "打卡",
    private static final int[] ICONS = new int[] {
        R.drawable.perm_group_calendar,
        R.drawable.perm_group_camera,
        R.drawable.perm_group_device_alarms,
        R.drawable.perm_group_location,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.simple_tabs);
        Log.i(TAG, "onCreate");
        LogUtil.LogOutput();
        // test
        LogUtil.LogCopy();
        
        LogUtil.Logrotation();
        // test
        
        //initilize the layout param for multi screen
        new MultiDevInit(this.getApplicationContext());
        
        CookieSyncManager.createInstance(this);
        FragmentPagerAdapter adapter = new BasketBallAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        
        Log.i(TAG, "set pager");
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);
        //set the initial page
        Log.i(TAG, "set indicator");
        customTab indicator = (customTab)findViewById(R.id.indicator);
        indicator.getLayoutParams().height = MultiDevInit.IndicatorH;
        indicator.setViewPager(pager, 0);
        //TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        //indicator.getLayoutParams().height = MultiDevInit.IndicatorH;//set the height of indicator
        //indicator.setViewPager(pager, 0);
    }

    /**
     * Called when the activity will start interacting with the user.
     * */
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }
    /**
     * Called when the activity is no longer visible to the user.
     * */
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }
    /**
     * Called after your activity has been stopped, prior to it being started again.
     * */
    protected void onRestart(){
        Log.i(TAG, "onRestart");
        super.onRestart();
    }
    /**
     * Called when the activity is becoming visible to the user.
     * */
    protected void OnStart(){
        Log.i(TAG, "onStart");
        super.onStart();
    }
	
    /**
     * Called when the activity is becoming visible to the user.
     * */
    protected void OnDestroy(){
        Log.i(TAG, "onDestroy");
        super.onStart();
    }
    
    /**
     * 管理所有從intent返回的activity
     * CheckInPage裡,經startActivityForResult()啟動的activity結束後的回傳值
     * CheckInActivity裡可設定resultCode
     * CheckInPage裡可設定requestCode
     * CheckInActivity可設定data
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bundle = data.getExtras();
        switch (requestCode) {
            //CheckInActivity
            case 999:
                if (resultCode == Activity.RESULT_OK) {
                	
                }else{
                	
                }
                break;
                //SummaryActivity
            case 998:
                if(resultCode == Activity.RESULT_OK){
                	
                }else{
                	
                }
                break;
            default:
                break;
        }
    }
    // BACKボタンが押された時の処理
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            // アラートダイアログ
            showDialog(0);
            return true;
        }
        return false;
    }
    @Override
    public Dialog onCreateDialog(int id) {
        switch (id) {
        case 0:
            //ダイアログの作成(AlertDialog.Builder)
            return new AlertDialog.Builder(MainActivity.this)
            .setMessage("要關閉球經救星嗎?")
            .setCancelable(false)
            // 「終了する」が押された時の処理
            .setPositiveButton("結束", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // アクティビティ消去
                    MainActivity.this.finish();
                }
            })
            // 「終了しない」が押された時の処理
            .setNegativeButton("繼續使用", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            })
            .create();
        }
        return null;
    }

    class BasketBallAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        public BasketBallAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.i(TAG, "getItem");
            return BasketFragment.newInstance(CONTENT[position % CONTENT.length], MainActivity.this);
        }
        @Override
        //取得每個tab上的字
        public CharSequence getPageTitle(int position) {
            Log.i(TAG, "getPageTitle");
            return CONTENT[position % CONTENT.length].toUpperCase();
        }
        //取得每個tab上的icon
        @Override 
        public int getIconResId(int index) {
            Log.i(TAG, "getIconResId");
            return ICONS[index];
        }

        @Override
        public int getCount() {
            Log.i(TAG, "getCount");
            return CONTENT.length;
        }
    }
}