package com.datavim.chatapp.model.records;

/**
 * Created by admin on 16-12-2015.
 */
public class BroadcastData {
    public int getBraodcastId() {
        return braodcastId;
    }

    public void setBraodcastId(int braodcastId) {
        this.braodcastId = braodcastId;
    }

    public String getBroadcastName() {
        return broadcastName;
    }

    public void setBroadcastName(String broadcastName) {
        this.broadcastName = broadcastName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getMembersJid() {
        return membersJid;
    }

    public void setMembersJid(String membersJid) {
        this.membersJid = membersJid;
    }

    private int braodcastId;
    private String broadcastName, createdBy, createdOn, members, membersJid;

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }

    private int membersCount;
}
