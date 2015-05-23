package com.example.tabwithicon;

import java.util.ArrayList;
import java.util.Map;
import com.example.tabwithicon.checkinpage.CheckInPage;
import com.example.tabwithicon.mainpage.MainPage;
import com.example.tabwithicon.recordboard.Item;
import com.example.tabwithicon.recordboard.PlayerGridViewAdapter;
import com.example.tabwithicon.recordboard.RecordBoardPage;
import com.example.tabwithicon.recordboard.teamObj;
import com.google.android.gms.maps.*;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.*;
import android.view.*;
import android.view.ViewGroup.*;
import android.widget.*;

public final class BasketFragment extends Fragment{
	static final String KEY_ADDRESS = "address";
	static final String KEY_ID = "id";
	static final String KEY_LAT = "lat";
	static final String KEY_LNG = "lng";
	static final String KEY_NAME = "name";
	static final String KEY_TYPE = "type";

	private static final String TAG = "letsBasket.BasketFragment";
	private static final String KEY_CONTENT = "BasketFragment:Content";
	private String mContent = "???";
	private static Context mContext = null;
	
	public static ArrayList<Item> player_settingGrid = new ArrayList<Item>();
	private static PlayerGridViewAdapter InitialAdapter;
	private static Boolean setMenu_flg = false;
	public static final String DEFAUT_STRING = "";

	//Constructor:TestFragment初始化,傳進陣列裡定義好的東西進來
	public static BasketFragment newInstance(String content, Context c) {        
		BasketFragment fragment = new BasketFragment();
		fragment.mContent = content;
		mContext = c;
		return fragment;
	}

    @Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "oncreate");
		super.onCreate(savedInstanceState);
		if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}
	}

    /*
     * 這裡選擇在哪個頁面生成哪些資訊
     * */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        LinearLayout layout = null;
        if(mContent.equals("人氣鬥牛場")){
            Log.i(TAG, "onCreateView 1");
            MainPage mMainPage = new MainPage();
            return mMainPage.createMainPage(inflater, getActivity());
        }else if(mContent.equals("記錄板")){
            Log.i(TAG, "onCreateView 2");
            RecordBoardPage mRecordBoardPage = new RecordBoardPage(getActivity());
            return mRecordBoardPage.createRecordBoardPage(inflater);
        }else if(mContent.equals("打卡")){
            Log.i(TAG, "onCreateView 3");
            CheckInPage mCheckInPage = new CheckInPage();
            return mCheckInPage.createCheckinPage(inflater, getActivity());
        }else if(mContent.equals("比賽數據")){
            Log.i(TAG, "onCreateView 4");
            layout = new LinearLayout(getActivity());
            TextView text = new TextView(getActivity());
            text.setGravity(Gravity.CENTER);
            text.setText(mContent);
            text.setTextSize(20 * getResources().getDisplayMetrics().density);
            text.setPadding(20, 20, 20, 20);
            layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            layout.setGravity(Gravity.CENTER);
            layout.addView(text);
            return layout;
        }else{
            Log.i(TAG, "onCreateView else");
            layout = new LinearLayout(getActivity());
            TextView text = new TextView(getActivity());
            text.setGravity(Gravity.CENTER);
            text.setText(mContent);
            text.setTextSize(20 * getResources().getDisplayMetrics().density);
            text.setPadding(20, 20, 20, 20);
            layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            layout.setGravity(Gravity.CENTER);
            layout.addView(text);
            return layout;
        }
    }

	@Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
        SupportMapFragment mainPage_f = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment checkinPage_f = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map1);
        if(mContent.equals("人氣鬥牛場") && mainPage_f.isResumed()){
            //destroy the google map embedded in main page
            getFragmentManager().beginTransaction().remove(mainPage_f).commit();
        }else if(mContent.equals("打卡") && checkinPage_f.isResumed()){
            //destroy the google map embedded in check in page
            getFragmentManager().beginTransaction().remove(checkinPage_f).commit();
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
    /**
     * method to control which action is activated when the pager is visible to user
     * */
    @Override
    public void setMenuVisibility(boolean visible) {
        super.setMenuVisibility(visible);
        Log.i(TAG, "setMenuVisibility S");
        if (visible && mContent == "記錄板" && setMenu_flg == false) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.gridview_player, null);
            GridView setPlayers = (GridView) view.findViewById(R.id.setplayer);
            player_settingGrid.clear();
            
            //set default player list in alertDialog by preferences	
            SharedPreferences pref = mContext.getSharedPreferences(teamObj.PLAYER_FILE_NAME, Context.MODE_PRIVATE);
            Map<String,?> keys = pref.getAll();
            Log.i(TAG, "setMenuVisibility: initialize player's number");
            if(keys.size() == 0){//first time use this app
                for(int i=0; i<10; i++){
                    if(i<9){
                        player_settingGrid.add(new Item(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.unchecked),
                                                        BitmapFactory.decodeResource(mContext.getResources(), R.drawable.basketball_player), 
                                                        String.valueOf(i)+"號"));
                    }else{//「+」icon	
                        player_settingGrid.add(new Item(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.unchecked),
                                                        BitmapFactory.decodeResource(mContext.getResources(), R.drawable.plus), 
                                                        "新增球員"));
                    }
                }
            }else{//get info of players from pref
                String player_num = null;
                for(Map.Entry<String,?> entry : keys.entrySet()){
                    player_num = pref.getString(entry.getKey(), DEFAUT_STRING);
                    player_settingGrid.add(new Item(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.unchecked),
                                                    BitmapFactory.decodeResource(mContext.getResources(), R.drawable.basketball_player), 
                                                    player_num));
                }
                player_settingGrid.add(new Item(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.unchecked),
                                                BitmapFactory.decodeResource(mContext.getResources(), R.drawable.plus), 
                                                "新增球員"));
            }

            Log.i(TAG, "setMenuVisibility: initialize adaper");
            InitialAdapter = new PlayerGridViewAdapter(mContext, R.layout.player_grid, player_settingGrid);
            //setPlayers.getLayoutParams().height = 200;
            //setPlayers.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            setPlayers.setAdapter(InitialAdapter);

            TextView title = new TextView(mContext);
            title.setText("請設定上場球員");
            title.setBackgroundColor(Color.DKGRAY);
            title.setPadding(10, 10, 10, 10);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.WHITE);
            title.setTextSize(20);
            
            Log.i(TAG, "setMenuVisibility: initialize alertdialog");
            final AlertDialog defBuilder = new AlertDialog.Builder(mContext)
            .setView(view)
            .setCustomTitle(title)
            .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
            .create();

            defBuilder.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button b = defBuilder.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //set the starters and bench player
                            String set_text = teamObj.setByUser(v, mContext);
                            if(set_text=="ok"){
                                setMenu_flg = true;
                                defBuilder.dismiss();
                            }else{
                                Toast.makeText(mContext, set_text, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            defBuilder.show();
            //defBuilder.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        Log.i(TAG, "setMenuVisibility E");
    }
    /**
     * getter and setter
     * */
    public ArrayList<Item> getPlayer_settingGrid() {
         return player_settingGrid;
    }

    public void setPlayer_settingGrid(ArrayList<Item> pg) {
        player_settingGrid = pg;
    }

    public static PlayerGridViewAdapter getInitialAdapter() {
        return InitialAdapter;
    }

    public static void setInitialAdapter(PlayerGridViewAdapter initialAdapter) {
        InitialAdapter = initialAdapter;
    }
}

