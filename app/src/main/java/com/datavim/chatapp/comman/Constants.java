package com.datavim.chatapp.comman;

import java.util.ArrayList;

public class Constants {
    //---------Development url -------------
    public static final int SERVER_PORT = 5222;
    public static final String SERVER_NAME = "13.126.100.171";
    public static final String SERVER_ADDRESS = "13.126.100.171";

    public static final String SERVER_URL = "http://sport-social-dev.ap-southeast-1.elasticbeanstalk.com/sport_social/";



    public static final String DB_PATH = "";
    public static final String DB_NAME = "SportO.db";
    public static final String GCM_SENDER_ID = "692883011384";
    public static final String LEVEL = "Level: ";
    public static final String USER_LEVEL = "user_level";
    public static final String SPORT_RATING = "sporto_rating";
    public static String SESSION_KEY = null;
    public static String AUTHORIZATION = "1697631e-f7e4-4ed6-8a7f-a0f198dc2c1e";
    public static String REFRESH_TOKEN_KEY = "";
    public static final String BEARER = "bearer ";
    public static final String API_KEY = "$2a$04$CmwxqWpKM0qW/veWiF7WsOCD9QAFO8c0dhKcdWxz9wEewLkcaW6kuBJgpH/XodfBDhE2p0OphnzzYnS0S18eUMh5G9sUYM";
    public static final String API_KEY_ACCESS_TOKEN = "x-access-token";

    /*SIGNUP*/
    public static final String MOBILENO = "mobile_no";
    public static final String COUNTRYCODE = "country_code";
    public static final String RECEIVEOTP = "receive_otp";
    public static final String EMAIL = "email";
    public static final String GENDER = "gender";
    public static final String PASSWORD = "password";
    public static final String PROFILEPICPATH = "profilepath";
    public static final String FULLNAME = "fullname";
    public static final String EVENTID = "eventId";
    public static final String LOCATION = "location";
    public static final String SPORTLIST = "list";
    public static final String SPORTNAME = "sport_name";
    public static final String SPORTID = "sport_id";
    public static final String USER_ID = "user_id";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ISBLOCKED = "isblocked";
    public static final String ISEXIT = "isexit";
    /*Location*/
    public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    public static final int FAST_CEILING_IN_SECONDS = 1;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;

    /*SearchSport*/
    public static final String SEARCHTEXT = "search_text";
    public static ArrayList<Integer> SPORTSID = new ArrayList<Integer>();
    public static final String MEDIA_TYPE_IMAGE = "Image";
    public static final String MEDIA_TYPE_AUDIO = "Audio";
    public static final String MEDIA_TYPE_VIDEO = "Video";


    public static final String FromJID = "FromJID";
    public static final String ToJID = "ToJID";

    /*Chat Conversation*/
    public static final String FRIEND_JID = "JID";
    public static final String FRIEND_NAME = "NAME";
    public static final int REQUEST_CODE = 2000;
    public static final String FRIEND_ID = "ID";

    public static final int FETCH_STARTED = 2001;
    public static final int FETCH_COMPLETED = 2002;

    public static final String INTENT_EXTRA_ALBUM = "album";
    public static final String INTENT_EXTRA_IMAGES = "images";
    public static final String INTENT_EXTRA_LIMIT = "limit";
    public static final int DEFAULT_LIMIT = 10;
    public static int limit;

    public static final String IMAGEPATH = "imgpath";
    public static final String IMAGEDETAILS = "imgdetails";
    public static final String CURRENTUSER = "currentuser";
    public static final String FROM_ACTIVITY = "fromActivity";

    public static String VEDIO_PATH = "SportO/Media/Video/Sent";
    public static String IMAGE_PATH = "SportO/Media/Images/Sent";
    public static final String MEDIATYPE = "";
    public static String UNDELIVERTYPE = "";
    public static final String FILERECEIVEPATH = "/sdcard/SportO/Media/Images/IMG-SportO00";
    public static final String FILERECEIVEPATH_EVENTPIC = "/sdcard/SportO/Media/Images/IMG-EVENT";
    public static String VIDEO_PATH = "SportO/Media/Video/Sent";
    public static final String FILERECEIVEVIDEOPATH = "/sdcard/SportO/Media/Video/VUD-SportO00";


    public static final String URL_UPLOAD_MEDIA = SERVER_URL + "api/chat_media/upload";
    public static final String URL_DOWNLOAD_MEDIA_IMAGE = SERVER_URL + "api/chat_media/download/";
}
