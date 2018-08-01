package com.datavim.chatapp.activity;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.FontRequestEmojiCompatConfig;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.support.v4.provider.FontRequest;
import android.util.Log;

import com.datavim.chatapp.R;
import com.datavim.chatapp.utils.FontsOverride;


/**
 * Created by Raviraj Desai on 28/04/17.
 */
public class ChatApplication extends Application {



    private static final String TAG = "EmojiCompatApplication";

    /** Change this to {@code false} when you want to use the downloadable Emoji font. */
    private static final boolean USE_BUNDLED_EMOJI = true;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        FontsOverride.setDefaultFont(this, "DEFAULT", "Montserrat-Regular.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "Montserrat-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "Montserrat-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "Montserrat-Regular.ttf");

        final EmojiCompat.Config config;
        if (USE_BUNDLED_EMOJI) {
            // Use the bundled font for EmojiCompat
            config = new BundledEmojiCompatConfig(this);
        } else {
            // Use a downloadable font for EmojiCompat
            final FontRequest fontRequest = new FontRequest(
                    "com.google.android.gms.fonts",
                    "com.google.android.gms",
                    "Noto Color Emoji Compat",
                    R.array.com_google_android_gms_fonts_certs);
            config = new FontRequestEmojiCompatConfig(getApplicationContext(), fontRequest)
                    .setReplaceAll(true)
                    .registerInitCallback(new EmojiCompat.InitCallback() {
                        @Override
                        public void onInitialized() {
                            Log.i(TAG, "EmojiCompat initialized");
                        }

                        @Override
                        public void onFailed(@Nullable Throwable throwable) {
                            Log.e(TAG, "EmojiCompat initialization failed", throwable);
                        }
                    });
        }
        EmojiCompat.init(config);


    }
}
