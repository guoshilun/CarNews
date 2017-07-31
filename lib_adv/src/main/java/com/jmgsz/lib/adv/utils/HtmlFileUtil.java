package com.jmgsz.lib.adv.utils;

import com.jmgsz.lib.adv.enums.AdSlotType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Html文件缓存工具类
 * Created by Wxl on 2017/7/18.
 */

public class HtmlFileUtil {

    /**
     * 缓存广告临时文件名的index的类型映射
     */
    private static Map<Integer, Integer> tempFileIndexMap;

    public synchronized static int getTempFileIndex(final String tempDir, int type) {
        initTempHtmlFileIndex(tempDir);
        int id = tempFileIndexMap.get(type);
        tempFileIndexMap.put(type, id >= 1000 ? 0 : id + 1);
        return id;
    }

    private synchronized static void initTempHtmlFileIndex(final String tempDir) {
        if (tempFileIndexMap == null) {
            tempFileIndexMap = new HashMap<>();
        } else {
            return;
        }
        File dir = new File(tempDir);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int index = 0; index < files.length; index++) {
                File tempFile = files[index];
                String name = tempFile.getName();
                String regex = "(\\d{1,2})_(\\d+)\\.html";//文件名为templateId_index.html
                Matcher matcher = Pattern.compile(regex).matcher(name);
                if (matcher.find()) {
                    String[] typeIndex = name.replaceAll(regex, "$1_$2").split("_");
                    if (typeIndex != null && typeIndex.length == 2) {
                        String typeStr = typeIndex[0];
                        String indexStr = typeIndex[1];
                        try {
                            int type = Integer.parseInt(typeStr);
                            int id = Integer.parseInt(indexStr);
                            Integer indexNum = tempFileIndexMap.get(type);
                            if (indexNum != null) {
                                int oldId = indexNum;
                                if (id > oldId) {
                                    tempFileIndexMap.put(type, id);
                                }
                            } else {
                                tempFileIndexMap.put(type, id);
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            continue;
                        }
                    }
                }
            }
        }
        for (int type : AdSlotType.getAdTypeList()) {
            if (tempFileIndexMap.get(type) == null) {
                tempFileIndexMap.put(type, 0);
            }
        }
    }
}
