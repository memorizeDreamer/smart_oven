package com.project.util;

import com.project.entity.OvenMobileRelation;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

public class FileUtil {

    private final static String FILE_PATH_SPILT = "/";

    private final static String FILE_NAME_POSTFIX = ".jpg";

    public static void writeFile(String filepath, FileInputStream inputStream){
        try {
            File result = new File(filepath);//要写入的图片
            File dir = result.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                result.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out = new FileOutputStream(result);
            int n = 0;
            byte[] bb = new byte[1024];
            while ((n = inputStream.read(bb)) != -1) {
                out.write(bb, 0, n);
            }
            out.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFilePath(OvenMobileRelation ovenMobileRelation, String taskId){
        return DateUtil.getCurrentDayString() + FILE_PATH_SPILT +
                ovenMobileRelation.getMobileId() + FILE_PATH_SPILT +
                ovenMobileRelation.getOvenId() + FILE_PATH_SPILT +
                taskId + FILE_PATH_SPILT +
                DateUtil.getCurrentSecString() + FILE_NAME_POSTFIX;
    }

    public static String getFilePath(String dayString,String mobileId,String ovenId){
        return dayString + FILE_PATH_SPILT +
                mobileId + FILE_PATH_SPILT +
                ovenId + FILE_PATH_SPILT;
    }

    public static String getFilePath(String dayString,String mobileId,String ovenId,String taskId, String fileName){
        return getFilePath(dayString,mobileId,ovenId) +
                taskId + FILE_PATH_SPILT +
                fileName + FILE_NAME_POSTFIX;
    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
