package com.xzmc.airuishi.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CommentDBHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "comment.db";
	private static final int VERSION = 1;
	private static SQLiteDatabase sqLiteDatabase;
	
	public SQLiteDatabase openSqLiteDatabase() {
		if (sqLiteDatabase == null) {
			sqLiteDatabase = getWritableDatabase();
		}
		return sqLiteDatabase;
	}
	
	public CommentDBHelper(Context context) {
		super(context, DBNAME, null, VERSION);
		openSqLiteDatabase();
	}
	
	public void closeSqLiteDatabase() {
		if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
			sqLiteDatabase.close();
		}
	}

	public void releaseSqliteDatabase() {
		if (sqLiteDatabase != null) {
			sqLiteDatabase.close();
		}
		sqLiteDatabase = null;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String create_comment = "create table if not exists "
				+ Comment.TABLENAME
				+ " ( _id integer primary key autoincrement , "
				+ Comment.USERID + " text , " 
				+ Comment.NICKNAME + " text , " 
				+ Comment.PICTURE + " text , "
				+ Comment.AUTHORITY + " text , " 
				+ Comment.CONTENT + " text , " 
				+ Comment.TIME + " text , " 
				+ Comment.ID + " text , " 
				+ Comment.STATUE + " text , " 
				+ Comment.ACCEPTID + " text , " 
				+ Comment.TONICKNAME + " text , "
				+ Comment.TOPICTURE + " text , "
				+ Comment.TOAUTHORITY + " text , "
				+ Comment.CONTENTID + " text ) ; ";
		db.execSQL(create_comment);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String deleteTable = "DROP TABLE " + Comment.TABLENAME;
		db.execSQL(deleteTable);
		String create_comment = "create table if not exists "
				+ Comment.TABLENAME
				+ " ( _id integer primary key autoincrement , "
				+ Comment.USERID + " text , " 
				+ Comment.NICKNAME + " text , " 
				+ Comment.PICTURE + " text , "
				+ Comment.AUTHORITY + " text , " 
				+ Comment.CONTENT + " text , " 
				+ Comment.TIME + " text , " 
				+ Comment.ID + " text , " 
				+ Comment.STATUE + " text , " 
				+ Comment.ACCEPTID + " text , " 
				+ Comment.TONICKNAME + " text , "
				+ Comment.TOPICTURE + " text , "
				+ Comment.TOAUTHORITY + " text , "
				+ Comment.CONTENTID + " text ) ; ";
		db.execSQL(create_comment);
	}
	
	public void insertComment(com.xzmc.airuishi.bean.Comment comment){
		ContentValues cv = new ContentValues();
		cv.put(Comment.USERID, comment.getUserId());
		cv.put(Comment.NICKNAME, comment.getNickname());
		cv.put(Comment.PICTURE, comment.getPicture());
		cv.put(Comment.AUTHORITY, comment.getAuthority());
		cv.put(Comment.CONTENT, comment.getContent());
		cv.put(Comment.TIME, comment.getTime());
		cv.put(Comment.ID, comment.getId());
		cv.put(Comment.STATUE, comment.getStatus());
		cv.put(Comment.ACCEPTID, comment.getAcceptId());
		cv.put(Comment.TONICKNAME, comment.getTonickname());
		cv.put(Comment.TOPICTURE, comment.getTopicture());
		cv.put(Comment.TOAUTHORITY, comment.getToauthority());
		cv.put(Comment.CONTENTID, comment.getContentId());
		sqLiteDatabase.insert(Comment.TABLENAME, null, cv);
	}

	//查询母评论：
	public List<com.xzmc.airuishi.bean.Comment> getMumCommentList(){
		List<com.xzmc.airuishi.bean.Comment> list = new ArrayList<com.xzmc.airuishi.bean.Comment>();
		Cursor cursor = sqLiteDatabase.query(Comment.TABLENAME, null, Comment.STATUE + " = ? ", new String[]{"0"}, null, null, null);
		while(cursor.moveToNext()){
			com.xzmc.airuishi.bean.Comment c = new com.xzmc.airuishi.bean.Comment();
			c.setUserId(cursor.getString(cursor.getColumnIndex(Comment.USERID)));
			c.setNickname(cursor.getString(cursor.getColumnIndex(Comment.NICKNAME)));
			c.setPicture(cursor.getString(cursor.getColumnIndex(Comment.PICTURE)));
			c.setAuthority(cursor.getString(cursor.getColumnIndex(Comment.AUTHORITY)));
			c.setContent(cursor.getString(cursor.getColumnIndex(Comment.CONTENT)));
			c.setTime(cursor.getString(cursor.getColumnIndex(Comment.TIME)));
			c.setId(cursor.getString(cursor.getColumnIndex(Comment.ID)));
			c.setStatus(cursor.getString(cursor.getColumnIndex(Comment.STATUE)));
			c.setAcceptId(cursor.getString(cursor.getColumnIndex(Comment.ACCEPTID)));
			c.setTonickname(cursor.getString(cursor.getColumnIndex(Comment.TONICKNAME)));
			c.setTopicture(cursor.getString(cursor.getColumnIndex(Comment.TOPICTURE)));
			c.setToauthority(cursor.getString(cursor.getColumnIndex(Comment.TOAUTHORITY)));
			c.setContentId(cursor.getString(cursor.getColumnIndex(Comment.CONTENTID)));
			list.add(c);
		}
		cursor.close();
		return list;
	}
	
	
	//查询子评论：
	public List<com.xzmc.airuishi.bean.Comment> getSonCommentList(String id){
		List<com.xzmc.airuishi.bean.Comment> list = new ArrayList<com.xzmc.airuishi.bean.Comment>();
//		Cursor cursor = sqLiteDatabase.query(Comment.TABLENAME, null, Comment.STATUE + " = ? and "+ Comment.CONTENTID + " = ?", new String[]{"1",id}, null, null, null);
		Cursor cursor = sqLiteDatabase.query(Comment.TABLENAME, null, Comment.STATUE + " = ? ", new String[]{"1"}, null, null, null);

		while(cursor.moveToNext()){
			com.xzmc.airuishi.bean.Comment c = new com.xzmc.airuishi.bean.Comment();
			c.setUserId(cursor.getString(cursor.getColumnIndex(Comment.USERID)));
			c.setNickname(cursor.getString(cursor.getColumnIndex(Comment.NICKNAME)));
			c.setPicture(cursor.getString(cursor.getColumnIndex(Comment.PICTURE)));
			c.setAuthority(cursor.getString(cursor.getColumnIndex(Comment.AUTHORITY)));
			c.setContent(cursor.getString(cursor.getColumnIndex(Comment.CONTENT)));
			c.setTime(cursor.getString(cursor.getColumnIndex(Comment.TIME)));
			c.setId(cursor.getString(cursor.getColumnIndex(Comment.ID)));
			c.setStatus(cursor.getString(cursor.getColumnIndex(Comment.STATUE)));
			c.setAcceptId(cursor.getString(cursor.getColumnIndex(Comment.ACCEPTID)));
			c.setTonickname(cursor.getString(cursor.getColumnIndex(Comment.TONICKNAME)));
			c.setTopicture(cursor.getString(cursor.getColumnIndex(Comment.TOPICTURE)));
			c.setToauthority(cursor.getString(cursor.getColumnIndex(Comment.TOAUTHORITY)));
			c.setContentId(cursor.getString(cursor.getColumnIndex(Comment.CONTENTID)));
			System.out.println("id:"+c.getId());
			System.out.println("contentid:"+c.getContentId());
			if(c.getContentId().equals(id)){
				list.add(c);
			}
		}
		cursor.close();
		return list;
	}
	
	
	/**
	 * 清空某张表内数据
	 */
	public void deleteAllDataFromTable(String Tablename){
		sqLiteDatabase.execSQL("DELETE FROM " + Tablename);
	}
}
