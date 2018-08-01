package com.datavim.chatapp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datavim.chatapp.R;
import com.datavim.chatapp.adpater.ChatConversationAdapter;
import com.datavim.chatapp.chat.ChatService;
import com.datavim.chatapp.comman.Constants;
import com.datavim.chatapp.database.DatabaseManager;
import com.datavim.chatapp.model.records.ChatData;
import com.datavim.chatapp.utils.CustomPicasso;
import com.datavim.chatapp.utils.OnItemLongClickListener;
import com.datavim.chatapp.utils.OnMessageRead;
import com.datavim.chatapp.utils.PreferenceManager;
import com.datavim.chatapp.utils.RoundedImageView;
import com.erikagtierrez.multiple_media_picker.Gallery;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


        import com.cocosw.bottomsheet.BottomSheet;
        import com.squareup.picasso.Picasso;

        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Locale;
        import java.util.Map;
        import java.util.Random;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


public class ChatConversation extends AppCompatActivity implements OnMessageRead {

    String friendJID = "";
    String formatedfriendJID = "";

    int friendID;
    String friendName = null, videoPath = "", picturePath = "", friendprofilepic = "", eventID = "", currentuser = "", blocked = "0", exitstatus = "0";
    ImageView btnAttachmentMsg;
    EmojiconEditText etChatMessage;
    Messenger sportoService = null;
    Messenger sportoMessanger = new Messenger(new SportoMessageHandler());
    private static final String TAG = "CHAT CONVERSATION";
    boolean isBound = false;
    private DatabaseManager sportoDatabaseManager;
    ArrayList<ChatData> chatDataArrayList;
    ChatConversationAdapter chatConversationAdapter;
    RecyclerView rvChatConversation;
    public static final int REQUEST_INKE_MULTI_IMAGE = 2251;
    private ArrayList<ChatData> unreadMessage = new ArrayList<>();
    Uri mCapturedImageURI;
    public static final int CAMERA_CAPTURE = 100;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1, MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private static String strSaveVideoPath = "";
    private static final String IMAGE_DIRECTORY_NAME = "photo";
    private String thumbnailPath;
    private Toolbar tbHeader;
    private CoordinatorLayout coordinatorLayout;
    ChatService chatService;
    private Snackbar snackbar;
    TextView toolbar_sub_title,btnSendMsg;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSION_GALLERY = 124;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSION_CAMERA = 125;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSION_LOCATION = 126;
    Button btnBlock;
    LinearLayout linearmessagebar;
    int icount = 0;
    TextView tVexit;
    static final int OPEN_MEDIA_PICKER = 1;  // Request code
    ImageView emoji_btn;
    EmojIconActions emojIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);
        Log.i(TAG, "o88");
        intialise();
        getUsername();
        getData();
        initToolbar();
        doBindService();
        initUI();
        initClickListener();
        notificationClear();
        loadChatHistory();
        String[] part = friendJID.split("@");

        if (part[1].equalsIgnoreCase(Constants.SERVER_NAME)) {
            tVexit.setVisibility(View.GONE);

            //    public static String GET_CHAT_HISTORY = "SELECT * FROM tblChat WHERE FROMJID='%s' OR TOJID='%s'";
            List<ChatData> Totalchat = sportoDatabaseManager.getChatHistory(friendJID);
            //public static String GET_CHAT_COUNT= "SELECT * FROM tblChat WHERE FROMJID='%s' AND TOJID='%s'";
            List<ChatData> count13 = sportoDatabaseManager.getChatsCountFromJid(friendJID, getUsername());
            // Log.e("oncreate_c13>",""+count13.size());//count113
            if (count13.size() > 0 || Totalchat.size() <= 0) {
                btnBlock.setVisibility(View.GONE);
            } else {
                if (!blocked.equalsIgnoreCase("O")) {

                    linearmessagebar.setVisibility(View.VISIBLE);
                  //  btnBlock.setVisibility(View.VISIBLE);
                    btnBlock.setText("Block");

                } else {
                    linearmessagebar.setVisibility(View.GONE);
                    //btnBlock.setVisibility(View.VISIBLE);
                    btnBlock.setText("UnBlock");
                }
            }
        } else {
            if (exitstatus != null) {
                if (exitstatus.equalsIgnoreCase("1")) {
                    tVexit.setVisibility(View.VISIBLE);
                    linearmessagebar.setVisibility(View.GONE);
                } else {
                    tVexit.setVisibility(View.GONE);
                    linearmessagebar.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateReadStatus(String jid) {
        try {
            sportoDatabaseManager.updateIsRead(jid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUnreadMsg() {
        sendUnreadMsgUpdate(friendJID);
    }

    private void intialise() {
        sportoDatabaseManager = DatabaseManager.getInstance(getApplicationContext());
        chatDataArrayList = new ArrayList<ChatData>();
    }

    private void notificationClear() {
        NotificationManager notifManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateReadStatus(friendJID);
    }

    private void initUI() {
        tVexit = (TextView) findViewById(R.id.txt_exit);

        linearmessagebar = (LinearLayout) findViewById(R.id.llmessagebar);
        btnBlock = (Button) findViewById(R.id.block_btn);
        btnBlock.setVisibility(View.GONE);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        rvChatConversation = (RecyclerView) findViewById(R.id.rv_chat_conversation);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(ChatConversation.this) {

                    private static final float SPEED = 300f;// Change this value (default=25f)

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }

                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return null;
                    }

                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }

        };

        rvChatConversation.setLayoutManager(layoutManager);

        rvChatConversation.setItemAnimator(new DefaultItemAnimator());
        rvChatConversation.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvChatConversation.setFitsSystemWindows(true);
        btnSendMsg = (TextView) findViewById(R.id.btn_send_msg);
        btnAttachmentMsg = (ImageView) findViewById(R.id.btn_attacment_msg);
        etChatMessage = (EmojiconEditText) findViewById(R.id.et_chat_msg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           /* btnSendMsg.setImageDrawable(getResources().getDrawable(R.drawable.tab_send, getApplicationContext().getTheme()));
            btnAttachmentMsg.setImageDrawable(getResources().getDrawable(R.drawable.tab_attached, getApplicationContext().getTheme()));*/

        } else {
         /*   btnSendMsg.setImageDrawable(getResources().getDrawable(R.drawable.tab_send));
            btnAttachmentMsg.setImageDrawable(getResources().getDrawable(R.drawable.tab_send));
         */
        }

        try {
            Message msg = Message.obtain(null,
                    chatService.SET_CURRENT_WINDOW_JID);
            Bundle bundle = new Bundle();
            bundle.putString("JID", friendJID);
            msg.setData(bundle);
            msg.replyTo = sportoMessanger;
            sportoService.send(msg);
        } catch (Exception e) {
        }


        btnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (blocked.equalsIgnoreCase("0")) {
                    int block = sportoDatabaseManager.BlockUser(friendJID);
                    if (block == 1) {
                        linearmessagebar.setVisibility(View.GONE);
                       // btnBlock.setVisibility(View.VISIBLE);
                        btnBlock.setText("UnBlock");
                        removeRoster(friendJID, friendName);

                    }
                    blocked = "1";

                } else {
                    int unblock = sportoDatabaseManager.UnblockUser(friendJID);
                    if (unblock == 1) {
                        linearmessagebar.setVisibility(View.VISIBLE);
                        ADDRoster(friendJID, friendName);
                       // btnBlock.setVisibility(View.VISIBLE);
                        btnBlock.setText("Block");

                    }
                    blocked = "0";
                }
            }
        });

        emoji_btn = (ImageView) findViewById(R.id.emoji_btn);

        emojIcon = new EmojIconActions(this, coordinatorLayout, etChatMessage , emoji_btn);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e(TAG, "Keyboard opened!");
            }

            @Override
            public void onKeyboardClose() {
                Log.e(TAG, "Keyboard closed");
            }
        });

        emojIcon.setUseSystemEmoji(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    private String getUsername() {
        currentuser = PreferenceManager.getPreference(this, PreferenceManager.KEY_JID);
        if (currentuser == null) {
            currentuser = "";
        }
        return currentuser;
    }


    private void initClickListener() {

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsgToService(etChatMessage.getText().toString());
            }
        });
        etChatMessage.addTextChangedListener(tw);
        btnAttachmentMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachmentMenu();
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private TextWatcher tw = new TextWatcher() {
        public void afterTextChanged(Editable s) {

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // you can check for enter key here
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (etChatMessage.getText().toString().trim().length() == 0) {
           //     btnSendMsg.setImageResource(R.drawable.send_msg_gray);
                btnSendMsg.setClickable(false);
            } else {

             //   btnSendMsg.setImageResource(R.drawable.send_msg_blue);
                btnSendMsg.setClickable(true);


            }
        }
    };

    private void getData() {
        Intent intent = getIntent();
        friendJID = intent.getStringExtra(Constants.FRIEND_JID);



        friendName = intent.getStringExtra(Constants.FRIEND_NAME);
        friendprofilepic = intent.getStringExtra(Constants.PROFILEPICPATH);
        exitstatus = intent.getStringExtra(Constants.ISEXIT);


        String block_value = intent.getStringExtra(Constants.ISBLOCKED);
        if (block_value != null) {
            blocked = intent.getStringExtra(Constants.ISBLOCKED);
        } else {
            blocked = "0";
        }

    }

    private void initToolbar() {


        ImageView ivBack = (ImageView)findViewById(R.id.iv_back);
        TextView tvName = (TextView)findViewById(R.id.tv_name);
        TextView tvStatus = (TextView)findViewById(R.id.tv_status);

        String[] friendNm = friendJID.split("@");
        tvName.setText(friendNm[0]);

        formatedfriendJID = friendNm[0];
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*tbHeader = (Toolbar) findViewById(R.id.tool_bar);
        tbHeader.setNavigationIcon(R.drawable.back_arrow_gray);

        RoundedImageView toolbar_logo = (RoundedImageView) findViewById(R.id.toolbar_logo);
        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText(friendName);

        toolbar_sub_title = (TextView) findViewById(R.id.toolbar_sub_title);


        toolbar_title.setTextColor(getResources().getColor(R.color.toolbar_text));
        String[] part = friendJID.split("@");

        *//*if (friendJID != null) {
            Log.i("Part1 Value","Log Part"+part[1]);
            if (part[1].equalsIgnoreCase(Constants.SERVER_NAME)) {

                CustomPicasso.getPicassoClient(this)
                        .load(URLConstants.URL_GET_USER_THUMBNAIL_BY_JID + part[0])
                        .placeholder(R.drawable.buddies_profile_pic)
                        .error(R.drawable.buddies_profile_pic)
                        .into(toolbar_logo);
            } else {
                if (friendprofilepic != null && !friendprofilepic.isEmpty() && !friendprofilepic.equalsIgnoreCase("null"))
                {
                    Picasso.with(this)
                            .load(friendprofilepic)
                            .placeholder(R.drawable.buddies_profile_pic)
                            .error(R.drawable.buddies_profile_pic)
                            .into(toolbar_logo);
                }
                else
                {
                    if(friendJID!=null)
                    {

                        String Profilepic=sportoDatabaseManager.getFriendProfilePic(friendJID);
                        Picasso.with(this)
                                .load(Profilepic)
                                .placeholder(R.drawable.buddies_profile_pic)
                                .error(R.drawable.buddies_profile_pic)
                                .into(toolbar_logo);
                    }
                }
            }
        }
        toolbar_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] part = friendJID.split("@");
                if (part[1].equalsIgnoreCase(Constants.SERVER_NAME)) {

                    Intent intent = new Intent(ChatConversation.this, UserDisplayPhoto.class);
                    intent.putExtra(Constants.USER_ID, String.valueOf(friendID));
                    startActivity(intent);

                } else {

                    //groupinfo
                }
            }
        });
        toolbar_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] part = friendJID.split("@");
                if (part[1].equalsIgnoreCase(Constants.SERVER_NAME)) {

                    Intent intent = new Intent(ChatConversation.this, UserProfile.class);
                    intent.putExtra(Constants.USER_ID, String.valueOf(friendID));
                    startActivity(intent);

                } else {


                }

            }
        });
*//*        tbHeader.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
    }


    private void sendMsgToService(String newMessage) {
        if (newMessage.length() > 0) {
            try {
                if (sportoService != null) {
                    Message msg = Message.obtain(null, chatService.CHAT_SEND_MESSAGE);
                    Bundle bundle = new Bundle();
                    bundle.putString(chatService.KEY_RECEIVER, friendJID);
                    bundle.putString(chatService.KEY_MESSAGE, newMessage);
                    msg.replyTo = sportoMessanger;
                    msg.setData(bundle);
                    sportoService.send(msg);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            loadChatHistory();
            etChatMessage.setText("");
        }
    }

    private void sendLocationToService(double latitude, double longitude) {
        try {
            if (sportoService != null) {
                String position = latitude + "@" + longitude;
                Message msg = Message.obtain(null, chatService.CHAT_SEND_LOCATION);
                Bundle bundle = new Bundle();
                bundle.putString(chatService.KEY_MESSAGE_TYPE, ChatData.ChatMessageType.MESSAGE_TYPE_LOCATION.toString());
                bundle.putString(chatService.KEY_RECEIVER, friendJID);
                String mediaUrl = "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude + "+&markers=icon:http://tinyurl.com/2ftvtt6 %7c " + latitude + "," + longitude + "&zoom=16&size=300x250&sensor=false";
                bundle.putString(chatService.KEY_MEDIA_URL, mediaUrl);
                bundle.putString(chatService.KEY_MESSAGE, position);
                msg.replyTo = sportoMessanger;
                msg.setData(bundle);
                sportoService.send(msg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        loadChatHistory();
    }

    private void removeRoster(String friendJID, String friendName) {
        try {
            Message msg = Message.obtain(null, ChatService.REMOVE_ROSTER);
            Bundle bundle = new Bundle();
            bundle.putString(ChatService.KEY_FRIEND_JID, friendJID);
            bundle.putString(ChatService.KEY_FRIEND_NAME, friendName);
            msg.replyTo = sportoMessanger;
            msg.setData(bundle);
            sportoService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void ADDRoster(String friendJID, String friendName) {
        try {
            Message msg = Message.obtain(null, ChatService.CHAT_ADD_ROSTERS);
            Bundle bundle = new Bundle();
            bundle.putString(ChatService.KEY_FRIEND_JID, friendJID);
            bundle.putString(ChatService.KEY_FRIEND_NAME, friendName);
            msg.replyTo = sportoMessanger;
            msg.setData(bundle);
            sportoService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void sendMultiImageToSevice(ArrayList images) {
        {
            try {
                if (sportoService != null) {
                    Message msg = Message.obtain(null, chatService.CHAT_SEND_FILE);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(chatService.KEY_IMAGE_LIST, images);
                    bundle.putString(chatService.KEY_MESSAGE_TYPE, ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA.toString());
                    bundle.putString(chatService.KEY_RECEIVER, friendJID);
                    bundle.putString(chatService.KEY_MEDIA_TYPE, ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_MULTI_IMAGE.toString());
                    msg.replyTo = sportoMessanger;
                    msg.setData(bundle);
                    sportoService.send(msg);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            loadChatHistory();
        }
    }

    private void loadChatHistory() {
        chatDataArrayList.clear();
        rvChatConversation.removeAllViewsInLayout();
        chatDataArrayList.addAll(sportoDatabaseManager.getChatHistory(friendJID));
        chatConversationAdapter = new ChatConversationAdapter(this, getApplicationContext(), chatDataArrayList, this);
        rvChatConversation.setAdapter(chatConversationAdapter);
//        chatConversationAdapter.notifyDataSetChanged();
        rvChatConversation.scrollToPosition(chatDataArrayList.size() - 1);




        chatConversationAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {

/*                ChatData.ChatMessageType.MESSAGE_TYPE_CHAT.toString()*/

                if (chatDataArrayList.get(position).getMessageType().equalsIgnoreCase(ChatData.ChatMessageType.MESSAGE_TYPE_CHAT.toString())) {

                    ChatData chatData = chatDataArrayList.get(position);
                    showDialog(chatData);
                }

            }
        });
        String[] part = friendJID.split("@");
        if (part[1].equalsIgnoreCase(Constants.SERVER_NAME)) {
            //    public static String GET_CHAT_HISTORY = "SELECT * FROM tblChat WHERE FROMJID='%s' OR TOJID='%s'";
            List<ChatData> Totalchat = sportoDatabaseManager.getChatHistory(friendJID);
            //public static String GET_CHAT_COUNT= "SELECT * FROM tblChat WHERE FROMJID='%s' AND TOJID='%s'";
            List<ChatData> count12 = sportoDatabaseManager.getChatsCountFromJid(friendJID, getUsername());
            // Log.e("Loadhistorychat12>",""+count12.size());//count113
            if (count12.size() > 0 || Totalchat.size() <= 0) {
                btnBlock.setVisibility(View.GONE);
            } else {
                if (!blocked.equalsIgnoreCase("O")) {
                    linearmessagebar.setVisibility(View.VISIBLE);
                   // btnBlock.setVisibility(View.VISIBLE);
                    btnBlock.setText("Block");

                } else {
                    linearmessagebar.setVisibility(View.GONE);
                  //  btnBlock.setVisibility(View.VISIBLE);
                    btnBlock.setText("UnBlock");
                }
            }
        }


    }

    @Override
    public void onMessageRead(ChatData chat) {
        synchronized (this) {
            if (sportoService != null) {
                try {
                    Message msg = Message.obtain(null,
                            chatService.CHAT_MESSAGE_READ);
                    Bundle bundle = new Bundle();
                    bundle.putString(chatService.KEY_RECEIVER, chat.getFromJID());
                    bundle.putInt(chatService.KEY_MESSAGE_ID, chat.getOpponentChatId());
                    bundle.putString(chatService.KEY_MESSAGE, chat.getChatMessage());
                    msg.replyTo = sportoMessanger;
                    msg.setData(bundle);
                    sportoService.send(msg);
                } catch (RemoteException e) {

                }
                sportoDatabaseManager.updateIsInbox(chat);
            } else {
                unreadMessage.add(chat);
            }
        }
    }

    public void resendImageToServer() {
    }

    class SportoMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ChatService.MSG_REGISTER_CLIENT:
                    Log.i(TAG, "Client Registered");
                    break;
                case ChatService.USER_AUTHENTICATED:
                    Log.i(TAG, "User Authenticated");
                    break;
                case ChatService.CHAT_RECEIVE_MESSAGE:
                    Log.i(TAG, "Chat Recived .............");
                    loadChatHistory();
                    break;
                case ChatService.CHAT_REFRESH_LIST:
                    loadChatHistory();
                    break;
                case ChatService.IS_CONNECTED:
                    // snackbar.dismiss();
                    break;
                case ChatService.IS_CONNECTING:
                    showDataSnacbar(" connecting ...!");
                    break;
                case ChatService.IS_DISCONNECTED:


                    break;

                default:
                    super.handleMessage(msg);
            }
        }

    }


    private void attachmentMenu() {
        new BottomSheet.Builder(this)
                .grid() // <-- important part
                .sheet(R.menu.menu_bottom_sheet)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.menu_gallery:

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    allPermissionsForGallery();
                                } else {
                                    openGallery();
                                }
                                break;
                            case R.id.menu_camera:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    allPermissionsCamera();
                                } else {
                                    chooseImage();
                                }
                                break;
                            case R.id.menu_location:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    allPermissionsForLocation();
                                } else {
                                 //   chatLocationShare();
                                }

                                break;

                        }
                        // TODO
                    }
                }).show();
    }

    void doBindService() {
        PreferenceManager.savePreference(getApplicationContext(), PreferenceManager.KEY_CONNECTING, "three");

        if (!isBound) {

            Intent service = new Intent(this, ChatService.class);
            service.putExtra("Messenger", sportoMessanger);
            startService(service);
            bindService(service, mConnection, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    void doUnbindService() {
        if (isBound) {
            if (sportoService != null) {
                try {
                    Message msg = Message.obtain(null,
                            ChatService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = sportoMessanger;
                    sportoService.send(msg);
                } catch (RemoteException e) {
                }
            }
            unbindService(mConnection);
            isBound = false;
            Log.i("SportoService", "SportoService Unbounded");
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            sportoService = new Messenger(service);
            Log.i("ChatService", "attached");
            try {
                Message msg = Message.obtain(null, ChatService.MSG_REGISTER_CLIENT);
                msg.replyTo = sportoMessanger;
                sportoService.send(msg);
                updateUnreadMsg();
            } catch (RemoteException e) {
            }
            if (friendJID == null || friendJID.equals(""))
                return;
            try {
                Message msg = Message.obtain(null,
                        ChatService.SET_CURRENT_WINDOW_JID);
                Bundle bundle = new Bundle();
                bundle.putString("JID", friendJID);
                msg.setData(bundle);
                msg.replyTo = sportoMessanger;
                sportoService.send(msg);
            } catch (Exception e) {
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
            Log.i(TAG, "detached");
            doBindService();
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       /* if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        }
        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }


        if (requestCode == REQUEST_INKE_MULTI_IMAGE && data != null) {
            ArrayList<String> list = new ArrayList<String>();
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            if (images.size() > 0) {
                for (int i = 0, l = images.size(); i < l; i++) {
                    list.add(i, images.get(i).path);
                }
                if (Constants.MEDIA_TYPE_IMAGE.equalsIgnoreCase(data.getStringExtra(Constants.MEDIATYPE))) {
                    sendMultiImageToSevice(list);
                } else {
                    sendMulitVideoToSevice(list);
                }
            }
        }

        if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK) {
            Uri correctedUri = null;
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCapturedImageURI);
                imageBitmap = imageOrientationValidator(imageBitmap, getRealPathFromURI1(mCapturedImageURI));
                correctedUri = getImageUri(imageBitmap);
            } catch (OutOfMemoryError e) {
                e.getMessage();
            } catch (FileNotFoundException e) {
                e.getMessage();
            } catch (IOException e) {
                e.getMessage();
            }
            beginCrop(correctedUri);
        }*/

        // For Camera Video


        if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK) {
            Uri correctedUri = null;
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCapturedImageURI);
              //  imageBitmap = imageOrientationValidator(imageBitmap, getRealPathFromURI1(mCapturedImageURI));
                correctedUri = getImageUri(imageBitmap);


                ArrayList<String> list = new ArrayList<String>();
                list.add(0, getRealPathFromURI1(mCapturedImageURI));
                sendMultiImageToSevice(list);


            } catch (OutOfMemoryError e) {
                e.getMessage();
            } catch (FileNotFoundException e) {
                e.getMessage();
            } catch (IOException e) {
                e.getMessage();
            }

        }else
        if (requestCode == 101) {
            if (data != null) {
                double latitude = data.getDoubleExtra(Constants.LATITUDE, 0.0);
                double longitude = data.getDoubleExtra(Constants.LONGITUDE, 0.0);
                if (latitude > 0 && longitude > 0) {
                    sendLocationToService(latitude, longitude);
                }
            }
        }else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            File videoFile = new File(strSaveVideoPath);
            if (videoFile.exists()) {
                videoPath = videoFile.getAbsolutePath();
                ArrayList<String> list = new ArrayList<String>();
                list.add(0, videoPath);
                sendMulitVideoToSevice(list);
            }
        }else if (requestCode == OPEN_MEDIA_PICKER) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK && data != null) {

                ArrayList<String> getMediaArrayList = data.getStringArrayListExtra("result");
                if(getMediaArrayList.size()>0)
                {
                    sendMultiImageToSevice(getMediaArrayList);
                }

              /*  ArrayList<String> list = new ArrayList<String>();
                ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                if (images.size() > 0) {
                    for (int i = 0, l = images.size(); i < l; i++) {
                        list.add(i, images.get(i).path);
                    }
                    if (Constants.MEDIA_TYPE_IMAGE.equalsIgnoreCase(data.getStringExtra(Constants.MEDIATYPE))) {
                        sendMultiImageToSevice(list);
                    } else {
                        sendMulitVideoToSevice(list);
                    }
                }*/

            }
        }

    }


    private void chooseImage() {
        final CharSequence[] items = {"Photo"/*, "Video"*/};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //    AlertDialog.Builder builder = new AlertDialog.Builder(InkeChatConversation.this,R.layout.dilogall_all);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Photo")) {
                    String fileName = "temp.jpg";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intentCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intentCam.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    startActivityForResult(intentCam, CAMERA_CAPTURE);
                }/* else if (items[item].equals("Video")) {
                    *//* calling video recording *//*
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
                }*/

            }
        });
        builder.show();
    }

    /**
     * CREATING FILE URI TO STORE IMAGE/VIDEO
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * RETURNING IMAGE / VIDEO
     */
    private static File getOutputMediaFile(int type) {

        // EXTERNAL SDCARD LOCATION
        /*File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);*/

        strSaveVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File mediaStorageDir = new File(strSaveVideoPath, Constants.VIDEO_PATH);

        // CREATE THE STORAGE DIRECTORY IF IT DOES NOT EXIST
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // CREATE A MEDIA FILE NAME
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
            strSaveVideoPath = mediaFile.getPath().toString();
        } else {
            return null;
        }

        return mediaFile;
    }

    private Bitmap xximageOrientationValidator(Bitmap bitmap, String path) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }

    public String getRealPathFromURI1(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }

    private Uri getImageUri(Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Title", null);
        return Uri.parse(path);
    }
/*
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {

            Uri croppedUri = Crop.getOutput(result);
            if (croppedUri != null) {
                Bitmap csBitmap = null;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                try {
                    csBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), croppedUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //updatedProfileImage = csBitmap;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                csBitmap = Bitmap.createScaledBitmap(csBitmap, 500, 500, true);
                csBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                SaveImage(csBitmap);
                picturePath = compressImage(croppedUri.toString());
                ArrayList<String> list = new ArrayList<String>();
                list.add(0, picturePath);
                sendMultiImageToSevice(list);
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }*/

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "SportO/Media/Images/Sent");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    private void sendMulitVideoToSevice(ArrayList images) {
        if (sportoService != null) {
            try {
                Message msg = Message.obtain(null, ChatService.CHAT_SEND_FILE);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(ChatService.KEY_IMAGE_LIST, images);
                bundle.putString(ChatService.KEY_MESSAGE_TYPE, ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA.toString());
                bundle.putString(ChatService.KEY_RECEIVER, friendJID);
                bundle.putString(ChatService.KEY_MEDIA_TYPE, ChatData.ChatMessageType.MESSAGE_TYPE_MEDIA_MULTI_VIDEO.toString());
                msg.replyTo = sportoMessanger;
                msg.setData(bundle);
                sportoService.send(msg);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        loadChatHistory();
    }

    public void sendDownVideoToService(String downloadUrl, String chatID) {
        try {
            if (sportoService != null) {

                Message msg = Message.obtain(null, ChatService.CHAT_DOWNLOAD_VIDEO_FILE);
                Bundle bundle = new Bundle();

                bundle.putString(ChatService.KEY_MESSAGE_TYPE, ChatData.ChatMessageType.MESSAGE_TYPE_DOWNLOAD_FILE.toString());
                /*bundle.putString(ChatService.KEY_MEDIA_DOWN_URL, downloadUrl);
                bundle.putString(ChatService.KEY_MEDIA_DOWN_CHATID, chatID);
mu
                */
                msg.replyTo = sportoMessanger;
                msg.setData(bundle);
                sportoService.send(msg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        loadChatHistory();
    }

    private void sendUnreadMsgUpdate(String friendJID) {
        {
            try {
                if (sportoService != null) {
                    Message msg = Message.obtain(null, ChatService.CHAT_UPDATE_READ_STATUS);
                    Bundle bundle = new Bundle();
                    bundle.putString(ChatService.KEY_RECEIVER, friendJID);
                    msg.replyTo = sportoMessanger;
                    msg.setData(bundle);
                    sportoService.send(msg);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDialog(final ChatData chatData) {
        String[] options = new String[0];
        if (chatData.getMessageType().equals(ChatData.ChatMessageType.MESSAGE_TYPE_CHAT.toString())) {
            options = new String[]{"Copy"};

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setInverseBackgroundForced(true);
        final String[] finalOptions = options;
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (finalOptions[which]) {
                    case "Copy":
                        copyTextToClipBoard(chatData.getChatMessage());
                        break;
                }
            }
        });
        builder.show();
    }

    private void copyTextToClipBoard(String chatMessage) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(chatMessage);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", chatMessage);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(this, "Message is copied to clip board", Toast.LENGTH_LONG).show();
    }

    private void showSnacbar() {
        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Connecting...", Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            chatService.stopSelf();
                            Thread.sleep(5000);
                            doBindService();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        // Changing message text color
        snackbar.setActionTextColor(getResources().getColor(R.color.red));
        // Changing action button text color
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.WHITE);
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextSize(17);
        textView.setTextColor(getResources().getColor(R.color.time_color));
        snackbar.show();
    }

    private void showDataSnacbar(String msg) {
        snackbar = Snackbar
                .make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);
        // Changing message text color
        snackbar.setActionTextColor(getResources().getColor(R.color.red));
        // Changing action button text color
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.WHITE);

        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextSize(17);
        textView.setTextColor(getResources().getColor(R.color.time_color));
        snackbar.show();
    }


/*    public void menuGallery() {
        Intent i = new Intent(ChatConversation.this, MultiImageVideoPicker.class);
        i.putExtra(Constants.CURRENTUSER, friendName);
        startActivityForResult(i, REQUEST_INKE_MULTI_IMAGE);
    }

    public void chatLocationShare() {
        Intent intentMap = new Intent(ChatConversation.this, ChatLocationShare.class);
        startActivityForResult(intentMap, 101);
    }*/

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void allPermissionsCamera() {

        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");


        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSION_CAMERA);
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSION_CAMERA);
            return;
        } else {

            chooseImage();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void allPermissionsForGallery() {

        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write Storage");
        if (!addPermission(permissionsList, android.Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read Storage");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSION_GALLERY);
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSION_GALLERY);
            return;
        } else {

                openGallery();

        }
    }



    @TargetApi(Build.VERSION_CODES.M)
    private void allPermissionsForLocation() {

        List<String> permissionsNeeded_ = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, android.Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded_.add("Access fine location");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded_.add("Read coarse  location");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded_.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded_.get(0);
                for (int i = 1; i < permissionsNeeded_.size(); i++)
                    message = message + ", " + permissionsNeeded_.get(i);

                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSION_LOCATION);
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSION_LOCATION);
            return;
        } else {

            chatLocationShare();

        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSION_GALLERY: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        ) {
                    // All Permissions Granted
                    openGallery();
                } else {
                    // Permission Denied
                    Toast.makeText(ChatConversation.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;

            case REQUEST_CODE_ASK_MULTIPLE_PERMISSION_LOCATION: {
                Map<String, Integer> perms_ = new HashMap<String, Integer>();
                perms_.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms_.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++)
                    perms_.put(permissions[i], grantResults[i]);
                if (perms_.get(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms_.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        ) {
                    // All Permissions Granted
                    chatLocationShare();
                } else {
                    // Permission Denied
                    Toast.makeText(ChatConversation.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            break;
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSION_CAMERA: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        ) {
                    // All Permissions Granted
                    chooseImage();
                } else {
                    // Permission Denied
                    Toast.makeText(ChatConversation.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(this, Gallery.class);
        intent.putExtra("title", "Send To "+formatedfriendJID);
        startActivityForResult(intent, OPEN_MEDIA_PICKER);

    }

    public void chatLocationShare() {
        Intent intentShareLocation = new Intent(this, ChatLocationShare.class);
        startActivityForResult(intentShareLocation, 101);
    }

}


