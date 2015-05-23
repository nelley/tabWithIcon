package com.example.tabwithicon.recordboard;

import java.util.ArrayList;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.example.tabwithicon.BasketFragment;
import com.example.tabwithicon.R;

/**
 * manage the bench player and starters
 * */
public class teamObj {
	
	private static final String TAG = "teamObj";
	
    static teamObj ObjInstance = null;
    static final int CT_PANEL = 9;
    static final int CT_PANEL_STARTER = 5;
    static final int CT_PANEL_BENCH = 7;
    private Context mContext = null;
    //undo stack
    public static Stack<playerObj> undoStack = new Stack<playerObj>();
    //array for bench players
    public static ArrayList<Item> benchArray = new ArrayList<Item>();
    //array for 5 players
    public static ArrayList<ArrayList<Item>> gridArray = new ArrayList<ArrayList<Item>>(CT_PANEL_STARTER);//4
    //adapters:0~4 is GridViewAdapter, 5 is benchGridAdapter
    public static RecordGridViewAdapter[] RecordGVAdapter = new RecordGridViewAdapter[CT_PANEL_STARTER +1];
    //pref
    public static final String PLAYER_FILE_NAME = "players";
    public static final String[] PLAYER_POS = new String[21];
    //
    int[] icons = {R.drawable.r, R.drawable.two, R.drawable.three, 
                   R.drawable.free, R.drawable.nonplayer, R.drawable.fail, 
                   R.drawable.block, R.drawable.steal, R.drawable.a};
    int[] lightIcons = {R.drawable.rr, R.drawable.lighttwo, R.drawable.lightthree, 
                        R.drawable.freef, R.drawable.nonplayer, R.drawable.failf, 
                        R.drawable.blockb, R.drawable.steals, R.drawable.aa};

    //
    Bitmap[] BitmapArray = new Bitmap[9];
    Bitmap[] LBitmapArray = new Bitmap[9];
    //text
    String[] textArray = {"","","",
                          "","","",
                          "","",""}; 
    //view
    public static int[] gridViewID = {R.id.gridPlayer1,R.id.gridPlayer2,
                                      R.id.gridPlayer3,R.id.gridPlayer4, R.id.gridPlayer5};
    /**
     * constructor
     * */
    public teamObj(Context c){
        mContext = c;
    }
    //singleton 
    static public teamObj getInstance(Context c){
        if(ObjInstance == null){
            ObjInstance = new teamObj(c);
            ObjInstance.initialize();
        }
        return ObjInstance;
    }
    /**
     * Initialization
     * */
    public void initialize(){
        //initialize the icons
        Resources res = mContext.getResources();
        for(int i=0; i< CT_PANEL; i++){
            BitmapArray[i] = BitmapFactory.decodeResource(res, icons[i]);
            LBitmapArray[i] = BitmapFactory.decodeResource(res, lightIcons[i]);
        }
        //set the gridview's icon and text, for 5 players
        for(int i=0; i< CT_PANEL_STARTER; i++){
            ArrayList<Item> tmp = new ArrayList<Item>();
            if(i == 0 || i == 1 || i == 4){//dark color
                //set the 3*3 square
                for(int k=0; k< CT_PANEL; k++){
                    tmp.add(new Item(BitmapArray[k], textArray[k]));
                }
            }else{//light color
                for(int k=0; k< CT_PANEL; k++){
                    tmp.add(new Item(LBitmapArray[k], textArray[k]));
                }
            }
            gridArray.add(i,tmp);
        }
        //bench players array setting
        for(int i = 0; i< CT_PANEL_BENCH; i++){
            benchArray.add(new Item(BitmapArray[4], "player"));
        }
        for(int i=0; i< RecordGVAdapter.length-1; i++){
            RecordGVAdapter[i] = new RecordGridViewAdapter(mContext, R.layout.row_grid, gridArray.get(i));
        }
        //bench array setting
        RecordGVAdapter[5] = new RecordGridViewAdapter(mContext, R.layout.row_grid, benchArray);
    }
    /**
     * set starter and bench player dialog's method
     * */
    static public String setByUser(View v, Context mContext){
        String results = null;
        ArrayList<Item> selectedPlayers = new ArrayList<Item>();
        ArrayList<Item> selectedStarters = new ArrayList<Item>();
        for(int i = 0; i< BasketFragment.player_settingGrid.size(); i++){
            Item mPlayer = BasketFragment.player_settingGrid.get(i);
            if(mPlayer.getIsPlayer()){
                if(mPlayer.getIsStarter()){//starters
                    selectedStarters.add(mPlayer);
                }else{//bench players
                    selectedPlayers.add(mPlayer);
                }
            }
        }

        if((selectedPlayers.size() + selectedStarters.size())< 5){
            results = "請選擇足夠的球員參加比賽";
        }else if((selectedPlayers.size() + selectedStarters.size()) > 12){
            results = "可登錄球員上限為十二人";
        }else if(selectedStarters.size() < 5){
            results = "請選五位球員為先發";
        }else if(selectedStarters.size() > 5){
            results = "先發球員不得超過五位";
        }else{
            //-----------------------------
            //update info to bench/starter players
            //-----------------------------
            for(int i=0; i<selectedPlayers.size(); i++){
                benchArray.get(i).setImage(null);//selectedPlayers.get(i).getImage()
                benchArray.get(i).setTitle(selectedPlayers.get(i).getTitle());
            }
            for(int i=0; i<5; i++){
                //update 0~4(starters), then get 0~8(3*3 square) to reset name of player
                gridArray.get(i).get(4).setImage(null);//selectedStarters.get(i).getImage()
                gridArray.get(i).get(4).setTitle(selectedStarters.get(i).getTitle());
                
            }
            //check selected player's number!!!
            View rootView = ((Activity) mContext).getWindow().getDecorView().findViewById(android.R.id.content);
            
            //for 5 players
            GridView[] p = new GridView[5];
            for(int i=0; i<p.length; i++){
                p[i] = (GridView)rootView.findViewById(teamObj.gridViewID[i]);
            }
            //notify data changed
            for(int i=0; i<RecordGVAdapter.length; i++){
                RecordGVAdapter[i].notifyDataSetChanged();
            }
            //redraw the views
            for(int i=0; i<p.length; i++){
                p[i].invalidateViews();
            }
            //-----------------------------
            //store info of players to local storage with no duplicate number
            //-----------------------------
            SharedPreferences pref = mContext.getSharedPreferences(PLAYER_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            int i = 0;
            for(; i<selectedStarters.size(); i++){
                editor.putString(PLAYER_POS[i], selectedStarters.get(i).getTitle());
            }
            for(int k=0; k<selectedPlayers.size(); k++){
                editor.putString(PLAYER_POS[i+k], selectedPlayers.get(k).getTitle());
            }
            editor.commit();
            results = "ok";
        }
        return results;
    }
    /**
     * 
     * */
    static public void addTimeLine(playerObj tmpObj) {
        //update undolist by clone obj
        try {
            undoStack.push(tmpObj.clone());
            Log.i(TAG, tmpObj.toString() + "is pushed to the top of stack");
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
    /**
     * getter and setter
     * */
    public static ArrayList<Item> getBenchArray() {
        return benchArray;
    }
    public static void setBenchArray(ArrayList<Item> benchArray) {
        teamObj.benchArray = benchArray;
    }
}
