package com.datavim.chatapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datavim.chatapp.model.records.MediaData;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by appl on 28/05/15.
 */
public class MediaTable {

    public static final String LOG_TAG = "tblMedia";
    public static final String MEDIA_TABLE_NAME = "tblMedia";

    public static final String KEY_MEDIA_ID = "MediaId";
    public static final String KEY_MEDIA_URL = "MediaUrl";
    public static final String KEY_MEDIA_LOCAL_PATH = "MediaLocalPath";
    public static final String KEY_MEDIA_TYPE = "MediaType";

    public static final String CREATE_MEDIA_TABLE = "create table "
            + MEDIA_TABLE_NAME + "("
            + KEY_MEDIA_ID + " integer primary key autoincrement, "
            + KEY_MEDIA_URL + " text, "
            + KEY_MEDIA_LOCAL_PATH + " text, "
            + KEY_MEDIA_TYPE + " text );";

    public static String GET_MEDIA_DATA = "SELECT * FROM "+MEDIA_TABLE_NAME+" WHERE "+KEY_MEDIA_ID+"='%s'";



    protected long addMedia(SQLiteDatabase db, MediaData mediaData) {

        long mediaId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_MEDIA_URL, mediaData.getMediaUrl());
            values.put(KEY_MEDIA_LOCAL_PATH, mediaData.getMediaLocalPath());
            values.put(KEY_MEDIA_TYPE, mediaData.getMediaType());
            mediaId = db.insert(MEDIA_TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            mediaId = -1;
        }
        return mediaId;
    }


    protected MediaData getMediaFromMediaID(SQLiteDatabase database, long mediaId) {

        Cursor cursor = database.rawQuery(String.format(GET_MEDIA_DATA, mediaId), null);
        MediaData mediaData = null;
        int a = cursor.getCount();
        if (cursor.moveToFirst()) {
            do {
                mediaData = new MediaData();
                mediaData.setMediaId(cursor.getInt(0));
                mediaData.setMediaUrl(cursor.getString(1));
                mediaData.setMediaLocalPath(cursor.getString(2));
                mediaData.setMediaType(cursor.getString(3));

            } while (cursor.moveToNext());
        }
        return mediaData;
    }


    protected List<MediaData> getShareMedia(SQLiteDatabase database, long mediaId) {

        List<MediaData> mediaDataList = new ArrayList<MediaData>();
        Cursor cursor = database.rawQuery(String.format(GET_MEDIA_DATA, mediaId), null);

        if (cursor.moveToFirst()) {
            do {

                MediaData mediaData = new MediaData();
                mediaData.setMediaId(cursor.getInt(0));
                mediaData.setMediaUrl(cursor.getString(1));
                mediaData.setMediaLocalPath(cursor.getString(2));
                mediaData.setMediaType(cursor.getString(3));
                mediaDataList.add(mediaData);

            } while (cursor.moveToNext());
        }
        return mediaDataList;
    }

    protected long updateMediaLocalPath(SQLiteDatabase db, String localPath, long id) {
        String where = KEY_MEDIA_ID + " = ?";
        String[] selection = {id + ""};
        int mediaId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_MEDIA_LOCAL_PATH, localPath);
            mediaId = db.update(MEDIA_TABLE_NAME, values, where, selection);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            mediaId = -1;
        }
        return mediaId;
    }

}
