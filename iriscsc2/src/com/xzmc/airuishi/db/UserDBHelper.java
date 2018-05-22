package com.xzmc.airuishi.db;

import com.xzmc.airuishi.utils.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lzw on 14-5-28.
 */
public class UserDBHelper extends SQLiteOpenHelper {
  private static final int DB_VER = 6;
  private static UserDBHelper currentUserDBHelper;

  private UserDBHelper(Context context, String name, int version) {
    super(context, name, null, version);
  }

  public synchronized static UserDBHelper getCurrentUserInstance(Context context) {
    
      String name = "chat_" + Utils.getID()+ ".db3";
      currentUserDBHelper = new UserDBHelper(context, name, DB_VER);
    return currentUserDBHelper;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    FriendsTable.getInstance().createTable(db);
  }

  @Override
  public void onOpen(SQLiteDatabase db) {
    super.onOpen(db);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    switch (newVersion) {
      case 6:
      case 2:
      case 1:
        break;
    }
  }

  public synchronized void closeHelper() {
    FriendsTable.getInstance().close();
    currentUserDBHelper = null;
  }
}
