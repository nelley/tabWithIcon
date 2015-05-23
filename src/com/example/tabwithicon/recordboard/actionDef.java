package com.example.tabwithicon.recordboard;

public class actionDef {
	//player info
	static final int PLAYER_NUMBER = 0;
	static final int PLAYER_NAME = 1;
	//2 point
	static final int ACT_TWOP_MA = 2;
	static final int ACT_TWOP_MI = 3;
	//3 point
	static final int ACT_THREEP_MA = 4;
	static final int ACT_THREEP_MI = 5;
	//free throw
	static final int ACT_FTMA = 6;
	static final int ACT_FTMI = 7;
	//rebound
	static final int ACT_DR = 8;
	static final int ACT_OR = 9;
	//assist
	static final int ACT_AS = 10;
	//block
	static final int ACT_BS = 11;
	//steal
	static final int ACT_ST = 12;
	//turnover
	static final int ACT_TO = 13;
	//foul
	static final int ACT_FOUL = 14;
	//total score 
	static final int TOTAL_SCORE = 15;
	//efficient
	static final int EFF = 16;
	//actions except 2 points, 3 points and free throw using by UNDO function
	static final int UNDO_OTHERS = 17;
	
	//-------------------------------------
	//2 point
	static final String ACT_strTWOP_MA = "兩分進帳";
	static final String ACT_strTWOP_MI = "兩分沒進";
	//3 point
	static final String ACT_strTHREEP_MA = "三分進帳";
	static final String ACT_strTHREEP_MI = "三分沒進";
	//free throw
	static final String ACT_strFTMA = "穩罰中一";
	static final String ACT_strFTMI = "罰球不進";
	//rebound
	static final String ACT_strDR = "怒拉一防守籃板";
	static final String ACT_strOR = "怒拉一進攻籃板";
	//assist
	static final String ACT_strAS = "妙傳助攻";
	//block
	static final String ACT_strBS = "送出火鍋一次";
	//steal
	static final String ACT_strST = "抄截加一";
	//turnover
	static final String ACT_strTO = "失誤一次";
	//foul
	static final String ACT_strFOUL = "犯規一次";
	
}
