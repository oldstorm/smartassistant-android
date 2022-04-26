package com.yctc.zhiting.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.app.main.framework.baseutil.LogUtil;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.DBConfig;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.RoomAreaBean;
import com.yctc.zhiting.entity.mine.UserInfoBean;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.StringUtil;
import com.yctc.zhiting.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;


public class DBManager {

    private static volatile DBManager INSTANCE;
    private DBHelper dbHelper;
    private SQLiteDatabase db;//不开放数据库对象，避免在外部调用关闭了数据库连接

    private DBManager(Context context) {
        dbHelper = new DBHelper(context, null);
        openDb();
    }

    public static DBManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DBManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DBManager(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 打开数据库
     */
    public void openDb() {
        db = dbHelper.getReadableDatabase();
    }

    /**
     * 关闭数据库
     */
    public void closeDb() {
        if (db.isOpen()) {
            db.close();
            INSTANCE = null;
        }
    }

    private long getLongFromCursor(Cursor cursor, String key) {
        return cursor.getLong(cursor.getColumnIndex(key));
    }

    private String getStringFromCursor(Cursor cursor, String key) {
        return cursor.getString(cursor.getColumnIndex(key));
    }

    private int getIntFromCursor(Cursor cursor, String key) {
        return cursor.getInt(cursor.getColumnIndex(key));
    }

    private Boolean getBooleanFromCursor(Cursor cursor, String key) {
        return cursor.getInt(cursor.getColumnIndex(key)) == 1;
    }

    /**
     * 检查数据库是否已连接上
     */
    private void checkIfDBIsOk() {
        if (db == null || !db.isOpen()) {
            throw new RuntimeException("Please call openDb first");
        }
    }

    /**
     * 检查用户表是否存在，不存在则建新表
     */
    private void checkUserInfoTable() {
        try {
            String createUserInfoTableSqlStr = "create table if not exists " + DBConfig.TABLE_USER_INFO
                    + "(user_id integer primary key,"
                    + "nickname text, phone text, icon_url text)";
            db.execSQL(createUserInfoTableSqlStr);
        } catch (Exception e) {

        }
    }

    /**
     * 检查家庭/公司表格是否存在，不存在则建新表
     * h_id  家庭id
     * name 家庭名称
     * sa_user_token sa的token
     * sa_user_id sa 的id
     * sa_lan_address sa地址
     * is_bind_sa 是否绑定sa
     * user_id 用户id
     * cloud_id 云端家庭id
     * cloud_user_id 云端用户id
     * area_id sa家庭id
     * sa_id sa设备id
     * area_type 区分家庭和公司， 1是家庭，2是公司
     */
    private void checkHomeCompanyTable() {
        try {
            String createRecentContactTableSqlStr = "create table if not exists " + DBConfig.TABLE_HOME_COMPANY
                    + "(h_id INTEGER primary key AUTOINCREMENT," +
                    "name text, sa_user_token text, sa_user_id integer, sa_lan_address text, is_bind_sa bool, user_id integer,ss_id text,mac_address text, cloud_id integer, cloud_user_id integer, area_id integer, sa_id text, area_type integer)";
            db.execSQL(createRecentContactTableSqlStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建房间/区域表
     *
     * @param id
     */
    private void checkRoomAreaTable(int id) {
        try {
            String createRecentContactTableSqlStr = "create table if not exists " + DBConfig.TABLE_ROOM_AREA + id + ""
                    + "(r_id integer primary key AUTOINCREMENT,"
                    + "name text, position integer)";
            db.execSQL(createRecentContactTableSqlStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取家庭/公司最后一条记录
     *
     * @return
     */
    public Cursor getLastHomeCompany() {
        Cursor cursor = null;
        try {
            String sql = "select * from " + DBConfig.TABLE_HOME_COMPANY + " order by h_id desc limit 1";
            cursor = db.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }

    /**
     * 获取家庭/公司
     *
     * @return
     */
    public HomeCompanyBean queryHomeCompanyById(long id) {
        checkIfDBIsOk();
        checkHomeCompanyTable();
        HomeCompanyBean homeCompanyBean = null;
        Cursor cursor = null;
        Cursor roomCursor = null;
        try {
            String sql = "select * from " + DBConfig.TABLE_HOME_COMPANY + " where h_id = " + id;
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                String name = cursor.getString(cursor.getColumnIndex("name"));
                homeCompanyBean = new HomeCompanyBean();
                homeCompanyBean.setLocalId(getIntFromCursor(cursor, "h_id"));
                homeCompanyBean.setName(name);
                sql = "select * from " + DBConfig.TABLE_LOCATION + " where area_id = " + id;
                roomCursor = db.rawQuery(sql, null);
                homeCompanyBean.setRoomAreaCount(roomCursor.getCount());

                homeCompanyBean.setId(getLongFromCursor(cursor, "cloud_id"));
                homeCompanyBean.setCloud_user_id(getIntFromCursor(cursor, "cloud_user_id"));
                homeCompanyBean.setArea_id(getLongFromCursor(cursor, "area_id"));
                homeCompanyBean.setArea_type(getIntFromCursor(cursor, "area_type"));
                roomCursor.close();
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (roomCursor != null)
                roomCursor.close();
        }
        return homeCompanyBean;
    }

    /**
     * 通过sa家庭id查家庭
     *
     * @return
     */
    public HomeCompanyBean queryHomeCompanyByAreaId(long areaId) {
        checkIfDBIsOk();
        checkHomeCompanyTable();
        HomeCompanyBean homeCompanyBean = null;
        Cursor cursor = null;
        try {
            String sql = "select * from " + DBConfig.TABLE_HOME_COMPANY + " where area_id = " + areaId;
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                String name = cursor.getString(cursor.getColumnIndex("name"));
                homeCompanyBean = new HomeCompanyBean();
                homeCompanyBean.setLocalId(getIntFromCursor(cursor, "h_id"));
                homeCompanyBean.setName(name);
                homeCompanyBean.setId(getLongFromCursor(cursor, "cloud_id"));
                homeCompanyBean.setCloud_user_id(getIntFromCursor(cursor, "cloud_user_id"));
                homeCompanyBean.setArea_id(getLongFromCursor(cursor, "area_id"));
                homeCompanyBean.setArea_type(getIntFromCursor(cursor, "area_type"));
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return homeCompanyBean;
    }

    /**
     * 通过sa的url查找sa的token
     *
     * @param url
     * @return
     */
    public String getSaTokenByUrl(String url) {
        checkIfDBIsOk();
        checkHomeCompanyTable();
        String saToken = null;
        Cursor cursor = null;
        try {
            String sql = "select * from " + DBConfig.TABLE_HOME_COMPANY + " where sa_lan_address = '" + url + "'";
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                saToken = cursor.getString(cursor.getColumnIndex("sa_user_token"));
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return saToken;
    }

    /**
     * 通过saId查找saToken
     *
     * @param saId
     * @return
     */
    public String getSaTokenBySAID(String saId) {
        checkIfDBIsOk();
        checkHomeCompanyTable();
        String saToken = null;
        Cursor cursor = null;
        try {
            String sql = "select * from " + DBConfig.TABLE_HOME_COMPANY + " where sa_id = '" + saId + "'";
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                saToken = cursor.getString(cursor.getColumnIndex("sa_user_token"));
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return saToken;
    }

    /**
     * 查询家庭/公司列表
     *
     * @return
     */
    public synchronized List<HomeCompanyBean> queryHomeCompanyList() {
        if (db == null || !db.isOpen()) openDb();
        checkIfDBIsOk();
        checkHomeCompanyTable();
        List<HomeCompanyBean> homeCompanyList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConfig.TABLE_HOME_COMPANY, null, null, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    HomeCompanyBean item = new HomeCompanyBean();
                    item.setLocalId(getLongFromCursor(cursor, "h_id"));
                    item.setSa_user_token(getStringFromCursor(cursor, "sa_user_token"));
                    item.setName(getStringFromCursor(cursor, "name"));
                    item.setUser_id(cursor.getInt(cursor.getColumnIndex("sa_user_id")));
                    item.setIs_bind_sa(getBooleanFromCursor(cursor, "is_bind_sa"));
                    item.setSs_id(getStringFromCursor(cursor, "ss_id"));
                    item.setBSSID(getStringFromCursor(cursor, "mac_address"));
                    item.setCloud_user_id(getIntFromCursor(cursor, "cloud_user_id"));
                    item.setId(getLongFromCursor(cursor, "cloud_id"));
                    item.setSa_lan_address(getStringFromCursor(cursor, "sa_lan_address"));
                    item.setArea_id(getLongFromCursor(cursor, "area_id"));
                    item.setSa_id(getStringFromCursor(cursor, "sa_id"));
                    item.setArea_type(getIntFromCursor(cursor, "area_type"));
                    homeCompanyList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return homeCompanyList;
    }

    /**
     * 通过云端用户id查找家庭
     *
     * @param cloudUserId
     * @return
     */
    public List<HomeCompanyBean> queryHomeCompanyListByCloudUserId(int cloudUserId) {
        if (db == null || !db.isOpen()) openDb();
        checkIfDBIsOk();
        checkHomeCompanyTable();
        List<HomeCompanyBean> homeCompanyList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConfig.TABLE_HOME_COMPANY, null, "cloud_user_id=?", new String[]{String.valueOf(cloudUserId)}, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    HomeCompanyBean item = new HomeCompanyBean();
                    item.setLocalId(getLongFromCursor(cursor, "h_id"));
                    item.setSa_user_token(getStringFromCursor(cursor, "sa_user_token"));
                    item.setName(getStringFromCursor(cursor, "name"));
                    item.setUser_id(cursor.getInt(cursor.getColumnIndex("sa_user_id")));
                    item.setIs_bind_sa(getBooleanFromCursor(cursor, "is_bind_sa"));
                    item.setSs_id(getStringFromCursor(cursor, "ss_id"));
                    item.setBSSID(getStringFromCursor(cursor, "mac_address"));
                    item.setCloud_user_id(getIntFromCursor(cursor, "cloud_user_id"));
                    item.setId(getLongFromCursor(cursor, "cloud_id"));
                    item.setArea_id(getLongFromCursor(cursor, "area_id"));
                    item.setSa_id(getStringFromCursor(cursor, "sa_id"));
                    item.setArea_type(getIntFromCursor(cursor, "area_type"));
                    homeCompanyList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return homeCompanyList;
    }

    /**
     * 查询本地家庭
     *
     * @return
     */
    public synchronized List<HomeCompanyBean> queryLocalHomeCompanyList() {
        if (db == null || !db.isOpen()) openDb();
        checkIfDBIsOk();
        checkHomeCompanyTable();
        List<HomeCompanyBean> homeCompanyList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConfig.TABLE_HOME_COMPANY, null, "cloud_id=0", null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    HomeCompanyBean item = new HomeCompanyBean();
                    item.setLocalId(getLongFromCursor(cursor, "h_id"));
                    item.setSa_user_token(getStringFromCursor(cursor, "sa_user_token"));
                    item.setName(getStringFromCursor(cursor, "name"));
                    item.setUser_id(cursor.getInt(cursor.getColumnIndex("sa_user_id")));
                    item.setIs_bind_sa(getBooleanFromCursor(cursor, "is_bind_sa"));
                    item.setSs_id(getStringFromCursor(cursor, "ss_id"));
                    item.setBSSID(getStringFromCursor(cursor, "mac_address"));
                    item.setCloud_user_id(getIntFromCursor(cursor, "cloud_user_id"));
                    item.setId(getLongFromCursor(cursor, "cloud_id"));
                    item.setArea_id(getLongFromCursor(cursor, "area_id"));
                    item.setSa_id(getStringFromCursor(cursor, "sa_id"));
                    item.setSa_lan_address(getStringFromCursor(cursor, "sa_lan_address"));
                    item.setArea_type(getIntFromCursor(cursor, "area_type"));
                    homeCompanyList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return homeCompanyList;
    }

    /**
     * 获取最后一个本地家庭的id
     *
     * @return
     */
    public int getLastHomeId() {

        return 1;
    }

    /**
     * 修改家庭公司名称
     *
     * @param id
     * @param name
     * @return
     */
    public int updateHomeCompany(long id, String name) {
        int count = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            count = db.update(DBConfig.TABLE_HOME_COMPANY, contentValues, "h_id=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 通过云端id修改家庭公司名称
     *
     * @param homeCompanyBean
     * @return
     */
    public int updateHomeCompanyByCloudId(HomeCompanyBean homeCompanyBean) {
        int count = 0;
        try {
            long id = homeCompanyBean.getId();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", homeCompanyBean.getName());
            contentValues.put("is_bind_sa", homeCompanyBean.isIs_bind_sa());
            contentValues.put("sa_user_id", homeCompanyBean.getUser_id());
            contentValues.put("user_id", homeCompanyBean.getUser_id());
            contentValues.put("cloud_user_id", homeCompanyBean.getCloud_user_id());
            contentValues.put("area_id", homeCompanyBean.isIs_bind_sa() ? id : 0);
            contentValues.put("cloud_id", id);
            contentValues.put("sa_id", homeCompanyBean.getSa_id());
            count = db.update(DBConfig.TABLE_HOME_COMPANY, contentValues, "cloud_id=? or area_id=?", new String[]{String.valueOf(id), String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 通过said修改家庭sa_lan_address
     *
     * @param saId
     * @param saLanAddress
     * @return
     */
    public int updateSALanAddressBySAId(String saId, String saLanAddress, String macAddress) {
        int count = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("sa_lan_address", saLanAddress);
            contentValues.put("mac_address", macAddress);
            count = db.update(DBConfig.TABLE_HOME_COMPANY, contentValues, "sa_id=?", new String[]{saId});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 根据token修改家庭名称
     *
     * @param token
     * @param name
     * @return
     */
    public int updateHCNameByToken(String token, String name) {
        int count = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            count = db.update(DBConfig.TABLE_HOME_COMPANY, contentValues, "sa_user_token=?", new String[]{token});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 通过satoken修改家庭信息
     *
     * @return
     */
    public int updateHomeCompanyBySaToken(HomeCompanyBean homeCompanyBean) {
        int count = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", homeCompanyBean.getName());
            contentValues.put("is_bind_sa", homeCompanyBean.isIs_bind_sa());
            contentValues.put("user_id", homeCompanyBean.getUser_id());
//            if (Constant.wifiInfo != null) {
//                contentValues.put("ss_id", Constant.wifiInfo.getSSID());
//                contentValues.put("mac_address", Constant.wifiInfo.getBSSID());
//            }
            count = db.update(DBConfig.TABLE_HOME_COMPANY, contentValues, "sa_user_token=?", new String[]{homeCompanyBean.getSa_user_token()});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 通过sa area_id修改家庭信息
     *
     * @return
     */
    public int updateHomeCompanyByAreaId(HomeCompanyBean homeCompanyBean) {
        int count = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", homeCompanyBean.getName());
            contentValues.put("is_bind_sa", homeCompanyBean.isIs_bind_sa());
            contentValues.put("user_id", homeCompanyBean.getUser_id());
            contentValues.put("sa_user_id", homeCompanyBean.getUser_id());
            contentValues.put("sa_user_token", homeCompanyBean.getSa_user_token());
            LogUtil.e("updateHomeCompanyByAreaId1=="+HomeUtil.isBssidEqual(homeCompanyBean));
            LogUtil.e("updateHomeCompanyByAreaId2=="+(Constant.wifiInfo != null));
            if (Constant.wifiInfo != null && HomeUtil.isBssidEqual(homeCompanyBean)) {
                contentValues.put("ss_id", Constant.wifiInfo.getSSID());
                String bssid = StringUtil.getBssid();
                if (!TextUtils.isEmpty(bssid))
                contentValues.put("mac_address", bssid);
            }
            count = db.update(DBConfig.TABLE_HOME_COMPANY, contentValues, "area_id=?", new String[]{String.valueOf(homeCompanyBean.getArea_id())});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 修改家庭
     *
     * @param homeCompanyBean
     * @return
     */
    public int updateHomeCompany(HomeCompanyBean homeCompanyBean) {
        int count = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", homeCompanyBean.getName());
            contentValues.put("sa_user_token", homeCompanyBean.getSa_user_token());
            contentValues.put("sa_lan_address", homeCompanyBean.getSa_lan_address());
            contentValues.put("user_id", homeCompanyBean.getUser_id());
            contentValues.put("sa_user_id", homeCompanyBean.getUser_id());
            contentValues.put("is_bind_sa", homeCompanyBean.isIs_bind_sa());
            contentValues.put("area_id", homeCompanyBean.getArea_id());
            contentValues.put("sa_id", homeCompanyBean.getSa_id());
            if (Constant.wifiInfo != null) {
                contentValues.put("ss_id", Constant.wifiInfo.getSSID());
                String bssid = StringUtil.getBssid();
                if (!TextUtils.isEmpty(bssid))
                    contentValues.put("mac_address", bssid);
            }
            count = db.update(DBConfig.TABLE_HOME_COMPANY, contentValues, "h_id=?", new String[]{String.valueOf(homeCompanyBean.getLocalId())});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 更新家庭areaId
     *
     * @param localId
     * @param areaId
     * @return
     */
    public int updateHCAreaId(long localId, long areaId, boolean bindWifi) {
        int count = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("cloud_id", areaId);
            if (Constant.wifiInfo != null) {
                contentValues.put("ss_id", Constant.wifiInfo.getSSID());
                if (bindWifi) {
                    String bssid = StringUtil.getBssid();
                    if (!TextUtils.isEmpty(bssid))
                        contentValues.put("mac_address", bssid);
                }
            }
            contentValues.put("cloud_user_id", UserUtils.getCloudUserId());
            count = db.update(DBConfig.TABLE_HOME_COMPANY, contentValues, "h_id=?", new String[]{String.valueOf(localId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 修改家庭cloud_id
     *
     * @return
     */
    public int updateHomeCompanyCloudId(long localId, long cloudId, int cloudUserId, int userId, String saToken) {
        int count = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("cloud_id", cloudId);
            contentValues.put("cloud_user_id", cloudUserId);
            contentValues.put("sa_user_id", userId);
            contentValues.put("sa_user_token", saToken);
            count = db.update(DBConfig.TABLE_HOME_COMPANY, contentValues, "h_id=?", new String[]{String.valueOf(localId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 修改家庭macAddress
     *
     * @param localId
     * @param macAddress
     * @return
     */
    public int updateHomeMacAddress(long localId, String macAddress) {
        int count = 0;
        try {
            ContentValues contentValues = new ContentValues();
            String bssid = StringUtil.getBssid();
            if (!TextUtils.isEmpty(bssid))
                contentValues.put("mac_address", bssid);
            count = db.update(DBConfig.TABLE_HOME_COMPANY, contentValues, "h_id=?", new String[]{String.valueOf(localId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 修改SAToken
     *
     * @param localId
     * @param saToken
     * @return
     */
    public int updateSATokenByLocalId(long localId, String saToken) {
        int count = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("sa_user_token", saToken);
            count = db.update(DBConfig.TABLE_HOME_COMPANY, contentValues, "h_id=?", new String[]{String.valueOf(localId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 删除家庭公司
     *
     * @param id
     * @return
     */
    public int removeFamily(long id) {
        int count = 0;
        try {
            count = db.delete(DBConfig.TABLE_HOME_COMPANY, "h_id=?", new String[]{String.valueOf(id)});
            if (count > 0) {
                removeLocationByHId(id, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 删除家庭公司
     *
     * @param id
     * @return
     */
    public int removeFamilyByAreaId(long id) {
        int count = 0;
        try {
            count = db.delete(DBConfig.TABLE_HOME_COMPANY, "area_id=?", new String[]{String.valueOf(id)});
            if (count > 0) {
                removeLocationByHId(id, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 删除家庭公司
     *
     * @param token
     * @return
     */
    public int removeFamilyByToken(String token) {
        int count = 0;
        try {
            count = db.delete(DBConfig.TABLE_HOME_COMPANY, "sa_user_token=?", new String[]{token});
            if (count > 0) {
                removeLocationBySaToken(token);
                removeDeviceBySaToken(token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 删除所有家庭
     * @return
     */
    public int removeAllFamily() {
        int count = db.delete(DBConfig.TABLE_HOME_COMPANY, null, null);
        return count;
    }

    public int removeVirtualSAFamily() {
        int count = db.delete(DBConfig.TABLE_HOME_COMPANY, "is_bind_sa=?", new String[]{"0"});
        return count;
    }

    /**
     * 修改家庭的云端用户id
     * @param localId
     * @return
     */
    public long unbindCloudUser(long localId) {
        long count = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("cloud_id", 0);
            contentValues.put("cloud_user_id", 0);
            count = db.update(DBConfig.TABLE_HOME_COMPANY, contentValues, "h_id=?", new String[]{String.valueOf(localId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 删除对应家庭的房间区域表
     *
     * @param id
     */
    public void delRoomAreasTable(int id) {
        db.execSQL("drop table " + DBConfig.TABLE_ROOM_AREA + id + "");
    }

    /**
     * 根据有云端的数据
     *
     * @return
     */
    public int removeFamilyByCloudUserId() {
        int count = 0;
        try {
            count = db.delete(DBConfig.TABLE_HOME_COMPANY, "cloud_user_id>0", null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 移除不是当前云端用户的家庭
     *
     * @param cloudUserId
     * @return
     */
    public int removeFamilyNotPresentUserFamily(int cloudUserId) {
        int count = 0;
        try {
            count = db.delete(DBConfig.TABLE_HOME_COMPANY, "cloud_user_id>0 and cloud_user_id!=?", new String[]{String.valueOf(cloudUserId)});

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }


    /**
     * 移除不是当前云端用户的家庭
     *
     * @param cloudId
     * @return
     */
    public int removeFamilyByCloudId(long cloudId) {
        int count = 0;
        try {
            count = db.delete(DBConfig.TABLE_HOME_COMPANY, "cloud_id = ?", new String[]{String.valueOf(cloudId)});

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }


    /**
     * 查询家庭/公司列表
     *
     * @return
     */
    public List<RoomAreaBean> queryRoomAreaList(int id) {
        checkIfDBIsOk();
        checkRoomAreaTable(id);
        List<RoomAreaBean> roomAreaList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(DBConfig.TABLE_ROOM_AREA + id + "", null, null, null, null, null, "position asc");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    RoomAreaBean item = new RoomAreaBean();
                    item.setId(getIntFromCursor(cursor, "r_id"));
                    item.setName(getStringFromCursor(cursor, "name"));
                    roomAreaList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomAreaList;
    }

    /**
     * 插入家庭/公司
     *
     * @param item
     */
    public long insertHomeCompany(HomeCompanyBean item, List<LocationBean> roomAreas, boolean insertWifiInfo) {
        long count = 0;
        checkIfDBIsOk();
        checkHomeCompanyTable();
        try {
            String tableName = DBConfig.TABLE_HOME_COMPANY;
            ContentValues contentValues = new ContentValues();
            //contentValues.put("h_id", item.getLocalId());
            contentValues.put("name", item.getName());
            contentValues.put("sa_user_token", item.getSa_user_token());
            contentValues.put("sa_lan_address", item.getSa_lan_address());
            contentValues.put("sa_user_id", item.getUser_id());
            contentValues.put("is_bind_sa", item.isIs_bind_sa());
            if (Constant.wifiInfo != null && !TextUtils.isEmpty(item.getSa_user_token()) && insertWifiInfo) {
                contentValues.put("ss_id", Constant.wifiInfo.getSSID());
                contentValues.put("mac_address", StringUtil.getBssid());
            }
            contentValues.put("cloud_id", item.getId());
            contentValues.put("cloud_user_id", item.getCloud_user_id());
            contentValues.put("area_id", item.getArea_id());
            contentValues.put("sa_id", item.getSa_id());
            contentValues.put("area_type", item.getArea_type());
            //使用replace，不使用insert，为了实现存在就更新，不存在就插入
            count = db.replace(tableName, null, contentValues);
            if (roomAreas != null && roomAreas.size() > 0) {
                insertLocationList(count, roomAreas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 插入家庭/公司
     *
     * @param item
     */
    public synchronized long insertCloudHomeCompany(HomeCompanyBean item) {
        long count = 0;
        checkIfDBIsOk();
        checkHomeCompanyTable();
        try {
            String tableName = DBConfig.TABLE_HOME_COMPANY;
            ContentValues contentValues = new ContentValues();
            //contentValues.put("h_id", item.getLocalId());
            contentValues.put("name", item.getName());
            contentValues.put("sa_user_token", item.getSa_user_token());
            contentValues.put("sa_lan_address", item.getSa_lan_address());
            contentValues.put("sa_user_id", item.getUser_id());
            contentValues.put("user_id", item.getUser_id());
            contentValues.put("is_bind_sa", item.isIs_bind_sa());
            contentValues.put("cloud_id", item.getId());
            contentValues.put("cloud_user_id", item.getCloud_user_id());
            contentValues.put("area_id", item.getArea_id());
            contentValues.put("sa_id", item.getSa_id());
            contentValues.put("area_type", item.getArea_type());
            //使用replace，不使用insert，为了实现存在就更新，不存在就插入
            count = db.replace(tableName, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 插入家庭、公司列表
     *
     * @param itemList
     * @return
     */
    public void insertHomeCompanyList(List<HomeCompanyBean> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            return;
        }
        if (db == null || !db.isOpen()) openDb();
        checkIfDBIsOk();
        checkLocationTable();

        String tableName = DBConfig.TABLE_HOME_COMPANY;
        db.beginTransaction();
        try {
            for (HomeCompanyBean item : itemList) {
                ContentValues contentValues = new ContentValues();
                //contentValues.put("h_id", item.getLocalId());
                contentValues.put("name", item.getName());
                contentValues.put("sa_user_token", item.getSa_user_token());
                contentValues.put("sa_lan_address", item.getSa_lan_address());
                contentValues.put("user_id", item.getUser_id());
                contentValues.put("is_bind_sa", item.isIs_bind_sa());
                contentValues.put("cloud_id", item.getId());
                contentValues.put("cloud_user_id", item.getCloud_user_id());
                db.replace(tableName, null, contentValues);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public UserInfoBean getUser() {
        checkUserInfoTable();
        Cursor cursor = null;
        try {
            String sql = "select * from " + DBConfig.TABLE_USER_INFO + " limit 1";
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                UserInfoBean userInfoBean = new UserInfoBean();
                userInfoBean.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                userInfoBean.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
                userInfoBean.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                userInfoBean.setIconUrl(cursor.getString(cursor.getColumnIndex("icon_url")));
                return userInfoBean;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 创建用户
     *
     * @param userInfoBean
     * @return
     */
    public long insertUser(UserInfoBean userInfoBean) {
        checkUserInfoTable();
        long count = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_id", userInfoBean.getUserId());
            contentValues.put("nickname", userInfoBean.getNickname());
            if (!TextUtils.isEmpty(userInfoBean.getPhone()))
                contentValues.put("phone", userInfoBean.getPhone());
            if (!TextUtils.isEmpty(userInfoBean.getIconUrl()))
                contentValues.put("icon_url", userInfoBean.getIconUrl());
            String tableName = DBConfig.TABLE_USER_INFO;
            //使用replace，不使用insert，为了实现存在就更新，不存在就插入
            count = db.replace(tableName, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 修改昵称
     *
     * @param userId
     * @param nickname
     * @return
     */
    public long updateUser(int userId, String nickname, String iconUrl) {
        long count = 0;
        try {
            ContentValues contentValues = new ContentValues();
            if (!TextUtils.isEmpty(nickname))
                contentValues.put("nickname", nickname);
            if (!TextUtils.isEmpty(iconUrl))
                contentValues.put("icon_url", iconUrl);
            count = db.update(DBConfig.TABLE_USER_INFO, contentValues, "user_id=?", new String[]{String.valueOf(userId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 检查房间表是否存在，不存在则建新表
     * r_id 主键 自增
     * name 名称
     * area_id 区域id
     * sort 排序
     * l_id 房间id（用于区别本地和服务器插入
     * user_count 人员数量
     */
    public void checkLocationTable() {
        try {
            String createLocationTableSqlStr = "create table if not exists " + DBConfig.TABLE_LOCATION
                    + "(r_id integer primary key AUTOINCREMENT,"
                    + "name text, sa_user_token text, area_id integer, sort integer, l_id integer, user_count integer)";
            db.execSQL(createLocationTableSqlStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建房间
     *
     * @param locationBean
     * @return
     */
    public long insertLocation(LocationBean locationBean) {
        checkIfDBIsOk();
        checkLocationTable();
        long count = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", locationBean.getName());
            contentValues.put("sort", locationBean.getSort());
            contentValues.put("area_id", locationBean.getArea_id());
            contentValues.put("sa_user_token", locationBean.getSa_user_token());
            contentValues.put("l_id", locationBean.getLocationId());
            contentValues.put("user_count", locationBean.getUser_count());
            String tableName = DBConfig.TABLE_LOCATION;
            //使用replace，不使用insert，为了实现存在就更新，不存在就插入
            count = db.replace(tableName, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 插入多个房间区域
     *
     * @param id
     * @param itemList
     */
    public void insertLocationList(long id, List<LocationBean> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            return;
        }

        checkIfDBIsOk();
        checkLocationTable();

        String tableName = DBConfig.TABLE_LOCATION;
        db.beginTransaction();
        try {
            for (LocationBean item : itemList) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", item.getName());
                contentValues.put("sort", item.getSort());
                contentValues.put("area_id", id);
                contentValues.put("sa_user_token", item.getSa_user_token());
                contentValues.put("l_id", item.getLocationId());
                contentValues.put("user_count", item.getUser_count());
                db.replace(tableName, null, contentValues);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 更新
     *
     * @param id
     * @return
     */
    public long updateLocationList(long id, List<LocationBean> itemList) {
        long count = 0;
        try {
            for (LocationBean item : itemList) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", item.getName());
                contentValues.put("sort", item.getSort());
                contentValues.put("sa_user_token", item.getSa_user_token());
                contentValues.put("user_count", item.getUser_count());
                db.update(DBConfig.TABLE_LOCATION, contentValues, "r_id=?", new String[]{String.valueOf(item.getId())});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 修改房间/区域名称
     *
     * @param id
     * @param name
     * @return
     */
    public int updateLocation(long hId, int id, String name) {
        int count = 0;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            count = db.update(DBConfig.TABLE_LOCATION, contentValues, "r_id=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 删除房间区域
     */
    public int removeLocation(int rId) {
        checkIfDBIsOk();
        checkLocationTable();
        int count = db.delete(DBConfig.TABLE_LOCATION, "r_id=?", new String[]{String.valueOf(rId)});
        return count;
    }

    /**
     * 删除房间区域
     */
    public int removeLocationByHId(long hid, int lId) {
        checkIfDBIsOk();
        checkLocationTable();
//        int count = db.delete(DBConfig.TABLE_LOCATION, "l_id=? and area_id=?", new String[]{String.valueOf(lId), String.valueOf(lId)});
        int count = db.delete(DBConfig.TABLE_LOCATION, "area_id=?", new String[]{String.valueOf(hid)});
        return count;
    }

    /**
     * 通过saToken删除房间区域
     */
    public int removeLocationBySaToken(String saToken) {
        int count = db.delete(DBConfig.TABLE_LOCATION, "sa_user_token=?", new String[]{saToken});
        return count;
    }

    /**
     * 查询房间/区域列表
     *
     * @return
     */
    public List<LocationBean> queryLocationList(long id) {
        checkIfDBIsOk();
        checkLocationTable();
        List<LocationBean> roomAreaList = new ArrayList<>();
        Cursor cursor = null;
        String sql;
        if (Constant.CurrentHome != null && !TextUtils.isEmpty(Constant.CurrentHome.getSa_user_token())) {
            sql = "select * from " + DBConfig.TABLE_LOCATION + " where area_id = " + id + " and sa_user_token = \"" + Constant.CurrentHome.getSa_user_token() + "\" order by sort asc ";
        } else {
            sql = "select * from " + DBConfig.TABLE_LOCATION + " where area_id = " + id + " order by sort asc ";
        }
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    LocationBean item = new LocationBean();
                    item.setId(getIntFromCursor(cursor, "r_id"));
                    item.setName(getStringFromCursor(cursor, "name"));
                    item.setArea_id(getLongFromCursor(cursor, "area_id"));
                    item.setSa_user_token(getStringFromCursor(cursor, "sa_user_token"));
                    item.setSort(getIntFromCursor(cursor, "sort"));
                    item.setUser_count(getIntFromCursor(cursor, "user_count"));
                    roomAreaList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomAreaList;
    }


    /**
     * 查询房间/区域列表
     *
     * @return
     */
    public List<LocationBean> queryLocations(long id) {
        checkIfDBIsOk();
        checkLocationTable();
        List<LocationBean> roomAreaList = new ArrayList<>();
        Cursor cursor = null;
        String sql = "select * from " + DBConfig.TABLE_LOCATION + " where area_id = " + id + " order by sort asc ";
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    LocationBean item = new LocationBean();
                    item.setId(getIntFromCursor(cursor, "r_id"));
                    item.setName(getStringFromCursor(cursor, "name"));
                    item.setArea_id(getLongFromCursor(cursor, "area_id"));
                    item.setSa_user_token(getStringFromCursor(cursor, "sa_user_token"));
                    item.setSort(getIntFromCursor(cursor, "sort"));
                    item.setUser_count(getIntFromCursor(cursor, "user_count"));
                    roomAreaList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomAreaList;
    }

    /**
     * 根据saToken查询房间/区域列表
     *
     * @return
     */
    public List<LocationBean> queryLocationListBySaToken(String saToken) {
        checkIfDBIsOk();
        checkLocationTable();
        List<LocationBean> roomAreaList = new ArrayList<>();
        Cursor cursor = null;
        String sql = "select * from " + DBConfig.TABLE_LOCATION + " where sa_user_token = '" + saToken + "' order by sort asc ";
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    LocationBean item = new LocationBean();
                    item.setId(getIntFromCursor(cursor, "r_id"));
                    item.setLocationId(getIntFromCursor(cursor, "l_id"));
                    item.setName(getStringFromCursor(cursor, "name"));
                    item.setArea_id(getLongFromCursor(cursor, "area_id"));
                    item.setSa_user_token(getStringFromCursor(cursor, "sa_user_token"));
                    item.setSort(getIntFromCursor(cursor, "sort"));
                    item.setUser_count(getIntFromCursor(cursor, "user_count"));
                    roomAreaList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomAreaList;
    }

    /************************ 设备 ******************************/
    /**
     * d_id 设备id
     * name 设备名称
     * sa_user_token satoken
     * area_id 家庭id
     * l_id  房间id
     * type 设备类型（eg: 开关：switch，灯：light）
     * logo_url 设备logo
     * plugin_id 设备关联插件Id
     * is_sa 是否SA设备
     * brand_id 品牌id
     * identity 设备唯一值
     */
    public void checkDeviceTable() {
        try {
            String createDeviceTableSqlStr = "create table if not exists " + DBConfig.TABLE_DEVICE
                    + "(d_id integer,"
                    + "name text, sa_user_token text, area_id integer, l_id integer, type text, logo_url text, plugin_id text, is_sa bool, brand_id text, identity text, control text)";
            db.execSQL(createDeviceTableSqlStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入多个设备
     *
     * @param itemList
     */
    public void insertDeviceList(List<DeviceMultipleBean> itemList, String saToken, long areaId) {
        if (itemList == null || itemList.isEmpty()) {
            return;
        }
        checkIfDBIsOk();
        checkDeviceTable();

        String tableName = DBConfig.TABLE_DEVICE;
        db.beginTransaction();
        try {
            for (DeviceMultipleBean item : itemList) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("d_id", item.getId());
                contentValues.put("name", item.getName());
                contentValues.put("sa_user_token", saToken);
                contentValues.put("area_id", areaId);
                contentValues.put("l_id", Constant.AREA_TYPE == 2 ? item.getDepartment_id() : item.getLocation_id());
                contentValues.put("logo_url", item.getLogo_url());
                contentValues.put("type", item.getType());
                contentValues.put("plugin_id", item.getPlugin_id());
                contentValues.put("identity", item.getIdentity());
                contentValues.put("is_sa", item.isIs_sa());
                contentValues.put("control", item.getControl());
                db.replace(tableName, null, contentValues);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 根据saToken查询房间/区域列表
     *
     * @return
     */
    public List<DeviceMultipleBean> queryDeviceListByAreaId(long areaId) {
        checkIfDBIsOk();
        checkDeviceTable();
        List<DeviceMultipleBean> deviceMultipleBeans = new ArrayList<>();
        Cursor cursor = null;
        String sql = "select * from " + DBConfig.TABLE_DEVICE + " where area_id = ?";

        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(areaId)});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    DeviceMultipleBean item = new DeviceMultipleBean();
                    item.setId(getIntFromCursor(cursor, "d_id"));
                    item.setLocation_id(getIntFromCursor(cursor, "l_id"));
                    item.setDepartment_id(getIntFromCursor(cursor, "l_id"));
                    item.setName(getStringFromCursor(cursor, "name"));
                    item.setArea_id(getIntFromCursor(cursor, "area_id"));
                    item.setSa_user_token(getStringFromCursor(cursor, "sa_user_token"));
                    item.setLogo_url(getStringFromCursor(cursor, "logo_url"));
                    item.setIdentity(getStringFromCursor(cursor, "identity"));
                    item.setPlugin_id(getStringFromCursor(cursor, "plugin_id"));
                    item.setIs_sa(getBooleanFromCursor(cursor, "is_sa"));
                    item.setControl(getStringFromCursor(cursor, "control"));
                    deviceMultipleBeans.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceMultipleBeans;
    }

    /**
     * 根据saToken查询房间/区域列表
     *
     * @return
     */
    public List<DeviceMultipleBean> queryDeviceListBySaToken(String saToken) {
        checkIfDBIsOk();
        checkDeviceTable();
        List<DeviceMultipleBean> deviceMultipleBeans = new ArrayList<>();
        Cursor cursor = null;
        String sql = "select * from " + DBConfig.TABLE_DEVICE + " where sa_user_token = ?";

        try {
            cursor = db.rawQuery(sql, new String[]{saToken});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    DeviceMultipleBean item = new DeviceMultipleBean();
                    item.setId(getIntFromCursor(cursor, "d_id"));
                    item.setLocation_id(getIntFromCursor(cursor, "l_id"));
                    item.setDepartment_id(getIntFromCursor(cursor, "l_id"));
                    item.setName(getStringFromCursor(cursor, "name"));
                    item.setArea_id(getIntFromCursor(cursor, "area_id"));
                    item.setSa_user_token(getStringFromCursor(cursor, "sa_user_token"));
                    item.setLogo_url(getStringFromCursor(cursor, "logo_url"));
                    item.setIdentity(getStringFromCursor(cursor, "identity"));
                    item.setPlugin_id(getStringFromCursor(cursor, "plugin_id"));
                    item.setIs_sa(getBooleanFromCursor(cursor, "is_sa"));
                    deviceMultipleBeans.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceMultipleBeans;
    }

    /**
     * 通过saToken删除设备
     */
    public int removeDeviceBySaToken(String saToken) {
        int count = 0;
        if (tableIsExist(DBConfig.TABLE_DEVICE)) {
            count = db.delete(DBConfig.TABLE_DEVICE, "sa_user_token=?", new String[]{saToken});
        }
        return count;
    }

    /**
     * 通过saToken删除设备
     */
    public int removeDeviceByAreaId(long areaId) {
        int count = 0;
        if (tableIsExist(DBConfig.TABLE_DEVICE)) {
            count = db.delete(DBConfig.TABLE_DEVICE, "area_id=?", new String[]{String.valueOf(areaId)});
        }
        return count;
    }

    private void quiteClose(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public boolean isOpen() {
        if (db != null || db.isOpen()) {
            return true;
        }
        return false;
    }

    public boolean tableIsExist(String tableName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        Cursor cursor = null;
        try {
            //这里表名可以是Sqlite_master
            String sql = "select count(*) as c from " + DBConfig.DataBaseName + " where type ='table' and name ='" + tableName.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
        return result;
    }

    /**
     * 检查场景表是否存在，不存在则建新表
     */
    private void checkSceneTable() {
        try {
            String createUserInfoTableSqlStr = "create table if not exists " + DBConfig.TABLE_SCENE
                    + "(sa_user_token text unique,"
                    + "scene text)";
            db.execSQL(createUserInfoTableSqlStr);
        } catch (Exception e) {

        }
    }

    /**
     * 插入场景
     *
     * @param saUserToken
     * @param scene
     * @return
     */
    public long insertScene(String saUserToken, String scene) {
        long count = 0;
        checkIfDBIsOk();
        checkSceneTable();
        try {
            String tableName = DBConfig.TABLE_SCENE;
            ContentValues contentValues = new ContentValues();
            contentValues.put("sa_user_token", saUserToken);
            contentValues.put("scene", scene);
            //使用replace，不使用insert，为了实现存在就更新，不存在就插入
            count = db.replace(tableName, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 更新场景
     *
     * @param saUserToken
     * @param scene
     * @return
     */
    public long updateScene(String saUserToken, String scene) {
        long count = 0;
        checkIfDBIsOk();
        checkHomeCompanyTable();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("scene", scene);
            count = db.update(DBConfig.TABLE_SCENE, contentValues, "sa_user_token=?", new String[]{saUserToken});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 查找场景
     *
     * @return
     */
    public String getScene(String saUserToken) {
        checkIfDBIsOk();
        checkSceneTable();
        String scene = "";
        Cursor cursor = null;
        try {
            String sql = "select * from " + DBConfig.TABLE_SCENE + " where sa_user_token = \"" + saUserToken + "\"";
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                scene = cursor.getString(cursor.getColumnIndex("scene"));
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return scene;
    }
}
