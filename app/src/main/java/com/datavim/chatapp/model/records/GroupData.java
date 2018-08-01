package com.datavim.chatapp.model.records;

/**
 * Created by apple on 07/10/15.
 */
public class GroupData {
    private int groupId ;
    private int groupUserId ;
    private String groupName = "";
    private String groupJID =  "" ;
    private String groupStatus = "";
    private String frofilePicUrl = "";
    private int activeChat = 0;
    private String timeOfLastMessage = "";
    private int isBlocked = 0;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupUserId() {
        return groupUserId;
    }

    public void setGroupUserId(int groupUserId) {
        this.groupUserId = groupUserId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupJID() {
        return groupJID;
    }

    public void setGroupJID(String groupJID) {
        this.groupJID = groupJID;
    }

    public String getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(String groupStatus) {
        this.groupStatus = groupStatus;
    }

    public String getFrofilePicUrl() {
        return frofilePicUrl;
    }

    public void setFrofilePicUrl(String frofilePicUrl) {
        this.frofilePicUrl = frofilePicUrl;
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

    public int getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(int isBlocked) {
        this.isBlocked = isBlocked;
    }
}
