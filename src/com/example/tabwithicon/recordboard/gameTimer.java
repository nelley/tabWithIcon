package com.example.tabwithicon.recordboard;

import com.example.tabwithicon.utility.timer;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

public class gameTimer extends timer{

    Context mContext;
    TextView timerView;
    public static gameTimer gtInstance = null;
    
    
    public static gameTimer getInstance(Context c, TextView t, long millisInFuture, long countDownInterval){
        if(gtInstance == null){
            gtInstance = new gameTimer(c, t, millisInFuture, countDownInterval);
        }
        return gtInstance;
    }
    public gameTimer(Context c, TextView t, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval, false);
        mContext = c;
        timerView = t;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        // インターバル(countDownInterval)毎に呼ばれる
        timerView.setText(Long.toString(millisUntilFinished/1000/60) + ":" + 
                          Long.toString(millisUntilFinished/1000%60) + ":" + 
                          Long.toString(millisUntilFinished/100%10));
    }

    @Override
    public void onFinish() {
        timerView.setText("00:00:0");
        Toast.makeText(mContext, "比賽結束!!!", Toast.LENGTH_SHORT).show();
    }

    public void update(long millisInFuture, long countDownInterval){
        gtInstance.cancel();
        gtInstance = new gameTimer(mContext, timerView, millisInFuture, countDownInterval);
    }
}
