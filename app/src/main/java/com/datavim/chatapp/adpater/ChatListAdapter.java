package com.datavim.chatapp.adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.datavim.chatapp.R;
import com.datavim.chatapp.comman.Constants;
import com.datavim.chatapp.database.DatabaseManager;
import com.datavim.chatapp.model.records.ChatData;
import com.datavim.chatapp.model.records.FriendData;
import com.datavim.chatapp.utils.OnItemClickListener;
import com.datavim.chatapp.utils.OnItemLongClickListener;
import com.datavim.chatapp.utils.PreferenceManager;
import com.datavim.chatapp.utils.RoundedImageView;
import com.datavim.chatapp.utils.UtilityClass;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by apple on 30/05/15.
 */
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FriendData> friendsArrayList;
    private ArrayList<FriendData> searchArraylist;
    OnItemClickListener onItemClickListener;
    OnItemLongClickListener onItemLongClickListener;

    public ChatListAdapter(Context context, ArrayList<FriendData> friendsArrayList) {
        this.context = context;
        this.friendsArrayList = friendsArrayList;
        this.searchArraylist = new ArrayList<FriendData>();
        this.searchArraylist.addAll(friendsArrayList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_chat_row, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        FriendData friendData = getItem(position);

            if(friendData.getFriendName()!=null) {
            //    viewHolder.txtDisplayName.setText(friendData.getFriendName());
                String[] friendName = friendData.getFriendJID().split("@");


                viewHolder.txtDisplayName.setText(friendName[0]);

            }
            else
            {
                viewHolder.txtDisplayName.setText(friendData.getFriendJID());
            }
                String[] part = friendData.getFriendJID().split("@");


       /* if(!part[1].equalsIgnoreCase(Constants.SERVER_NAME))
        {
            viewHolder.txtLastMsgText.setPadding(7,0,7,7);
        }
        else
        {
            viewHolder.txtLastMsgText.setPadding(7,7,7,7);
        }*/


            //Set Condition
            if(friendData.getProfileThumbUrl()!=null && !friendData.getProfileThumbUrl().isEmpty() && !friendData.getProfileThumbUrl().equalsIgnoreCase(null)) {
                Picasso.with(context)
                        .load(friendData.getProfileThumbUrl())
                        .placeholder(R.drawable.ic_notofication)
                        .error(R.drawable.ic_notofication)
                        .into(viewHolder.avatarImg);
            }

        if(!friendData.getTimeOfLastMessage().equalsIgnoreCase("0")) {

            viewHolder.txtLastMsgTime.setText(UtilityClass.getFormattedDate(Long.parseLong(friendData.getTimeOfLastMessage())));
        }
        else
        {
            viewHolder.txtLastMsgTime.setText("");
        }

        DatabaseManager databaseManager = DatabaseManager.getInstance(context);


        viewHolder.txtLastMsgText.setText(friendData.getLastMessage());

        int unread = databaseManager.getUnReadChatCount(friendData.getFriendJID());
        if (unread == 0) {
            viewHolder.txtUnreadMsgCounter.setVisibility(View.GONE);
            viewHolder.txtLastMsgTime.setTextColor(context.getResources().getColor(R.color.text_filled_color_new));
        } else {
            viewHolder.txtUnreadMsgCounter.setVisibility(View.VISIBLE);
            viewHolder.txtUnreadMsgCounter.setText(unread + "");
            viewHolder.txtLastMsgTime.setTextColor(context.getResources().getColor(R.color.text_filled_color_new));
        }
        ChatData chatData = databaseManager.getLastMessageFromContact(friendData.getFriendJID());
        if (chatData == null) {
            viewHolder.txtUnreadMsgCounter.setVisibility(View.GONE);
            return;
        }
       /* if (friendData.getTypeOfLastMessage().equalsIgnoreCase(ChatData.ChatMessageType.MESSAGE_TYPE_CHAT.toString()))
            viewHolder.txtLastMsgText.setText(friendData.getLastMessage());
        else
            viewHolder.txtLastMsgText.setText(friendData.getTypeOfLastMessage());
*/
    }
    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        public RoundedImageView avatarImg;
        public TextView txtDisplayName;
        public TextView txtLastMsgText;
        public TextView txtLastMsgTime;
        public TextView txtUnreadMsgCounter;


        public ViewHolder(View view) {
            super(view);

            avatarImg = (RoundedImageView) view.findViewById(R.id.chat_row_image);
            txtDisplayName = (TextView) view.findViewById(R.id.chat_row_name);
            txtLastMsgText = (TextView) view.findViewById(R.id.chat_row_last_msg);
            txtLastMsgTime = (TextView) view.findViewById(R.id.chat_row_time);
            txtUnreadMsgCounter = (TextView) view.findViewById(R.id.chat_row_msg_counter);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(view, getPosition());

            }
            return false;
        }
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.onItemClickListener = mItemClickListener;
    }


    public void setOnItemLongClickListener(final OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
    @Override
    public int getItemCount() {
        return friendsArrayList.size();
    }

    public FriendData getItem(int position) {
        return friendsArrayList.get(position);
    }

    private String getUsername() {
        String username = PreferenceManager.getPreference(context, PreferenceManager.KEY_JID);
        if (username == null) {
            username = "";
        }
        return username;
    }
}


