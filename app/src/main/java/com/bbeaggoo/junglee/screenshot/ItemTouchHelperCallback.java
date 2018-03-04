package com.bbeaggoo.junglee.screenshot;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

/**
 * Created by wlsdud.choi on 2016-04-04.
 */
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private Context context;
    public final String TAG = "[Simple][ItemTouchHelperCallback]";
    ItemTouchHelperListener listener;
    private int leftcolorCode;

    private Drawable background;
    private Drawable deleteIcon;
    private int xMarkMargin;
    private boolean initiated;

    //예를들어 "Archive" 라고 swipe시에 string을 set하기 위한 변수
    private String leftSwipeLable;

    public ItemTouchHelperCallback(Context context, ItemTouchHelperListener listener) {
        this.context = context;
        this.listener = listener;
    }

    // 각 view에서 어떤 user action이 가능한지 정의
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = -1;
        //if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
        Log.i("JYN", "getMovementFlags to GridLayoutManager");
        dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        //} else {
        //    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //}
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    // user가 item을 drag할 때, ItemTouchHelper가 onMove()를 호출
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        Log.i(TAG, "onMove. sourcePosition, targetPosition" + source.getAdapterPosition() + "," + target.getAdapterPosition());

        listener.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    // view가 swipe될 때, ItemTouchHelper는 뷰가 사라질때까지 animate한 후, onSwiped()를 호출
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.i(TAG, "onSwiped");
        listener.onItemRemove(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
    //출처: http://fullstatck.tistory.com/15 [풀스택 엔지니어]
}
