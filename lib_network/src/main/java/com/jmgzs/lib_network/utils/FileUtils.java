package com.jmgzs.lib_network.utils;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Wxl on 2017/6/9.
 */

public class FileUtils {

    public static String getAppBaseFile(Context context) {
        File sdCardPath;
        if (PermissionUtil.getInstance().isGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            sdCardPath = Environment.getExternalStorageDirectory();
            return sdCardPath.toString() + File.separator + "CarNews";
        }
        return context.getCacheDir().getPath();
    }

    private static final int BUFFER = 8192;


    // 读取文件
    public static String readTextFile(File file) throws IOException {
        String text = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            text = readTextInputStream(is);
            ;
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return text;
    }

    // 从流中读取文件
    public static String readTextInputStream(InputStream is) throws IOException {
        StringBuffer strbuffer = new StringBuffer();
        String line;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                strbuffer.append(line).append("\r\n");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return strbuffer.toString();
    }

    // 将文本内容写入文件
    public static void writeTextFile(File file, String str) throws IOException {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new FileOutputStream(file));
            out.write(str.getBytes());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    //复制文件夹
    public static void copyFileDir(File sourceFile, File targetFile)
            throws IOException {
        if (sourceFile.isDirectory() && targetFile.isDirectory()){
            File newDir = new File(targetFile, sourceFile.getName());
            if (!newDir.exists()){
                newDir.mkdirs();
            }
            File[] subFiles = sourceFile.listFiles();
            if (subFiles != null && subFiles.length > 0){
                for (File subFile : subFiles){
                    if (subFile != null && subFile.exists()){
                        if (subFile.isDirectory()){
                            copyFileDir(subFile, newDir);
                        }else{
                            File newFile = new File(newDir, subFile.getName());
                            if (!newFile.exists()){
                                newFile.createNewFile();
                            }
                            copyFile(subFile, newFile);
                        }
                    }
                }
            }
        }
    }
    // 复制文件
    public static void copyFile(File sourceFile, File targetFile)
            throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            byte[] buffer = new byte[BUFFER];
            int length;
            while ((length = inBuff.read(buffer)) != -1) {
                outBuff.write(buffer, 0, length);
            }
            outBuff.flush();
        } finally {
            if (inBuff != null) {
                inBuff.close();
            }
            if (outBuff != null) {
                outBuff.close();
            }
        }
    }

    // 创建文件夹
    public static void createDir(String dir) {
        File folderDir = new File(dir);
        if (!folderDir.exists()) {
            folderDir.mkdirs();
        }
    }

    // 创建文件
    public static File createFile(Context context, String dir, String fileName) {
        File folderDir = new File(dir);
        if (!folderDir.exists()) {
            folderDir.mkdirs();
        }
        File fileNew = new File(fileName);
        if (!fileNew.exists()) {
            try {
                fileNew.createNewFile();
            } catch (IOException e) {
                return null;
            }
        }
        return fileNew;
    }

    // 创建文件用于缓存
    public static File createFile(Context context, String cacheDir,
                                  String subDir, String fileName) {
        if (!isDirExist(context, cacheDir)) {
            File file = new File(getFilePath(context, cacheDir));
            file.mkdirs();
        }

        String cacheTempDir = getFilePath(context, cacheDir, subDir);
        File fileDir = new File(cacheTempDir);
        if (!fileDir.exists()) {
            File subFile = new File(cacheTempDir);
            subFile.mkdirs();
        }

        String fileTargetDir = cacheTempDir + fileName;
        File fileNew = new File(fileTargetDir);
        if (!fileNew.exists()) {
            try {
                fileNew.createNewFile();
            } catch (IOException e) {
                return null;
            }
        }

        return fileNew;
    }

    public static String getCachePath(Context context) {
        return getFilePath(context, "cache");
    }

    // 获取文件的路径
    public static String getFilePath(Context context, String fileDir) {
        String filePath = getAppBaseFile(context) + File.separator + fileDir;
        return filePath;
    }

    //
    public static String getFilePath(Context context, String cacheDir,
                                     String subDir) {
        String baseDir = getAppBaseFile(context);
        if (!"".equals(baseDir)) {
            baseDir = baseDir + File.separator + cacheDir + File.separator
                    + subDir + File.separator;
        }
        return baseDir;
    }

    public static String getFileDirBasePath(Context context) {
        return context.getFilesDir().getPath() + File.separator;
    }

    public static void deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean isDirExist(Context context, String cacheDir) {
        File file = new File(getFilePath(context, cacheDir));
        return file.isDirectory() && file.exists();
    }

    public static File createSDDir(Context context, String dirName) {
        File dir = new File(getAppBaseFile(context) + File.separator + dirName);
        if (!dir.exists() && Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            L.d("createSDDir:" + dir.getAbsolutePath());
            L.d("createSDDir:" + dir.mkdir());
        }
        return dir;
    }

    public static boolean isFileExist(Context ct,String fileName) {
        if (TextUtils.isEmpty(fileName)) return false ;
        File file = new File(getAppBaseFile(ct) + File.separator + fileName);
        return file.isFile() && file.exists();
    }

    public static boolean isFileExistOnFileDir(Context ct,String fileName) {
        if (TextUtils.isEmpty(fileName)) return false ;
        File file = new File(getFileDirBasePath(ct) + fileName);
        return file.isFile() && file.exists();
    }

    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) return false ;
        File file = new File(filePath);
        return file.isFile() && file.exists();
    }

    /**
     * 读取文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return String, 读取到的文本内容，失败返回null
     */
    public static String readAssets(Context context, String fileName) {
        InputStream is = null;
        String content = null;
        try {
            is = context.getAssets().open(fileName);
            if (is != null) {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = is.read(buffer);
                    if (readLength == -1)
                        break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                is.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            content = null;
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }

}
