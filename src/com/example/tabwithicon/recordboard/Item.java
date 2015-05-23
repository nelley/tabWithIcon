package com.example.tabwithicon.recordboard;

import android.graphics.Bitmap;

public class Item {
	Bitmap image_check;
	Bitmap image;
	String title;
	Boolean isStarter = false;
	Boolean isPlayer = false;
	/**
	 * constructor
	 * */
	public Item(Bitmap image_check, Bitmap image, String title) {
		super();
		this.image_check = image_check;
		this.image = image;
		this.title = title;
	}
	/**
	 * constructor
	 * */
	public Item(Bitmap image, String title) {
		super();
		this.image = image;
		this.title = title;
	}
	/**
	 * constructor
	 * */
	public Item(){
		super();
	}
	/**
	 * setter and getter
	 * */
	public Bitmap getImage() {
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Boolean getIsStarter() {
		return isStarter;
	}
	public void setIsStarter(Boolean isStarter) {
		this.isStarter = isStarter;
	}
	public Boolean getIsPlayer() {
		return isPlayer;
	}
	public void setIsPlayer(Boolean isPlayer) {
		this.isPlayer = isPlayer;
	}
	public Bitmap getImage_check() {
		return image_check;
	}
	public void setImage_check(Bitmap image_check) {
		this.image_check = image_check;
	}
	

}
