package com.bbeaggoo.junglee.category;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by wlsdud.choi on 2016-04-06.
 */
public class GridItemLayoutManger extends GridLayoutManager {
    public final String TAG = "[Simple][ItemLayoutManager]";
    public GridItemLayoutManger(Context context, int spanCount) {
        super(context, spanCount);
    }


    public GridItemLayoutManger(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        Log.i(TAG, "onLayoutChildren");
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        super.onMeasure(recycler, state, widthSpec, heightSpec);
        Log.i(TAG, "onMeasure");
    }


    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
        Log.i(TAG, "scrollToPosition. position : "+position);
    }

    @Override
    public void scrollToPositionWithOffset(int position, int offset) {
        super.scrollToPositionWithOffset(position, offset);
        Log.i(TAG, "scrollToPositionWithOffset. position : "+position+", offset : "+offset);

    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.i(TAG, "scrollVerticallyBy");
        return super.scrollVerticallyBy(dy, recycler, state);
    }
}
