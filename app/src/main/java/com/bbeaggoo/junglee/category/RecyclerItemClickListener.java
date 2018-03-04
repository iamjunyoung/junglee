package com.bbeaggoo.junglee.category;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Created by junyoung on 17. 3. 12..
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener  {
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
