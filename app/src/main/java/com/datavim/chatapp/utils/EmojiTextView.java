package com.datavim.chatapp.utils;

/**
 * Created by BOSS on 30/07/18.
 */


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.text.emoji.widget.EmojiTextViewHelper;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputFilter;
import android.util.AttributeSet;

public class EmojiTextView extends AppCompatTextView {

    private EmojiTextViewHelper mEmojiTextViewHelper;

    public EmojiTextView(Context context) {
        this(context, null);
    }

    public EmojiTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmojiTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getEmojiTextViewHelper().updateTransformationMethod();
    }

    @Override
    public void setFilters(InputFilter[] filters) {
        super.setFilters(getEmojiTextViewHelper().getFilters(filters));
    }

    @Override
    public void setAllCaps(boolean allCaps) {
        super.setAllCaps(allCaps);
        getEmojiTextViewHelper().setAllCaps(allCaps);
    }

    /**
     * Returns the {@link EmojiTextViewHelper} for this TextView.
     *
     * <p>This method can be called from super constructors through {@link
     * #setFilters(InputFilter[])} or {@link #setAllCaps(boolean)}.</p>
     */
    private EmojiTextViewHelper getEmojiTextViewHelper() {
        if (mEmojiTextViewHelper == null) {
            mEmojiTextViewHelper = new EmojiTextViewHelper(this);
        }
        return mEmojiTextViewHelper;
    }

}
