package com.xzmc.airuishi.utils; 

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xzmc.airuishi.db.MetaDbHelper;

/** 
 * @author xiaobian 
 * @version 创建时间：2015年4月21日 下午7:39:16 
 * 
 */
public class DBUtil {
	public static SQLiteDatabase getAppMetaDB(Context ctx) {
		return MetaDbHelper.getMetaDb(ctx);
	}
}
