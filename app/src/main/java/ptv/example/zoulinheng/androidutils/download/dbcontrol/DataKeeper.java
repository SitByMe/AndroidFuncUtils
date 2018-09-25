package ptv.example.zoulinheng.androidutils.download.dbcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ptv.example.zoulinheng.androidutils.download.dbcontrol.bean.SQLDownLoadInfo;

/**
 * Created by lhZou on 2018/8/28.
 * desc:信息存储类，主要在任务下载各个环节执行数据的存储
 */
public class DataKeeper {
    private SQLiteHelper dbHelper;
    private SQLiteDatabase db;
    private int doSaveTimes = 0;

    public DataKeeper(Context context) {
        this.dbHelper = new SQLiteHelper(context);
    }

    /**
     * 保存一个任务的下载信息到数据库
     *
     * @param downloadInfo
     */
    public void saveDownLoadInfo(SQLDownLoadInfo downloadInfo) {
        ContentValues cv = new ContentValues();
        cv.put("userID", downloadInfo.getUserID());
        cv.put("taskID", downloadInfo.getTaskID());
        cv.put("downLoadSize", downloadInfo.getDownloadSize());
        cv.put("fileName", downloadInfo.getFileName());
        cv.put("filePath", downloadInfo.getFilePath());
        cv.put("fileSize", downloadInfo.getFileSize());
        cv.put("url", downloadInfo.getUrl());
        Cursor cursor = null;
        try {
            db = dbHelper.getWritableDatabase();
            cursor = db.rawQuery(
                    "SELECT * from " + SQLiteHelper.TABLE_NAME
                            + " WHERE userID = ? AND taskID = ? ", new String[]{downloadInfo.getUserID(), downloadInfo.getTaskID()});
            if (cursor.moveToNext()) {
                db.update(SQLiteHelper.TABLE_NAME, cv, "userID = ? AND taskID = ? ", new String[]{downloadInfo.getUserID(), downloadInfo.getTaskID()});
            } else {
                db.insert(SQLiteHelper.TABLE_NAME, null, cv);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            doSaveTimes++;
            if (doSaveTimes < 5) { //最多只做5次数据保存，降低数据保存失败率
                saveDownLoadInfo(downloadInfo);
            } else {
                doSaveTimes = 0;
            }
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        doSaveTimes = 0;
    }

    public SQLDownLoadInfo getDownLoadInfo(String userID, String taskID) {
        SQLDownLoadInfo downloadInfo = null;
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * from " + SQLiteHelper.TABLE_NAME
                        + "WHERE userID = ? AND taskID = ? ", new String[]{userID, taskID});
        if (cursor.moveToNext()) {
            downloadInfo = new SQLDownLoadInfo();
            downloadInfo.setDownloadSize(cursor.getLong(cursor.getColumnIndex("downLoadSize")));
            downloadInfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
            downloadInfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            downloadInfo.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
            downloadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            downloadInfo.setTaskID(cursor.getString(cursor.getColumnIndex("taskID")));
            downloadInfo.setUserID(cursor.getString(cursor.getColumnIndex("userID")));
        }
        cursor.close();
        db.close();
        return downloadInfo;
    }

    public ArrayList<SQLDownLoadInfo> getAllDownLoadInfo() {
        ArrayList<SQLDownLoadInfo> downloadInfoList = new ArrayList<>();
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * from " + SQLiteHelper.TABLE_NAME, null);
        while (cursor.moveToNext()) {
            SQLDownLoadInfo downloadInfo = new SQLDownLoadInfo();
            downloadInfo.setDownloadSize(cursor.getLong(cursor.getColumnIndex("downLoadSize")));
            downloadInfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
            downloadInfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            downloadInfo.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
            downloadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            downloadInfo.setTaskID(cursor.getString(cursor.getColumnIndex("taskID")));
            downloadInfo.setUserID(cursor.getString(cursor.getColumnIndex("userID")));
            downloadInfoList.add(downloadInfo);
        }
        cursor.close();
        db.close();
        return downloadInfoList;
    }

    public ArrayList<SQLDownLoadInfo> getUserDownLoadInfo(String userID) {
        ArrayList<SQLDownLoadInfo> downloadInfoList = new ArrayList<>();
        db = dbHelper.getWritableDatabase();
        try {
            Cursor cursor;
            cursor = db.rawQuery(
                    "SELECT * from " + SQLiteHelper.TABLE_NAME + " WHERE userID = '" + userID + "'", null);
            while (cursor.moveToNext()) {
                SQLDownLoadInfo downloadInfo = new SQLDownLoadInfo();
                downloadInfo.setDownloadSize(cursor.getLong(cursor.getColumnIndex("downLoadSize")));
                downloadInfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
                downloadInfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
                downloadInfo.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
                downloadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                downloadInfo.setTaskID(cursor.getString(cursor.getColumnIndex("taskID")));
                downloadInfo.setUserID(cursor.getString(cursor.getColumnIndex("userID")));
                downloadInfoList.add(downloadInfo);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
        return downloadInfoList;
    }

    public void deleteDownLoadInfo(String userID, String taskID) {
        db = dbHelper.getWritableDatabase();
        db.delete(SQLiteHelper.TABLE_NAME, "userID = ? AND taskID = ? ", new String[]{userID, taskID});
        db.close();
    }

    public void deleteUserDownLoadInfo(String userID) {
        db = dbHelper.getWritableDatabase();
        db.delete(SQLiteHelper.TABLE_NAME, "userID = ? ", new String[]{userID});
        db.close();
    }

    public void deleteAllDownLoadInfo() {
        db = dbHelper.getWritableDatabase();
        db.delete(SQLiteHelper.TABLE_NAME, null, null);
        db.close();
    }
}
