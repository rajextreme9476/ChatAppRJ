package com.datavim.chatapp.chat;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.datavim.chatapp.R;
import com.datavim.chatapp.activity.ChatConversation;
import com.datavim.chatapp.comman.Constants;
import com.datavim.chatapp.database.DatabaseManager;
import com.datavim.chatapp.model.records.ChatData;
import com.datavim.chatapp.model.records.ChatMediaResponseModel;
import com.datavim.chatapp.model.records.FriendData;
import com.datavim.chatapp.model.records.MediaData;
import com.datavim.chatapp.utils.ConnectionUtils;
import com.datavim.chatapp.utils.PreferenceManager;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.sm.packet.StreamManagement;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.xdata.Form;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;


/**
 * Created by Raviraj Desai on 09-04-2017.
 */
public class ChatService extends Service implements Thread.UncaughtExceptionHandler {
    public static final int NOTIFICATION_CODE = 1;

    public static final String LOG_TAG = "ChatService";
    private static final String TAG = "BroadcastService";
    private AbstractXMPPConnection xmppConnection;
    private Messenger serviceMessenger = new Messenger(new MessageHandler());
    private ArrayList<Messenger> mClients = new ArrayList<Messenger>(); // Keeps track of all current registered clients.
    private ServiceDiscoveryManager serviceDiscoveryManager;
    private final Handler handler = new Handler();
    private DeliveryReceiptManager inkeDeliveryReceiptManager;
    private ReconnectionManager inkeReconnectionManager;
    public static boolean isConnected, isConnecting, isDisconnected;
    private boolean isAuthenticated = false;
    private String currentWindowJID = "";
    private DatabaseManager databaseManager;
    public static final int MSG_REGISTER_CLIENT = 0x0;
    public static final int ACTIVITY_BOUND = 0x1;
    public static final int USER_AUTHENTICATED = 0x2;
    public static final int USER_UNAUTHENTICATED = 0x40;
    public static final int MSG_UNREGISTER_CLIENT = 0x3;
    public static final int CHAT_SEND_MESSAGE = 0x4;
    public static final int CHAT_RECEIVE_MESSAGE = 0x5;
    public static final int CHAT_SEND_FILE = 0x6;
    public static final int CHAT_RETRACT_MESSAGE = 0x7;
    public static final int CHAT_MESSAGE_READ = 0x8;
    public static final int CHAT_REFRESH_LIST = 0x12;
    public static final int IS_CONNECTED = 0x13;
    public static final int IS_CONNECTING = 0x14;
    public static final int IS_DISCONNECTED = 0x15;
    public static final int CHAT_SEND_LOCATION = 0x17;
    public static final int CHAT_DOWNLOAD_VIDEO_FILE = 0x22;
    public static final int CHAT_DOWNLOAD_AUDIO_FILE = 0x23;
    public static final int CHAT_SEND_MULTI_IMAGE = 0x24;
    public static final int SET_CURRENT_WINDOW_JID = 0x26;
    public static final int CHAT_ADD_ROSTERS = 0x27;
    public static final int CHAT_UPDATE_READ_STATUS = 0x28;
    public static final int ADD_ROASTER_TRUE = 0x29;
    public static final int CREATE_GROUP = 0x9;
    public static final int CREATE_GROUP_SUCCESS = 0x10;
    public static final int CREATE_GROUP_FAIL = 0x11;
    public static final int CONNECT_WITH_ALL_GROUPS = 0x21;
    public static final int CONNECT_WITH_ALL_GROUPS_DATABASE = 0x30;
    public static final int REMOVE_ROSTER = 0x31;

    public static final int REMOVE_USER_FROM_GROUP = 0x32;
    private MultiUserChatManager multiUserChatManager;
    private final String MEDIAID = "mediaid";
    private final String DATE = "date";
    private final String TYPE = "type";
    private final String MEDIATYPE = "mediatype";
    private final String MEDIAURL = "mediaurl";
    private final String DISPLAYNAME = "displayname";
    private final String ISMAP = "ismap";
    private final String ISVCARD = "isvcard";
    private final String ISREAD = "isread";
    private final String ATTACHMENT = "attachment";
    public static String CURRENT_USER = "";
    public static String CURRENT_USER_NAME = "";
    public static final String KEY_RECEIVER = "receiver";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_MESSAGE_TYPE = "message_type";
    public static final String KEY_MESSAGE_DATE = "message_date";
    public static final String KEY_MESSAGE_ID = "message_id";
    public static final String KEY_DOWNLAOD_LINK = "downlaod_link";
    public static final String KEY_FILEPATH = "filepath";
    public static final String KEY_STRING_IMAGE = "strImg";
    public static final String KEY_TUMBNAIL_PATH = "thumbnailPath";
    public static final String KEY_MEDIA_URL = "mediaUrl";
    public static final String KEY_MEDIA_TYPE = "mediaType";
    public static final String KEY_STRING_AUDIO = "strAudio";
    public static final String KEY_IMAGE_LIST = "imgList";
    private String receiver = "";
    ArrayList images;
    private String strImg = "";
    private String downloadLink = "";
    String thumbnailPath = "";
    public static final String KEY_MEDIA_DOWN_URL = "url";
    public static final String KEY_MEDIA_DOWN_CHATID = "chatID";
    public static final String KEY_FRIEND_JID = "friendJID";
    public static final String KEY_FRIEND_NAME = "friendName";
    public static final int NOTIFICATION_ID = 1;
    public static final String KEY_ROOM_NAME = "room_name";
    public static final String KEY_ROOM_JID = "room_jid";
    public static final String KEY_ROOM_PARTICIPANT = "room_participant";
    public static final String CREATE_GROUP_SERVER_NAME = "@conference." + Constants.SERVER_NAME;
    public static final boolean isConnectionInProgress = false;

    private NotificationManager mNotificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return serviceMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void initiateConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                setConnection();

            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "ChatService started");
        //   startForeground(NOTIFICATION_CODE, getNotification());
        databaseManager = DatabaseManager.getInstance(getApplicationContext());
        initiateConnection();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (xmppConnection.isConnected()) {
            xmppConnection.disconnect();
        }
        //  stopForeground(true);
        //    sendBroadcast(new Intent("IWillStartAuto"));
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        super.onTaskRemoved(rootIntent);
        if (xmppConnection != null) {
            if (xmppConnection.isConnected())
                xmppConnection.disconnect();
        }
        //    startService(new Intent(getApplicationContext(), ChatService.class));
    }

    ReentrantLock lock = new ReentrantLock();

    public void setConnection() {
        lock.lock();
        try {
            //     XmppSingleton.getInstance().getConnection().isConnected();
            if (xmppConnection != null) {
                if (xmppConnection.isConnected() && xmppConnection.isAuthenticated()) {
                    return;
                }
            }

            XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
            configBuilder.setServiceName(Constants.SERVER_NAME);
            configBuilder.setHost(Constants.SERVER_ADDRESS);
            configBuilder.setPort(Constants.SERVER_PORT);
            configBuilder.setCompressionEnabled(false);
            configBuilder.setDebuggerEnabled(true);
            configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(new KeyManager[0], new TrustManager[]{new TLSUtils.AcceptAllTrustManager()}, new SecureRandom());
            configBuilder.setCustomSSLContext(sc);
            XMPPTCPConnection.setUseStreamManagementDefault(true);
            ProviderManager.addStreamFeatureProvider(StreamManagement.StreamManagementFeature.ELEMENT, StreamManagement.NAMESPACE, new PacketExtensionProvider<PacketExtension>() {
                @Override
                public PacketExtension parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
                    return null;
                }
            });
            xmppConnection = new XMPPTCPConnection(configBuilder.build());
            inkeReconnectionManager = ReconnectionManager.getInstanceFor(xmppConnection);
            inkeReconnectionManager.enableAutomaticReconnection();
            xmppConnection.setPacketReplyTimeout(60 * 1000);
            xmppConnection.addConnectionListener(connectionListener);
            xmppConnection.addAsyncPacketListener(normalPacketListener, normalPacketFilter);
            xmppConnection.addAsyncPacketListener(chatPacketListener, chatFilter);
            xmppConnection.addAsyncPacketListener(groupchatPacketListener, groupchatFilter);

            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");

            inkeDeliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(xmppConnection);
            inkeDeliveryReceiptManager.autoAddDeliveryReceiptRequests();
            inkeDeliveryReceiptManager.addReceiptReceivedListener(new ReceiptReceivedListener() {
                @Override
                public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
                    try {
                        String chatId = receiptId.substring(receiptId.indexOf('-') + 1);
                        ChatData chat = new ChatData();
                        chat.setChatId(Integer.parseInt(chatId));
                        chat.setDeliveredTime(System.currentTimeMillis() + "");
                        updateDeliveryReceipt(chat);
                        sendMessage(ChatService.CHAT_REFRESH_LIST, null);
                    } catch (Exception e) {

                    }
                }
            });
            xmppConnection.connect();
            Log.i("Credentials", getUsername() + "  " + getChatPwd());


            if (!getUsername().isEmpty() && !getUsername().equalsIgnoreCase("null") && !getUsername().equals(null)) {
                xmppConnection.login(getUsername(), getChatPwd(), System.currentTimeMillis() + "");

                boolean is = xmppConnection.isSecureConnection();

            }
            sendMessage(ADD_ROASTER_TRUE, null);

        } catch (XMPPException e) {
            e.printStackTrace();
            PreferenceManager.savePreference(getApplicationContext(),PreferenceManager.KEY_FIRST_INSTALATION,false);
            xmppConnection.disconnect();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void connected(XMPPConnection connection) {
            Log.i(LOG_TAG, "Connected");
            isConnected = true;
            sendMessage(IS_CONNECTED, null);
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {

            Log.i(LOG_TAG, "Authenticated");
            isAuthenticated = true;
            PingManager pingManager = PingManager.getInstanceFor(connection);
            pingManager.setPingInterval(10 * 1000);
            CURRENT_USER = getUsername();
            CURRENT_USER_NAME = getFullUsername();
            inkeReconnectionManager =  ReconnectionManager.getInstanceFor((AbstractXMPPConnection) connection);
            inkeReconnectionManager.enableAutomaticReconnection();
            getRoster(connection);
            serviceDiscoveryManager = ServiceDiscoveryManager
                    .getInstanceFor(xmppConnection);
            serviceDiscoveryManager.addFeature("http://jabber.org/protocol/disco#info");
            serviceDiscoveryManager.addFeature("http://jabber.org/protocol/disco#items");

            if (serviceDiscoveryManager == null) {

                serviceDiscoveryManager =
                        serviceDiscoveryManager = ServiceDiscoveryManager
                                .getInstanceFor(xmppConnection);
                serviceDiscoveryManager.addFeature("http://jabber.org/protocol/disco#info");
            }
            isConnected = true;
        }

        @Override
        public void connectionClosed() {
            isConnected = false;
            isAuthenticated = false;
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            isConnected = false;
            isAuthenticated = false;
        }

        @Override
        public void reconnectionSuccessful() {
            isConnected = true;
        }

        @Override
        public void reconnectingIn(int seconds) {
            isConnected = false;
            isAuthenticated = false;
        }

        @Override
        public void reconnectionFailed(Exception e) {
            isConnected = false;
        }
    };

    PacketFilter normalPacketFilter = MessageTypeFilter.NORMAL;
    PacketListener normalPacketListener = new PacketListener() {
        @Override
        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
            try {
                org.jivesoftware.smack.packet.Message message = (org.jivesoftware.smack.packet.Message) packet;
                DefaultPacketExtension extension = (DefaultPacketExtension) packet.getExtension("jabber:client");

                if (extension.getValue("isread") != null && extension.getValue("isread").equals("1")) {
                    String id = extension.getValue("chatid");
                    ChatData chat = new ChatData();
                    chat.setChatId(Integer.parseInt(id));
                    chat.setIsRead(1);
                    chat.setReadTime(System.currentTimeMillis() + "");
                    updateReadReceipt(chat);
                }
            } catch (NullPointerException e) {
                Log.e("Error Exception -- " + e.getMessage(), "" + e);
            }
        }

    };


    private void updateReadReceipt(ChatData chat) {
        if (PreferenceManager.getPreference(getApplicationContext(), PreferenceManager.READ_RECEIPT) == null || !PreferenceManager.getPreference(this, PreferenceManager.READ_RECEIPT).equals("false")) {
            databaseManager.updateReadReceived(chat);
            sendMessage(CHAT_REFRESH_LIST, null);
        }
    }

    private void updateDeliveryReceipt(ChatData chat) {
        databaseManager.updateDeliveryReceipt(chat);
        sendMessage(CHAT_REFRESH_LIST, null);
    }

    private String getLoginUsername() {
        String username = PreferenceManager.getPreference(this, PreferenceManager.KEY_JID);
        String[] user = username.split("@");
        return user[0];
    }

    private String getUsername() {
        String username = PreferenceManager.getPreference(this, PreferenceManager.KEY_JID);
        if (username == null) {
            username = "";
        }
        return username;
    }


    private String getFullUsername() {
        String username = PreferenceManager.getPreference(this, PreferenceManager.KEY_NAME);
        return username.toLowerCase();
    }

    private String getChatPwd() {

        String pass = PreferenceManager.getPreference(this, PreferenceManager.KEY_CHAT_PASSWORD);
        return pass;
    }

    private String getDisplayName() {
        String name = PreferenceManager.getPreference(this, PreferenceManager.KEY_NAME);
        return name;
    }

    private String getUserId() {
        String userId = PreferenceManager.getPreference(this, PreferenceManager.KEY_USER_ID);
        return userId;
    }

    public void sendMessage(int messageType, Object message) {
        if (mClients.isEmpty()) {
            return;
        }
        try {
            for (int i = 0; i < mClients.size(); i++) {
                mClients.get(i).send(Message.obtain(null, messageType, message));
            }
        } catch (RemoteException e) {
            // The client is dead. Remove it from the list; we are going through the list from back to front so this is safe to do inside the loop.
            mClients.remove(0);
        }
    }


    private void updateReadStatus(String jid) {
        try {
            databaseManager.updateIsRead(jid);

            sendMessage(CHAT_REFRESH_LIST, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addRoster(String jid, String name) {
        try {


            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
            Roster roster = Roster.getInstanceFor(xmppConnection);
            roster.createEntry(jid, name, null);
            Presence pres = new Presence(Presence.Type.subscribe);
            pres.setFrom(jid);
            xmppConnection.sendPacket(pres);
            getRoster(xmppConnection);

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }
    }

    private void getRoster(final XMPPConnection connection) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(LOG_TAG, "Roster thread started");
                Roster roster = Roster.getInstanceFor(connection);
                if (!roster.isLoaded()) {
                    Log.i(LOG_TAG, "Roster is not loaded");
                    try {
                        roster.reloadAndWait();
                    } catch (SmackException.NotLoggedInException e) {
                        e.printStackTrace();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }
                Log.i(LOG_TAG, "Roster count" + roster.getEntryCount());

                Collection<RosterEntry> entries = roster.getEntries();
                if (entries.size() > 0) {
                    for (RosterEntry entry : entries) {
                        Log.i(LOG_TAG, "Roster name" + entry.getName());
                        FriendData data = new FriendData();
                        data.setFriendJID(entry.getUser());
                        data.setFriendDisplayName("Raviraj");
                        data.setFriendName("Raivraj");
                        data.setFriendUserId(0);
                        data.setProfileThumbUrl("");
                        data.setProfilePicUrl("");
                        data.setFriendStatus("");
                        data.setIsGroup(0);
                        databaseManager.addFriend(data);
                    }
                }
                sendMessage(ADD_ROASTER_TRUE, null);
            }
        }).start();

    }

    PacketFilter chatFilter = MessageTypeFilter.CHAT;
    PacketListener chatPacketListener = new PacketListener() {
        @Override
        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

            Log.i("Received Stanzza", packet.toXML().toString());
            org.jivesoftware.smack.packet.Message message = (org.jivesoftware.smack.packet.Message) packet;
            ChatData chat = new ChatData();
            chat.setChatMessage(message.getBody());
            String chatId = message.getStanzaId().substring(message.getStanzaId().indexOf('-') + 1);
            chat.setOpponentChatId(Integer.parseInt(chatId));
            chat.setFromJID(message.getFrom().substring(0, message.getFrom().indexOf('/')));
            chat.settOJID(message.getTo());


            List<PacketExtension> pes = message.getExtensions();
            PacketExtension pe = message.getExtension("customdata", "jabber:client");
            parsePacketExtension((DefaultPacketExtension) pe, chat);

            FriendData data = new FriendData();
            data.setFriendJID(chat.getFromJID());
            data.setFriendDisplayName(chat.getDisplayName());
            data.setFriendName(chat.getDisplayName());
            data.setFriendUserId(0);
            data.setProfileThumbUrl("");
            data.setProfilePicUrl("");
            data.setFriendStatus("");
            data.setIsGroup(0);
            data.setIsBlocked(0);

            int checkBlock= databaseManager.getBlockFriendStatus(chat.getFromJID());
            if(checkBlock!=1)
            {

                int exit= databaseManager.getExitStatusFromDB(chat.getFromJID());
                if(exit!=1)
                {

                    if (databaseManager.addFriend(data) == true) {
                        addRoster(data.getFriendJID(), data.getFriendName());
                    }


                    if (chat.getMessageType() == null) {
                        chat.setMessageType(message.getType().toString());
                    }
                    //{"IsMediaDownloaded":"","timeVal":"1433572800647","chatMessage":"Hello chat_doodle message","displayName":"messi","fromJID":"messi","tOJID":"ronaldo@liveserver","messageType":"chat","mediaId":0,"isRead":0,"opponentChatId":0,"isDelivered":0,"chatId":3}
                    if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_CHAT.toString())) {
                        saveMessage(chat);
                        //sendDeliveryReceipt(chat);
                    } else if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_DELIVERY_RECEIPT.toString())) {
                        updateDeliveryReceipt(chat);
                        return;
                    } else if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_READ_RECEIPT.toString())) {
                        updateReadReceipt(chat);
                        return;
                    } else if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_IMAGE.toString())) {
                        saveMediaData(chat);
                        // sendDeliveryReceipt(chat);
                    } else if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_LOCATION.toString())) {
                        saveLocationData(chat);
                        // sendDeliveryReceipt(chat);
                    } else if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_AUDIO.toString())) {
                        saveMediaAudioData(chat);
                        // sendDeliveryReceipt(chat);
                    } else if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_VIDEO.toString())) {
                        saveMediaVideoData(chat);
                        //  sendDeliveryReceipt(chat);
                    }
                    sendMessage(CHAT_RECEIVE_MESSAGE, null);
                    showNotification(chat);
                }
            }
        }
    };


    PacketFilter groupchatFilter = MessageTypeFilter.GROUPCHAT;
    PacketListener groupchatPacketListener = new PacketListener() {
        @Override
        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
            Log.i("Group stanzas", packet.toString());
            org.jivesoftware.smack.packet.Message message = (org.jivesoftware.smack.packet.Message) packet;
            if (message.getFrom().contains(CURRENT_USER)) {
                return;
            }

            ChatData chat = new ChatData();
            chat.setChatMessage(message.getBody());
            String chatId = message.getStanzaId().substring(message.getStanzaId().indexOf('-') + 1);
            chat.setOpponentChatId(Integer.parseInt(chatId));
            chat.setFromJID(message.getFrom().substring(0, message.getFrom().indexOf('/')));
            chat.settOJID(message.getTo());
            List<PacketExtension> pes = message.getExtensions();
            PacketExtension pe = message.getExtension("customdata", "jabber:client");
            parsePacketExtension((DefaultPacketExtension) pe, chat);

            List<FriendData> friendDataList= databaseManager.getFriendAvailableDB(chat.getFromJID());

                int exit = databaseManager.getExitStatusFromDB(chat.getFromJID());
                if (exit != 1) {





                    if (chat.getMessageType() == null) {
                        chat.setMessageType(message.getType().toString());
                    }
                    //{"IsMediaDownloaded":"","timeVal":"1433572800647","chatMessage":"Hello chat_doodle message","displayName":"messi","fromJID":"messi","tOJID":"ronaldo@liveserver","messageType":"chat","mediaId":0,"isRead":0,"opponentChatId":0,"isDelivered":0,"chatId":3}
                    if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_CHAT.toString())) {
                        saveMessage(chat);
                        //sendDeliveryReceipt(chat);
                    } else if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_DELIVERY_RECEIPT.toString())) {
                        updateDeliveryReceipt(chat);
                        return;
                    } else if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_READ_RECEIPT.toString())) {
                        //updateReadReceipt(chat);
                        return;
                    } else if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_IMAGE.toString())) {
                        saveMediaData(chat);
                        // sendDeliveryReceipt(chat);
                    } else if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_LOCATION.toString())) {
                        saveLocationData(chat);
                        // sendDeliveryReceipt(chat);
                    } else if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_AUDIO.toString())) {
                        saveMediaAudioData(chat);
                        // sendDeliveryReceipt(chat);
                    } else if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_VIDEO.toString())) {
                        saveMediaVideoData(chat);
                        //  sendDeliveryReceipt(chat);
                    }
                    sendMessage(CHAT_RECEIVE_MESSAGE, null);
                    showNotification(chat);

            }

        }

    };


    private void parsePacketExtension(DefaultPacketExtension pe, ChatData chat) {
        chat.setMediaUrl(pe.getValue(MEDIAURL));
        chat.setDisplayName(pe.getValue(DISPLAYNAME));
        if (pe.getValue(MEDIAID) != null)
            chat.setMediaId(Integer.parseInt(pe.getValue(MEDIAID)));
        String date = pe.getValue(DATE);
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {


                chat.setTimeVal(date);

                /*
                Date dateObject = format.parse(date);
                long millis = dateObject.getTime() + (1000 * 60 * 330);
                if (millis > System.currentTimeMillis())
                    chat.setTimeVal(System.currentTimeMillis() + "");
                else
                    chat.setTimeVal(date);
*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (pe.getValue(MEDIATYPE) != null) {

            chat.setAttachment(pe.getValue(ATTACHMENT));

            if (pe.getValue(MEDIATYPE).equalsIgnoreCase("Audio"))
                chat.setMessageType(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_AUDIO.toString());
            if (pe.getValue(MEDIATYPE).equalsIgnoreCase("Video"))
                chat.setMessageType(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_VIDEO.toString());
            if (pe.getValue(MEDIATYPE).equalsIgnoreCase("Image"))
                chat.setMessageType(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_IMAGE.toString());


            if (pe.getValue(MEDIATYPE).equalsIgnoreCase("MAP"))
                chat.setMessageType(ChatData.ChatMessageType.MESSAGE_TYPE_LOCATION.toString());

            if (pe.getValue(MEDIATYPE).equalsIgnoreCase("Text"))
                chat.setMessageType(ChatData.ChatMessageType.MESSAGE_TYPE_CHAT.toString());

        } else {
            chat.setMessageType(ChatData.ChatMessageType.MESSAGE_TYPE_CHAT.toString());
        }
        if (pe.getValue(ISMAP) != null) {
            chat.setMessageType(ChatData.ChatMessageType.MESSAGE_TYPE_LOCATION.toString());
            String map = chat.getChatMessage().replaceAll("\\n", "@");
            chat.setChatMessage(map);
        }
    }

    private void showNotification(final ChatData chat) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isApplicationSentToBackground(getApplicationContext(), chat.getFromJID()) == false) {
                    return;
                }
                String message = "";
                if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_CHAT.toString())) {
                    message = chat.getChatMessage();
                } else {
                    message = chat.getMessageType();

                    if (message.equalsIgnoreCase("image")) {
                        message = "Sent you an image";
                    } else if (message.equalsIgnoreCase("location")) {
                        message = "Shared a location with you";
                    } else if (message.equalsIgnoreCase("video")) {
                        message = "Sent you a video";
                    }
                }
                FriendData friendData = databaseManager.getFriendFromJID(chat.getFromJID());
                if (friendData.getIsMute() == 1) {
                    return;
                }
                Intent chatScreenIntent;
                if (friendData.getFriendJID().equalsIgnoreCase("")) {

                    FriendData data = new FriendData();
                    //  GetGroupsByUserIDModel groupModel = arrayListGroups.get(i);
                    data.setFriendJID(chat.getFromJID());
                    data.setFriendDisplayName(chat.getDisplayName());
                    data.setFriendName(chat.getDisplayName());
                    // data.setFriendUserId(Integer.parseInt(chat.));
                    data.setProfileThumbUrl("");
                    data.setProfilePicUrl("");
                    data.setFriendStatus("");
                    data.setIsGroup(0);
                    databaseManager.addFriend(data);
                    friendData = databaseManager.getFriendFromJID(chat.getFromJID());
                    chatScreenIntent = new Intent(ChatService.this, ChatConversation.class);
                    chatScreenIntent.putExtra(Constants.FRIEND_JID, friendData.getFriendJID());
                    chatScreenIntent.putExtra(Constants.FRIEND_NAME, friendData.getFriendDisplayName());// other
                } else {

                    if (friendData != null) {
                        chatScreenIntent = new Intent(ChatService.this, ChatConversation.class);
                        chatScreenIntent.putExtra(Constants.FRIEND_JID, friendData.getFriendJID());
                        chatScreenIntent.putExtra(Constants.FRIEND_NAME, friendData.getFriendDisplayName());
                    } else {
                        FriendData data = new FriendData();
                        data.setFriendJID(chat.getFromJID());
                        data.setFriendDisplayName(chat.getDisplayName());
                        data.setFriendName(chat.getDisplayName());
                        // data.setFriendUserId(Integer.parseInt(chat.));
                        data.setProfileThumbUrl("");
                        data.setProfilePicUrl("");
                        data.setFriendStatus("");
                        data.setIsGroup(0);
                        databaseManager.addFriend(data);
                        friendData = databaseManager.getFriendFromJID(chat.getFromJID());
                        chatScreenIntent = new Intent(ChatService.this, ChatConversation.class);
                        chatScreenIntent.putExtra(Constants.FRIEND_JID, friendData.getFriendJID());
                        chatScreenIntent.putExtra(Constants.FRIEND_NAME, friendData.getFriendDisplayName());// other
                    }
                }

                String Profilepic= databaseManager.getFriendProfilePic(chat.getFromJID());
                Bitmap image=null;
                try {
                    URL url = new URL(Profilepic);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch(IOException e) {
                    System.out.println(e);
                }
                mNotificationManager = (NotificationManager) ChatService.this.getSystemService(Context.NOTIFICATION_SERVICE);
                PendingIntent contentIntent = PendingIntent.getActivity(ChatService.this, 0, chatScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder mBuilder;


                String[] part = friendData.getFriendJID().split("@");

                mBuilder = new NotificationCompat.Builder(ChatService.this).setSmallIcon(R.drawable.ic_notofication)
                        .setContentTitle(part[0]).setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setContentText(message);
                mBuilder.setContentIntent(contentIntent);
                mBuilder.setAutoCancel(true);
                int color = getResources().getColor(R.color.red);
                mBuilder.setColor(color);
                mBuilder.setPriority(Notification.PRIORITY_MAX);
                mBuilder.setDefaults(Notification.DEFAULT_SOUND);
                mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
                mBuilder.setDefaults(Notification.DEFAULT_ALL);
                mBuilder.setAutoCancel(true);
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
        }).start();

    }




    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }




    public boolean isApplicationSentToBackground(final Context context, final String fromJID) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;

            } else {
                String currentJID = currentWindowJID;
                if (topActivity.getClassName().equals(ChatConversation.class.getName()) && fromJID != null && currentJID != null && currentJID.equals(fromJID)) {
                    return false;
                } else {
                    return true;
                }
            }
        }

        return false;
    }

    private synchronized void saveMessage(ChatData chat) {

        ChatData chatData = new ChatData();
        chatData.setFromJID(chat.getFromJID());
        chatData.setChatMessage(chat.getChatMessage());
        chatData.settOJID(chat.gettOJID());
        chatData.setDisplayName(chat.getDisplayName());
        chatData.setMessageType(ChatData.ChatMessageType.MESSAGE_TYPE_CHAT.toString());
        chatData.setIsDelivered(chat.getIsDelivered());
        chatData.setOpponentChatId(chat.getOpponentChatId());
        if (chat.getTimeVal() == null)
            chatData.setTimeVal(System.currentTimeMillis() + "");
        else
            chatData.setTimeVal(chat.getTimeVal() + "");
        chatData.setIsTimedMsg(chat.getIsTimedMsg());
        chatData.setIsScheduledMsg(chat.getIsScheduledMsg());
        chatData.setIsRetracted(chat.getIsRetracted());
        chatData.setTimedMsgDuration(chat.getTimedMsgDuration());
        long chatId = databaseManager.addChat(chatData);
    }

    private synchronized void saveLocationData(ChatData chat) {

        ChatData chatData = new ChatData();
        chatData.setFromJID(chat.getFromJID());
        chatData.setChatMessage(chat.getChatMessage());
        chatData.settOJID(chat.gettOJID());
        chatData.setDisplayName(chat.getDisplayName());
        chatData.setMessageType(ChatData.ChatMessageType.MESSAGE_TYPE_LOCATION.toString());
        chatData.setIsDelivered(chat.getIsDelivered());
        chatData.setOpponentChatId(chat.getOpponentChatId());
        if (chat.getTimeVal() == null)
            chatData.setTimeVal(System.currentTimeMillis() + "");
        else
            chatData.setTimeVal(chat.getTimeVal() + "");
        chatData.setIsTimedMsg(chat.getIsTimedMsg());
        chatData.setIsScheduledMsg(chat.getIsScheduledMsg());
        chatData.setIsRetracted(chat.getIsRetracted());
        chatData.setTimedMsgDuration(chat.getTimedMsgDuration());
        String latitude = chat.getChatMessage().substring(0, chat.getChatMessage().indexOf('@')).trim();
        String longitude = chat.getChatMessage().substring(chat.getChatMessage().indexOf('@')).trim();
        String mediaUrl = "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude + "+&markers=icon:http://tinyurl.com/2ftvtt6 %7c " + latitude + "," + longitude + "&zoom=16&size=300x250&sensor=false";
        chatData.setMediaUrl(mediaUrl);
        long chatId = databaseManager.addChat(chatData);
    }

    private synchronized void saveMediaData(ChatData chat) {

        MediaData mediaData = new MediaData();
        mediaData.setMediaUrl(chat.getMediaUrl());
        mediaData.setMediaLocalPath("");
        mediaData.setMediaType(Constants.MEDIA_TYPE_IMAGE);
        long mediaId = databaseManager.addMedia(mediaData);

        ChatData chatData = new ChatData();
        chatData.setFromJID(chat.getFromJID());
        chatData.setChatMessage(chat.getChatMessage());
        chatData.settOJID(chat.gettOJID());
        chatData.setDisplayName(chat.getDisplayName());
        chatData.setMessageType(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_IMAGE.toString());
        chatData.setIsDelivered(chat.getIsDelivered());
        chatData.setOpponentChatId(chat.getOpponentChatId());
        if (chat.getTimeVal() == null)
            chatData.setTimeVal(System.currentTimeMillis() + "");
        else
            chatData.setTimeVal(chat.getTimeVal() + "");
        chatData.setIsTimedMsg(chat.getIsTimedMsg());
        chatData.setIsScheduledMsg(chat.getIsScheduledMsg());
        chatData.setIsRetracted(chat.getIsRetracted());
        chatData.setTimedMsgDuration(chat.getTimedMsgDuration());
        chatData.setMediaId(mediaId);
        chatData.setMediaUrl(chat.getMediaUrl());
        chatData.setAttachment(chat.getAttachment());
        long chatId = databaseManager.addChat(chatData);
        sendMessage(CHAT_RECEIVE_MESSAGE, null);
    }

    private synchronized void saveMediaAudioData(ChatData chat) {

        MediaData mediaData = new MediaData();
        mediaData.setMediaUrl(chat.getMediaUrl());
        mediaData.setMediaLocalPath("");
        mediaData.setMediaType(Constants.MEDIA_TYPE_AUDIO);
        long mediaId = databaseManager.addMedia(mediaData);

        ChatData chatData = new ChatData();
        chatData.setFromJID(chat.getFromJID());
        chatData.setChatMessage(chat.getChatMessage());
        chatData.settOJID(chat.gettOJID());
        chatData.setDisplayName(chat.getDisplayName());
        chatData.setMessageType(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_AUDIO.toString());
        chatData.setIsDelivered(chat.getIsDelivered());
        chatData.setOpponentChatId(chat.getOpponentChatId());
        if (chat.getTimeVal() == null)
            chatData.setTimeVal(System.currentTimeMillis() + "");
        else
            chatData.setTimeVal(chat.getTimeVal() + "");
        chatData.setIsTimedMsg(chat.getIsTimedMsg());
        chatData.setIsScheduledMsg(chat.getIsScheduledMsg());
        chatData.setIsRetracted(chat.getIsRetracted());
        chatData.setTimedMsgDuration(chat.getTimedMsgDuration());
        chatData.setMediaId(mediaId);
        chatData.setMediaUrl(chat.getMediaUrl());
        chatData.setAttachment(chat.getAttachment());
        long chatId = databaseManager.addChat(chatData);
        sendMessage(CHAT_RECEIVE_MESSAGE, null);
    }


    private synchronized void saveMediaVideoData(ChatData chat) {

        MediaData mediaData = new MediaData();
        mediaData.setMediaUrl(chat.getMediaUrl());
        mediaData.setMediaLocalPath("");
        mediaData.setMediaType(Constants.MEDIA_TYPE_VIDEO);
        long mediaId = databaseManager.addMedia(mediaData);

        ChatData chatData = new ChatData();
        chatData.setFromJID(chat.getFromJID());
        chatData.setChatMessage(chat.getChatMessage());
        chatData.settOJID(chat.gettOJID());
        chatData.setDisplayName(chat.getDisplayName());
        chatData.setMessageType(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_VIDEO.toString());
        chatData.setIsDelivered(chat.getIsDelivered());
        chatData.setOpponentChatId(chat.getOpponentChatId());
        if (chat.getTimeVal() == null)
            chatData.setTimeVal(System.currentTimeMillis() + "");
        else
            chatData.setTimeVal(chat.getTimeVal() + "");
        chatData.setIsTimedMsg(chat.getIsTimedMsg());
        chatData.setIsScheduledMsg(chat.getIsScheduledMsg());
        chatData.setIsRetracted(chat.getIsRetracted());
        chatData.setTimedMsgDuration(chat.getTimedMsgDuration());
        chatData.setMediaId(mediaId);
        chatData.setMediaUrl(chat.getMediaUrl());
        chatData.setAttachment(chat.getAttachment());
        long chatId = databaseManager.addChat(chatData);
        sendMessage(CHAT_RECEIVE_MESSAGE, null);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

    }

    public class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case ACTIVITY_BOUND:
                    break;
                case CHAT_SEND_MESSAGE: {
                    if (!ConnectionUtils.isInternetAvailable(ChatService.this) || !isConnected) {
                        ChatService.this.sendMessage(IS_DISCONNECTED, null);
                        return;
                    }
                    if (!msg.getData().containsKey(KEY_MESSAGE_TYPE)) {
                        createAndSendChatMessage(msg.getData().getString(KEY_RECEIVER), msg.getData().getString(KEY_MESSAGE));
                        break;
                    }
                    break;
                }
                case SET_CURRENT_WINDOW_JID: {
                    currentWindowJID = msg.getData().getString("JID");
                    break;
                }

                case CHAT_SEND_FILE: {

                    if (!ConnectionUtils.isInternetAvailable(ChatService.this) || !isConnected) {
                        ChatService.this.sendMessage(IS_DISCONNECTED, null);
                        return;
                    }
                    if (msg.getData().getString(KEY_MEDIA_TYPE).equals(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_MULTI_IMAGE.toString())) {
                        receiver = msg.getData().getString(KEY_RECEIVER);
                        images = msg.getData().getParcelableArrayList(KEY_IMAGE_LIST);
                        sendMultiImage(receiver, images);
                        break;
                    }
                    if (msg.getData().getString(KEY_MEDIA_TYPE).equals(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_MULTI_VIDEO.toString())) {
                        receiver = msg.getData().getString(KEY_RECEIVER);
                        images = msg.getData().getParcelableArrayList(KEY_IMAGE_LIST);
                        sendMultiVideo(receiver, images);
                        break;
                    }

                    break;
                }
                case CHAT_SEND_LOCATION: {
                    if (!ConnectionUtils.isInternetAvailable(ChatService.this) || !isConnected) {
                        ChatService.this.sendMessage(IS_DISCONNECTED, null);
                        return;
                    }
                    createAndSendLocation(msg.getData().getString(KEY_RECEIVER), msg.getData().getString(KEY_MEDIA_URL), msg.getData().getString(KEY_MESSAGE));
                    break;
                }
                case CHAT_MESSAGE_READ: {
                    if (!ConnectionUtils.isInternetAvailable(ChatService.this) || !isConnected) {
                        ChatService.this.sendMessage(IS_DISCONNECTED, null);
                        return;
                    }
                    sendReadReceipt(msg.getData().getString(KEY_RECEIVER), msg.getData().getInt(KEY_MESSAGE_ID), msg.getData().getString(KEY_MESSAGE));
                    break;

                }
                case CHAT_DOWNLOAD_VIDEO_FILE: {
                    if (!ConnectionUtils.isInternetAvailable(ChatService.this) || !isConnected) {
                        ChatService.this.sendMessage(IS_DISCONNECTED, null);
                        return;
                    }
                    new DowwnlaodMediaVideo().execute(msg.getData().getString(KEY_MEDIA_DOWN_URL), msg.getData().getString(KEY_MEDIA_DOWN_CHATID));
                    break;
                }
                case CHAT_ADD_ROSTERS: {
                    if (!ConnectionUtils.isInternetAvailable(ChatService.this) || !isConnected) {
                        ChatService.this.sendMessage(IS_DISCONNECTED, null);
                        return;
                    }


                    addRoster(msg.getData().getString(KEY_FRIEND_JID), msg.getData().getString(KEY_FRIEND_NAME));
                    break;
                }
                case CHAT_UPDATE_READ_STATUS: {
                    if (!ConnectionUtils.isInternetAvailable(ChatService.this) || !isConnected) {
                        ChatService.this.sendMessage(IS_DISCONNECTED, null);
                        return;
                    }
                    updateReadStatus(msg.getData().getString(KEY_FRIEND_JID));
                    break;
                }


                case CONNECT_WITH_ALL_GROUPS: {
                    connectWithAllGroups(msg.getData().getString(KEY_ROOM_JID));
                    break;
                }


                case CONNECT_WITH_ALL_GROUPS_DATABASE: {
                    connectWithAllGroupsDatabse();
                    break;
                }

                case CREATE_GROUP: {
                    createGroup(msg.getData().getString(KEY_ROOM_NAME), msg.getData().getString(KEY_ROOM_JID), msg.getData().getStringArrayList(KEY_ROOM_PARTICIPANT));
                    break;
                }

                case REMOVE_ROSTER:
                {
                    if (!ConnectionUtils.isInternetAvailable(ChatService.this) || !isConnected) {
                        ChatService.this.sendMessage(IS_DISCONNECTED, null);
                        return;
                    }


                    removeRoster(msg.getData().getString(KEY_FRIEND_JID), msg.getData().getString(KEY_FRIEND_NAME));
                    break;
                }

                case REMOVE_USER_FROM_GROUP:
                {
                    if (!ConnectionUtils.isInternetAvailable(ChatService.this) || !isConnected) {
                        ChatService.this.sendMessage(IS_DISCONNECTED, null);
                        return;
                    }


                    leaveUserFromGroup(msg.getData().getString(KEY_FRIEND_JID));
                    break;
                }

                    /*if (msg.getData().getString(KEY_MEDIA_TYPE).equals(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_AUDIO.toString())) {
                        receiver = msg.getData().getString(KEY_RECEIVER);
                        new AudioUploader().execute(msg.getData().getString(KEY_FILEPATH), scheduleDate, timedMessageValue + "");
                        break;
                    }
                    if (msg.getData().getString(KEY_MEDIA_TYPE).equals(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_MULTI_IMAGE.toString())) {
                        receiver = msg.getData().getString(KEY_RECEIVER);
                        images = msg.getData().getParcelableArrayList(KEY_IMAGE_LIST);
                        sendMultiImage(receiver, images, scheduleDate, timedMessageValue);
                        break;
                    }
                    if (msg.getData().getString(KEY_MEDIA_TYPE).equals(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_MULTI_VIDEO.toString())) {
                        receiver = msg.getData().getString(KEY_RECEIVER);
                        images = msg.getData().getParcelableArrayList(KEY_IMAGE_LIST);
                        sendMultiVideo(receiver, images, scheduleDate, timedMessageValue);
                        Utility.addMixPanelEvent(InkeChatService.this, "Powerchats_VideoSent");
                        break;
                    }

                }
                case CHAT_MESSAGE_READ: {
                    if (!ConnectionUtils.isInternetAvailable(InkeChatService.this) || !isConnected) {
                        InkeChatService.this.sendMessage(IS_DISCONNECTED, null);
                        return;
                    }
                    sendReadReceipt(msg.getData().getString(KEY_RECEIVER), msg.getData().getInt(KEY_MESSAGE_ID), msg.getData().getString(KEY_MESSAGE));
                    break;

                }
                case CHAT_SEND_LOCATION: {
                    if (!ConnectionUtils.isInternetAvailable(InkeChatService.this) || !isConnected) {
                        InkeChatService.this.sendMessage(IS_DISCONNECTED, null);
                        return;
                    }
                    if (msg.getData().containsKey(KEY_MESSAGE_DATE)) {
                        scheduleLocation(msg.getData().getString(KEY_RECEIVER), msg.getData().getString(KEY_MEDIA_URL), msg.getData().getString(KEY_MESSAGE), msg.getData().getString(KEY_MESSAGE_DATE));
                        break;
                    }
                    int timeValue = 0;
                    if (msg.getData().containsKey(KEY_MESSAGE_DURATION)) {
                        timeValue = msg.getData().getInt(KEY_MESSAGE_DURATION);
                    }
                    createAndSendLocation(msg.getData().getString(KEY_RECEIVER), msg.getData().getString(KEY_MEDIA_URL), msg.getData().getString(KEY_MESSAGE), timeValue);
                    break;
                }

*/
                default:

            }
        }

        private void sendMultiImage(String receiver, ArrayList images) {
            try {
                Bitmap bitmap = null;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                strImg = null;
                for (int i = 0; i < images.size(); i++) {
                    File picturePath = new File(images.get(i).toString());
                    Uri uri = Uri.fromFile(picturePath);
                    String filePath = compressImage(uri.toString());
                    bitmap = BitmapFactory.decodeFile(picturePath.getAbsolutePath(), options);
                    strImg = Base64.encodeToString(getBytesFromBitmap(bitmap),Base64.NO_WRAP);
                    createAndSendMultiMediaMessage(receiver, "", filePath.toString(), strImg, "image");
                }
                sendMediaMessageToServer(receiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        private void leaveUserFromGroup(String groupJID) {
            try {
                if (!multiUserChatManager.getJoinedRooms().contains(groupJID)) {
                    MultiUserChat mMultiUserChat = multiUserChatManager.getMultiUserChat(groupJID);
                    mMultiUserChat.leave();
                } else {
                    MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(groupJID);
                    multiUserChat.leave();
                }
            } catch (Exception e) {
                Log.i("Remove User", "Unable to remove user from group");
            }
        }

        private void sendMultiVideo(String receiver, ArrayList videosList) {
            try {
                Bitmap bitmap = null;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                strImg = null;
                for (int i = 0, l = videosList.size(); i < l; i++) {
                    strImg = null;
                    File videoFile = new File(videosList.get(i).toString());
                    bitmap = ThumbnailUtils.createVideoThumbnail(videoFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                    strImg = Base64.encodeToString(getBytesFromBitmap(bitmap), Base64.NO_WRAP);
                    createAndSendMultiVideoMessage(receiver, "", videoFile.toString(), strImg);
                }
                sendVideoMessageToServer(receiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    private void createAndSendChatMessage(String jid, String message) {


        ChatData chat = new ChatData();
        chat.settOJID(jid);
        if(jid.contains(CREATE_GROUP_SERVER_NAME))
        {
            chat.setDisplayName(chat.getFromJID());

        }
        else
        {
            chat.setDisplayName(getFullUsername());
        }
        chat.setFromJID(getUsername());
        chat.setChatMessage(message);
        chat.setTimeVal(System.currentTimeMillis() + "");
        chat.setMessageType(ChatData.ChatMessageType.MESSAGE_TYPE_CHAT.toString());

        long chatId = databaseManager.addChat(chat);
        chat.setChatId((int) chatId);
        try {
            org.jivesoftware.smack.packet.Message msg;

            if (jid.contains(CREATE_GROUP_SERVER_NAME)) {
                msg = new org.jivesoftware.smack.packet.Message(jid, org.jivesoftware.smack.packet.Message.Type.groupchat);
            } else {
                msg = new org.jivesoftware.smack.packet.Message(jid, org.jivesoftware.smack.packet.Message.Type.chat);
            }


            msg.setBody(chat.getChatMessage());
            /*msg.setFrom(chat.getFromJID());*/
            msg.setStanzaId(chat.getFromJID() + "-" + chat.getChatId());
            DefaultPacketExtension packetExtension = addExtensionData(chat);
            msg.addExtension(packetExtension);
            DeliveryReceiptRequest.addTo(msg);
            xmppConnection.sendPacket(msg);
            sendMessage(CHAT_REFRESH_LIST, null);
        } catch (SmackException.NotConnectedException e) {
            Log.i(LOG_TAG, "Sending message failed");
        }
    }

    private void createAndSendLocation(String jid, String mediaUrl, String position) {


        ChatData chat = new ChatData();
        chat.settOJID(jid);
        chat.setDisplayName(getFullUsername());
        chat.setFromJID(getUsername());
        String map = position.replaceAll("@", "\n");
        chat.setChatMessage("" + map);
        chat.setTimeVal(System.currentTimeMillis() + "");
        chat.setMessageType(ChatData.ChatMessageType.MESSAGE_TYPE_LOCATION.toString());
        chat.setMediaUrl(mediaUrl);
        long chatId = databaseManager.addChat(chat);
        chat.setChatId((int) chatId);
        try {
            org.jivesoftware.smack.packet.Message msg;
            if (jid.contains(CREATE_GROUP_SERVER_NAME)) {
                msg = new org.jivesoftware.smack.packet.Message(jid, org.jivesoftware.smack.packet.Message.Type.groupchat);
            } else {
                msg = new org.jivesoftware.smack.packet.Message(jid, org.jivesoftware.smack.packet.Message.Type.chat);
            }
            msg.setBody(map);
            msg.addExtension(addExtensionData(chat));
            msg.setStanzaId(chat.getFromJID() + "-" + chat.getChatId());

            DeliveryReceiptRequest.addTo(msg);
            xmppConnection.sendPacket(msg);
            sendMessage(CHAT_REFRESH_LIST, null);
        } catch (SmackException.NotConnectedException e) {
            Log.i(LOG_TAG, "Sending Location failed");
        }
    }


    /* ----------- MULTI IAMGE UPLAODING------------*/

    private void createAndSendMultiMediaMessage(String toJID, String downloadLink, String filePath, String strImg, String type) {

        MediaData mediaData = new MediaData();
        mediaData.setMediaUrl(downloadLink);
        mediaData.setMediaLocalPath(filePath);
        mediaData.setMediaType(Constants.MEDIA_TYPE_IMAGE);
        long mediaId = databaseManager.addMedia(mediaData);


        ChatData chat = new ChatData();
        chat.settOJID(toJID);
        chat.setDisplayName(getFullUsername());
        chat.setFromJID(getUsername());
        chat.setChatMessage("");
        chat.setIsDelivered(-1);
        chat.setTimeVal(System.currentTimeMillis() + "");
        chat.setMessageType(type);
        chat.setMediaId(mediaId);
        chat.setMediaUrl(downloadLink);
        chat.setAttachment(strImg);
        long chatId = databaseManager.addChat(chat);
        chat.setChatId((int) chatId);
        sendMessage(CHAT_REFRESH_LIST, null);
    }

    private void sendMediaMessageToServer(String toJID) {
        Constants.UNDELIVERTYPE = "image";
        ArrayList<ChatData> chatData = new ArrayList<ChatData>();
        chatData.addAll(databaseManager.getUndeliveredChats(toJID, ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_MULTI_IMAGE.toString()));
        for (int l = 0; l < chatData.size(); l++) {
            try {
                databaseManager.updateIsMediaDelivered(chatData.get(l).getChatId(), downloadLink);
                MediaData mediaData = databaseManager.getMediaDataFromMediaID(chatData.get(l).getMediaId());
                new MultiImageUploader().execute(toJID, mediaData.getMediaLocalPath(), String.valueOf(chatData.get(l).getChatId()));
            } catch (Exception e) {
            }
        }
    }

    private void sendVideoMessageToServer(String toJID) {
        Constants.UNDELIVERTYPE = "video";
        ArrayList<ChatData> chatData = new ArrayList<ChatData>();
        chatData.addAll(databaseManager.getUndeliveredChats(toJID, ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_MULTI_VIDEO.toString()));
        for (int l = 0; l < chatData.size(); l++) {
            try {
                databaseManager.updateIsMediaDelivered(chatData.get(l).getChatId(), downloadLink);
                MediaData mediaData = databaseManager.getMediaDataFromMediaID(chatData.get(l).getMediaId());
                new MultiVideoUploader().execute(toJID, mediaData.getMediaLocalPath(), String.valueOf(chatData.get(l).getChatId()));
            } catch (Exception e) {
            }
        }
    }

    private synchronized void sendReadReceipt(String name, int id, String message) {
        if (PreferenceManager.getPreference(getApplicationContext(), PreferenceManager.READ_RECEIPT) != null && PreferenceManager.getPreference(this, PreferenceManager.READ_RECEIPT).equals("false")) {
            return;
        }
        try {
            org.jivesoftware.smack.packet.Message msg = new org.jivesoftware.smack.packet.Message(name, org.jivesoftware.smack.packet.Message.Type.normal);
            DefaultPacketExtension extension = new DefaultPacketExtension("received ", "urn:xmpp:receipts");
            msg.addExtension(extension);
            DefaultPacketExtension customExtension = new DefaultPacketExtension("customdata", "jabber:client");
            customExtension.setValue("isread", "1");
            customExtension.setValue("chatid", id + "");
            msg.addExtension(customExtension);
            xmppConnection.sendPacket(msg);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private class MultiImageUploader extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "TRUE";
            basicImageUpload(params[1], receiver, Constants.MEDIA_TYPE_IMAGE, getUsername(), Long.parseLong(params[2]));
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("TRUE")) {
                Log.i("", "Picture Successfully uploaded.");
            } else if (result.equals("MURL")) {
                Log.i("", "Error Occured while Picture uploading.");
            } else if (result.equals("IOE")) {
                Log.i("", "Input/Output Error Occured while Picture uploading.");
            } else {
                Log.i("", "Error Occured while Picture uploading.");
            }
            super.onPostExecute(result);
        }
    }

    private class MultiVideoUploader extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // String response = videoUploadService(params[0], params[1], Constants.MEDIA_TYPE_VIDEO, Long.parseLong(params[2]), params[3], Integer.parseInt(params[4]));

            String response = "";
            basicVideoUpload(params[1], receiver, Constants.MEDIA_TYPE_VIDEO, getUsername(), Long.parseLong(params[2]));
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("TRUE")) {
                Log.i("", "Picture Successfully uploaded.");
            } else if (result.equals("MURL")) {
                Log.i("", "Error Occured while Picture uploading.");
            } else if (result.equals("IOE")) {
                Log.i("", "Input/Output Error Occured while Picture uploading.");
            } else {
                Log.i("", "Error Occured while Picture uploading.");
            }
            super.onPostExecute(result);
        }
    }


    private void createAndSendMultiVideoMessage(String toJID, String downloadLink, String filePath, String strImg) {

        MediaData mediaData = new MediaData();
        mediaData.setMediaUrl(downloadLink);
        mediaData.setMediaLocalPath(filePath);
        mediaData.setMediaType(Constants.MEDIA_TYPE_VIDEO);
        long mediaId = databaseManager.addMedia(mediaData);

        ChatData chat = new ChatData();
        chat.settOJID(toJID);
        chat.setDisplayName(getFullUsername());
        chat.setFromJID(getUsername());
        chat.setChatMessage("");
        chat.setIsDelivered(-1);
        chat.setIsMediaDownloaded("0");
        chat.setTimeVal(System.currentTimeMillis() + "");
        chat.setMessageType(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_VIDEO.toString());
        chat.setMediaId(mediaId);
        chat.setMediaUrl(downloadLink);
        chat.setAttachment(strImg);
        long chatId = databaseManager.addChat(chat);
        chat.setChatId((int) chatId);
        sendMessage(CHAT_REFRESH_LIST, null);
    }


    private void sendMulitMediaMessage(long chatId, String downloadLink) {
        databaseManager.updateIsMediaDelivered(chatId, downloadLink);
        databaseManager.updateIsSendMediaDownload(chatId, 1);
        ChatData chat = databaseManager.getChatFromChatID(chatId);


        chat.settOJID(chat.gettOJID());
        chat.setDisplayName(getFullUsername());
        chat.setFromJID(getUsername());
        chat.setChatMessage("");
        chat.setTimeVal(System.currentTimeMillis() + "");
        chat.setMessageType(chat.getMessageType().toString());
        chat.setMediaId(chat.getMediaId());
        chat.setMediaUrl(downloadLink);
        chat.setAttachment(chat.getAttachment());

        try {
            Gson gson = new Gson();
            String messageJSON = gson.toJson(chat);
            org.jivesoftware.smack.packet.Message msg;
            if (chat.gettOJID().contains(CREATE_GROUP_SERVER_NAME)) {
                msg = new org.jivesoftware.smack.packet.Message(chat.gettOJID(), org.jivesoftware.smack.packet.Message.Type.groupchat);
            } else {
                msg = new org.jivesoftware.smack.packet.Message(chat.gettOJID(), org.jivesoftware.smack.packet.Message.Type.chat);
            }

            msg.setBody(chat.getMessageType().toString());
            msg.setStanzaId(chat.getFromJID() + "-" + chat.getChatId());
            msg.addExtension(addExtensionData(chat));
            DeliveryReceiptRequest.addTo(msg);
            msg.setStanzaId(getUsername() + "-" + chat.getChatId());
            xmppConnection.sendPacket(msg);
            sendMessage(CHAT_REFRESH_LIST, null);
        } catch (SmackException.NotConnectedException e) {
            Log.i(LOG_TAG, "Sending image failed");
        }
    }

    public void basicImageUpload(final String fileStr, final String toJID, final String mediaType, final String fromJID, final long chatID) {
        {
            try {
                databaseManager.updateIsMediaDelivered(chatID, downloadLink);
                final File file = new File(fileStr);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        MediaType MEDIA_TYPE_IMAGE;
                        MEDIA_TYPE_IMAGE = MediaType.parse("image/jpg");
                        OkHttpClient client = new OkHttpClient();
                        client.setConnectTimeout(5, TimeUnit.MINUTES); // connect timeout
                        client.setReadTimeout(5, TimeUnit.MINUTES);
                        RequestBody requestBody = new MultipartBuilder()
                                .type(MultipartBuilder.FORM)
                                .addFormDataPart("toJID", receiver)
                                .addFormDataPart("fromJID", fromJID)
                                .addFormDataPart("mediaType", "image")
                                .addFormDataPart("file", "profile.png", RequestBody.create(MEDIA_TYPE_IMAGE, file))
                                .build();
                        Constants.AUTHORIZATION = PreferenceManager.getPreference(getApplicationContext(), PreferenceManager.KEY_ACCESS_TOKEN);
                        Request request = new Request.Builder()
                                .url(Constants.URL_UPLOAD_MEDIA).post(requestBody).header("Authorization", Constants.BEARER + Constants.AUTHORIZATION)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            if (response.code() == 200) {
                                Gson gson = new Gson();
                                String res = response.body().string();
                                ChatMediaResponseModel chatMediaResponseModel = gson.fromJson(res, ChatMediaResponseModel.class);
                                if (chatMediaResponseModel.getChatMediaType().equalsIgnoreCase("image")) {
                                    downloadLink = Constants.URL_DOWNLOAD_MEDIA_IMAGE + chatMediaResponseModel.getChatMediaId();
                                    sendMulitMediaMessage(chatID, downloadLink);
                                }
                            } else if (response.code() == 401) {

                            } else if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code " + response);
                            }
                        } catch (IOException e) {
                            Log.e("Upload Failed", e.getLocalizedMessage());

                               // downloadLink = Constants.URL_DOWNLOAD_MEDIA_IMAGE + chatMediaResponseModel.getChatMediaId();
                                sendMulitMediaMessage(chatID, "https://static.pexels.com/photos/30772/pexels-photo-30772.jpg");

                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                    }
                }.execute();
            } catch (Exception e) {
            }
        }
    }


    public void basicVideoUpload(final String fileStr, final String toJID, final String mediaType, final String fromJID, final long chatID) {
        {
            try {
                databaseManager.updateIsMediaDelivered(chatID, downloadLink);
                final File file = new File(fileStr);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        MediaType MEDIA_TYPE_VIDEO;
                        MEDIA_TYPE_VIDEO = MediaType.parse("video/mp4");
                        OkHttpClient client = new OkHttpClient();
                        client.setConnectTimeout(5, TimeUnit.MINUTES); // connect timeout
                        client.setReadTimeout(5, TimeUnit.MINUTES);

                        RequestBody requestBody = new MultipartBuilder()
                                .type(MultipartBuilder.FORM)
                                .addFormDataPart("toJID", receiver)
                                .addFormDataPart("fromJID", fromJID)
                                .addFormDataPart("mediaType", "video")
                                .addFormDataPart("file", "video.mp4", RequestBody.create(MEDIA_TYPE_VIDEO, file))
                                .build();

                        Constants.AUTHORIZATION = PreferenceManager.getPreference(getApplicationContext(), PreferenceManager.KEY_ACCESS_TOKEN);
                        Request request = new Request.Builder()
                                .url(Constants.URL_UPLOAD_MEDIA)
                                .post(requestBody).header("Authorization", Constants.BEARER + Constants.AUTHORIZATION)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            Log.e("Video Send Code", "" + response.code());
                            if (response.code() == 200) {

                                Gson gson = new Gson();
                                String res = response.body().string();
                                ChatMediaResponseModel chatMediaResponseModel = gson.fromJson(res, ChatMediaResponseModel.class);
                                if (chatMediaResponseModel.getChatMediaType().equalsIgnoreCase("video")) {
                                    downloadLink = Constants.URL_DOWNLOAD_MEDIA_IMAGE + chatMediaResponseModel.getChatMediaId();
                                    sendMulitMediaMessage(chatID, downloadLink);
                                }
                            } else {
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);
                            }
                        } catch (IOException e) {
                            Log.e("Upload Failed", e.getLocalizedMessage());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                    }
                }.execute();
            } catch (Exception e) {
            }
        }
    }

    private DefaultPacketExtension addExtensionData(ChatData chat) {
        DefaultPacketExtension dp = new DefaultPacketExtension("customdata", "jabber:client");
        if (dp.getValue(MEDIAID) != null)
            dp.setValue(MEDIAID, chat.getMediaId() + "");
        /*Long timestring = Long.parseLong(chat.getTimeVal());
        Date date = new Date((timestring - (1000 * 60 * 330)));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dp.setValue(DATE, format.format(date));*/

        dp.setValue(DATE, chat.getTimeVal());
        dp.setValue(MEDIAURL, chat.getMediaUrl() + "");
        dp.setValue(DISPLAYNAME, getDisplayName() + "");
        if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_LOCATION.toString())) {
            dp.setValue(ISMAP, "yes");
        }
        if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_VCARD.toString())) {
            dp.setValue(MEDIATYPE, "VCard");
            dp.setValue(ISVCARD, "yes");
        }
        if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_VIDEO.toString()))
            dp.setValue(MEDIATYPE, "Video");
        if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_AUDIO.toString()))
            dp.setValue(MEDIATYPE, "Audio");
        if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_IMAGE.toString()))
            dp.setValue(MEDIATYPE, "Image");
        if (chat.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_CHAT.toString()))
            dp.setValue(MEDIATYPE, "text");
        if (chat.getAttachment() != null) {
            dp.setValue(ATTACHMENT, chat.getAttachment());
        }
        dp.setValue(TYPE, chat.getMessageType());
        return dp;
    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        try {
            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

        } catch (ArithmeticException er) {
            er.printStackTrace();
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "SportO/Media/Images/Sent");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }


    class DowwnlaodMediaVideo extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... url) {
            try {
                OkHttpClient client = new OkHttpClient();
                client.setConnectTimeout(5, TimeUnit.MINUTES); // connect timeout
                client.setReadTimeout(5, TimeUnit.MINUTES);
                Constants.AUTHORIZATION = PreferenceManager.getPreference(getApplicationContext(), PreferenceManager.KEY_ACCESS_TOKEN);
                Request request = new Request.Builder().url(url[0])
                        .addHeader("Authorization", Constants.BEARER + Constants.AUTHORIZATION)
                        .addHeader("Content-Type", "application/json").build();
                Response response = client.newCall(request).execute();

                File file = null;
                file = new File(Constants.FILERECEIVEVIDEOPATH + url[1] + ".mp4");
                file.createNewFile();
                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream inputStream = response.body().byteStream();
                byte[] buffer = new byte[1024];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                }
                fileOutput.close();
                if (response.isSuccessful()) {
                    databaseManager.updateMediaIsDownlaodByID(Integer.parseInt(url[1]));
                    sendMessage(CHAT_REFRESH_LIST, null);
                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(String file_url) {
            try {

            } catch (NumberFormatException e) {
                // not an integer!
            }
        }
    }


    private Notification getNotification() {
        Notification notification;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setColor(getResources()
                .getColor(R.color.material_deep_teal_500))
                .setAutoCancel(true);

        notification = builder.build();

        notification.flags = Notification.FLAG_FOREGROUND_SERVICE | Notification.FLAG_AUTO_CANCEL;

        return notification;
    }

    private boolean createGroup(String roomName, String roomJID, ArrayList<String> participants) {
        MultiUserChat mMultiUserChat = multiUserChatManager.getMultiUserChat(roomJID + CREATE_GROUP_SERVER_NAME);
        try {
            mMultiUserChat.createOrJoin(CURRENT_USER);
            // mMultiUserChat.changeSubject("AndroidTest2");
            Form form = mMultiUserChat.getConfigurationForm();
            Form answerForm = form.createAnswerForm();
            answerForm.setAnswer("muc#roomconfig_persistentroom", true);
            answerForm.setAnswer("muc#roomconfig_roomname", roomName);
            mMultiUserChat.sendConfigurationForm(answerForm);
            for (int i = 0; i < participants.size(); i++) {
                mMultiUserChat.invite(participants.get(i), "#&#" + roomName);
            }
            sendMessage(CREATE_GROUP_SUCCESS, null);
            return true;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            sendMessage(CREATE_GROUP_FAIL, null);
            return false;
        } catch (SmackException e) {
            e.printStackTrace();
            sendMessage(CREATE_GROUP_FAIL, null);
            return false;
        }
    }

    private boolean getAllGroups(final XMPPConnection connection) {
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        try {
            Set<String> groups = manager.getJoinedRooms();
            for (String group : groups) {
                Log.i("Group name", group);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void connectWithAllGroups(String roomname) {

        if (!isAuthenticated)
            return;


        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(xmppConnection);
        try {

            MultiUserChat mMultiUserChat = multiUserChatManager.getMultiUserChat(roomname);
            DiscussionHistory history = new DiscussionHistory();
            ChatData chat = databaseManager.getLastMessageFromContact(roomname);
            if (chat != null) {
                Long lastmessageTime = Long.parseLong(chat.getTimeVal());
                long historySecs = (System.currentTimeMillis() - lastmessageTime) / 1000;
                history.setSeconds((int) historySecs);
            } else {
                history.setSeconds(0);
            }
            mMultiUserChat.join(CURRENT_USER, null, history, xmppConnection.getPacketReplyTimeout());
            Log.i("Joined groups", roomname);
            mMultiUserChat.grantAdmin(CURRENT_USER + "@" + Constants.SERVER_NAME);

        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (Exception e) {
            connectWithAllGroups(roomname);
            return;
        }
    }

    private void connectWithAllGroupsDatabse() {

        if (!isAuthenticated)
            return;

        List<FriendData> groups = databaseManager.getUserGroups();
        for (int i = 0; i < groups.size(); i++) {
            FriendData data = groups.get(i);
            if (databaseManager.getExitStatus(data.getFriendJID()) != 1)
            {
                try {
                    MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(xmppConnection);
                    MultiUserChat mMultiUserChat = multiUserChatManager.getMultiUserChat(data.getFriendJID());
                    DiscussionHistory history = new DiscussionHistory();
                    ChatData chat = databaseManager.getLastMessageFromContact(data.getFriendJID());
                    if (chat != null) {
                        Long lastmessageTime = Long.parseLong(chat.getTimeVal());
                        long historySecs = (System.currentTimeMillis() - lastmessageTime) / 1000;
                        history.setSeconds((int) historySecs);
                    } else {
                        history.setSeconds(0);
                    }
                    mMultiUserChat.join(CURRENT_USER, null, history, xmppConnection.getPacketReplyTimeout());
                    Log.i("Joined groups", data.getFriendJID());
                    mMultiUserChat.grantAdmin(CURRENT_USER + "@" + Constants.SERVER_NAME);
                    break;
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    connectWithAllGroupsDatabse();
                    return;
                }
            }
        }
    }


    private void removeRoster(String jid, String name) {
        try {

/*            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
            Roster roster = Roster.getInstanceFor(xmppConnection);
            roster.createEntry(jid, name, null);
            Presence pres = new Presence(Presence.Type.unsubscribe);
            pres.setStatus("Offline");
            pres.setTo(jid);
            pres.setFrom(getUsername()+"@"+Constants.SERVER_NAME);
            xmppConnection.sendPacket(pres);
            getRoster(xmppConnection);*/

            RosterPacket packet = new RosterPacket();
            packet.setType(IQ.Type.set);
            RosterPacket.Item item  = new RosterPacket.Item(jid, null);
            item.setItemType(RosterPacket.ItemType.remove);
            packet.addRosterItem(item);
            xmppConnection.sendPacket(packet);

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }/* catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }*/
    }

}


