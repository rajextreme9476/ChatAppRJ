package com.datavim.chatapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.datavim.chatapp.comman.Constants;
import com.datavim.chatapp.model.records.ChatData;
import com.datavim.chatapp.model.records.FriendData;
import com.datavim.chatapp.model.records.MediaData;
import com.datavim.chatapp.utils.PreferenceManager;
import com.datavim.chatapp.utils.UtilityClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;



/**
 * Created by apple on 28/05/15.
 */
public class DatabaseManager {

    public static final String LOG_TAG = "SportODatabaseManager";
    public static DatabaseManager sportoDatabaseManager = null;
    private static SQLiteDatabase inkeDataBase ;
    private Context context;
    private FriendTable friendTable = null;
    private ChatTable chatTable = null;
    private MediaTable mediaTable =null;
    private GroupTable groupTable =null;

    private DatabaseManager(Context context){
        this.context = context;
        InkeDataBaseHelper inkeDataBaseHelper = new InkeDataBaseHelper(context);
        inkeDataBase = inkeDataBaseHelper.getWritableDatabase();
        friendTable = new FriendTable();
        chatTable = new ChatTable();
        mediaTable = new MediaTable();
        groupTable=new GroupTable();
    }

    public static DatabaseManager getInstance(Context context){

        if(sportoDatabaseManager == null){
            sportoDatabaseManager = new DatabaseManager(context);
        }
        return sportoDatabaseManager;
    }

    public void close(){
        inkeDataBase.close();
    }


    public class InkeDataBaseHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = Constants.DB_NAME;

        public InkeDataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ChatTable.CREATE_CHAT_TABLE);
            db.execSQL(FriendTable.CREATE_FRIEND_TABLE);
            db.execSQL(MediaTable.CREATE_MEDIA_TABLE);
            db.execSQL(GroupTable.CREATE_GROUP_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public boolean addFriend(FriendData friendData){
        boolean status = false;
        if(friendTable == null){
            Log.i(LOG_TAG, " not initiated");
            return status;
        }
        status = friendTable.addFriends(inkeDataBase , friendData);
        return status;
    }

    public List<FriendData> getFriends() {

        return friendTable.getFriends(inkeDataBase);
    }

    public List<FriendData> getFPastChats(){

        return friendTable.getChatFriends(inkeDataBase);
    }

    public String getFriendTableDelete(){

        return friendTable.deleteTable(inkeDataBase);

    }


    public String getChatTableDelete(){

        return chatTable.deleteChatTable(inkeDataBase);

    }



    public List<FriendData> getArchivedChats(){

        return friendTable.getArchivedChats(inkeDataBase);
    }


    public List<FriendData> getUserFriends(){

        return friendTable.getUserFriends(inkeDataBase);
    }

    public long addMedia(MediaData mediaData){
        return mediaTable.addMedia(inkeDataBase, mediaData);
    }



    public long addChat(ChatData chatData){
        String jid = UtilityClass.getUserName(context);
        if(chatData.getFromJID().equals(jid)){
            jid = chatData.gettOJID();
            if(chatData.getMessageType().equalsIgnoreCase("Location"))
                friendTable.updateLastMessageInfo(inkeDataBase , jid,"You shared a location" ,chatData.getTimeVal(),chatData.getMessageType());
            else if(chatData.getMessageType().equalsIgnoreCase("Video"))
                friendTable.updateLastMessageInfo(inkeDataBase , jid,"You sent a video" ,chatData.getTimeVal(),chatData.getMessageType());
            else if(chatData.getMessageType().equalsIgnoreCase("Image"))
                friendTable.updateLastMessageInfo(inkeDataBase , jid,"You sent an image" ,chatData.getTimeVal(),chatData.getMessageType());
            else
                friendTable.updateLastMessageInfo(inkeDataBase , jid,chatData.getChatMessage() ,chatData.getTimeVal(),chatData.getMessageType());

        }else{
            jid = chatData.getFromJID();
            if(chatData.getMessageType().equalsIgnoreCase("Location"))
                friendTable.updateLastMessageInfo(inkeDataBase , jid,"Shared a location with you" ,chatData.getTimeVal(),chatData.getMessageType());
            else if(chatData.getMessageType().equalsIgnoreCase("Video"))
                friendTable.updateLastMessageInfo(inkeDataBase , jid,"Sent you a video" ,chatData.getTimeVal(),chatData.getMessageType());
            else if(chatData.getMessageType().equalsIgnoreCase("Image"))
                friendTable.updateLastMessageInfo(inkeDataBase , jid,"Sent you an image" ,chatData.getTimeVal(),chatData.getMessageType());
            else
                friendTable.updateLastMessageInfo(inkeDataBase , jid,chatData.getChatMessage() ,chatData.getTimeVal(),chatData.getMessageType());
        }
        return chatTable.addChatMessage(inkeDataBase, chatData);
    }


    public int updateChat(ChatData chatData){
        String jid = "";
        if(chatData.getFromJID().equals(PreferenceManager.getPreference(context, PreferenceManager.KEY_JID))){
            jid = chatData.gettOJID();
        }else{
            jid = chatData.getFromJID();
        }
        friendTable.updateArchiveStatus(inkeDataBase,jid,0);
        if(chatData.getMessageType().equalsIgnoreCase("Location"))
            friendTable.updateLastMessageInfo(inkeDataBase , jid,"Location" ,chatData.getTimeVal(),chatData.getMessageType());
        else
        if(chatData.getMessageType().equalsIgnoreCase("Vcard"))
            friendTable.updateLastMessageInfo(inkeDataBase , jid,"Contact" ,chatData.getTimeVal(),chatData.getMessageType());
        else  if(chatData.getMessageType().equalsIgnoreCase("Audio"))
            friendTable.updateLastMessageInfo(inkeDataBase , jid,"Audio" ,chatData.getTimeVal(),chatData.getMessageType());
        else if(chatData.getMessageType().equalsIgnoreCase("Video"))
            friendTable.updateLastMessageInfo(inkeDataBase , jid,"Video" ,chatData.getTimeVal(),chatData.getMessageType());
        else if(chatData.getMessageType().equalsIgnoreCase("Sticker"))
            friendTable.updateLastMessageInfo(inkeDataBase , jid,"Sticker" ,chatData.getTimeVal(),chatData.getMessageType());
        else if(chatData.getMessageType().equalsIgnoreCase("Ping"))
            friendTable.updateLastMessageInfo(inkeDataBase , jid,"Ping" ,chatData.getTimeVal(),chatData.getMessageType());
        else if(chatData.getMessageType().equalsIgnoreCase("Image"))
            friendTable.updateLastMessageInfo(inkeDataBase , jid,"Image" ,chatData.getTimeVal(),chatData.getMessageType());
        else

            friendTable.updateLastMessageInfo(inkeDataBase , jid,chatData.getChatMessage() ,chatData.getTimeVal(),chatData.getMessageType());
        return 0;
    }


    public long updateDeliveryReceipt(ChatData chatData){
        return chatTable.updateDelieveryStatus(inkeDataBase, chatData);
    }

    public long updateReadReceived(ChatData chatData){

        return chatTable.updateReadStatus(inkeDataBase, chatData);
    }

    public List<ChatData> getChatHistory(String JID){
        return chatTable.getChats(inkeDataBase, JID);
    }

    public ChatData getLastMessageFromContact(String JID){
        return chatTable.getLastMessageFromContact(inkeDataBase, JID);
    }

    public boolean updateRetractMessage(ChatData chat){
        boolean flag = chatTable.updateRetractMessage(inkeDataBase, chat);

        ChatData chatData = chatTable.getLastMessageFromContact(inkeDataBase,chat.getFromJID());
        String jid = chatData.getFromJID();
        if(chatData.getMessageType().equalsIgnoreCase("Location"))
            friendTable.updateLastMessageInfo(inkeDataBase , jid,"Location" ,chatData.getTimeVal(),chatData.getMessageType());
        else
        if(chatData.getMessageType().equalsIgnoreCase("Vcard"))
            friendTable.updateLastMessageInfo(inkeDataBase , jid,"Contact" ,chatData.getTimeVal(),chatData.getMessageType());
        else  if(chatData.getMessageType().equalsIgnoreCase("Audio"))
            friendTable.updateLastMessageInfo(inkeDataBase , jid,"Audio" ,chatData.getTimeVal(),chatData.getMessageType());
        else if(chatData.getMessageType().equalsIgnoreCase("Video"))
            friendTable.updateLastMessageInfo(inkeDataBase , jid,"Video" ,chatData.getTimeVal(),chatData.getMessageType());
        else if(chatData.getMessageType().equalsIgnoreCase("Stickers"))
            friendTable.updateLastMessageInfo(inkeDataBase , jid,"Sticker" ,chatData.getTimeVal(),chatData.getMessageType());
        else if(chatData.getMessageType().equalsIgnoreCase("Ping"))
            friendTable.updateLastMessageInfo(inkeDataBase , jid,"Ping" ,chatData.getTimeVal(),chatData.getMessageType());
        else if(chatData.getMessageType().equalsIgnoreCase("Image"))
            friendTable.updateLastMessageInfo(inkeDataBase , jid,"Image" ,chatData.getTimeVal(),chatData.getMessageType());
        else
            friendTable.updateLastMessageInfo(inkeDataBase , jid,chatData.getChatMessage() ,chatData.getTimeVal(),chatData.getMessageType());
        return flag;
    }

    public int setRetractMessage(ChatData chat){
        return chatTable.setRetractMessage(inkeDataBase, chat);
    }

    public int deleteMessage(ChatData chat){
        return chatTable.deleteMessage(inkeDataBase, chat);
    }


    public int deleteMessageformInfo(String chat){
        return chatTable.deleteMessageContactInfo(inkeDataBase, chat);
    }

    public int updateTimedExpired(ChatData chat){
        return chatTable.updateTimedExpire(inkeDataBase, chat);
    }

    public int updateIsInbox(ChatData chat){
        return chatTable.updateIsInbox(inkeDataBase, chat);
    }

    //Media
    public MediaData getMediaDataFromMediaID(long mediaID){
        return mediaTable.getMediaFromMediaID(inkeDataBase, mediaID);
    }

    public long updateMediaIsDownlaod(ChatData chatData){
        return chatTable.updateIsMediaDownload(inkeDataBase, chatData);
    }

    public long updateMediaIsDownlaodByID(int chatID){
        return chatTable.updateIsMediaDownloadById(inkeDataBase, chatID);
    }
    public long updateImageIsDownlaod(long mediaID,String path){
        return mediaTable.updateMediaLocalPath(inkeDataBase,path,mediaID);
    }

    public long updateWallpaper(String friendJID, String id){
        return friendTable.updateWallpaper(inkeDataBase,friendJID,id);
    }


    public long updateMediaIsDownlaodInProgress(ChatData chatData){
        return chatTable.updateIsMediaDownloadInProgress(inkeDataBase, chatData);
    }




    public void exportDatabse() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//"+context.getPackageName()+"//databases//"+Constants.DB_NAME+"";
                String backupDBPath = Constants.DB_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
    }

    public int getUnReadCount(String JID){
        return chatTable.getCountUnreadMessage(inkeDataBase,JID);
    }

    public int updateMuteStatus(String JID , int value){
        return friendTable.updateMuteStatus(inkeDataBase, JID, value);
    }

    public List<FriendData> getGroupChats(){
        return friendTable.getChatGroups(inkeDataBase);
    }

    public List<FriendData> getChatOnlyFriends(){
        return friendTable.getChatOnlyFriends(inkeDataBase);
    }

    public List<FriendData> getUserGroups(){
        return friendTable.getUserGroups(inkeDataBase);
    }

    public List<FriendData> getOnlyFriends(){
        return friendTable.getOnlyFriends(inkeDataBase);
    }

    public boolean clearChatHistory(String JID, String time) {
        if( chatTable.clearConversation(inkeDataBase, JID))
        {
            friendTable.updateLastMessageInfo(inkeDataBase,JID,"",time,"");
            return true;
        }else
            return false;
    }

    public void deleteChatsHistory(String JID) {
        friendTable.deleteChats(inkeDataBase, JID);
        chatTable.clearConversation(inkeDataBase, JID);

    }

    public void deleteFriend(String JID) {
        friendTable.deleteFriends(inkeDataBase,JID);
    }


    public void deletechatTable(String JID) {
        chatTable.deleteChatTables(inkeDataBase,JID);
    }


    public int hideFromChatList(String JID){
        return friendTable.hideFromChatList(inkeDataBase, JID);
    }


    public int BlockUser(String JID){
        return friendTable.blockFromChatList(inkeDataBase, JID);
    }

    public int getBlockFriendStatus(String JID){
        return friendTable.getBlockStatus(inkeDataBase, JID);
    }


    public int UnblockUser(String JID){
        return friendTable.unBlockFromChatList(inkeDataBase, JID);
    }


    public ChatData getChat(String chatId) {
        return chatTable.getChat(inkeDataBase,chatId);
    }


    public List<MediaData> getShareMedia(long mediaID){
        return mediaTable.getShareMedia(inkeDataBase, mediaID);
    }

    public String getFriendNameFromJID(String JID){
        return friendTable.getFriendNameFromJID(inkeDataBase,JID);
    }

    public String getFriendJidFromFriendUserId(String ID){
        return friendTable.getFriendJIDFromFriendUSERID(inkeDataBase,ID);
    }



    public String getFriendStatusFromJID(String JID){
        return friendTable.getFriendStatusFromJID(inkeDataBase,JID);
    }

    public int UpdateEXIT(String JID){
        return friendTable.updateExit(inkeDataBase,JID);
    }



    public String updateGroupNameFromJID(String JID, String groupName){
        return friendTable.updateGroupNameFromJID(inkeDataBase,groupName,JID);
    }

    public String updateContactPicUrl(String JID, String url){
        return friendTable.updateContactPhotoFromJID(inkeDataBase,url,JID);
    }


    public List<ChatData> getUndeliveredChats(String JID , String msgType){
        return chatTable.getundeliveredChats(inkeDataBase, JID ,msgType);
    }

    public long updateIsMediaDelivered(long chatID,String mediaUrl){
        return chatTable.updateIsMediaDelivered(inkeDataBase, chatID, mediaUrl);
    }

    public ChatData getChatFromChatID(long chatID){
        return chatTable.getChatsFromChatID(inkeDataBase, chatID);
    }

    public List<ChatData> getChatHistoryDB(String tojid, String fromjid){
        return chatTable.getChatsHistory(inkeDataBase, tojid,fromjid);
    }


    public int updateExit(int status , String JID){
        return friendTable.updateExitStatus(inkeDataBase, status,JID);
    }

    public int getExitStatus(String JID){
        return friendTable.getExitStatus(inkeDataBase,JID);
    }


    public FriendData getFriendFromJID(String JID){
        return friendTable.getFriendFromJID(inkeDataBase,JID);
    }

    public long updateIsSendMediaDownload(long chatID,int value){
        return chatTable.updateIsSendMediaDownload(inkeDataBase, chatID, value);
    }

    public int updateArchiveStatus(String jid, int status){
        return friendTable.updateArchiveStatus(inkeDataBase, jid, status);
    }

    public int updateIsRead(String toJID){
        return chatTable.updateIsRead(inkeDataBase, toJID);
    }

    public int getUnReadChatCount(String JID){
        return chatTable.getUnreadMessage(inkeDataBase,JID);
    }

    public int getUnReadAllChatCount(){
        return chatTable.getUnreadAllMessage(inkeDataBase);
    }

    public int getArchiveChatCount(){
        return friendTable.getCountArchivedChats(inkeDataBase);
    }


    public int getIsMuteStatus(String JID){
        return friendTable.getIsMuteStatus(inkeDataBase,JID);
    }

    public int getIsFriendExit(String JID){
        return friendTable.getIsMuteStatus(inkeDataBase,JID);
    }


    public void deleteAllConversations()
    {
        inkeDataBase.delete("tblChat", null, null);
        //inkeDataBase.delete("tblFriend", null, null);
        inkeDataBase.delete("tblMedia", null, null);
        //   inkeDataBase.delete("tblGroup", null, null);

    }

    public int deleteChatFromChatList(String fromJID, String toJID){
        return chatTable.deleteChatFromChatList(inkeDataBase, fromJID,toJID);
    }




    public String getWallPaperFromJID(String JID){
        return friendTable.getWallpaperFromJID(inkeDataBase,JID);
    }


    public String getFriendProfilePic(String JID){
        return friendTable.getProfilePicFromJID(inkeDataBase,JID);
    }


    public int getExitStatusFromDB(String JID){
        return friendTable.getFriendExitStatus(inkeDataBase,JID);
    }

    public String getFriendName(String JID){
        return friendTable.getFriendNameFromJID(inkeDataBase,JID);
    }

    public int updateScheduleId(int chatId ,int scheduleId){
        return chatTable.updateScheduleId(inkeDataBase,chatId,scheduleId);
    }



    public List<ChatData> getChatsCountFromJid(String tojid, String fromjid ){
        return chatTable.getChatsCount(inkeDataBase, tojid, fromjid);
    }

    public List<FriendData> getFriendAvailableDB(String fromjid ){
        return friendTable.getFriendAvailble(inkeDataBase,fromjid);
    }


}
