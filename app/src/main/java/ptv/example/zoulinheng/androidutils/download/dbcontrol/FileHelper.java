package ptv.example.zoulinheng.androidutils.download.dbcontrol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ptv.example.zoulinheng.androidutils.Constants;

/**
 * Created by lhZou on 2018/8/28.
 * desc:文件操作辅助类
 */
public class FileHelper {
    private static String userID = Constants.USER_ROOT;
    private static String baseFilePath = Constants.BASE_DOWNLOAD_PATH;
    private static String downloadFilePath = baseFilePath + "/" + userID + "/FILETEMP";
    /**
     * 下载文件的临时路径
     */
    private static String tempDirPath = baseFilePath + "/" + userID + "/TEMPDir";

    private static String[] wrongChars = {"/", "\\", "*", "?", "<", ">", "\"", "|"};

    // 创建文件
    public void newFile(File f) {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建目录
     *
     * @param
     */
    public static void newDirFile(File f) {
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * 获取一个文件列表的里的总文件大小
     *
     * @param willUpload
     * @return
     */
    public static double getSize(List<String> willUpload) {
        return (double) getSizeUnitByte(willUpload) / (1024 * 1024);
    }

    /**
     * 计算文件的大小，单位是字节
     *
     * @param willUpload
     * @return
     */
    public static long getSizeUnitByte(List<String> willUpload) {
        long allFileSize = 0;
        for (int i = 0; i < willUpload.size(); i++) {
            File newFile = new File(willUpload.get(i));
            if (newFile.exists() && newFile.isFile()) {
                allFileSize = allFileSize + newFile.length();
            }
        }
        return allFileSize;
    }

    /**
     * 获取默认文件存放路径
     */
    public static String getFileDefaultPath() {
        return downloadFilePath;
    }

    /**
     * 获取下载文件的临时路径
     */
    public static String getTempDirPath() {
        return tempDirPath;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String  原文件路径  如：c:/fqf.txt
     * @param newPath String  复制后路径  如：f:/fqf.txt
     * @return boolean
     */
    public static boolean copyFile(String oldPath, String newPath) {
        boolean isCopy = false;
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int byteRead;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {  //文件存在时
                inStream = new FileInputStream(oldPath); //读入原文件
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteRead);
                }
                isCopy = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isCopy;
    }

    public static void setUserID(String newUserID) {
        userID = newUserID;
        downloadFilePath = baseFilePath + "/" + userID + "/FILETEMP";
        tempDirPath = baseFilePath + "/" + userID + "/TEMPDir";
    }

    public static String getUserID() {
        return userID;
    }

    /**
     * 过滤附件ID中某些不能存在在文件名中的字符
     */
    public static String filterIDChars(String attID) {
        if (attID != null) {
            for (int i = 0; i < wrongChars.length; i++) {
                String c = wrongChars[i];
                if (attID.contains(c)) {
                    attID = attID.replaceAll(c, "");
                }
            }
        }
        return attID;
    }

    /**
     * 获取过滤ID后的文件名
     */
    public static String getFilterFileName(String flieName) {
        if (flieName == null || "".equals(flieName)) {
            return flieName;
        }
        boolean isNeedFilter = flieName.startsWith("(");
        int index = flieName.indexOf(")");
        if (isNeedFilter && index != -1) {
            int startIndex = index + 1;
            int endIndex = flieName.length();
            if (startIndex < endIndex) {
                return flieName.substring(startIndex, endIndex);
            }
        }
        return flieName;
    }
}
