package com.example.tabwithicon.recordboard;

import java.util.ArrayList;

import com.example.tabwithicon.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.GridView;
import android.widget.Toast;

public class playerDragListener implements OnDragListener{
	
	ArrayList<Item> mGridArray =new ArrayList<Item>();
	//Activity mActivity = null;
	Context mContext = null;
	RecordGridViewAdapter mGridAdapter;
	final int PLAYER_POSITION = 4; 
	
	public playerDragListener(Activity c){
		mContext = c;
	}
	@Override
	public boolean onDrag(View v, DragEvent event) {
		// Handles each of the expected events
		Drawable normalShape = mContext.getResources().getDrawable(R.drawable.normal_color);
		Drawable targetShape = mContext.getResources().getDrawable(R.drawable.target_shape);
    	View v1 = v.findViewById(R.id.gridPlayer1);
    	View v2 = v.findViewById(R.id.gridPlayer2);
    	View v3 = v.findViewById(R.id.gridPlayer3);
    	View v4 = v.findViewById(R.id.gridPlayer4);
    	View v5 = v.findViewById(R.id.gridPlayer5);
		switch (event.getAction()) {
		    //signal for the start of a drag and drop operation.
		    case DragEvent.ACTION_DRAG_STARTED:
		        // do nothing
		    	//if false, go to drag_ended
		        break;
		        
		    //the drag point has entered the bounding box of the View
		    case DragEvent.ACTION_DRAG_ENTERED:
		        v.setBackground(targetShape);	//change the shape of the view
		        break;
		        
		    //the user has moved the drag shadow outside the bounding box of the View
		    case DragEvent.ACTION_DRAG_EXITED:
		        v.setBackground(normalShape);	//change the shape of the view back to normal
		        break;
		        
		    //drag shadow has been released,the drag point is within the bounding box of the View
		    case DragEvent.ACTION_DROP:
		        // if the view is the bottomlinear, we accept the drag item
		    	if(v == v1 || v == v2 || v == v3 || v == v4 || v == v5) {
		    		
		    		teamObj tmpTeamObj = teamObj.getInstance(mContext);
		    		if(v== v1){
		    			mGridArray = teamObj.gridArray.get(0);
		    			mGridAdapter = teamObj.RecordGVAdapter[0];
		    		}else if(v == v2){
		    			mGridArray = teamObj.gridArray.get(1);
		    			mGridAdapter = teamObj.RecordGVAdapter[1];
		    		}else if(v == v3){
		    			mGridArray = teamObj.gridArray.get(2);
		    			mGridAdapter = teamObj.RecordGVAdapter[2];
		    		}else if(v == v4){
		    			mGridArray = teamObj.gridArray.get(3);
		    			mGridAdapter = teamObj.RecordGVAdapter[3];
		    		}else if(v == v5){
		    			mGridArray = teamObj.gridArray.get(4);
		    			mGridAdapter = teamObj.RecordGVAdapter[4];
		    		}
		    		
					GridView currentGV = (GridView)v;
					//prepare for swaping
					Item tmp = (Item)currentGV.getItemAtPosition(PLAYER_POSITION);
					Bitmap benchImg = tmp.getImage();
					String benchPlayer = tmp.getTitle();
					
					//update the value and GridView
					//get info from dragged view
					playerView pV = (playerView)event.getLocalState();
					Item mItem = new Item(pV.getPlayerImg(), pV.getPlayerNum());
					mGridArray.set(PLAYER_POSITION, mItem);
					mGridAdapter.notifyDataSetChanged();
					//exchange the player info to menu's view
					Item newMenu = new Item(benchImg, benchPlayer);
					//update the menu's view
					teamObj.getBenchArray().set(pV.getmPosition(), newMenu);
					pV.getBenchAdapter().notifyDataSetChanged();
					
					
				}else {
					Toast.makeText(mContext, "You can't drop the image here", Toast.LENGTH_LONG).show();
					break;
				}
				break;
			//the drag and drop operation has concluded.
			case DragEvent.ACTION_DRAG_ENDED:
				v.setBackground(normalShape);	//go back to normal shape
				break;
			default:
				break;
		}
		return true;
	}
}
