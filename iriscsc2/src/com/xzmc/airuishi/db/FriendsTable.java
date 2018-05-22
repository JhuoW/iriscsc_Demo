package com.xzmc.airuishi.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xzmc.airuishi.base.App;
import com.xzmc.airuishi.bean.QXUser;
import com.xzmc.airuishi.utils.ParcelableUtil;

/**
 * 浼氳瘽琛�
 */
public class FriendsTable {
	private static final String FRIENDID = "friendid";
	private static final String FRIEND_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `friends` ("
			+ "`id` INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "`friendid` VARCHAR(63) UNIQUE NOT NULL, "
			+ "`object` BLOB NOT NULL)";
	public static final String FRIEND_ID = "friendid";
	public static final String OBJECT = "object";
	// private static final String FRIEND_TABLE_SQL =
	// "CREATE TABLE IF NOT EXISTS `friends` ("
	// + "`id` INTEGER PRIMARY KEY AUTOINCREMENT, "
	// + "`friendid` VARCHAR(63) UNIQUE NOT NULL, "
	// + "`friendname` text, "
	// + "'friendimage' text, "
	// + "'friendcompany' text, "
	// + "'friendsex' text, "
	// + "'friendtrade' text, "
	// + "'friendaddress' text)";

	private static final String FRIEND_TABLE = "friends";
	public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS `friends`";

	private static FriendsTable roomsTable;
	private UserDBHelper dbHelper;

	private FriendsTable() {
		dbHelper = UserDBHelper.getCurrentUserInstance(App.ctx);
	}

	public synchronized static FriendsTable getInstance() {
		if (roomsTable == null) {
			roomsTable = new FriendsTable();
		}
		return roomsTable;
	}

	/**
	 * 寤鸿〃
	 * @param db
	 */
	void createTable(SQLiteDatabase db) {
		db.execSQL(FRIEND_TABLE_SQL);
	}

	/**
	 * 鍒犺〃
	 * 
	 * @param db
	 */
	void dropTable(SQLiteDatabase db) {
		db.execSQL(DROP_TABLE_SQL);
	}

	public List<QXUser> selectFriends() {
		List<QXUser> friends = new ArrayList<QXUser>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM friends", null);
		while (c.moveToNext()) {
			QXUser friend = createFriendByCursor(c);
			friends.add(friend);
		}
		c.close();
		return friends;
	}

	static QXUser createFriendByCursor(Cursor c) {
		byte[] msgBytes = c.getBlob(c.getColumnIndex(OBJECT));
		if (msgBytes != null) {
			QXUser user = (QXUser) ParcelableUtil.unmarshall(msgBytes,
					QXUser.CREATOR);
			return user;
		} else {
			return null;
		}
	}

	public void insertFriends(List<QXUser> friends) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			for (QXUser friend : friends) {
				ContentValues cv = new ContentValues();
				cv.put(FRIEND_ID, friend.getID());
				cv.put(OBJECT, marshallMsg(friend));
				db.insert(FRIEND_TABLE, null, cv);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 瀵硅薄搴忓垪鍖�
	 * @param msg
	 * @return
	 */
	public byte[] marshallMsg(QXUser user) {
		byte[] msgBytes = ParcelableUtil.marshall(user);
		if (msgBytes == null) {
			throw new NullPointerException("msg bytes is null");
		}
		return msgBytes;
	}

	public void deleteFriend(String friendid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(FRIEND_TABLE, "friendid=?", new String[] { friendid });
	}

	public void deleteAllFriend(List<QXUser> friends) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		for (QXUser friend : friends) {
			db.delete(FRIEND_TABLE, "friendid=?",
					new String[] { friend.getID() });
		}

	}

	void close() {
		roomsTable = null;
	}
}
