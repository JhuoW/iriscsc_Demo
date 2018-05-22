package com.xzmc.airuishi.db;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xzmc.airuishi.bean.QXUser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProfessDBHelper extends SQLiteOpenHelper{
	private static SQLiteDatabase sqLiteDatabase;
	private static final String DBNAME = "profess.db";
	private static final int VERSION = 1;

	public ProfessDBHelper(Context context) {
		super(context, DBNAME, null, VERSION);
		openSqLiteDatabase();
	}
	public SQLiteDatabase openSqLiteDatabase() {
		if (sqLiteDatabase == null) {
			sqLiteDatabase = getWritableDatabase();
		}
		return sqLiteDatabase;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String create_profess = "create table if not exists "
				+ Profess.TABLENAME
				+ " ( _id integer primary key autoincrement , "
				+ Profess.ID + " text , " 
				+ Profess.NAME + " text , " 
				+ Profess.IMAGE + " text , "
				+ Profess.SEX + " text , " 
				+ Profess.ADDRESS + " text , " 
				+ Profess.AUTHORITY + " text , " 
				+ Profess.ISPROFESS + " text , " 
				+ Profess.PROFESSINFO + " text , " 
				+ Profess.ONLINETIME + " text ) ; ";
		db.execSQL(create_profess);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String deleteTable = "DROP TABLE " + Profess.TABLENAME;
		db.execSQL(deleteTable);
		String create_profess = "create table if not exists "
				+ Profess.TABLENAME
				+ " ( _id integer primary key autoincrement , "
				+ Profess.ID + " text , " 
				+ Profess.NAME + " text , " 
				+ Profess.IMAGE + " text , "
				+ Profess.SEX + " text , " 
				+ Profess.ADDRESS + " text , " 
				+ Profess.AUTHORITY + " text , " 
				+ Profess.ISPROFESS + " text , " 
				+ Profess.PROFESSINFO + " text , " 
				+ Profess.ONLINETIME + " text ) ; ";
		db.execSQL(create_profess);
	}
	
	public void insertProfess(QXUser user){
		ContentValues cv = new ContentValues();
		cv.put(Profess.ID, user.getID());
		cv.put(Profess.NAME, user.getName());
		cv.put(Profess.IMAGE, user.getImage());
		cv.put(Profess.SEX, user.getSex());
		cv.put(Profess.ADDRESS, user.getAddress());
		cv.put(Profess.AUTHORITY, user.getAuthority());
		cv.put(Profess.ISPROFESS, user.getIsProfessor());
		cv.put(Profess.PROFESSINFO, user.getProfessorInfo());
		cv.put(Profess.ONLINETIME, user.getOnlineTime());
		sqLiteDatabase.insert(Profess.TABLENAME, null, cv);
	}
	
	public void insertintoProfess(JSONArray jsonarray){
		for (int i = 0; i < jsonarray.length(); i++) {
			ContentValues cv = new ContentValues();
			try {
				JSONObject child = jsonarray.getJSONObject(i);
				String id = child.getString("ID");
				String sex= child.getString("sex");
				String imgurl = child.getString("imgUrl");
				String nickname = child.getString("nickName");
				String isProfessor = child.getString("isProfessor");
				cv.put(Profess.ID, id);
				cv.put(Profess.NAME, nickname);
				cv.put(Profess.IMAGE, imgurl);
				cv.put(Profess.SEX, sex);
				cv.put(Profess.ISPROFESS, isProfessor);
				sqLiteDatabase.insert(Profess.TABLENAME, null, cv);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	
	public List<QXUser> getProfessUser(){
		List<QXUser> professor = new ArrayList<QXUser>();
		Cursor cursor = sqLiteDatabase.query(Profess.TABLENAME, null, Profess.ISPROFESS + " = ? ", new String[]{"true"}, null, null, null);
		while(cursor.moveToNext()){
			QXUser user = new QXUser();
			user.setID(cursor.getString(cursor.getColumnIndex(Profess.ID)));
			user.setImage(cursor.getString(cursor.getColumnIndex(Profess.IMAGE)));
			user.setIsProfessor(cursor.getString(cursor.getColumnIndex(Profess.ISPROFESS)));
			user.setName(cursor.getString(cursor.getColumnIndex(Profess.NAME)));
			user.setSex(cursor.getString(cursor.getColumnIndex(Profess.SEX)));
			professor.add(user);
		}
		return professor;
	}
	
	public List<QXUser> getCommonUser(){
		List<QXUser> professor = new ArrayList<QXUser>();
		Cursor cursor = sqLiteDatabase.query(Profess.TABLENAME, null, Profess.ISPROFESS + " = ? ", new String[]{"false"}, null, null, null);
		while(cursor.moveToNext()){
			QXUser user = new QXUser();
			user.setID(cursor.getString(cursor.getColumnIndex(Profess.ID)));
			user.setImage(cursor.getString(cursor.getColumnIndex(Profess.IMAGE)));
			user.setIsProfessor(cursor.getString(cursor.getColumnIndex(Profess.ISPROFESS)));
			user.setName(cursor.getString(cursor.getColumnIndex(Profess.NAME)));
			user.setSex(cursor.getString(cursor.getColumnIndex(Profess.SEX)));
			professor.add(user);
		}
		return professor;
	}
	
	
	public List<QXUser> getUser(){
		List<QXUser> professor = new ArrayList<QXUser>();
		Cursor cursor = sqLiteDatabase.query(Profess.TABLENAME, null, null, null, null, null, null);
		while(cursor.moveToNext()){
			QXUser user = new QXUser();
			user.setID(cursor.getString(cursor.getColumnIndex(Profess.ID)));
			user.setImage(cursor.getString(cursor.getColumnIndex(Profess.IMAGE)));
			user.setIsProfessor(cursor.getString(cursor.getColumnIndex(Profess.ISPROFESS)));
			user.setName(cursor.getString(cursor.getColumnIndex(Profess.NAME)));
			user.setSex(cursor.getString(cursor.getColumnIndex(Profess.SEX)));
			professor.add(user);
		}
		return professor;
	}
	
	public void deleteFriend(String friendid) {
		sqLiteDatabase.delete(Profess.TABLENAME, Profess.ID + " = ? ", new String[] { friendid });
	}
	
	/**
	 * 清空某张表内数据
	 */
	public void deleteAllDataFromTable(String Tablename){
		sqLiteDatabase.execSQL("DELETE FROM " + Tablename);
	}
}
