package com.xzmc.airuishi.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xzmc.airuishi.bean.City;

public class MetaDao {

	public static List<City> getCitysByProvinceId(Context ctx, String provinceId) {
		SQLiteDatabase db = MetaDbHelper.getMetaDb(ctx);
		List<City> citys = new ArrayList<City>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("SELECT * FROM city where province_id=?", new String[] { provinceId });
			while(cursor.moveToNext()){
				String id = cursor.getString(0);//id
				String name = cursor.getString(1);//name
				City c = new City(id, name);
				citys.add(c);
				c = null;
			}
			cursor.close();
		}
		db.close();
		return citys;
	}

}
