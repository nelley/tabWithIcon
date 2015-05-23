package com.example.tabwithicon.recordboard;

import java.util.ArrayList;
import com.example.tabwithicon.BasketFragment;
import com.example.tabwithicon.R;
import com.example.tabwithicon.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerGridViewAdapter extends ArrayAdapter<Item>{
	
    Context context;
    int layoutResourceId;
    ArrayList<Item> data = new ArrayList<Item>();
    final private String MAXIMUM_PLAYER = "新增上限為20位球員!!";
    //default icon for player selection
    Bitmap base = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.basketball_player);

    public PlayerGridViewAdapter(Context context, int layoutResourceId, ArrayList<Item> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;
        Item tmpItem = data.get(position);
        if (row == null) {
            final LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new RecordHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.player_number);
            holder.imageItem = (ImageView) row.findViewById(R.id.player_image);
            holder.starter = (ImageView) row.findViewById(R.id.player_starter);
            holder.pos = position;
            //row.getLayoutParams().height =  ViewGroup.LayoutParams.WRAP_CONTENT;
            //row.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            row.setTag(holder);
           
            if(tmpItem.getTitle() == "新增球員"){
				holder.starter.setVisibility(View.INVISIBLE);
				holder.imageItem.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						final View dialogView = (View)v.getParent().getParent();
						
						if(data.size()>20){
							Toast.makeText(context, MAXIMUM_PLAYER, Toast.LENGTH_SHORT).show();
						}else{
							//create the dialog for player's number input
							TextView title = new TextView(context);
							title.setText("請輸入球員背號");
							title.setBackgroundColor(Color.DKGRAY);
							title.setPadding(10, 10, 10, 10);
							title.setGravity(Gravity.CENTER);
							title.setTextColor(Color.WHITE);
							title.setTextSize(20);
							
							final View rootView = LayoutInflater.from(context).inflate(R.layout.newplayer_add, null);
							final AlertDialog defBuilder = new AlertDialog.Builder(context)
							.setView(rootView)
							.setCustomTitle(title)
							.setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
							.create();
							
							defBuilder.setOnShowListener(new DialogInterface.OnShowListener(){
								@Override
								public void onShow(DialogInterface dialog) {
									Button b = defBuilder.getButton(AlertDialog.BUTTON_POSITIVE);
									b.setOnClickListener(new View.OnClickListener(){
										@Override
										public void onClick(View v) {
											EditText input = (EditText)rootView.findViewById(R.id.addNewPlayer);
											CharSequence tmp = input.getText();
											Boolean chk_flg = true;
											String inputNum = tmp.toString() + "號";
											inputNum.replaceAll("\\s","");
											if(inputNum.equals("號")){
												Toast.makeText(context, "請輸入球員背號", Toast.LENGTH_SHORT).show();
											}else{
												for(int i=0 ;i<data.size(); i++){
													//duplicate check
													if(inputNum.equals(data.get(i).getTitle())){
														Toast.makeText(context, "此球員背號已存在!", Toast.LENGTH_SHORT).show();
														chk_flg = false;
													}
												}
												if(chk_flg){
													GridView setPlayers = (GridView) dialogView.findViewById(R.id.setplayer);
													//add new player item at the position before plus
													data.add(data.size()-1,
															new Item(BitmapFactory.decodeResource(context.getResources(), 
																	R.drawable.basketball_player), inputNum));
																	
													setPlayers.setAdapter(BasketFragment.getInitialAdapter());
													defBuilder.dismiss();
												}
											}
											
											
										}
										
									});
								}
								
							});
							defBuilder.show();
						}
					}
				});
			}else{
				holder.imageItem.setOnLongClickListener(new OnLongClickListener(){
					@Override
					public boolean onLongClick(View v) {
						//get the position
						View touchView = (View)v.getParent();
						RecordHolder touchItem= (RecordHolder)touchView.getTag();
						//get the whole dialog view
						touchView = (View)touchView.getParent();
						GridView setPlayers = (GridView)touchView.findViewById(R.id.setplayer);
						//remove touched item
						data.remove(touchItem.pos);
						
						setPlayers.setAdapter(BasketFragment.getInitialAdapter());
						return false;
					}
				});
				holder.imageItem.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						View touchView = (View)v.getParent();
						RecordHolder touchItem= (RecordHolder)touchView.getTag();
						int itemPos = touchItem.pos;
						//change when image clicked
						playerSelect(v, touchView, itemPos);
					}
				});
				holder.starter.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						View touchView = (View)v.getParent();
						RecordHolder touchItem= (RecordHolder)touchView.getTag();
						int itemPos = touchItem.pos;
						//change when starter icon clicked
						starterSelect(v, touchView, itemPos);
					}
				});
			}
		} else {
			holder = (RecordHolder) row.getTag();
		}
		Item item = data.get(position);
		holder.txtTitle.setText(item.getTitle());
		holder.imageItem.setImageBitmap(item.getImage());
		holder.starter.setImageBitmap(item.getImage_check());
		return row;
	}
	static class RecordHolder {
		TextView txtTitle;
		ImageView imageItem;
		ImageView starter;
		int pos;
	}
	/**
	 * set onclicklistener's action for player
	 * */
	public void playerSelect(View v, View touchView, int itemPos){
		Item tmpItem = data.get(itemPos);
		ImageView player_image = (ImageView)v;
		if(!tmpItem.getIsPlayer()){
			player_image.setImageResource(drawable.dribble);
			tmpItem.setImage(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.dribble));
			tmpItem.setIsPlayer(true);
			
		}else{
			//update the view
			player_image.setImageResource(drawable.basketball_player);
			//update the data
			tmpItem.setImage(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.basketball_player));
			tmpItem.setIsPlayer(false);
			
			//update the view of starter
			ImageView start_image = (ImageView)touchView.findViewById(R.id.player_starter);
			start_image.setImageResource(drawable.unchecked);
			//update the data 
			tmpItem.setImage_check(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.unchecked));
			tmpItem.setIsStarter(false);
		}
	}
	/**
	 * set onclicklistener's action for starter
	 * */
	public void starterSelect(View v, View touchView, int itemPos){
		Item tmpItem = data.get(itemPos);
		if(tmpItem.getIsPlayer()){//already selected as a player, cancel as a start
			ImageView starter_check = (ImageView)v;
			if(tmpItem.getIsStarter()){//already selected as the starter
				starter_check.setImageResource(drawable.unchecked);
				tmpItem.setImage_check(BitmapFactory.decodeResource(getContext().getResources(), 
										R.drawable.unchecked));
				tmpItem.setIsStarter(false);
			}else{//not the starter 
				starter_check.setImageResource(drawable.checked);
				tmpItem.setImage_check(BitmapFactory.decodeResource(getContext().getResources(), 
										R.drawable.checked));
				tmpItem.setIsStarter(true);
			}
		}else{//not selected as a player, set as a starter and player
			//update player image
			View parentView = (View)v.getParent();
			ImageView player_image = (ImageView)parentView.findViewById(R.id.player_image);
			player_image.setImageResource(drawable.dribble);
			tmpItem.setImage(BitmapFactory.decodeResource(getContext().getResources(), 
								R.drawable.dribble));
			tmpItem.setIsPlayer(true);
			
			//update starter image
			ImageView starter_check = (ImageView)v;
			starter_check.setImageResource(drawable.checked);
			tmpItem.setImage_check(BitmapFactory.decodeResource(getContext().getResources(), 
									R.drawable.checked));
			tmpItem.setIsStarter(true);
			
		}
	}
}
