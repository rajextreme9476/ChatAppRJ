package com.datavim.chatapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datavim.chatapp.model.records.FriendData;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by apple on 28/05/15.
 */
public class FriendTable {

    public static final String LOG_TAG = "tblFriend";

    public static final String FRIEND_TABLE_NAME = "tblFriend";

    public static final String KEY_FRIEND_ID = "FriendId";
    public static final String KEY_FRIEND_USER_ID = "FriendUserId";
    public static final String KEY_FRIEND_NAME = "FriendName";
    public static final String KEY_FRIENDS_DISPLAY_NAME = "FriendDisplayName";
    public static final String KEY_FRIEND_JID = "FriendJID";
    public static final String KEY_FRIEND_STATUS = "FriendStatus";
    public static final String KEY_PROFILE_PIC_URL = "ProfilePicUrl";
    public static final String KEY_PROFILE_PIC_THUMB_URL = "ProfileThumbPicUrl";
    public static final String KEY_QB_ID = "QBId";
    public static final String KEY_ACIVE_CHAT = "ActiveChat";
    public static final String KEY_FRIEND_LAST_MESSAGE = "LastMesssage";
    public static final String KEY_TIME_OF_LAST_MESSAGE = "TimeOfLastMessage";
    public static final String KEY_IS_BLOCKED = "IsBlocked";
    public static final String KEY_IS_GROUP = "IsGroup";
    public static final String KEY_IS_MUTE = "IsMute";
    public static final String KEY_IS_EXIT = "IsExit";
    public static final String KEY_IS_ARCHIVED = "IsArchived";
    public static final String TAG = "FRIENDTABLE";
    public static final String KEY_FRIEND_LAST_MESSAGE_TYPE = "LastMesssageType";
    public static final String KEY_WALLPAPER_ID = "wallpaer_id";


    public static final String CREATE_FRIEND_TABLE = "create table "
            + FRIEND_TABLE_NAME + "("
            + KEY_FRIEND_ID + " integer primary key autoincrement, "
            + KEY_FRIEND_USER_ID + " integer, "
            + KEY_FRIEND_NAME + " text, "
            + KEY_FRIENDS_DISPLAY_NAME + " text, "
            + KEY_FRIEND_JID + " text UNIQUE, "
            + KEY_FRIEND_STATUS + " text, "
            + KEY_PROFILE_PIC_URL + " text, "
            + KEY_PROFILE_PIC_THUMB_URL + " text, "
            + KEY_ACIVE_CHAT + " integer,"
            + KEY_FRIEND_LAST_MESSAGE + " text default null,"
            + KEY_FRIEND_LAST_MESSAGE_TYPE + " text default null,"
            + KEY_TIME_OF_LAST_MESSAGE + " integer default 0, "
            + KEY_QB_ID + " text,"
            + KEY_IS_BLOCKED + " integer default 0,"
            + KEY_IS_GROUP + " integer default 0,"
            + KEY_IS_MUTE + " integer default 0,"
            + KEY_IS_EXIT + " integer default 0,"
            + KEY_IS_ARCHIVED + " integer default 0,"+ KEY_WALLPAPER_ID + " text"
            + " );";

    private final String GET_ALL_FRIRND = "SELECT * FROM " + FRIEND_TABLE_NAME + " ORDER BY " + KEY_TIME_OF_LAST_MESSAGE + " DESC";

    private final String GET_ALL_USER_FRIEND = "SELECT * FROM " + FRIEND_TABLE_NAME + " ORDER BY " + KEY_FRIENDS_DISPLAY_NAME + " COLLATE NOCASE";

    private final String GET_USER_FRIENDS = "SELECT * FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_IS_GROUP + "!=1 AND "+ KEY_IS_BLOCKED + "=0 ORDER BY " + KEY_FRIENDS_DISPLAY_NAME;

    private final String GET_ALL_USER_GROUPS = "SELECT * FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_IS_GROUP + "=1" + " ORDER BY " + KEY_FRIENDS_DISPLAY_NAME;

    private final String GET_PAST_USER_CHAT = "SELECT * FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_TIME_OF_LAST_MESSAGE + "!=0 AND " + KEY_IS_BLOCKED + "=0 AND "+KEY_IS_ARCHIVED+"=0" + " ORDER BY " + KEY_TIME_OF_LAST_MESSAGE + " DESC";

    private final String GET_ARCHIVE_USER_CHAT = "SELECT * FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_TIME_OF_LAST_MESSAGE + "!=0 AND " + KEY_IS_BLOCKED + "=0 AND "+KEY_IS_ARCHIVED+"=1" + " ORDER BY " + KEY_TIME_OF_LAST_MESSAGE + " DESC";

    private final String GET_PAST_GROUP_CHAT = "SELECT * FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_IS_GROUP + " =1 AND " + KEY_IS_BLOCKED + "=0" + " ORDER BY " + KEY_TIME_OF_LAST_MESSAGE + " DESC";

    private final String GET_PAST_FRIEND_CHAT = "SELECT * FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_TIME_OF_LAST_MESSAGE + "!=0 AND " + KEY_IS_GROUP + " =0 AND " + KEY_IS_BLOCKED + "=0" + " ORDER BY " + KEY_TIME_OF_LAST_MESSAGE + " DESC";

    private final String GET_LAST_MESSAGE = "SELECT " + KEY_FRIEND_STATUS + "FROM" + FRIEND_TABLE_NAME + "WHERE" + KEY_FRIEND_JID + "='%s'";

    private final String GET_LAST_MESSAGE_TIME = "SELECT " + KEY_FRIEND_LAST_MESSAGE + "FROM" + FRIEND_TABLE_NAME + "WHERE" + KEY_FRIEND_JID + "='%s'";

    private final String GET_IF_FRIEND_EXIST = "SELECT * FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + "='%s'";

    private final String GET_EXIT_STATUS = "SELECT " + KEY_IS_EXIT + "FROM" + FRIEND_TABLE_NAME + "WHERE" + KEY_FRIEND_JID + "='%s'";

    private final String GET_FRIEND_BY_JID = "SELECT * FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + "='%s'" + " ORDER BY " + KEY_TIME_OF_LAST_MESSAGE + " DESC";

    private final String GET_ARCHIVE_COUNT_USER_CHAT = "SELECT * FROM " + FRIEND_TABLE_NAME + " WHERE "+KEY_IS_ARCHIVED+"=1" + " ORDER BY " + KEY_TIME_OF_LAST_MESSAGE + " DESC";

    private final String GET_FRIEND_EXITSTS= "SELECT * FROM " + FRIEND_TABLE_NAME + " WHERE "+KEY_FRIEND_JID+"='%s'";

    protected boolean addFriends(SQLiteDatabase database, FriendData friend) {
        boolean status = false;

        if(!friend.getFriendJID().contains("'")) {
            Cursor cursor = database.rawQuery(String.format(GET_IF_FRIEND_EXIST, friend.getFriendJID().toString()), null);
            if (cursor.getCount() == 0) {
                try {
                    ContentValues values = new ContentValues();
                    values.put(KEY_FRIEND_USER_ID, friend.getFriendUserId());
                    values.put(KEY_FRIEND_NAME, friend.getFriendName());
                    values.put(KEY_FRIENDS_DISPLAY_NAME, friend.getFriendDisplayName());
                    values.put(KEY_FRIEND_JID, friend.getFriendJID());
                    values.put(KEY_FRIEND_STATUS, friend.getFriendStatus());
                    values.put(KEY_PROFILE_PIC_URL, friend.getProfilePicUrl());
                    values.put(KEY_PROFILE_PIC_THUMB_URL, friend.getProfileThumbUrl());
                    values.put(KEY_QB_ID, friend.getFriendQBID());
                    values.put(KEY_ACIVE_CHAT, friend.getActiveChat());
                    values.put(KEY_IS_BLOCKED, friend.getIsBlocked());
                    values.put(KEY_IS_GROUP, friend.getIsGroup());
                    values.put(KEY_IS_MUTE, friend.getIsMute());
                    values.put(KEY_IS_EXIT, friend.getIsExit());
                    if (friend.getIsGroup() == 1) {
                        values.put(KEY_FRIEND_LAST_MESSAGE, "");
                        values.put(KEY_FRIEND_LAST_MESSAGE_TYPE, "event");
                        values.put(KEY_TIME_OF_LAST_MESSAGE, System.currentTimeMillis());

                    }
                    database.insertOrThrow(FRIEND_TABLE_NAME, null, values);
                    status = true;

                } catch (Exception e) {
                    status = false;
                }
            } else {
                try {
                    ContentValues values = new ContentValues();
                    values.put(KEY_FRIEND_USER_ID, friend.getFriendUserId());
                    values.put(KEY_FRIEND_NAME, friend.getFriendName());
                    values.put(KEY_FRIENDS_DISPLAY_NAME, friend.getFriendDisplayName());
                    values.put(KEY_FRIEND_JID, friend.getFriendJID());
                    values.put(KEY_FRIEND_STATUS, friend.getFriendStatus());
                    values.put(KEY_PROFILE_PIC_URL, friend.getProfilePicUrl());
                    values.put(KEY_PROFILE_PIC_THUMB_URL, friend.getProfileThumbUrl());
                    values.put(KEY_QB_ID, friend.getFriendQBID());
                    values.put(KEY_ACIVE_CHAT, friend.getActiveChat());
                    values.put(KEY_IS_BLOCKED, friend.getIsBlocked());
                    values.put(KEY_IS_GROUP, friend.getIsGroup());
                    values.put(KEY_IS_MUTE, friend.getIsMute());
                    values.put(KEY_IS_EXIT, friend.getIsExit());
                    database.update(FRIEND_TABLE_NAME, values, KEY_FRIEND_JID + " =?", new String[]{friend.getFriendJID()});
                    status = true;

                } catch (Exception e) {
                    status = false;
                }
            }
        }   return status;
    }

    protected List<FriendData> getFriends(SQLiteDatabase database) {

        List<FriendData> friends = new ArrayList<FriendData>();
        Cursor cursor = database.rawQuery(GET_ALL_FRIRND, null);

        if (cursor.moveToFirst()) {
            do {
                FriendData friendData = new FriendData();
                friendData.setFriendId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_ID))));
                friendData.setFriendUserId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_USER_ID))));
                friendData.setFriendName(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_NAME)));
                friendData.setFriendDisplayName(cursor.getString(cursor.getColumnIndex(KEY_FRIENDS_DISPLAY_NAME)));
                friendData.setFriendJID(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_JID)));
                friendData.setFriendStatus(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_STATUS)));
                friendData.setLastMessage(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_LAST_MESSAGE)));
                friendData.setProfilePicUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_URL)));
                friendData.setProfileThumbUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_THUMB_URL)));
                friendData.setActiveChat(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ACIVE_CHAT))));
                friendData.setTimeOfLastMessage(cursor.getLong(cursor.getColumnIndex(KEY_TIME_OF_LAST_MESSAGE)) + "");
                friendData.setFriendQBID(cursor.getString(cursor.getColumnIndex(KEY_QB_ID)));
                friendData.setIsBlocked(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_BLOCKED))));
                friendData.setIsGroup(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_GROUP))));
                friendData.setIsMute(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_MUTE))));
                friendData.setIsExit(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_EXIT))));

                friends.add(friendData);
            } while (cursor.moveToNext());
        }
        return friends;
    }


    protected List<FriendData> getFriendAvailble(SQLiteDatabase database, String jid) {

        List<FriendData> friends = new ArrayList<FriendData>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + FRIEND_TABLE_NAME +" WHERE "+KEY_FRIEND_JID + "='"+jid+"'", null);

        if (cursor.moveToFirst()) {
            do {
                FriendData friendData = new FriendData();
                friendData.setFriendId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_ID))));
                friendData.setFriendUserId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_USER_ID))));
                friendData.setFriendName(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_NAME)));
                friendData.setFriendDisplayName(cursor.getString(cursor.getColumnIndex(KEY_FRIENDS_DISPLAY_NAME)));
                friendData.setFriendJID(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_JID)));
                friendData.setFriendStatus(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_STATUS)));
                friendData.setLastMessage(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_LAST_MESSAGE)));
                friendData.setProfilePicUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_URL)));
                friendData.setProfileThumbUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_THUMB_URL)));
                friendData.setActiveChat(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ACIVE_CHAT))));
                friendData.setTimeOfLastMessage(cursor.getLong(cursor.getColumnIndex(KEY_TIME_OF_LAST_MESSAGE)) + "");
                friendData.setFriendQBID(cursor.getString(cursor.getColumnIndex(KEY_QB_ID)));
                friendData.setIsBlocked(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_BLOCKED))));
                friendData.setIsGroup(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_GROUP))));
                friendData.setIsMute(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_MUTE))));
                friendData.setIsExit(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_EXIT))));

                friends.add(friendData);
            } while (cursor.moveToNext());
        }
        return friends;
    }




    protected List<FriendData> getUserGroups(SQLiteDatabase database) {

        List<FriendData> friends = new ArrayList<FriendData>();
        Cursor cursor = database.rawQuery(GET_ALL_USER_GROUPS, null);

        if (cursor.moveToFirst()) {
            do {
                FriendData friendData = new FriendData();
                friendData.setFriendId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_ID))));
                friendData.setFriendUserId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_USER_ID))));
                friendData.setFriendName(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_NAME)));
                friendData.setFriendDisplayName(cursor.getString(cursor.getColumnIndex(KEY_FRIENDS_DISPLAY_NAME)));
                friendData.setFriendJID(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_JID)));
                friendData.setFriendStatus(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_STATUS)));
                friendData.setProfilePicUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_URL)));
                friendData.setProfileThumbUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_THUMB_URL)));
                friendData.setActiveChat(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ACIVE_CHAT))));
                friendData.setTimeOfLastMessage(cursor.getInt(cursor.getColumnIndex(KEY_TIME_OF_LAST_MESSAGE)) + "");
                friendData.setFriendQBID(cursor.getString(cursor.getColumnIndex(KEY_QB_ID)));
                friendData.setIsBlocked(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_BLOCKED))));
                friendData.setIsGroup(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_GROUP))));
                friendData.setIsMute(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_MUTE))));
                friendData.setIsExit(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_EXIT))));
                friends.add(friendData);
            } while (cursor.moveToNext());
        }
        return friends;
    }

    protected List<FriendData> getUserFriends(SQLiteDatabase database) {

        List<FriendData> friends = new ArrayList<FriendData>();
        Cursor cursor = database.rawQuery(GET_ALL_USER_FRIEND, null);

        if (cursor.moveToFirst()) {
            do {
                FriendData friendData = new FriendData();
                friendData.setFriendId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_ID))));
                friendData.setFriendUserId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_USER_ID))));
                friendData.setFriendName(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_NAME)));
                friendData.setFriendDisplayName(cursor.getString(cursor.getColumnIndex(KEY_FRIENDS_DISPLAY_NAME)));
                friendData.setFriendJID(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_JID)));
                friendData.setFriendStatus(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_STATUS)));
                friendData.setProfilePicUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_URL)));
                friendData.setProfileThumbUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_THUMB_URL)));
                friendData.setActiveChat(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ACIVE_CHAT))));
                friendData.setTimeOfLastMessage(cursor.getInt(cursor.getColumnIndex(KEY_TIME_OF_LAST_MESSAGE)) + "");
                friendData.setFriendQBID(cursor.getString(cursor.getColumnIndex(KEY_QB_ID)));
                friendData.setIsBlocked(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_BLOCKED))));
                friendData.setIsGroup(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_GROUP))));
                friendData.setIsMute(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_MUTE))));
                friendData.setIsExit(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_EXIT))));
                friends.add(friendData);
            } while (cursor.moveToNext());
        }
        return friends;
    }

    protected List<FriendData> getChatFriends(SQLiteDatabase database) {

        List<FriendData> friends = new ArrayList<FriendData>();
        Cursor cursor = database.rawQuery(GET_PAST_USER_CHAT, null);

        if (cursor.moveToFirst()) {
            do {
                FriendData friendData = new FriendData();
                friendData.setFriendId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_ID))));
                friendData.setFriendUserId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_USER_ID))));
                friendData.setFriendName(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_NAME)));
                friendData.setFriendDisplayName(cursor.getString(cursor.getColumnIndex(KEY_FRIENDS_DISPLAY_NAME)));
                friendData.setFriendJID(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_JID)));
                friendData.setFriendStatus(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_STATUS)));
                friendData.setProfilePicUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_URL)));
                friendData.setProfileThumbUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_THUMB_URL)));
                friendData.setActiveChat(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ACIVE_CHAT))));
                friendData.setTimeOfLastMessage(cursor.getLong(cursor.getColumnIndex(KEY_TIME_OF_LAST_MESSAGE)) + "");

                friendData.setLastMessage(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_LAST_MESSAGE)) + "");
                friendData.setTypeOfLastMessage(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_LAST_MESSAGE_TYPE)) + "");
                friendData.setFriendQBID(cursor.getString(cursor.getColumnIndex(KEY_QB_ID)));
                friendData.setIsBlocked(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_BLOCKED))));
                friendData.setIsGroup(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_GROUP))));
                friendData.setIsMute(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_MUTE))));
                friendData.setIsExit(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_EXIT))));
                friends.add(friendData);
            } while (cursor.moveToNext());
        }
        return friends;
    }

    protected String getLastMessageTime(SQLiteDatabase database, String JID) {

        String query = String.format(GET_LAST_MESSAGE_TIME, JID);
        Cursor cursor = database.rawQuery(query, null);
        String lastMsgTime = "";
        try {
            if (cursor.moveToNext()) {
                lastMsgTime = cursor.getString(Integer.parseInt(cursor.getString(8)));
            } else {
                lastMsgTime = "";
            }
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.toString());
            lastMsgTime = "";
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            lastMsgTime = "";
        } finally {
            cursor.close();
        }
        return lastMsgTime;
    }


    protected String getLastMessage(SQLiteDatabase database, String JID) {
        String query = String.format(GET_LAST_MESSAGE, JID);
        Cursor cursor = database.rawQuery(query, null);
        String lastMsg = "";
        try {
            if (cursor.moveToNext()) {
                lastMsg = cursor.getString(Integer.parseInt(cursor.getString(7)));
            } else {
                lastMsg = "";
            }
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.toString());
            lastMsg = "";
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            lastMsg = "";
        } finally {
            cursor.close();
        }
        return lastMsg;
    }

    protected void updateLastMessageInfo(SQLiteDatabase database, String jid, String message, String time, String type) {
        String[] jids = {jid};
        ContentValues values = new ContentValues();
        values.put(KEY_FRIEND_LAST_MESSAGE, message);
        values.put(KEY_TIME_OF_LAST_MESSAGE, time);
        values.put(KEY_FRIEND_LAST_MESSAGE_TYPE, type);
        values.put(KEY_IS_BLOCKED, 0);
        int i =database.update(FRIEND_TABLE_NAME, values, KEY_FRIEND_JID + "=?", jids);
        Log.e("Success",""+i);
    }


    public boolean deleteChats(SQLiteDatabase database, String jid) {
        String where = KEY_FRIEND_JID + " = ?";
        String[] selection = {jid+ ""};
        int flag = database.delete(FRIEND_TABLE_NAME, where, selection);
        if (flag == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteFriends(SQLiteDatabase database, String jid) {
        String where = KEY_FRIEND_JID + " = ?";
        String[] selection = {jid+ ""};
        int flag = database.delete(FRIEND_TABLE_NAME, where, selection);
        if (flag == -1) {
            return false;
        } else {
            return true;
        }
    }


    protected int updateMuteStatus(SQLiteDatabase database, String jid, int value) {
        String[] jids = {jid};
        ContentValues values = new ContentValues();
        values.put(KEY_IS_MUTE, value);
        return database.update(FRIEND_TABLE_NAME, values, KEY_FRIEND_JID + "=?", jids);
    }


    protected List<FriendData> getChatGroups(SQLiteDatabase database) {

        List<FriendData> friends = new ArrayList<FriendData>();
        Cursor cursor = database.rawQuery(GET_PAST_GROUP_CHAT, null);

        if (cursor.moveToFirst()) {
            do {
                FriendData friendData = new FriendData();
                friendData.setFriendId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_ID))));
                friendData.setFriendUserId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_USER_ID))));
                friendData.setFriendName(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_NAME)));
                friendData.setFriendDisplayName(cursor.getString(cursor.getColumnIndex(KEY_FRIENDS_DISPLAY_NAME)));
                friendData.setFriendJID(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_JID)));
                friendData.setFriendStatus(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_STATUS)));
                friendData.setProfilePicUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_URL)));
                friendData.setProfileThumbUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_THUMB_URL)));
                friendData.setActiveChat(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ACIVE_CHAT))));
                friendData.setTimeOfLastMessage(cursor.getLong(cursor.getColumnIndex(KEY_TIME_OF_LAST_MESSAGE)) + "");
                friendData.setLastMessage(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_LAST_MESSAGE)) + "");
                friendData.setTypeOfLastMessage(cursor.getLong(cursor.getColumnIndex(KEY_FRIEND_LAST_MESSAGE_TYPE)) + "");
                friendData.setFriendQBID(cursor.getString(cursor.getColumnIndex(KEY_QB_ID)));
                friendData.setIsBlocked(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_BLOCKED))));
                friendData.setIsGroup(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_GROUP))));
                friendData.setIsMute(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_MUTE))));
                friendData.setIsExit(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_EXIT))));
                friends.add(friendData);
            } while (cursor.moveToNext());
        }
        return friends;
    }

    protected List<FriendData> getChatOnlyFriends(SQLiteDatabase database) {

        List<FriendData> friends = new ArrayList<FriendData>();
        Cursor cursor = database.rawQuery(GET_PAST_FRIEND_CHAT, null);

        if (cursor.moveToFirst()) {
            do {
                FriendData friendData = new FriendData();
                friendData.setFriendId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_ID))));
                friendData.setFriendUserId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_USER_ID))));
                friendData.setFriendName(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_NAME)));
                friendData.setFriendDisplayName(cursor.getString(cursor.getColumnIndex(KEY_FRIENDS_DISPLAY_NAME)));
                friendData.setFriendJID(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_JID)));
                friendData.setFriendStatus(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_STATUS)));
                friendData.setProfilePicUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_URL)));
                friendData.setProfileThumbUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_THUMB_URL)));
                friendData.setActiveChat(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ACIVE_CHAT))));
                friendData.setTimeOfLastMessage(cursor.getLong(cursor.getColumnIndex(KEY_TIME_OF_LAST_MESSAGE)) + "");
                friendData.setLastMessage(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_LAST_MESSAGE)) + "");
                friendData.setTypeOfLastMessage(cursor.getLong(cursor.getColumnIndex(KEY_FRIEND_LAST_MESSAGE_TYPE)) + "");
                friendData.setFriendQBID(cursor.getString(cursor.getColumnIndex(KEY_QB_ID)));
                friendData.setIsBlocked(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_BLOCKED))));
                friendData.setIsGroup(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_GROUP))));
                friendData.setIsMute(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_MUTE))));
                friendData.setIsExit(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_EXIT))));
                friends.add(friendData);
            } while (cursor.moveToNext());
        }
        return friends;
    }

    protected List<FriendData> getOnlyFriends(SQLiteDatabase database) {

        List<FriendData> friends = new ArrayList<FriendData>();
        Cursor cursor = database.rawQuery(GET_USER_FRIENDS, null);

        if (cursor.moveToFirst()) {
            do {
                FriendData friendData = new FriendData();
                friendData.setFriendId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_ID))));
                friendData.setFriendUserId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_USER_ID))));
                friendData.setFriendName(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_NAME)));
                friendData.setFriendDisplayName(cursor.getString(cursor.getColumnIndex(KEY_FRIENDS_DISPLAY_NAME)));
                friendData.setFriendJID(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_JID)));
                friendData.setFriendStatus(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_STATUS)));
                friendData.setProfilePicUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_URL)));
                friendData.setProfileThumbUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_THUMB_URL)));
                friendData.setActiveChat(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ACIVE_CHAT))));
                friendData.setTimeOfLastMessage(cursor.getLong(cursor.getColumnIndex(KEY_TIME_OF_LAST_MESSAGE)) + "");
                friendData.setLastMessage(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_LAST_MESSAGE)) + "");
                friendData.setTypeOfLastMessage(cursor.getLong(cursor.getColumnIndex(KEY_FRIEND_LAST_MESSAGE_TYPE)) + "");
                friendData.setFriendQBID(cursor.getString(cursor.getColumnIndex(KEY_QB_ID)));
                friendData.setIsBlocked(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_BLOCKED))));
                friendData.setIsGroup(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_GROUP))));
                friendData.setIsMute(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_MUTE))));
                friendData.setIsExit(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_EXIT))));
                friends.add(friendData);
            } while (cursor.moveToNext());
        }
        return friends;
    }


    public int hideFromChatList(SQLiteDatabase inkeDataBase, String jid) {
        String where = KEY_FRIEND_JID + "=?";
        ContentValues values = new ContentValues();
        values.put(KEY_IS_ARCHIVED, 1);
        int status = inkeDataBase.update(FRIEND_TABLE_NAME, values, where, new String[]{jid});
        return status;
    }


    public int blockFromChatList(SQLiteDatabase inkeDataBase, String jid) {
        String where = KEY_FRIEND_JID + "=?";
        ContentValues values = new ContentValues();
        values.put(KEY_IS_BLOCKED, 1);
        int status = inkeDataBase.update(FRIEND_TABLE_NAME, values, where, new String[]{jid});
        return status;
    }


    public int unBlockFromChatList(SQLiteDatabase inkeDataBase, String jid) {
        String where = KEY_FRIEND_JID + "=?";
        ContentValues values = new ContentValues();
        values.put(KEY_IS_BLOCKED, 0);
        int status = inkeDataBase.update(FRIEND_TABLE_NAME, values, where, new String[]{jid});
        return status;
    }


    public int updateExit(SQLiteDatabase inkeDataBase, String jid) {
        String where = KEY_FRIEND_JID + "=?";
        ContentValues values = new ContentValues();
        values.put(KEY_IS_EXIT, 1);
        int status = inkeDataBase.update(FRIEND_TABLE_NAME, values, where, new String[]{jid});
        return status;
    }


    public int updateExitStatus(SQLiteDatabase inkeDataBase, int isExit, String jid) {
        String where = KEY_FRIEND_JID + "=?";
        ContentValues values = new ContentValues();
        values.put(KEY_IS_EXIT, isExit);
        int status = inkeDataBase.update(FRIEND_TABLE_NAME, values, where, new String[]{jid});
        return status;
    }

    public String getFriendNameFromJID(SQLiteDatabase inkeDatabase, String jid) {
        Cursor cursor = inkeDatabase.rawQuery("SELECT " + KEY_FRIENDS_DISPLAY_NAME + " FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + "='"+jid+"'", null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(KEY_FRIENDS_DISPLAY_NAME));
                return name;
            } while (cursor.moveToNext());
        }
        return "";

    }


    public String getFriendJIDFromFriendUSERID(SQLiteDatabase inkeDatabase, String friendid) {
        Cursor cursor = inkeDatabase.rawQuery("SELECT " + KEY_FRIEND_JID + " FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_USER_ID + " = '" + friendid + "'", null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(KEY_FRIEND_JID));
                return name;
            } while (cursor.moveToNext());
        }
        return "";

    }

    public String getFriendStatusFromJID(SQLiteDatabase inkeDatabase, String jid) {
        Cursor cursor = inkeDatabase.rawQuery("SELECT " + KEY_FRIEND_STATUS + " FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + " = '" + jid + "'", null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(KEY_FRIEND_STATUS));
                return name;
            } while (cursor.moveToNext());
        }
        return "";

    }

    public String deleteTable(SQLiteDatabase inkeDatabase) {
        String table=null;
        inkeDatabase.execSQL("delete from "+ FRIEND_TABLE_NAME );
         return table;
    }



    public int getExitStatus(SQLiteDatabase inkeDatabase, String jid) {
        Cursor cursor = inkeDatabase.rawQuery("SELECT " + KEY_IS_EXIT + " FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + " = '" + jid + "'", null);
        if (cursor.moveToFirst()) {
            do {
                int exitStatus = cursor.getInt(cursor.getColumnIndex(KEY_IS_EXIT));
                return exitStatus;
            } while (cursor.moveToNext());
        }
        return 0;
    }

    public int getBlockStatus(SQLiteDatabase inkeDatabase, String jid) {
       // Cursor cursor = inkeDatabase.rawQuery("SELECT * FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + " = '" + jid + "'" +" AND "+KEY_IS_BLOCKED+"=1", null);
        Cursor cursor = inkeDatabase.rawQuery("SELECT " + KEY_IS_BLOCKED + " FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + " = '" + jid + "'" , null);
        if (cursor.moveToFirst()) {
            do {
                int exitStatus = cursor.getInt(cursor.getColumnIndex(KEY_IS_BLOCKED));
                return exitStatus;
            } while (cursor.moveToNext());
        }
        return 0;
    }


    public String getProfilePicFromJID(SQLiteDatabase inkeDatabase, String jid) {
        // Cursor cursor = inkeDatabase.rawQuery("SELECT * FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + " = '" + jid + "'" +" AND "+KEY_IS_BLOCKED+"=1", null);
        Cursor cursor = inkeDatabase.rawQuery("SELECT " + KEY_PROFILE_PIC_THUMB_URL + " FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + " = '" + jid + "'" , null);
        if (cursor.moveToFirst()) {
            do {
                String exitStatus = cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_THUMB_URL));
                return exitStatus;
            } while (cursor.moveToNext());
        }
        return null;
    }

    public String getKeyFriendsDisplayName(SQLiteDatabase inkeDatabase, String jid) {
        // Cursor cursor = inkeDatabase.rawQuery("SELECT * FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + " = '" + jid + "'" +" AND "+KEY_IS_BLOCKED+"=1", null);
        Cursor cursor = inkeDatabase.rawQuery("SELECT " + KEY_FRIENDS_DISPLAY_NAME + " FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + " = '" + jid + "'" , null);
        if (cursor.moveToFirst()) {
            do {
                String exitStatus = cursor.getString(cursor.getColumnIndex(KEY_FRIENDS_DISPLAY_NAME));
                return exitStatus;
            } while (cursor.moveToNext());
        }
        return null;
    }



    public int getFriendExitStatus(SQLiteDatabase inkeDatabase, String jid) {
        // Cursor cursor = inkeDatabase.rawQuery("SELECT * FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + " = '" + jid + "'" +" AND "+KEY_IS_BLOCKED+"=1", null);
        Cursor cursor = inkeDatabase.rawQuery("SELECT " + KEY_IS_EXIT + " FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + " = '" + jid + "'" , null);
        if (cursor.moveToFirst()) {
            do {
                int exitStatus = cursor.getInt(cursor.getColumnIndex(KEY_IS_EXIT));
                return exitStatus;
            } while (cursor.moveToNext());
        }
        return 0;
    }




    protected FriendData getFriendFromJID(SQLiteDatabase database, String JID) {

        List<FriendData> friends = new ArrayList<FriendData>();
        Cursor cursor = database.rawQuery(String.format(GET_FRIEND_BY_JID,JID), null);
        FriendData friendData = new FriendData();

        if (cursor.moveToFirst()) {
            do {
                friendData.setFriendId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_ID))));
                friendData.setFriendUserId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_USER_ID))));
                friendData.setFriendName(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_NAME)));
                friendData.setFriendDisplayName(cursor.getString(cursor.getColumnIndex(KEY_FRIENDS_DISPLAY_NAME)));
                friendData.setFriendJID(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_JID)));
                friendData.setFriendStatus(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_STATUS)));
                friendData.setProfilePicUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_URL)));
                friendData.setProfileThumbUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_THUMB_URL)));
                friendData.setActiveChat(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ACIVE_CHAT))));
                friendData.setTimeOfLastMessage(cursor.getLong(cursor.getColumnIndex(KEY_TIME_OF_LAST_MESSAGE)) + "");
                friendData.setFriendQBID(cursor.getString(cursor.getColumnIndex(KEY_QB_ID)));
                friendData.setIsBlocked(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_BLOCKED))));
                friendData.setIsGroup(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_GROUP))));
                friendData.setIsMute(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_MUTE))));

            } while (cursor.moveToNext());
        }
        return friendData;
    }

    protected String updateGroupNameFromJID(SQLiteDatabase database, String id, String message) {
        String[] jids = {id};
        ContentValues values = new ContentValues();
        values.put(KEY_FRIENDS_DISPLAY_NAME, message);
        database.update(FRIEND_TABLE_NAME, values, KEY_FRIEND_ID + "=?", jids);
        return "";
    }

    protected String updateContactPhotoFromJID(SQLiteDatabase database, String id, String message) {
        String[] jids = {id};
        ContentValues values = new ContentValues();
        values.put(KEY_PROFILE_PIC_THUMB_URL, message);
        database.update(FRIEND_TABLE_NAME, values, KEY_FRIEND_ID + "=?", jids);
        return "";
    }

   /* protected String getUserStatus(SQLiteDatabase database,String id)
    {
        String[] jids = {id};
        String status= Cursor cursor = inkeDatabase.rawQuery("SELECT " + KEY_IS_EXIT + " FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + " = '" + jid + "'", null);
        return status;
    }
    */

    protected int updateArchiveStatus(SQLiteDatabase database, String id, int status) {
        String[] jids = {id};
        ContentValues values = new ContentValues();
        values.put(KEY_IS_ARCHIVED, status);
        int flag = database.update(FRIEND_TABLE_NAME, values, KEY_FRIEND_JID + "=?", jids);
        return flag;
    }


    public int getCountArchivedChats(SQLiteDatabase inkeDataBase) {
        Cursor cursor = inkeDataBase.rawQuery(GET_ARCHIVE_COUNT_USER_CHAT, null);
        return cursor.getCount();
    }

    public List<FriendData> getArchivedChats(SQLiteDatabase inkeDataBase) {
        List<FriendData> friends = new ArrayList<FriendData>();
        Cursor cursor = inkeDataBase.rawQuery(GET_ARCHIVE_USER_CHAT, null);

        if (cursor.moveToFirst()) {
            do {
                FriendData friendData = new FriendData();
                friendData.setFriendId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_ID))));
                friendData.setFriendUserId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_USER_ID))));
                friendData.setFriendName(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_NAME)));
                friendData.setFriendDisplayName(cursor.getString(cursor.getColumnIndex(KEY_FRIENDS_DISPLAY_NAME)));
                friendData.setFriendJID(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_JID)));
                friendData.setFriendStatus(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_STATUS)));
                friendData.setProfilePicUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_URL)));
                friendData.setProfileThumbUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_THUMB_URL)));
                friendData.setActiveChat(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ACIVE_CHAT))));
                friendData.setTimeOfLastMessage(cursor.getLong(cursor.getColumnIndex(KEY_TIME_OF_LAST_MESSAGE)) + "");
                friendData.setLastMessage(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_LAST_MESSAGE)) + "");
                friendData.setTypeOfLastMessage(cursor.getString(cursor.getColumnIndex(KEY_FRIEND_LAST_MESSAGE_TYPE)) + "");
                friendData.setFriendQBID(cursor.getString(cursor.getColumnIndex(KEY_QB_ID)));
                friendData.setIsBlocked(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_BLOCKED))));
                friendData.setIsGroup(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_GROUP))));
                friendData.setIsMute(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IS_MUTE))));
                friends.add(friendData);
            } while (cursor.moveToNext());
        }
        return friends;
    }

    public int getIsMuteStatus(SQLiteDatabase inkeDatabase, String jid) {
        Cursor cursor = inkeDatabase.rawQuery("SELECT " + KEY_IS_MUTE + " FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + " = '" + jid + "'", null);
        if (cursor.moveToFirst()) {
            do {
                int isMute = cursor.getInt(cursor.getColumnIndex(KEY_IS_MUTE));
                return isMute;
            } while (cursor.moveToNext());
        }
        return 0;
    }

    protected int updateWallpaper(SQLiteDatabase database, String id, String wallpaperID) {
        String[] jids = {id};
        ContentValues values = new ContentValues();
        values.put(KEY_WALLPAPER_ID, wallpaperID);
        int flag = database.update(FRIEND_TABLE_NAME, values, KEY_FRIEND_JID + "=?", jids);
        return flag;
    }

    public String getWallpaperFromJID(SQLiteDatabase inkeDatabase, String jid) {
        Cursor cursor = inkeDatabase.rawQuery("SELECT " + KEY_WALLPAPER_ID + " FROM " + FRIEND_TABLE_NAME + " WHERE " + KEY_FRIEND_JID + " = '" + jid + "'", null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(KEY_WALLPAPER_ID));
                return name;
            } while (cursor.moveToNext());
        }
        return "";

    }





}
