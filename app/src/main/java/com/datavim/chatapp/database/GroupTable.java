package com.datavim.chatapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datavim.chatapp.model.records.GroupData;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by apple on 07/10/15.
 */
public class GroupTable {

    public static final String LOG_TAG = "tblGroup";

    public static final String GROUP_TABLE_NAME = "tblGroup";

    public static final String KEY_GROUP_ID = "GroupId";
    public static final String KEY_GROUP_USER_ID = "GroupUserId";
    public static final String KEY_GROUP_NAME = "GroupName";
    public static final String KEY_GROUP_JID = "GroupJID";
    public static final String KEY_GROUP_STATUS = "GroupStatus";
    public static final String KEY_PROFILE_PIC_URL = "ProfilePicUrl";
    public static final String KEY_GROUP_LAST_MESSAGE = "LastMesssage";
    public static final String KEY_TIME_OF_LAST_MESSAGE = "TimeOfLastMessage";
    public static final String KEY_IS_BLOCKED = "IsBlocked";



    public static final String CREATE_GROUP_TABLE = "create table "
            + GROUP_TABLE_NAME + "("
            + KEY_GROUP_ID + " integer primary key autoincrement, "
            + KEY_GROUP_USER_ID + " integer, "
            + KEY_GROUP_NAME + " text, "
            + KEY_GROUP_JID + " text UNIQUE, "
            + KEY_GROUP_STATUS + " text, "
            + KEY_PROFILE_PIC_URL + " text, "
            + KEY_GROUP_LAST_MESSAGE+" text defualt null,"
            + KEY_TIME_OF_LAST_MESSAGE + " integer default 0, "
            + KEY_IS_BLOCKED + " integer default 0 );";

    private static final String GET_ALL_FRIRND = "SELECT * FROM " + GROUP_TABLE_NAME +" ORDER BY "+KEY_TIME_OF_LAST_MESSAGE+" DESC";

    private static final String GET_LAST_MESSAGE = "SELECT "+KEY_GROUP_STATUS+"FROM"+GROUP_TABLE_NAME+"WHERE"+ KEY_GROUP_JID+"='%s'";

    private static final String GET_LAST_MESSAGE_TIME = "SELECT "+KEY_GROUP_LAST_MESSAGE+"FROM"+GROUP_TABLE_NAME+"WHERE"+ KEY_GROUP_JID+"='%s'";

    protected int addGroups(SQLiteDatabase database, GroupData group) {
        int status = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_GROUP_USER_ID, group.getGroupUserId());
            values.put(KEY_GROUP_NAME, group.getGroupName());
            values.put(KEY_GROUP_JID, group.getGroupJID());
            values.put(KEY_GROUP_STATUS, group.getGroupStatus());
            values.put(KEY_PROFILE_PIC_URL, group.getFrofilePicUrl());
            values.put(KEY_TIME_OF_LAST_MESSAGE, group.getTimeOfLastMessage());
            values.put(KEY_IS_BLOCKED, group.getIsBlocked());
            status = (int)database.insertOrThrow(GROUP_TABLE_NAME, null, values);

        } catch (Exception e) {
            status = -1;
        }
        return status;
    }

    protected List<GroupData> getGroups(SQLiteDatabase database) {

        List<GroupData> groups = new ArrayList<GroupData>();
        Cursor cursor = database.rawQuery(GET_ALL_FRIRND, null);

        if (cursor.moveToFirst()) {
            do {
                GroupData groupData = new GroupData();
                groupData.setGroupId(Integer.parseInt(cursor.getString(0)));
                groupData.setGroupJID(cursor.getString(1));
                groupData.setGroupName(cursor.getString(2));
                groupData.setGroupJID(cursor.getString(3));
                groupData.setGroupStatus(cursor.getString(4));
                groupData.setFrofilePicUrl(cursor.getString(5));
                groupData.setActiveChat(Integer.parseInt(cursor.getString(6)));
                groupData.setTimeOfLastMessage(cursor.getString(7));
                groupData.setTimeOfLastMessage(cursor.getString(8));
                groupData.setIsBlocked(Integer.parseInt(cursor.getString(9)));
                groups.add(groupData);
            } while (cursor.moveToNext());
        }
        return groups;
    }

    protected String getLastMessageTime(SQLiteDatabase database, String JID)
    {

        String query = String.format(GET_LAST_MESSAGE_TIME, JID);
        Cursor cursor = database.rawQuery(query, null);
        String lastMsgTime = "";
        try
        {
            if(cursor.moveToNext()) {
                lastMsgTime = cursor.getString(Integer.parseInt(cursor.getString(8)));
            }else
            {
                lastMsgTime="";
            }
        }
        catch (NullPointerException e)
        {
            Log.e(LOG_TAG, e.toString());
            lastMsgTime = "";
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, e.toString());
            lastMsgTime = "";
        }
        finally
        {
            cursor.close();
        }
        return lastMsgTime;
    }


    protected String getLastMessage(SQLiteDatabase database, String JID)
    {
        String query = String.format(GET_LAST_MESSAGE, JID);
        Cursor cursor = database.rawQuery(query, null);
        String lastMsg = "";
        try
        {
            if(cursor.moveToNext()) {
                lastMsg = cursor.getString(Integer.parseInt(cursor.getString(7)));
            }else
            {
                lastMsg="";
            }
        }
        catch (NullPointerException e)
        {
            Log.e(LOG_TAG, e.toString());
            lastMsg = "";
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, e.toString());
            lastMsg = "";
        }
        finally
        {
            cursor.close();
        }
        return lastMsg;
    }


}
