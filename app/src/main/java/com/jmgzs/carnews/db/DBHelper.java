package com.jmgzs.carnews.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.bean.Photo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mac on 17/6/14.
 * Description:
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB = "car_news.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NEWS = "news_store";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_AID = "aid";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_SUMMARY = "summary";
    private static final String COLUMN_SOURCE = "source";
    private static final String COLUMN_PUBLISH_TIME = "publish_time";
    private static final String COLUMN_IMAGES = "image_list";
    private static final String COLUMN_TIME = "create_time";

    private static final String CREATE_TABLE = "create table if not exists " + TABLE_NEWS + "("
            + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_AID + " integer not null ,"
            + COLUMN_TITLE + " VARCHAR not null,"
            + COLUMN_SUMMARY + " VARCHAR not null,"
            + COLUMN_SOURCE + " varchar not null,"
            + COLUMN_PUBLISH_TIME + " varchar not null,"
            + COLUMN_IMAGES + " varchar not null,"
            + COLUMN_TIME + " timestamp not null default('now','localtime'))";

    private static volatile DBHelper instance;

    public static DBHelper getInstance(Context ct) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper(ct);
                }
            }
        }
        return instance;
    }

    private DBHelper(Context ct) {
        super(ct, DB, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table is exists " + TABLE_NEWS);
        onCreate(sqLiteDatabase);
    }

////////////////////////////////////////////
    /**
     * 删除收藏
     *
     * @param aid
     * @return
     */
    public synchronized boolean deleteNews(String aid) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null && aid != null && aid.length() > 0) {
//            String delSql = "delete from "+TABLE_NEWS+" where aid = "+aid;
            try {
                int row = db.delete(TABLE_NEWS, "aid=?", new String[]{aid});
                return row > 0;
            } finally {
                db.close();
            }
        }
        return false;
    }

    /**
     * 清空收藏
     *
     * @return
     */
    public synchronized boolean deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            try {
                int row = db.delete(TABLE_NEWS, null, null);
                return row > 0;
            } finally {
                db.close();
            }
        }
        return false;
    }

    /**
     * 新增收藏
     *
     * @param dataBean
     * @return
     */
    public boolean addNews(NewsDataBean dataBean) {
        List<NewsDataBean> list = Collections.singletonList(dataBean);
        return addNews(list);
    }

    /**
     * 新增收藏
     *
     * @param list
     * @return
     */

    public synchronized boolean addNews(List<NewsDataBean> list) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null && list != null && list.size() > 0) {
            try {
                db.beginTransaction();
                for (NewsDataBean info : list) {
                    ContentValues cv = new ContentValues();
                    cv.put(COLUMN_AID, info.getAid());
                    cv.put(COLUMN_IMAGES, arrayToStr(info.getImg_list()));
                    cv.put(COLUMN_PUBLISH_TIME, info.getPublish_time());
                    cv.put(COLUMN_SOURCE, info.getPublish_source());
                    cv.put(COLUMN_TITLE, info.getTitle());
                    cv.put(COLUMN_SUMMARY, info.getAbstr());
                    db.insert(TABLE_NEWS, null, cv);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                return true;
            } finally {
                db.close();
            }
        }
        return false;
    }

    /**
     * 查询新闻
     *
     * @return
     */
    public ArrayList<NewsDataBean> queryNews() {
        SQLiteDatabase db = getReadableDatabase();

        if (db == null) {
            return null;
        }

        Cursor cr = db.query(true, TABLE_NEWS, null, null, null, null, null, "id desc", null);
        if (cr == null) return null;
        try {
            ArrayList<NewsDataBean> list = new ArrayList<>();
            for (cr.moveToFirst(); !cr.isAfterLast(); cr.moveToNext()) {
                NewsDataBean bean = new NewsDataBean();
                bean.setAid(cr.getInt(cr.getColumnIndex(COLUMN_AID)));
                bean.setPublish_source(cr.getString(cr.getColumnIndex(COLUMN_SOURCE)));
                bean.setPublish_time(cr.getString(cr.getColumnIndex(COLUMN_PUBLISH_TIME)));
                bean.setAbstr(cr.getString(cr.getColumnIndex(COLUMN_TITLE)));
                bean.setTitle(cr.getString(cr.getColumnIndex(COLUMN_TITLE)));

                String images = cr.getString(cr.getColumnIndex(COLUMN_IMAGES));
                ArrayList photos = strToArray(images);
                bean.setImg_list(photos);
                list.add(bean);
            }
        } finally {
            cr.close();
            db.close();
        }
        return null;
    }

    /**
     * 检测是否已经收藏过
     * @param aid
     * @return
     */
    public boolean checkNews(String aid) {
        SQLiteDatabase db = getReadableDatabase();
        if (db != null && aid != null && aid.length() > 0) {

            Cursor cr = db.query(TABLE_NEWS, new String[]{COLUMN_AID}, "aid = ?", new String[]{aid}, null, null, null);
            try {
                return cr != null && cr.getCount() > 0;
            } finally {
                if (cr != null) cr.close();
            }
        }
        return false;
    }

    private ArrayList<Photo> strToArray(String str) {
        ArrayList<Photo> photos = new ArrayList<>();
        //TODO
        return null;
    }

    private String arrayToStr(ArrayList list) {
        return list == null || list.size() == 0 ? "" : list.toString().replace("[", "").replace("]", "");
    }
}
