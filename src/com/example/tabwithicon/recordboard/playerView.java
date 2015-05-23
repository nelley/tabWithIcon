package com.example.tabwithicon.recordboard;

import android.graphics.Bitmap;
import android.view.View;

public class playerView {
	
	/**
	 * menu's view
	 * */
	View mView;
	
	/**
	 * player's photo
	 * */
	Bitmap playerImg;
	
	/**
	 * play's number
	 * */
	String playerNum;
	
	/**
	 * bench's custom adapter
	 * */
	RecordGridViewAdapter benchAdapter;
	
	/**
	 * position in menu's item
	 * */
	int mPosition;
	
	public playerView(View v, Bitmap pImg, String pNum, RecordGridViewAdapter BA, int position){
		this.mView = v;
		this.playerImg = pImg;
		this.playerNum = pNum;
		this.benchAdapter = BA;
		this.mPosition = position;
	}

	public View getmView() {
		return mView;
	}

	public void setmView(View mView) {
		this.mView = mView;
	}

	public Bitmap getPlayerImg() {
		return playerImg;
	}

	public void setPlayerImg(Bitmap playerImg) {
		this.playerImg = playerImg;
	}

	public String getPlayerNum() {
		return playerNum;
	}

	public void setPlayerNum(String playerNum) {
		this.playerNum = playerNum;
	}

	public RecordGridViewAdapter getBenchAdapter() {
		return benchAdapter;
	}

	public void setBenchAdapter(RecordGridViewAdapter benchAdapter) {
		this.benchAdapter = benchAdapter;
	}

	public int getmPosition() {
		return mPosition;
	}

	public void setmPosition(int mPosition) {
		this.mPosition = mPosition;
	}
	
}
