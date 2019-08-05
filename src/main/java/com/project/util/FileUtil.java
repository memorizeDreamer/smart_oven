package com.project.util;

import com.project.entity.OvenMobileRelation;

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

    public static String getFilePath(OvenMobileRelation ovenMobileRelation){
        return DateUtil.getCurrentDayString() + FILE_PATH_SPILT +
                ovenMobileRelation.getModileId() + FILE_PATH_SPILT +
                ovenMobileRelation.getOvenId() + FILE_PATH_SPILT +
                DateUtil.getCurrentSecString() + FILE_NAME_POSTFIX;
    }

    public static String getFilePath(String dayString,String mobileId,String ovenId,String taskId){
        return dayString + FILE_PATH_SPILT +
                mobileId + FILE_PATH_SPILT +
                ovenId + FILE_PATH_SPILT +
                taskId + FILE_NAME_POSTFIX;
    }
}
