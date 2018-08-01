package com.datavim.chatapp.model.records;

/**
 * Created by admin on 23-10-2015.
 */
public class MediaData {

    int MediaId;
    String MediaUrl;
    String MediaLocalPath;
    String MediaType;

    public int getMediaId() {
        return MediaId;
    }

    public void setMediaId(int mediaId) {
        MediaId = mediaId;
    }

    public String getMediaUrl() {
        return MediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        MediaUrl = mediaUrl;
    }

    public String getMediaLocalPath() {
        return MediaLocalPath;
    }

    public void setMediaLocalPath(String mediaLocalPath) {
        MediaLocalPath = mediaLocalPath;
    }

    public String getMediaType() {
        return MediaType;
    }

    public void setMediaType(String mediaType) {
        MediaType = mediaType;
    }
}
