package com.datavim.chatapp.model.records;

/**
 * Created by apple on 01/06/15.
 */
public class FriendData {

    private int friendId;
    private int friendUserId;
    private String friendName = "";
    private String friendDisplayName = "";
    private String friendJID = "";
    private String friendStatus = "";
    private String profilePicUrl = "";
    private String profileThumbUrl = "";
    private String friendQBID = "";
    private int activeChat = 0;
    private String timeOfLastMessage = "";
    private int isBlocked = 0;
    private int isGroup = 0;
    private int isMute = 0;
    private int isExit = 0;

    public int getIsExit() {
        return isExit;
    }

    public void setIsExit(int isExit) {
        this.isExit = isExit;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    private int isSelected = 0;


    public int getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(int isGroup) {
        this.isGroup = isGroup;
    }

    public int getIsMute() {
        return isMute;
    }

    public void setIsMute(int isMute) {
        this.isMute = isMute;
    }


    public String getTypeOfLastMessage() {
        return typeOfLastMessage;
    }

    public void setTypeOfLastMessage(String typeOfLastMessage) {
        this.typeOfLastMessage = typeOfLastMessage;
    }

    private String typeOfLastMessage = "";

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    private String lastMessage = "";


    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public int getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(int friendUserId) {
        this.friendUserId = friendUserId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendJID() {
        return friendJID;
    }

    public void setFriendJID(String friendJID) {
        this.friendJID = friendJID;
    }

    public String getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(String friendStatus) {
        this.friendStatus = friendStatus;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String frofilePicUrl) {
        this.profilePicUrl = frofilePicUrl;
    }

    public int getActiveChat() {
        return activeChat;
    }

    public void setActiveChat(int activeChat) {
        this.activeChat = activeChat;
    }

    public String getTimeOfLastMessage() {
        return timeOfLastMessage;
    }

    public void setTimeOfLastMessage(String timeOfLastMessage) {
        this.timeOfLastMessage = timeOfLastMessage;
    }

    public String getFriendDisplayName() {
        return friendDisplayName;
    }

    public void setFriendDisplayName(String friendDisplayName) {
        this.friendDisplayName = friendDisplayName;
    }

    public String getProfileThumbUrl() {
        return profileThumbUrl;
    }

    public void setProfileThumbUrl(String profileThumbUrl) {
        this.profileThumbUrl = profileThumbUrl;
    }

    public String getFriendQBID() {
        return friendQBID;
    }

    public void setFriendQBID(String friendQBID) {
        this.friendQBID = friendQBID;
    }

    public int getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(int isBlocked) {
        this.isBlocked = isBlocked;
    }
}
