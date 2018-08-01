package com.datavim.chatapp.model.records;

/**
 * Created by appl on 02/06/15.
 */
public class ChatData {

    public enum ChatMessageType {
        MESSAGE_TYPE_CHAT {
            @Override
            public String toString() {
                return "chat";
            }
        },
        MESSAGE_TYPE_TIMED {
            @Override
            public String toString() {
                return "timed";
            }
        },
        MESSAGE_TYPE_SCHEDULED {
            @Override
            public String toString() {
                return "scheduled";
            }
        },
        MESSAGE_TYPE_DELIVERY_RECEIPT {
            @Override
            public String toString() {
                return "deliveryReceipt";
            }
        }, MESSAGE_TYPE_READ_RECEIPT {
            @Override
            public String toString() {
                return "readReceipt";
            }
        }, MESSAGE_TYPE_RETRACT {
            @Override
            public String toString() {
                return "retract";
            }
        },MESSAGE_TYPE_MEDIA {
            @Override
            public String toString() {
                return "media";
            }
        },MESSAGE_TYPE_STICKERS {
            @Override
            public String toString() {
                return "sticker";
            }
    },MESSAGE_TYPE_LOCATION {
            @Override
            public String toString() {
                return "location";
            }
        },MESSAGE_TYPE_VCARD {
            @Override
            public String toString() {
                return "vCard";
            }
        },MESSAGE_TYPE_MEDIA_IMAGE {
            @Override
            public String toString() {
                return "image";
            }
        },MESSAGE_TYPE_MEDIA_AUDIO {
            @Override
            public String toString() {
                return "audio";
            }
        },MESSAGE_TYPE_MEDIA_VIDEO {
            @Override
            public String toString() {
                return "video";
            }
        },MESSAGE_TYPE_MEDIA_PING {
            @Override
            public String toString() {
                return "ping";
            }
        },MESSAGE_TYPE_MEDIA_DOODLE {
            @Override
            public String toString() {
                return "doodle";
            }
        },MESSAGE_TYPE_DOWNLOAD_FILE {
            @Override
            public String toString() {
                return "downlaod";
            }
        },MESSAGE_TYPE_MEDIA_MULTI_IMAGE {
            @Override
            public String toString() {
                return "multiimages";
            }
        },MESSAGE_TYPE_MEDIA_MULTI_VIDEO {
            @Override
            public String toString() {
                return "multivideos";
            }
        },MESSAGE_TYPE_ADD_ROSTER {
            @Override
            public String toString() {
                return "roster";
            }
        }
   }

    private int chatId;
    private String fromJID;
    private String chatMessage;
    private String displayName;
    private String timeVal;

    private String tOJID;
    private String messageType;
    private long mediaId = 0;
    private String IsMediaDownloaded = "";
    private int isDelivered = 0;
    private int isRead = 0;
    private int opponentChatId = 0;
    private int isTimedMsg = 0;
    private int isScheduledMsg = 0;
    private int timedMsgDuration = 0;
    private int isRetracted = 0;
    private int isTimedExpired = 0;
    private int isInbox = 0;

    public int getBroadcastId() {
        return broadcastId;
    }

    public void setBroadcastId(int broadcastId) {
        this.broadcastId = broadcastId;
    }

    private int broadcastId = -1;
    private String deliveredTime;
    private String readTime;
    private String mediaUrl;
    private String attachment;

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }



    public int getIsInbox() {
        return isInbox;
    }

    public void setIsInbox(int isInbox) {
        this.isInbox = isInbox;
    }

    public int getIsTimedExpired() {
        return isTimedExpired;
    }

    public void setIsTimedExpired(int isTimedExpired) {
        this.isTimedExpired = isTimedExpired;
    }

    public String getReadTime() {
        return readTime;
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }

    public String getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(String deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public int getIsRetracted() {
        return isRetracted;
    }

    public void setIsRetracted(int isRetracted) {
        this.isRetracted = isRetracted;
    }


    public int getIsTimedMsg() {
        return isTimedMsg;
    }

    public void setIsTimedMsg(int isTimedMsg) {
        this.isTimedMsg = isTimedMsg;
    }

    public int getIsScheduledMsg() {
        return isScheduledMsg;
    }

    public void setIsScheduledMsg(int isScheduledMsg) {
        this.isScheduledMsg = isScheduledMsg;
    }

    public int getTimedMsgDuration() {
        return timedMsgDuration;
    }

    public void setTimedMsgDuration(int timedMsgDuration) {
        this.timedMsgDuration = timedMsgDuration;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getFromJID() {
        return fromJID;
    }


    public void setFromJID(String fromJID) {
        this.fromJID = fromJID;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTimeVal() {
        return timeVal;
    }

    public void setTimeVal(String timeVal) {
        this.timeVal = timeVal;
    }

    public String gettOJID() {
        return tOJID;
    }

    public void settOJID(String tOJID) {
        this.tOJID = tOJID;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public long getMediaId() {
        return mediaId;
    }

    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }

    public String getIsMediaDownloaded() {
        return IsMediaDownloaded;
    }

    public void setIsMediaDownloaded(String isMediaDownloaded) {
        IsMediaDownloaded = isMediaDownloaded;
    }

    public int getIsDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(int isDelivered) {
        this.isDelivered = isDelivered;
    }

    public int getOpponentChatId() {
        return opponentChatId;
    }

    public void setOpponentChatId(int opponentChatId) {
        this.opponentChatId = opponentChatId;
    }

}
