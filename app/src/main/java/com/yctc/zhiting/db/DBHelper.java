package com.yctc.zhiting.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;

import com.yctc.zhiting.config.DBConfig;


public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getName();

    public DBHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DBConfig.DB_NAME, factory, DBConfig.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: " + "oldVersion:" + oldVersion + " newVersion:" + newVersion);
        if (oldVersion >= newVersion)
            return;
        switch (oldVersion) {
            case 1:
                //updateDbVersion1(db);
            default:
                break;
        }
        Log.i(TAG, "数据库更新成功");
    }

//    private void updateDbVersion1(SQLiteDatabase db) {
//        try {
//            Cursor msgCursor;
//            // 所有消息表添加以下多个字段
//            msgCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name LIKE ?",
//                    new String[]{DBConfig.TABLE_MSG + "_%"});
//            while (msgCursor.moveToNext()) {
//                String sql = "ALTER TABLE " + msgCursor.getString(0) + " ADD " + "from_nickname" + " TEXT";
//                String sql2 = "ALTER TABLE " + msgCursor.getString(0) + " ADD " + "category" + " TEXT";
//                String sql3 = "ALTER TABLE " + msgCursor.getString(0) + " ADD " + "from_color" + " TEXT";
//                String sql4 = "ALTER TABLE " + msgCursor.getString(0) + " ADD " + "from_avatar_text" + " TEXT";
//                db.execSQL(sql);
//                db.execSQL(sql2);
//                db.execSQL(sql3);
//                db.execSQL(sql4);
//            }
//            Cursor recentContactCursor;
//            //添加联系人列表的发送状态（为了保留发送失败的状态）
//            recentContactCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name LIKE ?",
//                    new String[]{DBConfig.TABLE_RECENT_CONTACT + "_%"});
//            while (recentContactCursor.moveToNext()) {
//                String sql = "ALTER TABLE " + recentContactCursor.getString(0) + " ADD " + "send_state" + " integer";
//                db.execSQL(sql);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
