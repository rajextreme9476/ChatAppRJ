package com.datavim.chatapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datavim.chatapp.comman.Constants;
import com.datavim.chatapp.model.records.ChatData;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by apple on 28/05/15.
 */
public class ChatTable {

    public static final String LOG_TAG = "tblFriend";
    public static final String CHAT_TABLE_NAME = "tblChat";
    public static final String KEY_CHAT_ID = "ChatId";
    public static final String KEY_FROM_JID = "FROMJID";
    public static final String KEY_CHAT_MESSAGE = "ChatMessage";
    public static final String KEY_DISPLAY_NAME = "DisplayName";
    public static final String KEY_TIME_VAL = "TimeVal";
    public static final String KEY_TO_JID = "TOJID";
    public static final String KEY_MESSAGE_TYPE = "MessageType";
    public static final String KEY_MEDIA_ID = "MediaId";
    public static final String KEY_IS_MEDIA_DOWNLOADED = "IsMediaDownloaded";
    public static final String KEY_IS_DELIEVERED = "IsDelivered";
    public static final String KEY_IS_READ = "IsRead";
    public static final String KEY_IS_SCHEDULED = "IsScheduled";
    public static final String KEY_IS_TIMED = "IsTimed";
    public static final String KEY_IS_RETRACTED = "IsRetracted";
    public static final String KEY_TIMED_DURATION = "TimedDuration";
    public static final String KEY_DELIVERD_TIME = "deliveryTime";
    public static final String KEY_READ_TIME = "readTime";
    public static final String KEY_IS_TIMED_EXPIRED = "isTimedExpired";
    public static final String KEY_OPPONENT_CHAT_ID = "OpponentChatId";
    public static final String KEY_IS_INBOX = "isInbox";
    public static final String KEY_MEDIA_URL = "mediaUrl";
    public static final String KEY_ATTACHMENT = "attachment";
    public static final String KEY_BROADCAST_ID = "broadcast_id";

    public static final int MESSAGE_DELEIVERED = 1;

    public static final String CREATE_CHAT_TABLE = "create table "
            + CHAT_TABLE_NAME + "("
            + KEY_CHAT_ID + " integer primary key autoincrement, "
            + KEY_FROM_JID + " text, "
            + KEY_CHAT_MESSAGE + " text, "
            + KEY_DISPLAY_NAME + " text, "
            + KEY_TIME_VAL + " text, "
            + KEY_TO_JID + " text, "
            + KEY_MESSAGE_TYPE + " text, "
            + KEY_MEDIA_ID + " integer default 0 , "
            + KEY_OPPONENT_CHAT_ID + " integer default 0 , "
            + KEY_IS_MEDIA_DOWNLOADED + " text, "
            + KEY_IS_DELIEVERED + " integer default 0, "
            + KEY_IS_READ + " integer default 0 ,"
            + KEY_IS_TIMED + " integer default 0, "
            + KEY_IS_SCHEDULED + " integer default 0, "
            + KEY_IS_RETRACTED + " integer default 0, "
            + KEY_TIMED_DURATION + " integer default 0,"
            + KEY_DELIVERD_TIME + " text ,"
            + KEY_READ_TIME + " text ,"
            + KEY_IS_TIMED_EXPIRED + " integer default 0 , "
            + KEY_IS_INBOX + " integer default 1,"
            + KEY_MEDIA_URL + " text,"
            + KEY_ATTACHMENT + " text,"
            + KEY_BROADCAST_ID + " int default -1"
            +" );";


    public static String GET_CHAT_COUNT= "SELECT * FROM tblChat WHERE FROMJID='%s' AND TOJID='%s'";

    public static String GET_CHAT_HISTORY = "SELECT * FROM tblChat WHERE FROMJID='%s' OR TOJID='%s'";

    public static String CLEAR_CHAT_HISTORY = "DELETE FROM tblChat WHERE FROMJID='%s' OR TOJID='%s'";


    public static String GET_COUNT_UNREAD_MESSAGE = "SELECT * FROM tblChat WHERE FROMJID='%s' AND isInbox = 1 ";

    public static String GET_LAST_MESSAGE_FROM_CONTACT = "SELECT * FROM tblChat WHERE FROMJID='%s' OR TOJID='%s' ORDER BY " + KEY_CHAT_ID + " DESC LIMIT 1";

    public static String GET_CHAT_MESSAGE = "SELECT * FROM tblChat WHERE " + KEY_CHAT_ID + " = ";

    public static String GET_UNDELIVERED_CHATS = "SELECT * FROM tblChat WHERE IsDelivered=-1 AND TOJID='%s' AND MessageType='%s' ";

    // public static String GET_UNDELIVERED_VIDEO_CHATS = "SELECT * FROM tblChat WHERE IsDelivered=-1 AND TOJID='%s'";

    public static String GET_CHATS_FROM_CHATID = "SELECT * FROM "+CHAT_TABLE_NAME+" WHERE "+KEY_CHAT_ID+"='%s'";

    public static String GET_UNREAD_MESSAGE = "SELECT * FROM tblChat WHERE FROMJID='%s' AND isInbox = 1";

    public static String GET_ISMUTE = "SELECT * FROM tblChat WHERE FROMJID='%s' AND isInbox = 1";

    public static String DELETE_CHATS_CONVERSATIONS = "DELETE FROM "+CHAT_TABLE_NAME+"";

    public static String DELETE_CHATS_CONVERSATIONS_SEQ= "DELETE FROM SQLITE_SEQUENCE WHERE name="+CHAT_TABLE_NAME+"";

    private final String GET_IF_CHAT_EXIST = "SELECT * FROM " + CHAT_TABLE_NAME + " WHERE " + KEY_TIME_VAL + "='%s'";

    public static String GET_UNREAD_ALL_MESSAGE = "SELECT * FROM tblChat WHERE isInbox = 1";




    protected long addChatMessage(SQLiteDatabase db, ChatData chatData) {
        Cursor cursor = db.rawQuery(String.format(GET_IF_CHAT_EXIST, chatData.getTimeVal().toString()), null);
        long chatId = -1;
        if (cursor.getCount() == 0) {
            try {
                ContentValues values = new ContentValues();
                values.put(KEY_MESSAGE_TYPE, chatData.getMessageType());
                values.put(KEY_TIME_VAL, chatData.getTimeVal());
                values.put(KEY_DISPLAY_NAME, chatData.getDisplayName());
                values.put(KEY_CHAT_MESSAGE, chatData.getChatMessage());
                values.put(KEY_FROM_JID, chatData.getFromJID());
                values.put(KEY_TO_JID, chatData.gettOJID());
                values.put(KEY_IS_MEDIA_DOWNLOADED, chatData.getIsMediaDownloaded());
                values.put(KEY_IS_DELIEVERED, chatData.getIsDelivered());
                values.put(KEY_IS_READ, chatData.getIsRead());
                values.put(KEY_OPPONENT_CHAT_ID, chatData.getOpponentChatId());
                values.put(KEY_IS_TIMED, chatData.getIsTimedMsg());
                values.put(KEY_IS_SCHEDULED, chatData.getIsScheduledMsg());
                values.put(KEY_TIMED_DURATION, chatData.getTimedMsgDuration());
                values.put(KEY_IS_RETRACTED, chatData.getIsRetracted());
                values.put(KEY_MEDIA_ID, chatData.getMediaId());
                values.put(KEY_MEDIA_URL, chatData.getMediaUrl());
                values.put(KEY_ATTACHMENT, chatData.getAttachment());
                chatId = db.insert(CHAT_TABLE_NAME, null, values);

            } catch (Exception e) {
                Log.d(LOG_TAG, e.toString());
                chatId = -1;
            }}
            return chatId;
        }

    protected long updateDelieveryStatus(SQLiteDatabase db, ChatData chat) {

        String where = KEY_CHAT_ID + " = ?";
        String[] selection = {chat.getChatId() + ""};
        int chatId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IS_DELIEVERED, 1);
            values.put(KEY_DELIVERD_TIME, chat.getTimeVal());
            chatId = db.update(CHAT_TABLE_NAME, values, where, selection);

        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            chatId = -1;
        }
        return chatId;
    }

    protected long updateReadStatus(SQLiteDatabase db, ChatData chat) {

        String where = KEY_CHAT_ID + " = ?";
        String[] selection = {chat.getChatId() + ""};
        int chatId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IS_READ, 1);
            values.put(KEY_READ_TIME, chat.getTimeVal());
            chatId = db.update(CHAT_TABLE_NAME, values, where, selection);

        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            chatId = -1;
        }
        return chatId;
    }



    protected List<ChatData> getChats(SQLiteDatabase database, String JID) {

        List<ChatData> chatDataList = new ArrayList<ChatData>();
        Cursor cursor = database.rawQuery(String.format(GET_CHAT_HISTORY, JID, JID), null);

        if (cursor.moveToFirst()) {
            do {
                ChatData chatData = new ChatData();
                chatData.setChatId(Integer.parseInt(cursor.getString(0)));
                chatData.setFromJID(cursor.getString(1));
                chatData.setChatMessage(cursor.getString(2));
                chatData.setDisplayName(cursor.getString(3));
                chatData.setTimeVal(cursor.getString(4));
                chatData.settOJID(cursor.getString(5));
                chatData.setMessageType(cursor.getString(6));
                chatData.setMediaId(Integer.parseInt(cursor.getString(7)));
                chatData.setOpponentChatId(Integer.parseInt(cursor.getString(8)));
                chatData.setIsMediaDownloaded(cursor.getString(9));
                chatData.setIsDelivered(Integer.parseInt(cursor.getString(10)));
                chatData.setIsRead(Integer.parseInt(cursor.getString(11)));
                chatData.setIsTimedMsg(Integer.parseInt(cursor.getString(12)));
                chatData.setIsScheduledMsg(Integer.parseInt(cursor.getString(13)));
                chatData.setIsRetracted(Integer.parseInt(cursor.getString(14)));
                chatData.setTimedMsgDuration(Integer.parseInt(cursor.getString(15)));
                chatData.setDeliveredTime(cursor.getString(16));
                chatData.setReadTime(cursor.getString(17));
                chatData.setIsTimedExpired(Integer.parseInt(cursor.getString(18)));
                chatData.setIsInbox(Integer.parseInt(cursor.getString(19)));
                chatData.setMediaUrl(cursor.getString(20));
                chatData.setAttachment(cursor.getString(21));
                chatDataList.add(chatData);

            } while (cursor.moveToNext());
        }
        return chatDataList;
    }

    protected List<ChatData> getChatsHistory(SQLiteDatabase database, String ToJID, String FROMJID) {

        List<ChatData> chatDataList = new ArrayList<ChatData>();
        Cursor cursor = database.rawQuery(String.format("SELECT * FROM tblChat WHERE TOJID='" + ToJID + "' AND FROMJID='" + FROMJID + "'"), null);

        if (cursor.moveToFirst()) {
            do {
                ChatData chatData = new ChatData();
                chatData.setChatId(Integer.parseInt(cursor.getString(0)));
                chatData.setFromJID(cursor.getString(1));
                chatData.setChatMessage(cursor.getString(2));
                chatData.setDisplayName(cursor.getString(3));
                chatData.setTimeVal(cursor.getString(4));
                chatData.settOJID(cursor.getString(5));
                chatData.setMessageType(cursor.getString(6));
                chatData.setMediaId(Integer.parseInt(cursor.getString(7)));
                chatData.setOpponentChatId(Integer.parseInt(cursor.getString(8)));
                chatData.setIsMediaDownloaded(cursor.getString(9));
                chatData.setIsDelivered(Integer.parseInt(cursor.getString(10)));
                chatData.setIsRead(Integer.parseInt(cursor.getString(11)));
                chatData.setIsTimedMsg(Integer.parseInt(cursor.getString(12)));
                chatData.setIsScheduledMsg(Integer.parseInt(cursor.getString(13)));
                chatData.setIsRetracted(Integer.parseInt(cursor.getString(14)));
                chatData.setTimedMsgDuration(Integer.parseInt(cursor.getString(15)));
                chatData.setDeliveredTime(cursor.getString(16));
                chatData.setReadTime(cursor.getString(17));
                chatData.setIsTimedExpired(Integer.parseInt(cursor.getString(18)));
                chatData.setIsInbox(Integer.parseInt(cursor.getString(19)));
                chatData.setMediaUrl(cursor.getString(20));
                chatData.setAttachment(cursor.getString(21));
                chatDataList.add(chatData);

            } while (cursor.moveToNext());
        }
        return chatDataList;
    }



    protected int getCountUnreadMessage(SQLiteDatabase database, String JID) {

        String query = String.format(GET_COUNT_UNREAD_MESSAGE, JID);
        Cursor cursor = database.rawQuery(query, null);
        int getCount = 0;
        try {
            getCount = cursor.getCount();
        } catch (NullPointerException npe) {
            Log.e(LOG_TAG, npe.toString());
            getCount = 0;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            getCount = 0;
        } finally {
            cursor.close();
            cursor = null;
        }
        return getCount;
    }

    protected ChatData getLastMessageFromContact(SQLiteDatabase database, String JID) {

        Cursor cursor = database.rawQuery(String.format(GET_LAST_MESSAGE_FROM_CONTACT, JID, JID), null);
        ChatData chatData = null;
        if (cursor.moveToFirst()) {
            do {
                chatData = new ChatData();
                chatData.setChatId(Integer.parseInt(cursor.getString(0)));
                chatData.setFromJID(cursor.getString(1));
                chatData.setChatMessage(cursor.getString(2));
                chatData.setDisplayName(cursor.getString(3));
                chatData.setTimeVal(cursor.getString(4));
                chatData.settOJID(cursor.getString(5));
                chatData.setMessageType(cursor.getString(6));
                chatData.setMediaId(Integer.parseInt(cursor.getString(7)));
                chatData.setOpponentChatId(Integer.parseInt(cursor.getString(8)));
                chatData.setIsMediaDownloaded(cursor.getString(9));
                chatData.setIsDelivered(Integer.parseInt(cursor.getString(10)));
                chatData.setIsRead(Integer.parseInt(cursor.getString(11)));
                chatData.setIsTimedMsg(Integer.parseInt(cursor.getString(12)));
                chatData.setIsScheduledMsg(Integer.parseInt(cursor.getString(13)));
                chatData.setIsRetracted(Integer.parseInt(cursor.getString(14)));
                chatData.setTimedMsgDuration(Integer.parseInt(cursor.getString(15)));
                chatData.setDeliveredTime(cursor.getString(16));
                chatData.setReadTime(cursor.getString(17));

            } while (cursor.moveToNext());
        }
        return chatData;
    }


    public boolean updateRetractMessage(SQLiteDatabase inkeDataBase, ChatData chat) {
        boolean status = false;
        String where = KEY_OPPONENT_CHAT_ID + " = ?";
        String[] selection = {chat.getOpponentChatId() + ""};
        int flag = inkeDataBase.delete(CHAT_TABLE_NAME, where, selection);
        if (flag == -1) {
            return false;
        } else {
            return true;
        }
    }

    public int setRetractMessage(SQLiteDatabase inkeDataBase, ChatData chat) {
        String where = KEY_CHAT_ID + " = ?";
        String[] selection = {chat.getChatId() + ""};
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IS_RETRACTED, 1);
            return inkeDataBase.update(CHAT_TABLE_NAME, values, where, selection);

        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            return -1;
        }
    }

    public int deleteMessage(SQLiteDatabase inkeDataBase, ChatData chat) {
        String where = KEY_CHAT_ID + " = ?";
        String[] selection = {chat.getChatId() + ""};
        try {
            return inkeDataBase.delete(CHAT_TABLE_NAME, where, selection);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            return -1;
        }
    }


    public int deleteMessageContactInfo(SQLiteDatabase inkeDataBase, String JID) {
        String where = KEY_TO_JID + " = ?";
        String[] selection = {JID + ""};
        try {
            return inkeDataBase.delete(CHAT_TABLE_NAME, where, selection);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            return -1;
        }
    }


    public int updateTimedExpire(SQLiteDatabase inkeDataBase, ChatData chat) {
        String where = KEY_CHAT_ID + " = ?";
        String[] selection = {chat.getChatId() + ""};
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IS_TIMED_EXPIRED, 1);
            return inkeDataBase.update(CHAT_TABLE_NAME, values, where, selection);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            return -1;
        }
    }

    public int updateIsInbox(SQLiteDatabase inkeDataBase, ChatData chat) {
        String where = KEY_CHAT_ID + " = ?";
        String[] selection = {chat.getChatId() + ""};
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IS_INBOX, 0);
            return inkeDataBase.update(CHAT_TABLE_NAME, values, where, selection);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            return -1;
        }
    }

    protected boolean clearConversation(SQLiteDatabase db, String JID) {
        try {
            String[] JIDS = {JID, JID};
            db.execSQL(String.format(CLEAR_CHAT_HISTORY, JID, JID));
            return true;

        } catch (Exception e) {
            return false;
        }

    }

    public boolean deleteChatTables(SQLiteDatabase database, String jid) {
        String where = KEY_FROM_JID + " = ?";
        String[] selection = {jid+ ""};
        int flag = database.delete(CREATE_CHAT_TABLE, where, selection);
        if (flag == -1) {
            return false;
        } else {
            return true;
        }
    }


    protected long updateIsMediaDownload(SQLiteDatabase db, ChatData chat) {
        String where = KEY_CHAT_ID + " = ?";
        String[] selection = {chat.getChatId() + ""};
        int chatId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IS_MEDIA_DOWNLOADED, 1);
            chatId = db.update(CHAT_TABLE_NAME, values, where, selection);

        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            chatId = -1;
        }
        return chatId;
    }

    protected long updateIsMediaDownloadById(SQLiteDatabase db, int id) {
        String where = KEY_CHAT_ID + " = ?";
        String[] selection = {id + ""};
        int chatId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IS_MEDIA_DOWNLOADED, 1);
            chatId = db.update(CHAT_TABLE_NAME, values, where, selection);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            chatId = -1;
        }
        return chatId;
    }

    protected long updateIsMediaDownloadInProgress(SQLiteDatabase db, ChatData chat) {
        String where = KEY_CHAT_ID + " = ?";
        String[] selection = {chat.getChatId() + ""};
        int chatId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IS_MEDIA_DOWNLOADED, 2);
            chatId = db.update(CHAT_TABLE_NAME, values, where, selection);

        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            chatId = -1;
        }
        return chatId;

    }


    public ChatData getChat(SQLiteDatabase inkeDataBase, String chatId) {

        Cursor cursor = inkeDataBase.rawQuery(GET_CHAT_MESSAGE + chatId, null);
        ChatData chatData = null;
        if (cursor.moveToFirst()) {
            do {
                chatData = new ChatData();
                chatData.setChatId(Integer.parseInt(cursor.getString(0)));
                chatData.setFromJID(cursor.getString(1));
                chatData.setChatMessage(cursor.getString(2));
                chatData.setDisplayName(cursor.getString(3));
                chatData.setTimeVal(cursor.getString(4));
                chatData.settOJID(cursor.getString(5));
                chatData.setMessageType(cursor.getString(6));
                chatData.setMediaId(Integer.parseInt(cursor.getString(7)));
                chatData.setOpponentChatId(Integer.parseInt(cursor.getString(8)));
                chatData.setIsMediaDownloaded(cursor.getString(9));
                chatData.setIsDelivered(Integer.parseInt(cursor.getString(10)));
                chatData.setIsRead(Integer.parseInt(cursor.getString(11)));
                chatData.setIsTimedMsg(Integer.parseInt(cursor.getString(12)));
                chatData.setIsScheduledMsg(Integer.parseInt(cursor.getString(13)));
                chatData.setIsRetracted(Integer.parseInt(cursor.getString(14)));
                chatData.setTimedMsgDuration(Integer.parseInt(cursor.getString(15)));
                chatData.setDeliveredTime(cursor.getString(16));
                chatData.setReadTime(cursor.getString(17));
                chatData.setMediaUrl(cursor.getString(cursor.getColumnIndex(KEY_MEDIA_URL)));
            } while (cursor.moveToNext());
        }
        return chatData;
    }

    protected List<ChatData> getundeliveredChats(SQLiteDatabase database, String JID, String msgType) {

        List<ChatData> chatDataList = new ArrayList<ChatData>();
        Cursor cursor=null;
        msgType= Constants.UNDELIVERTYPE;
       cursor = database.rawQuery(String.format(GET_UNDELIVERED_CHATS, JID,msgType), null);

        if (cursor.moveToFirst()) {
            do {
                ChatData chatData = new ChatData();
                //    if (cursor.getString(6).equalsIgnoreCase(msgType)) {
                chatData.setChatId(Integer.parseInt(cursor.getString(0)));
                chatData.setFromJID(cursor.getString(1));
                chatData.setChatMessage(cursor.getString(2));
                chatData.setDisplayName(cursor.getString(3));
                chatData.setTimeVal(cursor.getString(4));
                chatData.settOJID(cursor.getString(5));
                chatData.setMessageType(cursor.getString(6));
                chatData.setMediaId(Integer.parseInt(cursor.getString(7)));
                chatData.setOpponentChatId(Integer.parseInt(cursor.getString(8)));
                chatData.setIsMediaDownloaded(cursor.getString(9));
                chatData.setIsDelivered(Integer.parseInt(cursor.getString(10)));
                chatData.setIsRead(Integer.parseInt(cursor.getString(11)));
                chatData.setIsTimedMsg(Integer.parseInt(cursor.getString(12)));
                chatData.setIsScheduledMsg(Integer.parseInt(cursor.getString(13)));
                chatData.setIsRetracted(Integer.parseInt(cursor.getString(14)));
                chatData.setTimedMsgDuration(Integer.parseInt(cursor.getString(15)));
                chatData.setDeliveredTime(cursor.getString(16));
                chatData.setReadTime(cursor.getString(17));
                chatData.setIsTimedExpired(Integer.parseInt(cursor.getString(18)));
                chatData.setIsInbox(Integer.parseInt(cursor.getString(19)));
                chatData.setMediaUrl(cursor.getString(20));
                chatData.setAttachment(cursor.getString(21));
                chatDataList.add(chatData);
                //      }
            } while (cursor.moveToNext());
        }
        return chatDataList;
    }

    protected long updateIsMediaDelivered(SQLiteDatabase db, long id, String mediaUrl) {
        String where = KEY_CHAT_ID + " = ?";
        String[] selection = {id + ""};
        int chatId = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IS_DELIEVERED, 0);
            values.put(KEY_MEDIA_URL, mediaUrl);
            chatId = db.update(CHAT_TABLE_NAME, values, where, selection);

        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            chatId = -1;
        }
        return chatId;
    }

    protected ChatData getChatsFromChatID(SQLiteDatabase database, long chatID) {

        Cursor cursor = database.rawQuery(String.format(GET_CHATS_FROM_CHATID, chatID), null);
        ChatData chatData = null;

        if (cursor.moveToFirst()) {
            do {
                chatData = new ChatData();
                chatData.setChatId(Integer.parseInt(cursor.getString(0)));
                chatData.setFromJID(cursor.getString(1));
                chatData.setChatMessage(cursor.getString(2));
                chatData.setDisplayName(cursor.getString(3));
                chatData.setTimeVal(cursor.getString(4));
                chatData.settOJID(cursor.getString(5));
                chatData.setMessageType(cursor.getString(6));
                chatData.setMediaId(Integer.parseInt(cursor.getString(7)));
                chatData.setOpponentChatId(Integer.parseInt(cursor.getString(8)));
                chatData.setIsMediaDownloaded(cursor.getString(9));
                chatData.setIsDelivered(Integer.parseInt(cursor.getString(10)));
                chatData.setIsRead(Integer.parseInt(cursor.getString(11)));
                chatData.setIsTimedMsg(Integer.parseInt(cursor.getString(12)));
                chatData.setIsScheduledMsg(Integer.parseInt(cursor.getString(13)));
                chatData.setIsRetracted(Integer.parseInt(cursor.getString(14)));
                chatData.setTimedMsgDuration(Integer.parseInt(cursor.getString(15)));
                chatData.setDeliveredTime(cursor.getString(16));
                chatData.setReadTime(cursor.getString(17));
                chatData.setIsTimedExpired(Integer.parseInt(cursor.getString(18)));
                chatData.setIsInbox(Integer.parseInt(cursor.getString(19)));
                chatData.setMediaUrl(cursor.getString(20));
                chatData.setAttachment(cursor.getString(21));
            } while (cursor.moveToNext());
        }
        return chatData;
    }



    protected long updateIsSendMediaDownload(SQLiteDatabase db, long id, int status) {
        String where = KEY_CHAT_ID + " = ?";
        String[] selection = {id + ""};
        int chatId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IS_MEDIA_DOWNLOADED, String.valueOf(status));
            chatId = db.update(CHAT_TABLE_NAME, values, where, selection);

        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            chatId = -1;
        }
        return chatId;
    }

    public int updateIsRead(SQLiteDatabase inkeDataBase, String toJID) {
        String where = KEY_FROM_JID + " = ?";
        String[] selection = {toJID + ""};
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IS_INBOX,0);
            return inkeDataBase.update(CHAT_TABLE_NAME, values, where, selection);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            return -1;
        }
    }

    public int updateScheduleId(SQLiteDatabase inkeDataBase, int chatId , int scheduleId) {
        String where = KEY_CHAT_ID + " = ?";
        String[] selection = {chatId + ""};
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IS_SCHEDULED,scheduleId);
            return inkeDataBase.update(CHAT_TABLE_NAME, values, where, selection);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            return -1;
        }
    }

    protected int getUnreadMessage(SQLiteDatabase database, String JID) {

        String query = String.format(GET_UNREAD_MESSAGE, JID);
        Cursor cursor = database.rawQuery(query, null);
        int getCount = 0;
        try {
            getCount = cursor.getCount();
        } catch (NullPointerException npe) {
            Log.e(LOG_TAG, npe.toString());
            getCount = 0;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            getCount = 0;
        } finally {
            cursor.close();
            cursor = null;
        }
        return getCount;
    }

    protected int getUnreadAllMessage(SQLiteDatabase database) {

        String query = String.format(GET_UNREAD_ALL_MESSAGE);
        Cursor cursor = database.rawQuery(query, null);
        int getCount = 0;
        try {
            getCount = cursor.getCount();
        } catch (NullPointerException npe) {
            Log.e(LOG_TAG, npe.toString());
            getCount = 0;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            getCount = 0;
        } finally {
            cursor.close();
            cursor = null;
        }
        return getCount;
    }

    public int deleteChatFromChatList(SQLiteDatabase inkeDataBase, String fromJID, String toJID) {
    String where = KEY_FROM_JID + " = ?"+ " AND "+KEY_TO_JID+" = ?";
    String[] selection = {fromJID + "",toJID+""};
    try {
        return inkeDataBase.delete(CHAT_TABLE_NAME, where, selection);
    } catch (Exception e) {
        Log.d(LOG_TAG, e.toString());
        return -1;
    }
    }


    protected List<ChatData> getChatsCount(SQLiteDatabase database, String tojid, String fromjid) {

        List<ChatData> chatDataList = new ArrayList<ChatData>();
        Cursor cursor = database.rawQuery(String.format(GET_CHAT_COUNT, fromjid, tojid), null);

        if (cursor.moveToFirst()) {
            do {
                ChatData chatData = new ChatData();
                chatData.setChatId(Integer.parseInt(cursor.getString(0)));
                chatData.setFromJID(cursor.getString(1));
                chatData.setChatMessage(cursor.getString(2));
                chatData.setDisplayName(cursor.getString(3));
                chatData.setTimeVal(cursor.getString(4));
                chatData.settOJID(cursor.getString(5));
                chatData.setMessageType(cursor.getString(6));
                chatData.setMediaId(Integer.parseInt(cursor.getString(7)));
                chatData.setOpponentChatId(Integer.parseInt(cursor.getString(8)));
                chatData.setIsMediaDownloaded(cursor.getString(9));
                chatData.setIsDelivered(Integer.parseInt(cursor.getString(10)));
                chatData.setIsRead(Integer.parseInt(cursor.getString(11)));
                chatData.setIsTimedMsg(Integer.parseInt(cursor.getString(12)));
                chatData.setIsScheduledMsg(Integer.parseInt(cursor.getString(13)));
                chatData.setIsRetracted(Integer.parseInt(cursor.getString(14)));
                chatData.setTimedMsgDuration(Integer.parseInt(cursor.getString(15)));
                chatData.setDeliveredTime(cursor.getString(16));
                chatData.setReadTime(cursor.getString(17));
                chatData.setIsTimedExpired(Integer.parseInt(cursor.getString(18)));
                chatData.setIsInbox(Integer.parseInt(cursor.getString(19)));
                chatData.setMediaUrl(cursor.getString(20));
                chatData.setAttachment(cursor.getString(21));
                chatDataList.add(chatData);

            } while (cursor.moveToNext());
        }
        return chatDataList;
    }


    public String deleteChatTable(SQLiteDatabase inkeDatabase) {
        String table=null;
        inkeDatabase.execSQL("delete from "+ CHAT_TABLE_NAME);
        return table;
    }


}


