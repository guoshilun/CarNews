package com.jmgzs.carnews.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * 日志记录工具类
 * 
 * @filename : L.java
 * @author : zhaotun
 * @description :
 * @version : 1.0
 */
public class L {

	public static final int LOG_LEVEL_SYSO = 6;
	public static final int LOG_LEVEL_VERBOSE = 5;
	public static final int LOG_LEVEL_DEBUG = 4;
	public static final int LOG_LEVEL_INFO = 3;
	public static final int LOG_LEVEL_WARN = 2;
	public static final int LOG_LEVEL_ERROR = 1;
	public static final int LOG_LEVEL_FATAL = 0;
	public static final int LOG_LEVEL_DEFAULT_RETURN = -1;

	private static int CURRENT_LOG_LEVEL = LOG_LEVEL_SYSO; // 屏蔽log,修改为0

	private static final String TAG = L.class.getSimpleName();

	private static String DEFAULT_TAG = TAG;

	private L() {
	}

	public static void setTag(String tag) {
		if (!TextUtils.isEmpty(tag)) {
			DEFAULT_TAG = tag;
		} else {
			DEFAULT_TAG = TAG;
		}
	}

	public static String getTag() {
		return DEFAULT_TAG;
	}

	/**
	 * set log level
	 * 
	 * @param level
	 */
	public static void setLogLevel(int level) {
		CURRENT_LOG_LEVEL = level;
	}

	/**
	 * 错误级别：Verbose
	 * 
	 * @param msg
	 * @return
	 */
	public static int v(String msg) {
		return v(getTag(), msg);
	}

	/**
	 * 错误级别：Verbose
	 * 
	 * @param tag
	 * @param msg
	 * @return
	 */
	public static int v(String tag, String msg) {
		return v(tag, msg, (Throwable) null);
	}

	/**
	 * 错误级别：Verbose
	 * 
	 * @param msg
	 * @param e
	 * @return
	 */
	public static int v(String msg, Throwable e) {
		return v(getTag(), msg, e);
	}

	/**
	 * 错误级别：Verbose
	 * 
	 * @param tag
	 * @param msg
	 * @param e
	 * @return
	 */
	public static int v(String tag, String msg, Throwable e) {
		if (CURRENT_LOG_LEVEL >= LOG_LEVEL_VERBOSE && !TextUtils.isEmpty(tag)) {
			return Log.v(tag, msg, e);
		} else {
			return LOG_LEVEL_DEFAULT_RETURN;
		}
	}

	/**
	 * Send a Verbose log message
	 * 
	 * @param tag
	 * @param msg
	 * @param args
	 * @return
	 */
	public static int v(String tag, String msg, Object... args) {
		if (CURRENT_LOG_LEVEL >= LOG_LEVEL_VERBOSE) {
			if (args != null && args.length > 0) {
				msg = String.format(msg, args);
			}

			return v(tag, msg);
		} else {
			return LOG_LEVEL_DEFAULT_RETURN;
		}
	}

	/**
	 * 错误级别：Debug
	 * 
	 * @param msg
	 * @return
	 */
	public static int d(String msg) {
		return d(getTag(), msg);
	}

	/**
	 * 错误级别：Debug
	 * 
	 * @param tag
	 * @param msg
	 * @return
	 */
	public static int d(String tag, String msg) {
		return d(tag, msg, (Throwable) null);
	}

	/**
	 * 错误级别：Debug
	 * 
	 * @param msg
	 * @param e
	 * @return
	 */
	public static int d(String msg, Throwable e) {
		return d(getTag(), msg, e);
	}

	/**
	 * 错误级别：Debug
	 * 
	 * @param tag
	 * @param msg
	 * @param e
	 * @return
	 */
	public static int d(String tag, String msg, Throwable e) {
		if (CURRENT_LOG_LEVEL >= LOG_LEVEL_DEBUG && !TextUtils.isEmpty(tag)) {
			return Log.d(tag, msg, e);
		} else {
			return LOG_LEVEL_DEFAULT_RETURN;
		}
	}

	/**
	 * Send a Debug log message
	 * 
	 * @param tag
	 * @param msg
	 * @param args
	 * @return
	 */
	public static int d(String tag, String msg, Object... args) {
		if (CURRENT_LOG_LEVEL >= LOG_LEVEL_DEBUG) {
			if (args != null && args.length > 0) {
				msg = String.format(msg, args);
			}

			return d(tag, msg);
		} else {
			return LOG_LEVEL_DEFAULT_RETURN;
		}
	}

	/**
	 * 错误级别：Info
	 * 
	 * @param msg
	 * @return
	 */
	public static int i(String msg) {
		return i(getTag(), msg);
	}

	/**
	 * 错误级别：Info
	 * 
	 * @param tag
	 * @param msg
	 * @return
	 */
	public static int i(String tag, String msg) {
		return i(tag, msg, (Throwable) null);
	}

	/**
	 * 错误级别：Info
	 * 
	 * @param msg
	 * @param e
	 * @return
	 */
	public static int i(String msg, Throwable e) {
		return i(getTag(), msg, e);
	}

	/**
	 * 错误级别：Info
	 * 
	 * @param tag
	 * @param msg
	 * @param e
	 * @return
	 */
	public static int i(String tag, String msg, Throwable e) {
		if (CURRENT_LOG_LEVEL >= LOG_LEVEL_INFO && !TextUtils.isEmpty(tag)) {
			return Log.i(tag, msg, e);
		} else {
			return LOG_LEVEL_DEFAULT_RETURN;
		}
	}

	/**
	 * Send a Info log message
	 * 
	 * @param tag
	 * @param msg
	 * @param args
	 * @return
	 */
	public static int i(String tag, String msg, Object... args) {
		if (CURRENT_LOG_LEVEL >= LOG_LEVEL_INFO) {
			if (args != null && args.length > 0) {
				msg = String.format(msg, args);
			}

			return i(tag, msg);
		} else {
			return LOG_LEVEL_DEFAULT_RETURN;
		}
	}

	/**
	 * 错误级别：Warn
	 * 
	 * @param msg
	 * @return
	 */
	public static int w(String msg) {
		return w(getTag(), msg);
	}

	/**
	 * 错误级别：Warn
	 * 
	 * @param tag
	 * @param msg
	 * @return
	 */
	public static int w(String tag, String msg) {
		return w(tag, msg, (Throwable) null);
	}

	/**
	 * 错误级别：Warn
	 * 
	 * @param tag
	 * @param e
	 * @return
	 */
	public static int w(String tag, Throwable e) {
		if (CURRENT_LOG_LEVEL >= LOG_LEVEL_WARN && !TextUtils.isEmpty(tag)) {
			return Log.w(tag, e);
		} else {
			return LOG_LEVEL_DEFAULT_RETURN;
		}
	}

	/**
	 * 错误级别：Warn
	 * 
	 * @param tag
	 * @param msg
	 * @param e
	 * @return
	 */
	public static int w(String tag, String msg, Throwable e) {
		if (CURRENT_LOG_LEVEL >= LOG_LEVEL_WARN && !TextUtils.isEmpty(tag)) {
			return Log.w(tag, msg, e);
		} else {
			return LOG_LEVEL_DEFAULT_RETURN;
		}
	}

	/**
	 * Send a Warn log message
	 * 
	 * @param tag
	 * @param msg
	 * @param args
	 * @return
	 */
	public static int w(String tag, String msg, Object... args) {
		if (CURRENT_LOG_LEVEL >= LOG_LEVEL_WARN) {
			if (args != null && args.length > 0) {
				msg = String.format(msg, args);
			}

			return w(tag, msg);
		} else {
			return LOG_LEVEL_DEFAULT_RETURN;
		}
	}

	/**
	 * 错误级别：Error
	 * 
	 * @param msg
	 * @return
	 */
	public static int e(String msg) {
		return e(getTag(), msg);
	}

	/**
	 * 错误级别：Error
	 * 
	 * @param tag
	 * @param msg
	 * @return
	 */
	public static int e(String tag, String msg) {
		return e(tag, msg, (Throwable) null);
	}

	/**
	 * 错误级别：Error
	 * 
	 * @param msg
	 * @param e
	 * @return
	 */
	public static int e(String msg, Throwable e) {
		return e(getTag(), msg, e);
	}

	/**
	 * 错误级别：Error
	 * 
	 * @param tag
	 * @param msg
	 * @param e
	 * @return
	 */
	public static int e(String tag, String msg, Throwable e) {
		if (CURRENT_LOG_LEVEL >= LOG_LEVEL_ERROR && !TextUtils.isEmpty(tag)) {
			return Log.e(tag, msg, e);
		} else {
			return LOG_LEVEL_DEFAULT_RETURN;
		}
	}

	/**
	 * Send a Error log message
	 * 
	 * @param tag
	 * @param msg
	 * @param args
	 * @return
	 */
	public static int e(String tag, String msg, Object... args) {
		if (CURRENT_LOG_LEVEL >= LOG_LEVEL_ERROR) {
			if (args != null && args.length > 0) {
				msg = String.format(msg, args);
			}

			return e(tag, msg);
		} else {
			return LOG_LEVEL_DEFAULT_RETURN;
		}
	}

	/**
	 * Send a Fatal Error log message
	 * 
	 * @param msg
	 * @return
	 */
	public static int f(String msg) {
		return f(getTag(), msg);
	}

	/**
	 * Send a Fatal Error log message
	 * 
	 * @param tag
	 * @param msg
	 * @return
	 */
	public static int f(String tag, String msg) {
		if (CURRENT_LOG_LEVEL >= LOG_LEVEL_FATAL && !TextUtils.isEmpty(tag)) {
			return Log.wtf(tag, msg);
		} else {
			return LOG_LEVEL_DEFAULT_RETURN;
		}
	}

	/**
	 * Send a Fatal Error log message
	 * 
	 * @param tag
	 * @param e
	 * @return
	 */
	public static int f(String tag, Throwable e) {
		if (CURRENT_LOG_LEVEL >= LOG_LEVEL_FATAL && !TextUtils.isEmpty(tag)) {
			return Log.wtf(tag, e);
		} else {
			return LOG_LEVEL_DEFAULT_RETURN;
		}
	}

	/**
	 * Send a Fatal Error log message
	 * 
	 * @param tag
	 * @param msg
	 * @param e
	 * @return
	 */
	public static int f(String tag, String msg, Throwable e) {
		if (CURRENT_LOG_LEVEL >= LOG_LEVEL_FATAL && !TextUtils.isEmpty(tag)) {
			return Log.wtf(tag, msg, e);
		} else {
			return LOG_LEVEL_DEFAULT_RETURN;
		}
	}

	/**
	 * 打印信息标准输出
	 * 
	 * @param data
	 */
	public static void syso(String data) {
		if (CURRENT_LOG_LEVEL >= LOG_LEVEL_SYSO) {
			System.out.println(data);
		}
	}

	/**
	 * 打印log到文件中
	 * 
	 * @param logPath
	 *            日志文件路径
	 * @param logData
	 *            日志内容
	 * @param append
	 *            是否追加内容
	 */
	public static void wltf(String logPath, String logData, boolean append) {
		if (TextUtils.isEmpty(logPath) || TextUtils.isEmpty(logData)) {
			return;
		}

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(logPath, append));
			if (append) {
				writer.append(logData);
			} else {
				writer.write(logData);
			}
			writer.newLine();
		} catch (Throwable e) {
			e(getTag(), e);
		} finally {
			try {
				if (writer != null) {
					writer.flush();
					writer.close();
				}
			} catch (Throwable e2) {
				e(getTag(), e2);
			} finally {
				writer = null;
			}
		}
	}

}
