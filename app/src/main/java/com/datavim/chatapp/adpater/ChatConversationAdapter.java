package com.datavim.chatapp.adpater;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.text.emoji.widget.EmojiAppCompatTextView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.datavim.chatapp.R;
import com.datavim.chatapp.activity.ChatConversation;
import com.datavim.chatapp.activity.ImageViewer;
import com.datavim.chatapp.database.DatabaseManager;
import com.datavim.chatapp.model.records.ChatData;
import com.datavim.chatapp.model.records.MediaData;
import com.datavim.chatapp.utils.CustomPicasso;
import com.datavim.chatapp.utils.EmojiTextView;
import com.datavim.chatapp.utils.OnMessageRead;
import com.datavim.chatapp.utils.parser.Parser;
import com.datavim.chatapp.utils.renderer.MarkdownBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import com.datavim.chatapp.comman.Constants;
import com.datavim.chatapp.utils.OnItemClickListener;
import com.datavim.chatapp.utils.OnItemLongClickListener;
import com.datavim.chatapp.utils.PreferenceManager;

import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by admin on 12-04-2016.
 */
public class ChatConversationAdapter extends RecyclerView.Adapter<ChatConversationAdapter.ViewHolder> {
    private Context context;
    private ChatConversation activity;
    OnItemClickListener onItemClickListener;
    private ArrayList<ChatData> chatDataArrayList;
    private DatabaseManager sportoDatabaseManager;
    private boolean isAutoImageDownload = true;
    private boolean isAutoAudioDownload = false;
    private boolean isAutoVideoDownload = true;
    private OnMessageRead onMessageRead;
    OnItemLongClickListener onItemLongClickListener;




    public ChatConversationAdapter(ChatConversation activity, Context context, ArrayList<ChatData> chatDataArrayList, OnMessageRead onMessageRead) {
        this.context = context;
        this.chatDataArrayList = chatDataArrayList;
        this.activity = activity;
        this.onMessageRead = onMessageRead;
        sportoDatabaseManager = sportoDatabaseManager.getInstance(context);


    }


    private String getUsername() {
        String username = PreferenceManager.getPreference(context, PreferenceManager.KEY_JID);
        return username;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_chat_conversation, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ChatData chatData = getItem(position);

        if (chatData != null) {
            if (chatData.getIsInbox() == 1) {
                onMessageRead.onMessageRead(chatData);
            }

            if (chatData.getMessageType().equalsIgnoreCase("chat")) {
                if ((chatData.getFromJID()).equalsIgnoreCase(getUsername())) {
                    holder.llReceiverImg.setVisibility(View.GONE);
                    holder.llReceiverLocation.setVisibility(View.GONE);
                    holder.llReceiverVideo.setVisibility(View.GONE);
                    holder.llReceiverMsg.setVisibility(View.GONE);

                    holder.llSenderImg.setVisibility(View.GONE);
                    holder.llSenderVideo.setVisibility(View.GONE);
                    holder.llSenderLocation.setVisibility(View.GONE);
                    holder.llSenderMsg.setVisibility(View.VISIBLE);


                    int bulletPointColor = ContextCompat.getColor(context, R.color.red);
                    int codeBackgroundColor = ContextCompat.getColor(context, R.color.white_light_gray);
                    Typeface codeBlockTypeface = ResourcesCompat.getFont(context, R.font.inika);

                    final CharSequence text = new MarkdownBuilder(bulletPointColor, codeBackgroundColor,
                            codeBlockTypeface, new Parser())
                            .markdownToSpans(chatData.getChatMessage());



                    holder.tvSenderMsg.setText(text);


                    holder.tvSenderMsgTime.setText(getTime(Long.parseLong(chatData.getTimeVal())));
                    if (chatData.getIsDelivered() == 0) {
                        holder.ivSenderMsgReceipt.setImageResource(R.drawable.sent);
                    } else if (chatData.getIsRead() == 1) {
                        holder.ivSenderMsgReceipt.setImageResource(R.drawable.read);
                    } else if (chatData.getIsDelivered() == 1) {
                        holder.ivSenderMsgReceipt.setImageResource(R.drawable.delivered);
                    }
                } else {


                    String[] part = chatData.getFromJID().split("@");
                    if (part[1].equalsIgnoreCase(Constants.SERVER_NAME)) {
                        holder.tvRecevierName.setVisibility(View.GONE);
                    } else {
                        holder.tvRecevierName.setVisibility(View.VISIBLE);
                        holder.tvRecevierName.setText(chatData.getDisplayName());
                    }
                    holder.llSenderImg.setVisibility(View.GONE);
                    holder.llSenderMsg.setVisibility(View.GONE);
                    holder.llSenderLocation.setVisibility(View.GONE);
                    holder.llReceiverVideo.setVisibility(View.GONE);

                    holder.llReceiverMsg.setVisibility(View.VISIBLE);
                    holder.llReceiverLocation.setVisibility(View.GONE);
                    holder.llReceiverImg.setVisibility(View.GONE);
                    holder.llSenderVideo.setVisibility(View.GONE);

                    holder.tvReceiverMsg.setText(chatData.getChatMessage());
                    holder.tvReceiverMsgTime.setText(getTime(Long.parseLong(chatData.getTimeVal())));
                }

            }
            if (chatData.getMessageType().equalsIgnoreCase("location")) {
                if (chatData.getFromJID().equalsIgnoreCase(getUsername())) {

                    holder.llSenderLocation.setVisibility(View.VISIBLE);
                    holder.llReceiverMsg.setVisibility(View.GONE);
                    holder.llReceiverLocation.setVisibility(View.GONE);
                    holder.llReceiverImg.setVisibility(View.GONE);

                    holder.llSenderMsg.setVisibility(View.GONE);
                    holder.llSenderImg.setVisibility(View.GONE);
                    holder.llReceiverVideo.setVisibility(View.GONE);
                    holder.llSenderVideo.setVisibility(View.GONE);

                    Picasso.with(context).load(chatData.getMediaUrl()).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(holder.ivSenderLocation);

                 /*   CustomPicasso.getPicassoClient(context).load(mediaData.getMediaUrl()).placeholder(new BitmapDrawable(context.getResources(), bitmap))
                            .error(new BitmapDrawable(context.getResources(), bitmap)).into(holder.ivReceiverImg);
*/

                    holder.tvSenderLocationTime.setText(getTime(Long.parseLong(chatData.getTimeVal())));

                    if (chatData.getIsDelivered() == 0) {
                        holder.ivSenderLocationReceipt.setImageResource(R.drawable.sent);
                    } else if (chatData.getIsRead() == 1) {
                        holder.ivSenderLocationReceipt.setImageResource(R.drawable.read);
                    } else if (chatData.getIsDelivered() == 1) {
                        holder.ivSenderLocationReceipt.setImageResource(R.drawable.delivered);
                    }
                } else {
                    holder.llReceiverLocation.setVisibility(View.VISIBLE);
                    holder.llReceiverVideo.setVisibility(View.GONE);
                    holder.llReceiverImg.setVisibility(View.GONE);
                    holder.llReceiverMsg.setVisibility(View.GONE);


                    holder.llSenderMsg.setVisibility(View.GONE);
                    holder.llSenderLocation.setVisibility(View.GONE);
                    holder.llSenderImg.setVisibility(View.GONE);
                    holder.llSenderVideo.setVisibility(View.GONE);


                    String[] part = chatData.getFromJID().split("@");
                    if (part[1].equalsIgnoreCase(Constants.SERVER_NAME)) {
                        holder.tvreceiverlocationname.setVisibility(View.GONE);
                    } else {
                        holder.tvreceiverlocationname.setVisibility(View.VISIBLE);
                        holder.tvreceiverlocationname.setText(chatData.getDisplayName());
                    }

                    String string = chatData.getChatMessage();
                    String[] parts = string.split("@");
                    String lat = parts[0];
                    String lon = parts[1];
                    String locationURL = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lon + "&markers=icon:http://tinyurl.com/2ftvtt6 %7c " + lat + "," + lon + "&zoom=16&size=300x250&sensor=false";
                    //  Picasso.with(context).load(locationURL).into(holder.ivReceiverLocation);
                    Picasso.with(context).load(locationURL).placeholder(R.drawable.placeholder).
                            error(R.drawable.placeholder).into(holder.ivReceiverLocation);

                    holder.tvReceiverLocationTime.setText(getTime(Long.parseLong(chatData.getTimeVal())));
                }
                holder.llReceiverLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String string = chatData.getChatMessage();
                        String[] parts = string.split("@");
                        String latitude = parts[0];
                        String longitude = parts[1];
                        String label = "SportO maps";
                        String uriBegin = "geo:" + latitude + "," + longitude;
                        String query = latitude + "," + longitude + "(" + label + ")";
                        String encodedQuery = Uri.encode(query);
                        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                        Uri uri = Uri.parse(uriString);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
            }
            if (chatData.getMessageType().equalsIgnoreCase("image")) {

                final String fromJID = chatData.getFromJID();
                long mediaqId = chatData.getMediaId();
                final MediaData mediaqData = sportoDatabaseManager.getMediaDataFromMediaID(mediaqId);

                if (mediaqData.getMediaType().equalsIgnoreCase("Image")) {
                    if (chatData.getFromJID().equalsIgnoreCase(getUsername())) {

                        holder.llSenderImg.setVisibility(View.VISIBLE);
                        holder.llSenderMsg.setVisibility(View.GONE);
                        holder.llSenderLocation.setVisibility(View.GONE);
                        holder.llSenderVideo.setVisibility(View.GONE);

                        holder.llReceiverLocation.setVisibility(View.GONE);
                        holder.llReceiverImg.setVisibility(View.GONE);
                        holder.llReceiverVideo.setVisibility(View.GONE);
                        holder.llReceiverMsg.setVisibility(View.GONE);


                        long mediaId = chatData.getMediaId();
                        MediaData mediaData = sportoDatabaseManager.getMediaDataFromMediaID(mediaId);
                        File imgFile = new File(mediaData.getMediaLocalPath());
                        if (imgFile.exists()) {
                            Glide.with(context)
                                    .load(imgFile.getAbsolutePath())
                                    .placeholder(R.drawable.placeholder).centerCrop().into(holder.ivSenderImg);
                        }
                        holder.tvSenderImgTime.setText(getTime(Long.parseLong(chatData.getTimeVal())));
                        if (chatData.getIsMediaDownloaded().equalsIgnoreCase("1")) {
                            holder.pbSenderImg.setVisibility(View.GONE);
                        } else if (chatData.getIsMediaDownloaded().equalsIgnoreCase("2")) {
                            Log.e("CHAT ADAPTER", "VDIEO SEND RETRY");
                        } else if (chatData.getIsMediaDownloaded().equalsIgnoreCase("0")) {
                            holder.pbSenderImg.setVisibility(View.VISIBLE);
                        }

                    } else {


                        holder.llSenderImg.setVisibility(View.GONE);
                        holder.llSenderMsg.setVisibility(View.GONE);
                        holder.llSenderLocation.setVisibility(View.GONE);
                        holder.llSenderVideo.setVisibility(View.GONE);

                        holder.llReceiverImg.setVisibility(View.VISIBLE);
                        holder.llReceiverLocation.setVisibility(View.GONE);
                        holder.llReceiverVideo.setVisibility(View.GONE);
                        holder.llReceiverMsg.setVisibility(View.GONE);


                        String[] part = chatData.getFromJID().split("@");
                        if (part[1].equalsIgnoreCase(Constants.SERVER_NAME)) {
                            holder.tvreceiverimgname.setVisibility(View.GONE);
                        } else {
                            holder.tvreceiverimgname.setVisibility(View.VISIBLE);
                            holder.tvreceiverimgname.setText(chatData.getDisplayName());
                        }


                        Bitmap bitmap = null;
                        byte[] decodedString = null;

                        if(chatData.getAttachment()!= null && !chatData.getAttachment().isEmpty() && !chatData.getAttachment().equalsIgnoreCase("null")) {
                            holder.pbReceiverImg.setVisibility(View.GONE);
                            decodedString = Base64.decode(chatData.getAttachment(), Base64.DEFAULT);
                            bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            holder.ivReceiverImg.setImageBitmap(bitmap);
                            return;
                        }

                        final MediaData mediaData = sportoDatabaseManager.getMediaDataFromMediaID(chatData.getMediaId());
                        String mediaPath = mediaData.getMediaLocalPath();
                        File imgFile = new File(mediaPath);
                        if (mediaPath != null && !mediaPath.equals("")) {
                            Log.i("File Exit", "!!!!!");
                            if (imgFile.exists()) {
                                holder.pbReceiverImg.setVisibility(View.VISIBLE);
                                Glide.with(context)
                                        .load(imgFile.getAbsolutePath())
                                        .placeholder(R.drawable.placeholder).centerCrop().into(holder.ivReceiverImg);
                                holder.pbReceiverImg.setVisibility(View.GONE);

                            }
                        } else {
                            if (!isAutoImageDownload == true) {

                                if (chatData.getAttachment() != null) {
                                    holder.pbReceiverImg.setVisibility(View.VISIBLE);
                                    decodedString = Base64.decode(chatData.getAttachment(), Base64.DEFAULT);
                                    bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    holder.ivReceiverImg.setImageBitmap(bitmap);
                                    holder.pbReceiverImg.setVisibility(View.GONE);
                                }

                                Glide.with(context)
                                        .load(imgFile.getAbsolutePath())
                                        .placeholder(R.drawable.placeholder).centerCrop().into(holder.ivReceiverImg);
                                holder.ivReceiverImgDownloadBtn.setVisibility(View.VISIBLE);

                            } else {

                                if (chatData.getAttachment() != null) {
                                    holder.pbReceiverImg.setVisibility(View.VISIBLE);
                                    decodedString = Base64.decode(chatData.getAttachment(), Base64.DEFAULT);
                                    bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    holder.ivReceiverImg.setImageBitmap(bitmap);
                                    holder.pbReceiverImg.setVisibility(View.GONE);
                                }
                                if (!mediaData.getMediaUrl().equals("")) {
                                    holder.pbReceiverImg.setVisibility(View.VISIBLE);
                                    CustomPicasso.getPicassoClient(context).load(mediaData.getMediaUrl()).placeholder(new BitmapDrawable(context.getResources(), bitmap))
                                            .error(new BitmapDrawable(context.getResources(), bitmap)).into(holder.ivReceiverImg);

                                    final String fileName = "" + mediaData.getMediaId();

                                    Target target = new Target() {
                                        @Override
                                        public void onPrepareLoad(Drawable arg0) {
                                            return;
                                        }

                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                                            try {

                                                holder.pbReceiverImg.setVisibility(View.GONE);
                                                String path = Constants.FILERECEIVEPATH + fileName + ".jpg";
                                                File file = null;
                                                file = new File(path);
                                                file.createNewFile();

                                                FileOutputStream ostream = new FileOutputStream(file);
                                                Bitmap saveBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                                                Canvas c = new Canvas(saveBitmap);
                                                c.drawColor(Color.WHITE);
                                                c.drawBitmap(bitmap, 0, 0, null);
                                                saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                                                saveBitmap.recycle();

                                                ostream.close();
                                                ostream.flush();
                                                sportoDatabaseManager.updateImageIsDownlaod(mediaData.getMediaId(), path);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable arg0) {
                                            return;
                                        }
                                    };

                                    CustomPicasso.getPicassoClient(context)
                                            .load(mediaData.getMediaUrl())
                                            .into(target);
                                    holder.pbReceiverImg.setVisibility(View.GONE);
                                }
                            }
                        }

                        //        holder.tvReceiverImgTime.setVisibility(View.GONE);
                        holder.tvReceiverImgTime.setText(getTime(Long.parseLong(chatData.getTimeVal())));
                    }
                    if (chatData.getIsDelivered() == 0) {
                        holder.ivSenderImgReceipt.setImageResource(R.drawable.sent);
                    } else if (chatData.getIsRead() == 1) {
                        holder.ivSenderImgReceipt.setImageResource(R.drawable.read);
                    } else if (chatData.getIsDelivered() == 1) {
                        holder.ivSenderImgReceipt.setImageResource(R.drawable.delivered);
                    }

                    holder.ivReceiverImgResendBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.resendImageToServer();
                        }
                    });
                    holder.ivReceiverImgDownloadBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.ivReceiverImgDownloadBtn.setVisibility(View.GONE);

                            Bitmap bitmap = null;
                            final ChatData chatData = getItem(position);
                            final MediaData mediaData = sportoDatabaseManager.getMediaDataFromMediaID(chatData.getMediaId());

                            if (!mediaData.getMediaUrl().equals("")) {
                                holder.pbReceiverImg.setVisibility(View.VISIBLE);

                                CustomPicasso.getPicassoClient(context).load(mediaData.getMediaUrl()).placeholder(new BitmapDrawable(context.getResources(), bitmap))
                                        .error(new BitmapDrawable(context.getResources(), bitmap)).into(holder.ivReceiverImg);
                                final String fileName = "" + mediaData.getMediaId();
                                Target target = new Target() {
                                    @Override
                                    public void onPrepareLoad(Drawable arg0) {
                                        return;
                                    }

                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                                        try {
                                            holder.pbReceiverImg.setVisibility(View.GONE);
                                            String path = Constants.FILERECEIVEPATH + fileName + ".jpg";
                                            File file = null;
                                            file = new File(path);
                                            file.createNewFile();
                                            FileOutputStream ostream = new FileOutputStream(file);
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                                            ostream.close();
                                            sportoDatabaseManager.updateImageIsDownlaod(mediaData.getMediaId(), path);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable arg0) {
                                        return;
                                    }
                                };
                                CustomPicasso.getPicassoClient(context)
                                        .load(mediaData.getMediaUrl())
                                        .into(target);
                            }
                        }
                    });

                    final String filename = "/sdcard/SportO/Media/Images/IMG-SportO00";

                    holder.ivReceiverImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ChatData chatData = getItem(position);
                            long id = chatData.getMediaId();
                            MediaData mediaData = sportoDatabaseManager.getMediaDataFromMediaID(chatData.getMediaId());

                            Intent intent = new Intent(context, ImageViewer.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Constants.IMAGEPATH, filename + id + ".jpg");
                            context.startActivity(intent);
                        }
                    });

                    holder.ivSenderImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ChatData chatData = getItem(position);
                            long id = chatData.getMediaId();
                            MediaData mediaData = sportoDatabaseManager.getMediaDataFromMediaID(chatData.getMediaId());

                            Intent intent = new Intent(context, ImageViewer.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Constants.IMAGEPATH, mediaData.getMediaLocalPath());
                            context.startActivity(intent);
                        }
                    });
                }
            }

            if ((chatData.getMessageType()).equalsIgnoreCase("Video")) {

                //String username = UtilityClass.getUserName1(context);
                String fromJID = chatData.getFromJID();

                if (fromJID.equalsIgnoreCase(getUsername())) {

                    holder.llSenderVideo.setVisibility(View.VISIBLE);
                    holder.llSenderImg.setVisibility(View.GONE);
                    holder.llSenderMsg.setVisibility(View.GONE);
                    holder.llSenderLocation.setVisibility(View.GONE);

                    holder.llReceiverMsg.setVisibility(View.GONE);
                    holder.llReceiverLocation.setVisibility(View.GONE);
                    holder.llReceiverImg.setVisibility(View.GONE);
                    holder.llReceiverVideo.setVisibility(View.GONE);


                    //  String iamgePath = chatData.getMediaId();
                    long mediaId = chatData.getMediaId();
                    MediaData mediaData = sportoDatabaseManager.getMediaDataFromMediaID(mediaId);
                    File imgFile = new File(mediaData.getMediaLocalPath());
                    if (imgFile.exists()) {
                  /*  Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(imgFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                    senderVideoFile.setImageBitmap(bitmap);
*/
                        Glide.with(context)
                                .load(imgFile.getAbsolutePath())
                                .placeholder(R.drawable.placeholder).centerCrop().into(holder.ivSenderVideo);

                    }
                    holder.tvSenderVideoTime.setText(getTime(Long.parseLong(chatData.getTimeVal())));
                    if (chatData.getIsMediaDownloaded().equalsIgnoreCase("1")) {
                        holder.pbSenderVideo.setVisibility(View.GONE);
                        holder.ivSenderBtnVideoPlay.setVisibility(View.VISIBLE);

                    } else if (chatData.getIsMediaDownloaded().equalsIgnoreCase("2")) {
                        Log.e("CHAT ADAPTER", "VDIEO SEND RETRY");
                    } else if (chatData.getIsMediaDownloaded().equalsIgnoreCase("0")) {
                        holder.pbSenderVideo.setVisibility(View.VISIBLE);
                    }

                } else {

                    holder.llReceiverVideo.setVisibility(View.VISIBLE);
                    holder.llReceiverMsg.setVisibility(View.GONE);
                    holder.llReceiverLocation.setVisibility(View.GONE);
                    holder.llReceiverImg.setVisibility(View.GONE);

                    holder.llSenderImg.setVisibility(View.GONE);
                    holder.llSenderMsg.setVisibility(View.GONE);
                    holder.llSenderLocation.setVisibility(View.GONE);
                    holder.llSenderVideo.setVisibility(View.GONE);


                    String[] part = chatData.getFromJID().split("@");
                    if (part[1].equalsIgnoreCase(Constants.SERVER_NAME)) {
                        holder.tvreceivervideoname.setVisibility(View.GONE);
                    } else {
                        holder.tvreceivervideoname.setVisibility(View.VISIBLE);
                        holder.tvreceivervideoname.setText(chatData.getDisplayName());
                    }


                    if (chatData.getIsMediaDownloaded().equalsIgnoreCase("1")) {
                        if (chatData.getAttachment() != null) {
                            byte[] decodedString = Base64.decode(chatData.getAttachment(), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            holder.ivReceiverVideo.setImageBitmap(bitmap);
                        }

                    } else {

                        if (isAutoVideoDownload == true && context != null) {
                            holder.ivReceiverVideoBtnDownload.setVisibility(View.GONE);
                            holder.pbReceiverVideo.setVisibility(View.VISIBLE);
                            sportoDatabaseManager.updateMediaIsDownlaodInProgress(chatData);
                            MediaData mediaData = sportoDatabaseManager.getMediaDataFromMediaID(chatData.getMediaId());
                            String downlaodLink = chatData.getMediaUrl();
                            String chatId = String.valueOf(chatData.getChatId());
                            activity.sendDownVideoToService(downlaodLink, chatId);
                        }
                    }

                    MediaData mediaData = sportoDatabaseManager.getMediaDataFromMediaID(chatData.getMediaId());
                    holder.tvReceiverVideoTime.setText(getTime(Long.parseLong(chatData.getTimeVal())));

                    if (chatData.getIsMediaDownloaded().equalsIgnoreCase("0")) {
                        holder.ivReceiverVideoBtnDownload.setVisibility(View.VISIBLE);
                        holder.pbReceiverVideo.setVisibility(View.GONE);
                        holder.ivReceiverVideoBtnDownload.setVisibility(View.GONE);
                    }
                    if (chatData.getIsMediaDownloaded().equalsIgnoreCase("1")) {
                        holder.ivrReceiverVideoBtnPlay.setVisibility(View.VISIBLE);
                        holder.pbReceiverVideo.setVisibility(View.GONE);
                        holder.ivReceiverVideoBtnDownload.setVisibility(View.GONE);
                    }
                    if (chatData.getIsMediaDownloaded().equalsIgnoreCase("2")) {
                        holder.pbReceiverVideo.setVisibility(View.VISIBLE);
                        holder.ivReceiverVideoBtnDownload.setVisibility(View.GONE);
                    }
                }
                if (chatData.getIsDelivered() == 0) {
                    holder.ivSenderVideoReceipt.setImageResource(R.drawable.sent);
                } else if (chatData.getIsRead() == 1) {
                    holder.ivSenderVideoReceipt.setImageResource(R.drawable.read);
                } else if (chatData.getIsDelivered() == 1) {
                    holder.ivSenderVideoReceipt.setImageResource(R.drawable.delivered);
                }

                holder.ivSenderBtnVideoPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ChatData chatData = getItem(position);
                        MediaData mediaData = sportoDatabaseManager.getMediaDataFromMediaID(chatData.getMediaId());
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(Uri.parse("file://" + mediaData.getMediaLocalPath()), "video/mp4");
                        context.startActivity(intent);

                    }
                });

                holder.ivReceiverVideoBtnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            ChatData chatData = getItem(position);
                            long id = chatData.getMediaId();
                            holder.ivReceiverVideoBtnDownload.setVisibility(View.GONE);
                            holder.pbReceiverVideo.setVisibility(View.VISIBLE);

                            sportoDatabaseManager.updateMediaIsDownlaodInProgress(chatData);
                            MediaData mediaData = sportoDatabaseManager.getMediaDataFromMediaID(chatData.getMediaId());
                            String downlaodLink = chatData.getMediaUrl();
                            String chatId = String.valueOf(chatData.getChatId());
                            ((ChatConversation) context).sendDownVideoToService(downlaodLink, chatId);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                holder.ivrReceiverVideoBtnPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ChatData chatData = getItem(position);
                        final String filename = "/sdcard/SportO/Media/Video/VUD-SportO00" + chatData.getChatId();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(Uri.parse("file://" + filename + ".mp4"), "video/mp4");
                        context.startActivity(intent);
                    }
                });
            }

        }

    }

    @Override
    public int getItemCount() {
        return chatDataArrayList.size();
    }

    public ChatData getItem(int position) {
        return chatDataArrayList.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView avatarImg;
        public TextView txtDisplayName;
        public TextView txtLastMsgText;
        public TextView txtLastMsgTime;
        public TextView txtUnreadMsgCounter;

        /*-- FOR TEXT MESSAGE--*/

        LinearLayout llReceiverMsg;
        TextView tvReceiverMsg;
        TextView tvReceiverMsgTime;
        TextView tvRecevierName;

        LinearLayout llSenderMsg;
        TextView tvSenderMsg;

        TextView tvSenderMsgTime;
        ImageView ivSenderMsgReceipt;

             /*-- FOR LOCATION --*/

        LinearLayout llReceiverLocation;
        TextView tvReceiverLocationAddress;
        ImageView ivReceiverLocation;
        TextView tvReceiverLocationTime;
        TextView tvreceiverlocationname;

        LinearLayout llSenderLocation;
        ImageView ivSenderLocation;
        TextView tvSenderLocationTime;
        ImageView ivSenderLocationReceipt;

        /*-- FOR IMAGE MESSAGE--*/

        LinearLayout llReceiverImg;
        ImageView ivReceiverImg;
        ProgressBar pbReceiverImg;
        ImageView ivReceiverImgResendBtn;
        ImageView ivReceiverImgDownloadBtn;
        TextView tvreceiverimgname;

        TextView tvReceiverImgTime;

        LinearLayout llSenderImg;
        ImageView ivSenderImg;
        ProgressBar pbSenderImg;
        TextView tvSenderImgTime;
        ImageView ivSenderImgReceipt;

        /*-- FOR VIDEO --*/

        LinearLayout llReceiverVideo;
        ImageView ivReceiverVideo;
        ProgressBar pbReceiverVideo;
        ImageView ivReceiverVideoBtnDownload;
        ImageView ivrReceiverVideoBtnPlay;
        TextView tvReceiverVideoTime;
        TextView tvreceivervideoname;

        LinearLayout llSenderVideo;
        ImageView ivSenderVideo;
        ProgressBar pbSenderVideo;
        ImageView ivSenderBtnVideoPlay;
        TextView tvSenderVideoTime;
        ImageView ivSenderVideoReceipt;


        /*For Block*/
        LinearLayout llblockuser;
        Button btnBlockUser;


        public ViewHolder(View view) {
            super(view);

            avatarImg = (ImageView) view.findViewById(R.id.chat_row_image);
            txtDisplayName = (TextView) view.findViewById(R.id.chat_row_name);
            txtLastMsgText = (TextView) view.findViewById(R.id.chat_row_last_msg);
            txtLastMsgTime = (TextView) view.findViewById(R.id.chat_row_time);
            txtUnreadMsgCounter = (TextView) view.findViewById(R.id.chat_row_msg_counter);


            //For Text Message
            llReceiverMsg = (LinearLayout) view.findViewById(R.id.rl_received_msg);
            tvReceiverMsg = (TextView) view.findViewById(R.id.tv_receiver_msg);
            tvReceiverMsgTime = (TextView) view.findViewById(R.id.tv_receive_msg_time);
            tvRecevierName = (TextView) view.findViewById(R.id.tv_receiver_msg_name);


            llSenderMsg = (LinearLayout) view.findViewById(R.id.rl_send_msg);
            ivSenderMsgReceipt = (ImageView) view.findViewById(R.id.iv_sender_msg_status);
            tvSenderMsg = (TextView) view.findViewById(R.id.tv_sender_msg);
            tvSenderMsgTime = (TextView) view.findViewById(R.id.tv_sender_msg_time);

            // For location Message
            llReceiverLocation = (LinearLayout) view.findViewById(R.id.ll_receiver_location);
            ivReceiverLocation = (ImageView) view.findViewById(R.id.iv_receiver_location_photo);
            tvReceiverLocationTime = (TextView) view.findViewById(R.id.tv_receiver_loaction_time);
            tvreceiverlocationname = (TextView) view.findViewById(R.id.tv_receiver_location_name);

            llSenderLocation = (LinearLayout) view.findViewById(R.id.ll_sender_location);
            ivSenderLocation = (ImageView) view.findViewById(R.id.iv_sender_location_photo);
            ivSenderLocationReceipt = (ImageView) view.findViewById(R.id.iv_sender_location_status);
            tvSenderLocationTime = (TextView) view.findViewById(R.id.tv_sender_location_time);


            // For Image Message
            llReceiverImg = (LinearLayout) view.findViewById(R.id.rl_receive_img);
            ivReceiverImg = (ImageView) view.findViewById(R.id.iv_recive_img);
            tvReceiverImgTime = (TextView) view.findViewById(R.id.tv_receiver_img_time);
            pbReceiverImg = (ProgressBar) view.findViewById(R.id.pb_reciver_img);
            ivReceiverImgDownloadBtn = (ImageView) view.findViewById(R.id.iv_receiver_img_download);
            ivReceiverImgResendBtn = (ImageView) view.findViewById(R.id.iv_receiver_img_resend);
            tvreceiverimgname = (TextView) view.findViewById(R.id.tv_receiver_img_name);


            llSenderImg = (LinearLayout) view.findViewById(R.id.ll_sender_img);
            ivSenderImg = (ImageView) view.findViewById(R.id.iv_sender_img);
            pbSenderImg = (ProgressBar) view.findViewById(R.id.iv_sender_progress);
            tvSenderImgTime = (TextView) view.findViewById(R.id.tv_sender_img_time);
            ivSenderImgReceipt = (ImageView) view.findViewById(R.id.iv_sender_img_status);

            // For Video Message
            llReceiverVideo = (LinearLayout) view.findViewById(R.id.ll_receiver_video);
            ivReceiverVideo = (ImageView) view.findViewById(R.id.iv_receiver_video);
            tvReceiverVideoTime = (TextView) view.findViewById(R.id.tv_receiver_video_time);
            ivReceiverVideoBtnDownload = (ImageView) view.findViewById(R.id.iv_receiver_btn_download);
            ivrReceiverVideoBtnPlay = (ImageView) view.findViewById(R.id.iv_receiver_video_play);
            pbReceiverVideo = (ProgressBar) view.findViewById(R.id.iv_receiver_video_progress);
            tvreceivervideoname = (TextView) view.findViewById(R.id.tv_receiver_video_name);


            llSenderVideo = (LinearLayout) view.findViewById(R.id.ll_sender_video);
            ivSenderVideo = (ImageView) view.findViewById(R.id.iv_sender_video);
            ivSenderVideoReceipt = (ImageView) view.findViewById(R.id.iv_sender_video_status);
            tvSenderVideoTime = (TextView) view.findViewById(R.id.tv_sender_video_time);
            ivSenderBtnVideoPlay = (ImageView) view.findViewById(R.id.iv_sender_btn_play);
            pbSenderVideo = (ProgressBar) view.findViewById(R.id.iv_sender_video_progress);


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
        public boolean onLongClick(View v) {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(v, getPosition());

            }
            return false;
        }
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    private String getTime(long timeInMillis) {
        Date date = new Date(timeInMillis);
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        String formatted = formatter.format(date);
        return formatted;
    }
}
