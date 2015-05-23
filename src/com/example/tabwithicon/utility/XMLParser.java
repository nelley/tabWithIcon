package com.example.tabwithicon.utility;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetManager;


@SuppressLint("ValidFragment")
public class XMLParser extends Fragment{
	
	private static final String TAG = "letsBasket.XMLParser";

	Activity mActivity = null;
	ArrayList<HashMap<String, String>> bktList = new ArrayList<HashMap<String, String>>();
	AssetManager massetmanager;
	//constructor
	public XMLParser(){
		
	}
	public XMLParser(AssetManager a, Activity b){
		massetmanager = a;
		mActivity = b;
	}
	
	public String openXML(String fileName){
		try {
			InputStream in = massetmanager.open(fileName);
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer, "UTF-8");
			return writer.toString();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * for mainpage,將從server端接收到的球場資訊存入Hashmap中,以待後續使用.
	 * 必須思考怎麼跟checkin page做整合
	 * */
	public ArrayList<HashMap<String, String>> Geoparser(String XMLFile){
		bktList.clear();
		String GeoMaParser = preProGeoMarker(XMLFile);
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput( new StringReader ( GeoMaParser ));//從server接收xml?案開始解析
			int eventType = parser.getEventType();
			
			while(eventType != XmlPullParser.END_DOCUMENT)
			{
				//management by HashMap
				HashMap<String, String> map= new HashMap<String, String>(); 
				switch(eventType)
				{
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						String tag = parser.getName();
						if("marker".equals(tag)){
							//-----[深度が2のtitleだけ取得]
							int depth = parser.getDepth();
							if(depth == 2){
								//obtain the value for listview in main page
								map.put(Utilities.getCourtName(), parser.getAttributeValue(0));
								map.put("cur_people", parser.getAttributeValue(1));
								map.put(Utilities.getLAT(), parser.getAttributeValue(2));
								map.put(Utilities.getLNG(), parser.getAttributeValue(3));
								map.put("photo", parser.getAttributeValue(4));
								//map.put(Utilities.getTYPE(), parser.getAttributeValue(5));
								bktList.add(map);
							}
						}
						break;
				}
				eventType = parser.next();
			}
		} catch (IOException e) {
			Log.i(TAG, "XML Parse error");
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return bktList;
	}
	/**
	 * rewrite here!!!!!!!!!!!!!!!!!!!
	 * */
	public ArrayList<HashMap<String, String>> InfoListparser(String bktInfoList){
		bktList.clear();
		String FInfoList = preProGeoMarker(bktInfoList);
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput( new StringReader ( FInfoList ));//從server接收xml?案開始解析
			int eventType = parser.getEventType();
			
			while(eventType != XmlPullParser.END_DOCUMENT)
			{
				//management by HashMap
				HashMap<String, String> map= new HashMap<String, String>(); 
				switch(eventType)
				{
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						String tag = parser.getName();
						if("marker".equals(tag)){
							//-----[深度が2のtitleだけ取得]
							int depth = parser.getDepth();
							if(depth == 2){
								//obtain the value for listview in main page
								map.put("address", parser.getAttributeValue(0));
								map.put("courtname", parser.getAttributeValue(1));
								map.put("distance", parser.getAttributeValue(2));
								map.put("idx", parser.getAttributeValue(3));
								map.put("lng", parser.getAttributeValue(4));
								map.put("lat", parser.getAttributeValue(5));
								bktList.add(map);
							}
						}
						break;
				}
				eventType = parser.next();
			}
		} catch (IOException e) {
			Log.i(TAG, "XML Parse error");
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		
		return bktList;
	}
	
	
	
	/**
	 * 整理從server端接收到的訊息格式
	 * */
	private String preProGeoMarker(String XMLFile) {
		String ResXmlFile = null;
		
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput( new StringReader ( XMLFile ));//從server接收xml?案開始解析
			
			int myType= parser.getEventType();
			while(myType != parser.END_DOCUMENT){
				switch(myType){
					case XmlPullParser.TEXT:
						ResXmlFile = parser.getText();
						break;
				}
				myType = parser.next();
			}
		}catch(XmlPullParserException | IOException e){
			e.printStackTrace();
		}
		//把開頭的換行符號去掉
		ResXmlFile = ResXmlFile.replace("\n", "");
		return ResXmlFile;
	}
}
