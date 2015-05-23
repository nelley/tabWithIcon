package com.example.tabwithicon.recordboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tabwithicon.R;
import com.example.tabwithicon.utility.AnimatorPath;
import com.example.tabwithicon.utility.MultiDevInit;
import com.example.tabwithicon.utility.PathEvaluator;
import com.example.tabwithicon.utility.PathPoint;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RecordBoardPage {

    private static final String TAG = "letsBasket.RecordBoardPage";
    //definition of control flags
    final int DEFAULT_ACTION = 999;
    final int TOUCH_OK = 1;
    final int DISTANCE = 40;
    final int RIVAL_ACTION = 17;
    final int DEFAULT_X = 0;
    final int DEFAULT_Y = 0;
    Activity mActivity = null;

    final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    final String[] qString = new String[]{"上半場", "下半場", "第一節", "第二節", "第三節", "第四節"};
    private RelativeLayout.LayoutParams timeTitleParams, timeParams, scoreTitleParams, scoreParams;
    private ImageView oImg ;
    private boolean touch_flg = false;
    //player's panel
    final GridView[] pPanel = new GridView[5];
    //default definition of img
    Integer[] imageSet = {R.drawable.basketicon, R.drawable.basketicon,R.drawable.basketicon};
    //img for icons except player icon
    Integer[] rebound = {R.drawable.r_press, R.drawable.r_d,R.drawable.r_o};
    Integer[] twoPoint = {R.drawable.two_press, R.drawable.in,R.drawable.miss};
    Integer[] threePoint = {R.drawable.three_press, R.drawable.in,R.drawable.miss};
    Integer[] freeThrow = {R.drawable.free_press, R.drawable.in,R.drawable.miss};
    Integer[] to_n_foul = {R.drawable.fail_press, R.drawable.fail_up,R.drawable.fail_down};

    //using in onTouchListener
    private float lastTouchY;
    private float currentY;
    int lastPos = -1;
    //GridView
    GridView gridView;
    GridView p1,p2,p3,p4,p5;
    ImageView bktCourt, benchBtn, summary, rival, undo, mBall, mBallAnim, mBallAna, missIcon, testBtn;
    TextView strTime, strTimeTitle, strScore, strScoreTitle;

    private int actionCode = 999;
    private String name = "player";
    private String ActText = "720度轉身扣籃";
    ArrayList<Item> mGridArray =new ArrayList<Item>();
    private String actTime;
    //for animation
    AnimatorPath path = null;
    private float midX, midY, disX, disY;
    /**
     * constructor
     * */
    public RecordBoardPage(Activity a){
        mActivity = a;
    }

    /**
     * create the view
     * */
    public View createRecordBoardPage(LayoutInflater inflater){
        Log.i(TAG, "createRecordBoardPage S");
        final View mixedView = inflater.inflate(R.layout.gridlayout, null);
        oImg = new ImageView(mActivity);
        teamObj.getInstance(mActivity);

        //init bktcourt, meanBtn, scoreboard's layout size
        initLayoutParams(mixedView);

        //init panel for 5 players
        initPanel(mixedView);

        //init scoreboard & timer
        initScoreBoard(mixedView);

        //init benchlist
        initBenchList(mixedView);

        //init rival's score
        initRival(mixedView);

        //init undo button
        initUndo(mixedView);

        //summary icon
        initSummary(mixedView);
        

        //set drag and drop listener
        initDragnDrop(mixedView);

        Log.i(TAG, "createRecordBoardPage E");
        return mixedView;
    }
    /**
     * init drag and drop listener
     * */
    private void initDragnDrop(View v) {
        for(int i=0; i<pPanel.length; i++){
            pPanel[i].setOnDragListener(new playerDragListener(mActivity));
        }
        v.findViewById(R.id.gridLayoutTop).setOnDragListener(new playerDragListener(mActivity));
        v.findViewById(R.id.timescore).setOnDragListener(new playerDragListener(mActivity));
        v.findViewById(R.id.toplinear).setOnDragListener(new playerDragListener(mActivity));
        v.findViewById(R.id.benchBtn).setOnDragListener(new playerDragListener(mActivity));
        
    }
    /**
     * init summary btn
     * */
    private void initSummary(View v) {
        summary = (ImageView) v.findViewById(R.id.summary);
        summary.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, summaryActivity.class);
                // requestCode : startActivityForResult の第二引数で指定した値が渡される
                int requestCode = 998;
                //intent.putExtra("absPosition", String.valueOf(position));
                mActivity.startActivityForResult(intent, requestCode);
            }
        });
    }
    /**
     * init undo button
     * */
    private void initUndo(View v) {
        undo = (ImageView) v.findViewById(R.id.undo);
        undo.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!teamObj.undoStack.empty()){
                    undo();
                    //php json test
                    AsyRecordUpload jasonUpload = new AsyRecordUpload(mActivity);
                    jasonUpload.execute("aaa","aaaaa","bbbb");
                }
            }
        });
    }
    /**
     * init rival score button's view
     * */
    private void initRival(View v) {
        rival = (ImageView) v.findViewById(R.id.rival);
        rival.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                setScore(0,1);
                playerObj mPlayerObj = playerObj.getInstance(mActivity, RIVAL_ACTION, "rival", "rival", "time", DEFAULT_X, DEFAULT_Y);
                teamObj.addTimeLine(mPlayerObj);
            }
        });
    }
    /**
     * init bench list's view
     * */
    private void initBenchList(View v) {
        // init bench's view
        benchBtn = (ImageView) v.findViewById(R.id.benchBtn);
        // init bench player's gridview
        final GridView benchGridView = gridViewFactory(new GridView(mActivity), R.layout.bench, R.id.benchList);
        benchGridView.setAdapter(teamObj.RecordGVAdapter[5]);

        benchBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                //get the parent's parent view in the original View
                ViewGroup mParentView = (ViewGroup) v.getParent().getParent();
                //check the current have the benchList view or not
                GridView AddedView = (GridView) mActivity.findViewById(R.id.benchList);
                int isExist = mParentView.indexOfChild(AddedView);
                if(isExist == -1){//no benchList view in current view
                    //initialize the benchLayout's param
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(WC, WC);
                    params.leftMargin = MultiDevInit.mLeftMargin;
                    params.topMargin = MultiDevInit.mTopMargin;
                    params.height =  MultiDevInit.GVH;

                    mParentView.addView(benchGridView, params);
                    benchGridView.setOnItemLongClickListener(new myOnItemLongClickListener(mActivity, teamObj.RecordGVAdapter[5]));
                }else{
                    //have benchList view in current view
                    mParentView.removeView(AddedView);
                }
            }
        });

    }

	private void initScoreBoard(View v) {
        strTimeTitle = (TextView) v.findViewById(R.id.timeTitle);
        strTimeTitle.setGravity(Gravity.CENTER);
        strTimeTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, MultiDevInit.titleSize);
        strTimeTitle.setLayoutParams(timeTitleParams);
        
        strTime = (TextView) v.findViewById(R.id.time);
        strTime.setGravity(Gravity.CENTER);
        strTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, MultiDevInit.contextSize);
        strTime.setLayoutParams(timeParams);
        
        gameTimer.getInstance(mActivity, strTime, 180000, 100);
        strTime.setOnLongClickListener(new OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                TextView title = new TextView(mActivity);
                title.setText("請輸入比賽時間");
                title.setBackgroundColor(Color.DKGRAY);
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.WHITE);
                title.setTextSize(20);
                
                final View rootView = LayoutInflater.from(mActivity).inflate(R.layout.time_setting, null);
                final NumberPicker qp = (NumberPicker) rootView.findViewById(R.id.quarter);
                qp.setMaxValue(5);
                qp.setMinValue(0);
                qp.setDisplayedValues(qString);
                final NumberPicker npM = (NumberPicker) rootView.findViewById(R.id.npMinute);
                npM.setMinValue(0);
                npM.setMaxValue(48);
                npM.setValue(10);
                final NumberPicker npS = (NumberPicker) rootView.findViewById(R.id.npSecond);
                npS.setMinValue(0);
                npS.setMaxValue(59);
                npS.setValue(00);
                
                final AlertDialog defBuilder = new AlertDialog.Builder(mActivity)
                .setView(rootView)
                .setCustomTitle(title)
                .setPositiveButton(android.R.string.ok, null) //Set to null. then override the onclick
                .create();
                defBuilder.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = defBuilder.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!(npM.getValue() == 0 && npS.getValue() == 0)){
                                    String quarter = qString[qp.getValue()];//quarter
                                    int min = npM.getValue();
                                    int sec = npS.getValue();
                                    //set text
                                    String tmp = min + ":" + sec;
                                    strTime.setText(tmp +":0");
                                    //set timer
                                    gameTimer.gtInstance.update(min*60*1000 + sec*1000, 100);
                                    gameTimer.gtInstance.create();
                                    defBuilder.dismiss();
                                }else{
                                    Toast.makeText(mActivity, "ok", Toast.LENGTH_SHORT).show();
                                }
                                
                            }
                        });
                    }
                });
                defBuilder.show();
                
                return false;
            }
        });
        strTime.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                String mTimer = strTime.getText().toString();
                if(mTimer.equals("00:00:0")){
                    CustomToast("請長按以設定比賽時間");
                }else{
                    
                    if(gameTimer.gtInstance.isRunning()){
                        gameTimer.gtInstance.pause();
                    }else{
                        gameTimer.gtInstance.resume();
                    }
                }
                
            }
        });
        //init score
        strScoreTitle = (TextView) v.findViewById(R.id.scoreTitle);
        strScoreTitle.setGravity(Gravity.CENTER);
        strScoreTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, MultiDevInit.titleSize);
        strScoreTitle.setLayoutParams(scoreTitleParams);
        
        strScore = (TextView) v.findViewById(R.id.score);
        strScore.setGravity(Gravity.CENTER);
        strScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, MultiDevInit.contextSize);
        strScore.setLayoutParams(scoreParams);
    }

	/**
     * init player's panel
     * */
    private void initPanel(View v) {
        for(int i=0; i<pPanel.length; i++){
            pPanel[i] = (GridView)v.findViewById(teamObj.gridViewID[i]);
            pPanel[i].getLayoutParams().height = MultiDevInit.GVH;
            pPanel[i].getLayoutParams().width = MultiDevInit.GVW;
            pPanel[i].setAdapter(teamObj.RecordGVAdapter[i]);
        }
        //for click
        pPanel[0].setOnTouchListener(new OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                doRecord(v, event, pPanel[0]);//false⇒独自Viewの下にいるViewにTouchEventを渡す。true⇒独自Viewの下にいるViewにTouchEventを渡さない
                return false;
            }
        });
        pPanel[1].setOnTouchListener(new OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                doRecord(v, event, pPanel[1]);
                return false;
            }
        });
        pPanel[2].setOnTouchListener(new OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                doRecord(v, event, pPanel[2]);
                return false;
            }
        });
        pPanel[3].setOnTouchListener(new OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                doRecord(v, event, pPanel[3]);
                return false;
            }
        });
        pPanel[4].setOnTouchListener(new OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                doRecord(v, event, pPanel[4]);
                return false;
            }
        });
        //for long click
        pPanel[0].setOnItemLongClickListener(new OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 4){
                    playerSummary(view);
                }
                return false;
            }
        });
        pPanel[1].setOnItemLongClickListener(new OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 4){
                    playerSummary(view);
                }
                return false;
            }
        });
        pPanel[2].setOnItemLongClickListener(new OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 4){
                    playerSummary(view);
                }
                return false;
            }
        });
        pPanel[3].setOnItemLongClickListener(new OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 4){
                    playerSummary(view);
                }
                return false;
            }
        });
        pPanel[4].setOnItemLongClickListener(new OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 4){
                    playerSummary(view);
                }
                return false;
            }
        });
    }

	/**
     * init layout params for score board
     * */
    private void initLayoutParams(View v) {
        //init layout params
        RelativeLayout scoreBoardRL = (RelativeLayout)v.findViewById(R.id.timescoreLayer);
        LayoutParams SBRLparams = scoreBoardRL.getLayoutParams();
        SBRLparams.height = MultiDevInit.bktCourtH;
        SBRLparams.width = MultiDevInit.xPIXEL - MultiDevInit.bktCourtW;
        
        timeTitleParams = new RelativeLayout.LayoutParams(MultiDevInit.titleW, MultiDevInit.titleH);
        
        timeParams = new RelativeLayout.LayoutParams(MultiDevInit.titleW, ((MultiDevInit.bktCourtH/2) - MultiDevInit.titleH));
        timeParams.addRule(RelativeLayout.BELOW, R.id.timeTitle);//set relative position
        
        scoreTitleParams = new RelativeLayout.LayoutParams(MultiDevInit.titleW, MultiDevInit.titleH);
        scoreTitleParams.addRule(RelativeLayout.BELOW, R.id.time);//set relative position
        
        scoreParams = new RelativeLayout.LayoutParams(MultiDevInit.titleW, ((MultiDevInit.bktCourtH/2) - MultiDevInit.titleH));
        scoreParams.addRule(RelativeLayout.BELOW, R.id.scoreTitle);//set relative position
        
        //init mainBtn
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.toplinear);
        LayoutParams params = layout.getLayoutParams();
        params.height = MultiDevInit.GVH;
        params.width = MultiDevInit.GVW;
        // playcourt's image
        bktCourt = (ImageView) v.findViewById(R.id.bktCourt);
        bktCourt.getLayoutParams().height = MultiDevInit.bktCourtH;
        bktCourt.getLayoutParams().width = MultiDevInit.bktCourtW;
        
        mBall = imageViewFactory(mBall, R.layout.ball, R.id.ballIV);
        
        bktCourt.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                ViewGroup mRelativeBkt = (ViewGroup) v.getParent().getParent();
                ImageView AddedView = (ImageView) mActivity.findViewById(R.id.ballIV);
                int isExist = mRelativeBkt.indexOfChild(AddedView);
                if(isExist != -1){
                	mRelativeBkt.removeView(AddedView);
                }
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(WC, WC);
                
                // set the touched coordination
                params.leftMargin = (int) event.getX()-8;//have to adjust for multi-screen
                params.topMargin = (int) event.getY()-8;//have to adjust for multi-screen
                mRelativeBkt.addView(mBall, params);
                //view added flg
                touch_flg = true;
                
                animFactory((event.getX()-8), (event.getY()-8));
                return false;
            }
            /**
             * set the destination and middle path of the animation
             * */
            private void animFactory(float tx, float ty) {
                //diff xy
                disX = ((MultiDevInit.bktCourtW/2) - tx);
                disY = (MultiDevInit.STATUS_BAR_H - ty);
                //cal for middle diff
                midX = (float) (disX * 0.5);
                midY = (float) (disY * 1.2);
                
                if(midX > 0){//left side
                    midX = midX - 100;
                    midY = midY - 100;
                }else{//right side
                    midX = midX + 100;
                    midY = midY + 100;
                }
            }
        });
    }
    /**
     * 
     * */
    public void doRecord(View v, MotionEvent event, GridView player){
        //identified which image in gridview was touched
        int pos = player.pointToPosition((int) event.getX(), (int) event.getY());
        //get the touched gridview's item
        for(int i=0; i<teamObj.gridViewID.length; i++){
            if(v == v.findViewById(teamObj.gridViewID[i])){
                mGridArray = teamObj.gridArray.get(i);
            }
        }
        final ViewGroup mRelative = (ViewGroup) v.getParent();
        int curView = -9;
        View ChildView = null;
        //no response to player item + ACTION_DOWN/ACTION_MOVE 
        if(!((event.getAction() == 0 || event.getAction() == 2) && pos == 4)){
            switch (event.getAction()){
                //--------touch the screen--------//
                case MotionEvent.ACTION_DOWN://0
                    lastPos = pos;
                    lastTouchY = event.getY();
                    if(pos < 6){
                        //initialize the img by touched position
                        imageSetter(pos);
                        //check whether the img was already been added
                        curView = mRelative.indexOfChild(oImg);
                        if(curView != -1){
                            ChildView = mRelative.getChildAt(curView);
                            mRelative.removeView(ChildView);
                        }
                        //show the default img when touching icon
                        FrameLayout GVItem = (FrameLayout)((ViewGroup) v).getChildAt(pos);
                        if(GVItem != null){
                            mRelative.addView(oImg, ImgIntializer(pos, imageSet[0], GVItem, v));
                        }
                    }
                    break;
                //--------finger moved--------//
                case MotionEvent.ACTION_MOVE://2
                    currentY = event.getY();
                    //identified moving direction
                    if(currentY < lastTouchY){
                        //UP
                        float diff = lastTouchY - currentY;
                        Boolean isValid = (diff > DISTANCE) ? true : false;
                        if(isValid){
                            oImg.setImageResource(imageSet[1]);
                        }
                    }else{
                        //DOWN
                        float diff = currentY - lastTouchY;
                        Boolean isValid = (diff > DISTANCE) ? true : false;
                        if(isValid){
                            oImg.setImageResource(imageSet[2]);
                        }
                    }
                    break;
                    //--------leave the screen--------//
                case MotionEvent.ACTION_UP://1
                    //get the player's number
                    Item playerInfo = mGridArray.get(4);
                    name = (playerInfo.getTitle() == "") ? "沒有人" : playerInfo.getTitle();
                    currentY = event.getY();
                    //if touched icon is block/steal/assist
                    if(lastPos > 5){
                        isMade(lastPos);
                        if(actionCode != DEFAULT_ACTION){
                            actTime = strTime.getText().toString();
                            playerObj tmpPlayer = playerObj.getInstance(mActivity, actionCode, name, name, actTime, DEFAULT_X, DEFAULT_X);
                            tmpPlayer.setSummary(tmpPlayer, 1);
                            teamObj.addTimeLine(tmpPlayer);
                            CustomToast(name, ActText);
                        }else{
                            Toast.makeText(mActivity, "player touched", Toast.LENGTH_SHORT).show();
                        }
                    //if touched icon are others
                    }else{
                        //identified moving direction(MOVING UP)
                        if(currentY < lastTouchY){
                            float diff = lastTouchY - currentY;
                            Boolean isValid = (diff > DISTANCE) ? true : false;
                            if(isValid){
                                //check made or miss, get the player's action
                                isMade(lastPos);
                                if(actionCode != DEFAULT_ACTION){
                                    actTime = strTime.getText().toString();
                                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mBall.getLayoutParams();
                                    playerObj tmpPlayer = playerObj.getInstance(mActivity, actionCode, name, name, actTime, lp.leftMargin, lp.topMargin);
                                    tmpPlayer.setSummary(tmpPlayer, 1);
                                    teamObj.addTimeLine(tmpPlayer);
                                    CustomToast(name, ActText);
                                    //if 2 or 3 point made, show animation
                                    if(actionCode == 2 || actionCode == 4){
                                        // perform anim only in bktcourt be touched
                                        if(touch_flg){
                                            // add imageview for anim
                                            mBallAnim  = imageViewFactory(mBallAnim, R.layout.ball_animation, R.id.ballanim);
                                            animStart(v, mBall, mBallAnim, "mBallAnimLoc", lp.leftMargin, lp.topMargin);
                                        }
                                    }
                                    
                                }else{
                                    Toast.makeText(mActivity, "player touched", Toast.LENGTH_SHORT).show();
                                }
                            }
                        // MOVING DOWN
                        }else{
                            float diff = currentY - lastTouchY;
                            Boolean isValid = (diff > DISTANCE) ? true : false;
                            if(isValid){
                                // check made or miss, get the player's action
                                isMissed(lastPos);
                                if(actionCode != DEFAULT_ACTION){
                                    actTime = strTime.getText().toString();
                                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mBall.getLayoutParams();
                                    playerObj tmpPlayer = playerObj.getInstance(mActivity, actionCode, name, name, actTime, lp.leftMargin, lp.topMargin);
                                    tmpPlayer.setSummary(tmpPlayer, 1);
                                    teamObj.addTimeLine(tmpPlayer);
                                    CustomToast(name, ActText);
                                    // if 2 or 3 point missed, show animation
                                    if(actionCode == 3 || actionCode == 5){
                                        // perform anim only in bktcourt be touched
                                        if(touch_flg){
                                            deleView(mRelative, R.id.ballIV);
                                            //add x icon
                                            missIcon = imageViewFactory(missIcon, R.layout.miss_icon, R.id.missIcon);
                                            //set the location of ballAnim
                                            RelativeLayout.LayoutParams missParam = new RelativeLayout.LayoutParams(WC, WC);
                                            missParam.leftMargin = lp.leftMargin;
                                            missParam.topMargin = lp.topMargin;
                                            mRelative.addView(missIcon, missParam);
                                            //after 0.5 sec, remove it
                                            Handler removeH = new Handler();
                                            removeH.postDelayed(new Runnable(){
                                                public void run(){
                                                    mRelative.removeView(missIcon);
                                                }
                                            }, 500);
                                        }
                                    }
                                }else{
                                    Toast.makeText(mActivity, "player touched", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                    deleView(mRelative, R.id.ballIV);
                    reset(mRelative, ChildView, curView, mBall);
                    break;
                default:
                    deleView(mRelative, R.id.ballIV);
                    reset(mRelative, ChildView, curView, mBall);
            }
        }
    }
    /**
     * reset flag & params when finger ACTION_UP
     * */
    private void reset(ViewGroup parentV, View childV, int cv, ImageView iv) {
        //remove the img when finger untouched
        cv = parentV.indexOfChild(oImg);
        childV = parentV.getChildAt(cv);
        parentV.removeView(childV);
        ActText = "";
        lastPos = -1;
        actionCode = 999;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv.getLayoutParams();
        lp.leftMargin = 0;
        lp.topMargin = 0;
    }

	/**
     * perform animation
     * */
    private void animStart(View v, ImageView mB, ImageView mBAnim,
            String getterStr, int x, int y) {
        final ViewGroup mParentView = (ViewGroup) v.getParent();
        int isExist = mParentView.indexOfChild(mBAnim);
        if(isExist != -1){
            mParentView.removeView(mBAnim);
        }
        //set the location of ballAnim
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(WC, WC);
        params.leftMargin = x;
        params.topMargin = y;
        mParentView.addView(mBAnim, params);
        deleView(mParentView, R.id.ballIV);
        //set path of animation
        path = new AnimatorPath();
        path.moveTo(0, 0);
        path.curveTo(0, 0, midX, midY, disX, disY);
        // Set up the animation
        final ObjectAnimator anim = ObjectAnimator.ofObject(this, getterStr, 
                new PathEvaluator(), path.getPoints().toArray());
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(500);
        anim.start();
        //del the view
        Handler afterAnim = new Handler();
        afterAnim.postDelayed(new Runnable(){
            public void run() {
                deleView(mParentView, R.id.ballanim);}
        }, 1000);
        //reset variables
        touch_flg = false;
        midX = 0;
        midY = 0;
        disX = 0;
        disY = 0;
        
    }

	/**
     * 
     * */
    private void deleView(ViewGroup v, int imageId) {
        ImageView touchedView = (ImageView) mActivity.findViewById(imageId);
        int isExist = v.indexOfChild(touchedView);
        if(isExist != -1){
            v.removeView(touchedView);
        }
    }

	/**
     * translate touched item to action code/action text
     * */
    private void isMade(int mlastPos) {
        switch(String.valueOf(mlastPos)){
        case "0": 
            actionCode = actionDef.ACT_DR;
            ActText = actionDef.ACT_strDR;
            break; 
        case "1": 
            actionCode = actionDef.ACT_TWOP_MA;
            ActText = actionDef.ACT_strTWOP_MA;
            setScore(2,0);
            break; 
        case "2": 
            actionCode = actionDef.ACT_THREEP_MA;
            ActText = actionDef.ACT_strTHREEP_MA;
            setScore(3,0);
            break; 
        case "3": 
            actionCode = actionDef.ACT_FTMA;
            ActText = actionDef.ACT_strFTMA;
            setScore(1,0);
            break;
        case "5": 
            actionCode = actionDef.ACT_TO;
            ActText = actionDef.ACT_strTO;
            break;
        case "6": 
            actionCode = actionDef.ACT_BS;
            ActText = actionDef.ACT_strBS;
            break;
        case "7": 
            actionCode = actionDef.ACT_ST;
            ActText = actionDef.ACT_strST;
            break;
        case "8": 
            actionCode = actionDef.ACT_AS;
            ActText = actionDef.ACT_strAS;
            break;
        default:
        }
    }
    /**
     * translate touched item to action code/action text
     * */
    private void isMissed(int mLastPos) {
        switch(String.valueOf(mLastPos)){
        case "0": 
            actionCode = actionDef.ACT_OR; 
            ActText = actionDef.ACT_strOR;
            break; 
        case "1": 
            actionCode = actionDef.ACT_TWOP_MI;
            ActText = actionDef.ACT_strTWOP_MI;
            break; 
        case "2": 
            actionCode = actionDef.ACT_THREEP_MI;
            ActText = actionDef.ACT_strTHREEP_MI;
            break; 
        case "3": 
            actionCode = actionDef.ACT_FTMI;
            ActText = actionDef.ACT_strFTMI;
            break;
        case "5": 
            actionCode = actionDef.ACT_FOUL;
            ActText = actionDef.ACT_strFOUL;
            break;
        case "6": 
            actionCode = actionDef.ACT_BS;
            ActText = actionDef.ACT_strBS;
            break;
        case "7": 
            actionCode = actionDef.ACT_ST;
            ActText = actionDef.ACT_strST;
            break;
        case "8": 
            actionCode = actionDef.ACT_AS;
            ActText = actionDef.ACT_strAS;
            break;
        default:
       }
    }
    /**
     * set the score to update the score board
     * */
    private void setScore(int l, int r) {
        String tmpScore[] = strScore.getText().toString().split(":");
        int leftScore = Integer.valueOf(tmpScore[0]);
        int rightScore = Integer.valueOf(tmpScore[1]);

        String newRightScore = String.valueOf(rightScore + r);
        String newLeftScore = String.valueOf(leftScore + l);

        strScore.setText(newLeftScore + ":" + newRightScore);
    }

    /**
     * Initialize the added's img param
     * @return layout param of img's size
     * */
    public LayoutParams ImgIntializer(int pos, Integer img, FrameLayout FLayout, View v){
        //oImg.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
        oImg.setImageResource(img);

        //set default img's param 
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(WC, WC);
        //set appear coordination and size
        params.leftMargin = v.getLeft() + FLayout.getLeft();;
        params.topMargin = v.getTop() + FLayout.getTop() - (MultiDevInit.pH*2);
        params.width = MultiDevInit.pW;
        params.height = MultiDevInit.pH;

        return params;
    }
    /**
     * initialize the plus and minus images
     * */
    private void imageSetter(int pos) {
        switch(pos){
            case 0: imageSet = rebound; break;
            case 1: imageSet = twoPoint; break;
            case 2: imageSet = threePoint; break;
            case 3: imageSet = freeThrow; break;
            case 5: imageSet = to_n_foul; break;
            default:
        }
    }
    /**
     * toast show in 0.5 second method
     * */
    private void CustomToast(String str, String value) {
        final Toast toast = Toast.makeText(mActivity, str + value, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, MultiDevInit.bktCourtH);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {toast.cancel();}
        }, 500);
    }
    /**
     * toast show in 0.5 second method
     * */
    private void CustomToast(String str) {
        final Toast toast = Toast.makeText(mActivity, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, MultiDevInit.bktCourtH);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {toast.cancel();}
        }, 500);
    }
    /**
     * transform number to act
     * */
    private void undo(){
        playerObj mPlayerObj = teamObj.undoStack.pop();
        int rightUndo = 0;
        int leftUndo = 0;
        if(mPlayerObj.playerAct == RIVAL_ACTION){
            rightUndo = -1;
            setScore(leftUndo, rightUndo);
        }else{
            switch(mPlayerObj.playerAct){
            case actionDef.ACT_TWOP_MA:
                leftUndo = -2;
                break;
            case actionDef.ACT_THREEP_MA:
                leftUndo = -3;
                break;
            case actionDef.ACT_FTMA:
                leftUndo = -1;
            default:
                break;
            }
            //undo summary
            mPlayerObj.setSummary(mPlayerObj, -1);
            //undo score board
            setScore(leftUndo, rightUndo);
            CustomToast(mPlayerObj.playerName, actionToText(mPlayerObj.playerAct));
        }
    }
    /**
     * translate playerAct to text
     * */
    private String actionToText(int playerAct) {
        String text = "";
        switch(playerAct){
            case actionDef.ACT_TWOP_MA:
                text = "兩分進帳";
                break;
            case actionDef.ACT_TWOP_MI:
                text = "兩分沒進";
                break;
            case actionDef.ACT_THREEP_MA:
                text = "三分進帳";
                break;
            case actionDef.ACT_THREEP_MI:
                text = "三分沒進";
                break;
            case actionDef.ACT_FTMA:
                text = "穩罰中一";
                break;
            case actionDef.ACT_FTMI:
                text = "罰球不進";
                break;
            case actionDef.ACT_DR:
                text = "怒拉一防守籃板";
                break;
            case actionDef.ACT_OR:
                text = "怒拉一進攻籃板";
                break;
            case actionDef.ACT_AS:
                text = "妙傳助攻";
                break;
            case actionDef.ACT_BS:
                text = "送出火鍋一次";
                break;
            case actionDef.ACT_ST:
                text = "抄截加一";
                break;
            case actionDef.ACT_TO:
                text = "失誤一次";
                break;
            case actionDef.ACT_FOUL:
                text = "犯規一次";
                break;
            default:
                text = "DEFAULT";
                break;
        }
        text = text + "取消";
        return text;
    }
    public void setMBallAnimLoc(PathPoint newLoc) {
        mBallAnim.setTranslationX(newLoc.mX);
        mBallAnim.setTranslationY(newLoc.mY);
    }
    /**
     * init the imageview for adding new view
     * */
    public ImageView imageViewFactory(ImageView IV, int layoutId, int iconId){
        View tmpLayout = LayoutInflater.from(mActivity).inflate(layoutId, null);
        IV = (ImageView) tmpLayout.findViewById(iconId);
        ViewGroup tmpViewGroup = (ViewGroup)tmpLayout;
        // de-correlation between newView and its parent
        tmpViewGroup.removeView(IV);
        return IV;
    }
    
    /**
     * init the gridview for adding new view
     * */
    public GridView gridViewFactory(GridView GV, int layoutId, int iconId){
        View tmpLayout = LayoutInflater.from(mActivity).inflate(layoutId, null);
        GV = (GridView) tmpLayout.findViewById(iconId);
        ViewGroup tmpViewGroup = (ViewGroup)tmpLayout;
        // de-correlation between newView and its parent
        tmpViewGroup.removeView(GV);
        return GV;
    }
    /**
     * show the player summary dailog
     * */
    public void playerSummary(View v){
        //get the touched gridview's item
        for(int i=0; i<teamObj.gridViewID.length; i++){
            if(v == v.findViewById(teamObj.gridViewID[i])){
                mGridArray = teamObj.gridArray.get(i);
            }
        }
        Item mPlay = mGridArray.get(4);
        String mNum = mPlay.getTitle();
        
        TextView title = new TextView(mActivity);
        title.setText( mNum + "出手分布圖");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(0, 0, 0, 0);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        
        final View rootView = LayoutInflater.from(mActivity).inflate(R.layout.player_summary, null);
        ImageView bktCourt = (ImageView) rootView.findViewById(R.id.bktCourt);
        bktCourt.getLayoutParams().height = MultiDevInit.bktCourtH;
        bktCourt.getLayoutParams().width = MultiDevInit.bktCourtW;
        
        final AlertDialog defBuilder = new AlertDialog.Builder(mActivity)
        .setView(rootView)
        .setCustomTitle(title)
        .setPositiveButton(android.R.string.ok, null) //Set to null. then override the onclick
        .create();
        defBuilder.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = defBuilder.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        defBuilder.dismiss();
                    }
                });
            }
        });
        defBuilder.show();
        //add the made and missed point
        playAnalysis(v, (ViewGroup)rootView, mNum);
   
    }
    /**
     * show the imageview(ball icon) in xy position
     * */
    public void playAnalysis(View v, ViewGroup dialogV, String pNum){
        for(int i =0; i< teamObj.undoStack.size(); i++){
            playerObj tObj = teamObj.undoStack.get(i);
            if(tObj.playerNum == pNum){
                int xx = tObj.xPos;
                int yy = tObj.yPos;
                if((xx != 0 )||(yy != 0)){
                    if(tObj.playerAct == 2 || tObj.playerAct == 4){
                        //shot was made
                        ImageView tBallAna = null; 
                        tBallAna = imageViewFactory(tBallAna, R.layout.ball_animation, R.id.ballanim);
                        
                        //set the location of BallAna
                        RelativeLayout.LayoutParams ps = new RelativeLayout.LayoutParams(WC, WC);
                        ps.leftMargin = xx;
                        ps.topMargin = yy;
                        ((ViewGroup) dialogV).addView(tBallAna, ps);
                    }else if(tObj.playerAct == 3 || tObj.playerAct == 5){
                        //shot was missed
                        ImageView tMiss = null;
                        tMiss = imageViewFactory(tMiss, R.layout.miss_icon, R.id.missIcon);
                        //set the location of missed icon
                        RelativeLayout.LayoutParams miss = new RelativeLayout.LayoutParams(WC, WC);
                        miss.leftMargin = xx;
                        miss.topMargin = yy;
                        ((ViewGroup) dialogV).addView(tMiss, miss);
                        
                    }else{
                        //another action 
                        Log.i(TAG, "no action");
                    }
                }
            }
        }
    }
}
