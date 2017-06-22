package com.jmgzs.carnews.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.jmgzs.carnews.bean.NewsDataBean;
import com.jmgzs.carnews.bean.Photo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.R.attr.id;
import static com.umeng.message.proguard.k.C;

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
    private static final String COLUMN_DEL = "del";

    private static final String CREATE_TABLE = "create table if not exists " + TABLE_NEWS + "("
            + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_AID + " integer not null ,"
            + COLUMN_TITLE + " VARCHAR not null,"
            + COLUMN_SUMMARY + " VARCHAR not null,"
            + COLUMN_SOURCE + " varchar not null,"
            + COLUMN_PUBLISH_TIME + " varchar not null,"
            + COLUMN_IMAGES + " varchar not null,"
            + COLUMN_DEL + " integer default 0,"
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
            try {
                ContentValues cv = new ContentValues();
                cv.put(COLUMN_DEL, 1);
                int row = db.update(TABLE_NEWS, cv, COLUMN_AID + "=?", new String[]{aid});
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

       Cursor cr = db.query(true, TABLE_NEWS, null, COLUMN_DEL+"=?", new String[]{"0"}, null, null, COLUMN_ID+" desc", null);
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
                bean.setImg_list(strToArray(images));
                list.add(bean);
            }
        } finally {
            cr.close();
            db.close();
        }
        return null;
    }

    /**
     * 检测是否已经收藏过,收藏过转换del=0,否则新增一条数据
     *
     * @param info 新闻
     * @return
     */
    public boolean checkNews(NewsDataBean info) {
        int aid = info.getAid();
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cr = db.query(TABLE_NEWS, new String[]{COLUMN_AID}, COLUMN_AID + "=?",
                    new String[]{String.valueOf(aid)}, null, null, null);
            try {
                if (cr != null && cr.getCount() > 0) {
                    cr.moveToFirst();
                    int del = cr.getInt(cr.getColumnIndex(COLUMN_DEL));
                    if (del == 0) return true; //已收藏过
                    else {
                        ContentValues cv = new ContentValues();
                        cv.put(COLUMN_DEL, 0);
                        del = db.update(TABLE_NEWS, cv, COLUMN_AID + "=?", new String[]{String.valueOf(aid)});
                        return del > 0;
                    }
                } else {
                    return addNews(info);
                }
            } finally {
                if (cr != null) cr.close();
            }
        }
        return false;
    }

    private List<String> strToArray(String str) {
        if (TextUtils.isEmpty(str)) return null;
        String[] array = str.split(",");
        return Arrays.asList(array);
    }

    private String arrayToStr(List list) {
        return list == null || list.size() == 0 ? "" : list.toString().replace("[", "").replace("]", "");
    }
}
