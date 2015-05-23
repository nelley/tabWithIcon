package com.example.tabwithicon.recordboard;

import java.util.HashMap;

import com.example.tabwithicon.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class summaryActivity extends Activity implements OnClickListener{

    private static final String TAG = "letsBasket.summaryActivity";
    Intent i = null;
    private final int H = ViewGroup.LayoutParams.WRAP_CONTENT; 
    private final int W = ViewGroup.LayoutParams.MATCH_PARENT; 
    HashMap<String, playerObj> mPlayerMap = new HashMap<String, playerObj>(); 
    public String touchedName = null;

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.summary_layout);
        
        //get all player's record
        mPlayerMap = playerObj.playerMap;
        int cnt = 0;
        for(String key : mPlayerMap.keySet()){
            if(key != "rival"){
                TableLayout tl = (TableLayout) findViewById(R.id.summarytable);
                TableRow tr = new TableRow(this);
                playerObj tmpObj= mPlayerMap.get(key);
                
                //add the player's number and name
                for(int i=0; i<tmpObj.recordsArray.length; i++){
                    TextView text = new TextView(this);
                    text.setText(tmpObj.recordsArray[i]);
                    text.setGravity(Gravity.CENTER_HORIZONTAL);
                    if((i%2)==0){
                        text.setBackgroundColor(Color.BLUE);
                    }else{
                        text.setBackgroundColor(Color.DKGRAY);
                    }
                    // set number/name/total score unclickable
                    if(i != 15){
                        text.setOnClickListener(this);
                        text.setId(cnt++);
                    }
                    tr.addView(text);
                    tr.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                tl.addView(tr, createParam(W, H));
            }
            
        }
    }
    
    private TableLayout.LayoutParams createParam(int w, int h){ 
        return new TableLayout.LayoutParams(w, h);
    }
    //BACKボタンが押された時の処理
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            i = getIntent();
            summaryActivity.this.setResult(Activity.RESULT_OK, i);
            summaryActivity.this.finish();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        //get the touched item's value
        final int clicked_item = v.getId(); //get id for clicked TableRow 
        final TextView tr = (TextView)v.findViewById(clicked_item);
        final int act = clicked_item % 16;
        String record = tr.getText().toString();

        ViewGroup tbView = (ViewGroup)v.getParent();
        TextView playerPos = (TextView)tbView.findViewById(clicked_item - act);//get tablerow's view
        touchedName = (String) playerPos.getText();//get the player's number
        final TextView tr_total_score = (TextView)tbView.findViewById(clicked_item - act + 15);//total_score's view

        String content = getContent(act);
        //if the "number" & "name" been touched
        
        //create number and name input dialog
        
        //input check(duplication check)
        
        
        
        //the other been touched

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.modified, null);

        builder.setView(view);
        TextView title = new TextView(this);
        title.setText(content);
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder.setCustomTitle(title);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //alertDialog.getWindow().setLayout(600, 400);

        final TextView record_adjusted = (TextView)view.findViewById(R.id.edit_text);
        Button btn_plus = (Button)view.findViewById(R.id.btn_plus);
        Button btn_minus = (Button)view.findViewById(R.id.btn_minus);
        Button btn_ok = (Button)view.findViewById(R.id.btn_ok);

        record_adjusted.setText(record);

        //mDialog.show();
        alertDialog.setOnDismissListener(new OnDismissListener(){
            @Override
            public void onDismiss(DialogInterface dialog) {
            	
            }
        });
        
        btn_plus.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                int newValue = Integer.valueOf(record_adjusted.getText().toString());
                newValue = newValue + 1;
                record_adjusted.setText(String.valueOf(newValue));
            }
        });
        btn_minus.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                int newValue = Integer.valueOf(record_adjusted.getText().toString());
                newValue = newValue - 1;
                record_adjusted.setText(String.valueOf(newValue));
            }
        });
        btn_ok.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                tr.setText(record_adjusted.getText());//update new value to view
                //update new value to mPlayerMap
                playerObj tmp = playerObj.playerMap.get(touchedName);
                tmp.recordsArray[act] = record_adjusted.getText().toString();
                if(act == actionDef.ACT_TWOP_MA || 
                   act == actionDef.ACT_THREEP_MA || 
                   act == actionDef.ACT_FTMA){
                    //increment both shoot and made
                    int total_score = Integer.valueOf(tmp.recordsArray[2])*2+
                                      Integer.valueOf(tmp.recordsArray[4])*3+
                                      Integer.valueOf(tmp.recordsArray[6]);
                    tmp.recordsArray[15] = String.valueOf(total_score);
                    tr_total_score.setText(tmp.recordsArray[15]);
                }
                alertDialog.dismiss();
            }
        });
    }

    private String getContent(int contentId) {
        String content = null;
        switch(contentId){
        case 0:
            content = "背號"; break;
        case 1:
            content = "名稱"; break;	
        case 2:
            content = "二分命中"; break;
        case 3:
            content = "二分出手"; break;
        case 4:
            content = "三分命中"; break;
        case 5:
            content = "三分出手"; break;
        case 6:
            content = "罰球命中"; break;
        case 7:
            content = "罰球出手"; break;
        case 8:
            content = "防守籃板"; break;
        case 9:
            content = "進攻籃板"; break;
        case 10:
            content = "助攻"; break;
        case 11:
            content = "火鍋"; break;
        case 12:
            content = "抄截"; break;
        case 13:
            content = "失誤"; break;
        case 14:
            content = "犯規"; break;
        case 15:
            content = "得分"; break;
        default:
            content = "???"; break;
        }
        return content;
    }
}
