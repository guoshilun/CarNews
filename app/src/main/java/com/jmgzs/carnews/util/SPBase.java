package com.jmgzs.carnews.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.jmgzs.carnews.base.App;

import java.util.Set;

public class SPBase {

    private volatile static SharedPreferences mSP;

    protected static int INIT_METHOD_1 = 1;
    protected static int INIT_METHOD_2 = 2;

    protected static int INIT_METHOD_DEFAULT = INIT_METHOD_1;

    protected static final String USER_PREFERENCE_FILE_NAME = "user_preferences";

    protected SPBase() {
    }

    protected static void initDefault(Context context) {
        if (INIT_METHOD_DEFAULT == INIT_METHOD_1) {
            initSP(context);
        } else {
            initSP(context, USER_PREFERENCE_FILE_NAME);
        }
    }

    protected static void initSP(Context context) {
        initSP(context, context.getPackageName());
    }

    protected static void initSP(Context context, String preferenceName) {
        if (mSP == null) {
            synchronized (SPBase.class) {
                if (mSP == null) {
                    mSP = context.getApplicationContext().getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
                }
            }
        }
    }

    public static SharedPreferences getSharedPreferences() {
        if (mSP == null) {
            initDefault(App.getInstance());
        }

        return mSP;
    }

//    private static void initValidate() {
//        if (mSP == null) {
//            throw new ExceptionInInitializerError("必须在Application中调用init方法，初始化后才能使用！");
//        }
//    }

    public static String getString(String key, String defValue) {
//		initValidate();

        if (key == null) {
            return defValue;
        }

        return getSharedPreferences().getString(key, defValue);
    }

    public static boolean putString(String key, String value) {
//		initValidate();

        if (key == null) {
            return false;
        }

        return getSharedPreferences().edit().putString(key, value).commit();
    }

    @SuppressLint("NewApi")
    public static Set<String> getStringSet(String key, Set<String> defValue) {
//		initValidate();

        if (key == null) {
            return defValue;
        }
        return getSharedPreferences().getStringSet(key, defValue);
    }

    @SuppressLint("NewApi")
    public static boolean putStringSet(String key, Set<String> value) {
//		initValidate();

        if (key == null) {
            return false;
        }

        return getSharedPreferences().edit().putStringSet(key, value).commit();
    }

    public static int getInt(String key, int defValue) {
//		initValidate();

        if (key == null) {
            return defValue;
        }

        return getSharedPreferences().getInt(key, defValue);
    }

    public static boolean putInt(String key, int value) {
//		initValidate();

        if (key == null) {
            return false;
        }

        return getSharedPreferences().edit().putInt(key, value).commit();
    }

    public static long getLong(String key, long defValue) {
//		initValidate();

        if (key == null) {
            return defValue;
        }

        return getSharedPreferences().getLong(key, defValue);
    }

    public static boolean putLong(String key, long value) {
//		initValidate();

        if (key == null) {
            return false;
        }

        return getSharedPreferences().edit().putLong(key, value).commit();
    }

    public static float getFloat(String key, float defValue) {
//		initValidate();

        if (key == null) {
            return defValue;
        }

        return getSharedPreferences().getFloat(key, defValue);
    }

    public static boolean putFloat(String key, float value) {
//		initValidate();

        if (key == null) {
            return false;
        }

        return getSharedPreferences().edit().putFloat(key, value).commit();
    }

    public static double getDouble(String key, double defValue) {
//		initValidate();

        if (key == null) {
            return defValue;
        }

        try {
            return Double.parseDouble(getSharedPreferences().getString(key, String.valueOf(defValue)));
        } catch (Exception e) {
            return defValue;
        }
    }

    public static boolean putDouble(String key, double value) {
        return putString(key, String.valueOf(value));
    }

    public static boolean getBoolean(String key, boolean defValue) {
//		initValidate();

        if (key == null) {
            return defValue;
        }

        return getSharedPreferences().getBoolean(key, defValue);
    }

    public static boolean putBoolean(String key, boolean value) {
//		initValidate();

        if (key == null) {
            return false;
        }

        return getSharedPreferences().edit().putBoolean(key, value).commit();
    }

    public static boolean remove(String key) {
//		initValidate();

        if (key == null) {
            return false;
        }

        return getSharedPreferences().edit().remove(key).commit();
    }

    public static boolean clear() {
//		initValidate();

        return getSharedPreferences().edit().clear().commit();
    }

}
