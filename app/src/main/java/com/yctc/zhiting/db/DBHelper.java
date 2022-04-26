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
//        if (oldVersion >= newVersion)
//            return;
//        switch (oldVersion) {
//            case 1:
//                updateDbVersion1(db);
//
//            default:
//                break;
//        }
        if (oldVersion < newVersion)
            updateDbVersion1(db);
        Log.i(TAG, "数据库更新成功");
    }

    /**
     * 给房间/部门添加 user_count字段
     * 给家庭/公司表添加mode字段，区分是公司还是家庭
     * @param db
     */
    private void updateDbVersion1(SQLiteDatabase db) {
        try {

            Cursor hcCursor;
            hcCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                    new String[]{DBConfig.TABLE_HOME_COMPANY});

            while (hcCursor.moveToNext()){
                String hcSql = "ALTER TABLE " + hcCursor.getString(0) + " ADD " + "area_type" + " integer";
                db.execSQL(hcSql);
            }

            Cursor locationCursor;
            // 所有消息表添加以下多个字段
            locationCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                    new String[]{DBConfig.TABLE_LOCATION});
            while (locationCursor.moveToNext()) {
                String sql = "ALTER TABLE " + locationCursor.getString(0) + " ADD " + "user_count" + " integer";
                db.execSQL(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
