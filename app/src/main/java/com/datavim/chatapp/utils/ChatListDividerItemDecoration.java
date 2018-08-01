package com.datavim.chatapp.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.datavim.chatapp.R;


/**
 * Created by apple on 05/04/16.
 */
public class ChatListDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public ChatListDividerItemDecoration(Context context) {

                   // mDivider = ResourcesCompat.getDrawable(context.getResources(), R.drawable.navigation_list_divider, null);
       // mDivider = ContextCompat.getDrawable(context, R.drawable.navigation_list_divider);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDivider = context.getResources().getDrawable(R.drawable.navigation_list_divider, context.getTheme());
        } else {
            mDivider = ContextCompat.getDrawable(context, R.drawable.navigation_list_divider);
        }

    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            //   if (parent.getChildAt(i).getVisibility() == View.GONE)
            {   mDivider.setBounds(180, top, right, bottom);
                mDivider.draw(c);}

        }
    }
}
