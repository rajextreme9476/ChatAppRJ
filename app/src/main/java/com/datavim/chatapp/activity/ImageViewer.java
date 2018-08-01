package com.datavim.chatapp.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.datavim.chatapp.R;
import com.datavim.chatapp.comman.Constants;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by admin on 10-08-2015.
 * Raviraj
 */
public class ImageViewer extends AppCompatActivity
{
    private Toolbar toolbar = null;
    PhotoViewAttacher mAttacher;
    ImageView ivImageView;
    private String imgPath="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageviewer);
        initToolBar();
        getData();
        ivImageView = (ImageView) findViewById(R.id.iv_image_view);

        Drawable bitmap = Drawable.createFromPath(imgPath);
        ivImageView.setImageDrawable(bitmap);
        mAttacher = new PhotoViewAttacher(ivImageView);
    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        imgPath = bundle.getString(Constants.IMAGEPATH);

    }

    private void shareImage() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        String imagePath = Environment.getExternalStorageDirectory()
                + "/"+imgPath;
        File imageFileToShare = new File(imgPath);
        Uri uri = Uri.fromFile(imageFileToShare);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image!"));
    }


    private void initToolBar() {
        Toolbar tbHeader = (Toolbar) findViewById(R.id.toolbar);
        tbHeader.setNavigationIcon(R.drawable.arrow_back);
        tbHeader.inflateMenu(R.menu.menu_imageviewer);
        tbHeader.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tbHeader.setTitleTextColor(getResources().getColor(R.color.text_filled_color_new));

        tbHeader.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_share) {
                    shareImage();
                }

                 return false;
            }
        });

    }




}