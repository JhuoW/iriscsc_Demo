package com.xzmc.airuishi.utils; 

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xzmc.airuishi.db.MetaDbHelper;

/** 
 * @author xiaobian 
 * @version ����ʱ�䣺2015��4��21�� ����7:39:16 
 * 
 */
public class DBUtil {
	public static SQLiteDatabase getAppMetaDB(Context ctx) {
		return MetaDbHelper.getMetaDb(ctx);
	}
}
