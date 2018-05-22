package com.xzmc.airuishi.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xzmc.airuishi.R;

public class MetaDbHelper {

	public static SQLiteDatabase getMetaDb(Context ctx) {
		File db = ctx.getDatabasePath("meta.data");
		if (!db.exists()) {
			db.getParentFile().mkdirs();
			try {
				InputStream in = ctx.getResources().openRawResource(R.raw.meta);
				FileOutputStream out = new FileOutputStream(db);
				byte buf[] = new byte[1024];
				for (int len = -1; (len = in.read(buf)) != -1;) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return SQLiteDatabase.openOrCreateDatabase(db, null);
	}

}
