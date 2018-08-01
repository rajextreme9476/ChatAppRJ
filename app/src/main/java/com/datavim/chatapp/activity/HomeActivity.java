package com.datavim.chatapp.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.datavim.chatapp.R;
import com.datavim.chatapp.adpater.ChatListAdapter;
import com.datavim.chatapp.chat.ChatService;
import com.datavim.chatapp.comman.Constants;
import com.datavim.chatapp.database.DatabaseManager;
import com.datavim.chatapp.model.records.FriendData;
import com.datavim.chatapp.utils.ChatListDividerItemDecoration;
import com.datavim.chatapp.utils.OnItemClickListener;
import com.datavim.chatapp.utils.OnItemLongClickListener;
import com.datavim.chatapp.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    Boolean isBound=false;
    Messenger messenger ;
    DatabaseManager databaseManager;
    private ChatListAdapter chatListAdapter;
    private ArrayList<FriendData> friendChatArrayList = new ArrayList<FriendData>();
    private RecyclerView chatRecyclerView;
    private TextView tv_no_chats_avilable;
    List<FriendData> friendData = new ArrayList<>();
    public static Messenger inkeService = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
       // setDummyDataRJIO();
    //    setDummyDataRAIRTEL();
        messenger = new Messenger(new MessageHandler());
        doBindService();
        init();
        addData();
    }

    private void init() {

        tv_no_chats_avilable = (TextView) findViewById(R.id.tv_no_chats_avilable);
        chatRecyclerView = (RecyclerView) findViewById(R.id.rv_chat_list);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        chatRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        chatRecyclerView.setFitsSystemWindows(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            chatRecyclerView.addItemDecoration(new ChatListDividerItemDecoration(
                    getApplicationContext()
            ));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(chatListAdapter!=null)
        addData();
        chatListAdapter.notifyDataSetChanged();
    }

    void doBindService()
    {
        if (!isBound)
        {
            Intent service = new Intent(this, ChatService.class);
            service.putExtra("Messenger", messenger);
            startService(service);
            bindService(service, mConnection, Context.BIND_AUTO_CREATE);

            isBound = true;
        }
    }

    class MessageHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case ChatService.MSG_REGISTER_CLIENT:
                    break;
                case ChatService.USER_AUTHENTICATED:
                    break;
                case ChatService.CHAT_RECEIVE_MESSAGE:
                    addData();
                    chatListAdapter.notifyDataSetChanged();
                case ChatService.CHAT_REFRESH_LIST:
                    addData();
                    chatListAdapter.notifyDataSetChanged();
                default:
                    super.handleMessage(msg);
            }
        }
    }



    private ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service
        )
        {
            inkeService = new Messenger(service);
            Log.i("ChatService", "attached");
            try
            {
                Message msg = Message.obtain(null,
                        ChatService.MSG_REGISTER_CLIENT);
                msg.replyTo = messenger;
                inkeService.send(msg);
            }
            catch (RemoteException e)
            {
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            isBound = false;
            Log.i("ChatService", "detached");

        }
    };



    /*----- Show list ------ */

    private void addData()
    {
        databaseManager = DatabaseManager.getInstance(getApplicationContext());
        friendData = databaseManager.getFriends();
        friendChatArrayList.clear();
        for (FriendData fd : friendData) {
            friendChatArrayList.add(fd);
        }
        if (friendChatArrayList.size() == 0) {
            tv_no_chats_avilable.setVisibility(View.VISIBLE);
        } else {
            tv_no_chats_avilable.setVisibility(View.GONE);
        }
        chatListAdapter = new ChatListAdapter(getApplicationContext(), friendChatArrayList);

        chatRecyclerView.setAdapter(chatListAdapter);
        chatListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

              /*  try {
                    Message msg = Message.obtain(null, ChatService.CHAT_ADD_ROSTERS);
                    Bundle bundle = new Bundle();
                    bundle.putString(ChatService.KEY_FRIEND_JID, chatListAdapter.getItem(position).getFriendJID());
                    bundle.putString(ChatService.KEY_FRIEND_NAME, "");
                    msg.replyTo = messenger;
                    msg.setData(bundle);
                    messenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }*/

                databaseManager.updateIsRead(chatListAdapter.getItem(position).getFriendJID());
                Intent intent = new Intent(HomeActivity.this, ChatConversation.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.FRIEND_JID, chatListAdapter.getItem(position).getFriendJID());
                bundle.putString(Constants.FRIEND_NAME, chatListAdapter.getItem(position).getFriendName());
                bundle.putString(Constants.PROFILEPICPATH,chatListAdapter.getItem(position).getProfileThumbUrl());
                bundle.putString(Constants.EVENTID,String.valueOf(chatListAdapter.getItem(position).getFriendUserId()));
                bundle.putString(Constants.ISBLOCKED,String.valueOf(chatListAdapter.getItem(position).getIsBlocked()));
                bundle.putString(Constants.ISEXIT,String.valueOf(chatListAdapter.getItem(position).getIsExit()));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        chatListAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                String name=friendChatArrayList.get(position).getFriendJID();
                String[] part = name.split("@");
                if(part[1].equalsIgnoreCase(Constants.SERVER_NAME)) {
                   // showDialog(friendChatArrayList.get(position));
                }
                else
                {
                    //showDialogClear(friendChatArrayList.get(position));
                }
            }
        });
    }


}
