package com.datavim.chatapp.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.datavim.chatapp.R;
import com.datavim.chatapp.chat.ChatService;
import com.datavim.chatapp.database.DatabaseManager;
import com.datavim.chatapp.model.records.FriendData;
import com.datavim.chatapp.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText etNumber,etPassword;
    AppCompatButton btnLogin;
    String mobile,pass;


    ChatService chatService;
    Boolean isBound=false;
    Messenger messenger = new Messenger(new MessageHandler());
    DatabaseManager databaseManager;
    List<FriendData> friendData = new ArrayList<>();
    ProgressDialog progressDialog;
    public static Messenger inkeService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isLogin = PreferenceManager.getPreferenceBoolean(LoginActivity.this,PreferenceManager.KEY_FIRST_INSTALATION);
        if(isLogin)
        {
            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }

        Log.e("itsgood","ok instant");
        setContentView(R.layout.activity_login_chat);
        initView();
        initClickListner();
    }

    private void initClickListner() {

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobile = etNumber.getText().toString();
                pass = etPassword.getText().toString();
                setUserData();
                doBindService();
                progressDialog.setMessage("loading");
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    progressDialog.dismiss();
                    PreferenceManager.savePreference(getApplicationContext(),PreferenceManager.KEY_FIRST_INSTALATION,true);
                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                    }
                }, 7000);
            }
        });
    }

    private void initView() {

        etNumber = (EditText)findViewById(R.id.input_number);
        etPassword = (EditText)findViewById(R.id.input_password);
        btnLogin = (AppCompatButton)findViewById(R.id.btn_login);
        progressDialog = new ProgressDialog(LoginActivity.this);
    }

    private void setUserData() {

        PreferenceManager.savePreference(this, PreferenceManager.KEY_JID, mobile);
        PreferenceManager.savePreference(this, PreferenceManager.KEY_NAME, "");
        PreferenceManager.savePreference(this, PreferenceManager.KEY_CHAT_PASSWORD,pass);
    }


    void doBindService()
    {
        if (!isBound)
        {
            Intent service = new Intent(this, ChatService.class);
            service.putExtra("Messenger", messenger);
            ChatService.isConnecting = true;
            startService(service);
            bindService(service, mConnection, Context.BIND_AUTO_CREATE);
            try {

                if(messenger!=null) {
                    Message msg = Message.obtain(null,
                            ChatService.CONNECT_WITH_ALL_GROUPS_DATABASE);
                    msg.replyTo = messenger;
                    messenger.send(msg);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            isBound = true;
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

    class MessageHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case ChatService.MSG_REGISTER_CLIENT:
                    break;
                case ChatService.USER_UNAUTHENTICATED:
                case ChatService.USER_AUTHENTICATED:

                    break;
                case ChatService.CHAT_RECEIVE_MESSAGE:
                    break;
                case ChatService.CHAT_REFRESH_LIST:
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
