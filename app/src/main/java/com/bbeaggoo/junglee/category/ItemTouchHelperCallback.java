package com.bbeaggoo.junglee.category;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
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

    public ItemTouchHelperCallback(Context context, ItemTouchHelperListener listener){
        this.context = context;
        this.listener = listener;
    }

    // 각 view에서 어떤 user action이 가능한지 정의
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = -1;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            Log.i("JYN", "getMovementFlags to GridLayoutManager");
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    // user가 item을 drag할 때, ItemTouchHelper가 onMove()를 호출
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        Log.i(TAG, "onMove. sourcePosition, targetPosition"+source.getAdapterPosition()+","+target.getAdapterPosition());

        listener.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    // view가 swipe될 때, ItemTouchHelper는 뷰가 사라질때까지 animate한 후, onSwiped()를 호출
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.i(TAG, "onSwiped");
        listener.onItemRemove(viewHolder.getAdapterPosition());
    }

    /*
    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return super.getSwipeDirs(recyclerView, viewHolder);
    }
    */

    /*
    private void init() {
        background = new ColorDrawable();
        xMarkMargin = (int) context.getResources().getDimension(R.dimen.ic_clear_margin);
        deleteIcon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete);
        deleteIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        initiated = true;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;

        if (!initiated) {
            init();
        }

        int itemHeight = itemView.getBottom() - itemView.getTop();

        //
        //Setting Swipe Background
        ((ColorDrawable) background).setColor(getLeftcolorCode());
        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);

        int intrinsicWidth = deleteIcon.getIntrinsicWidth();
        int intrinsicHeight = deleteIcon.getIntrinsicWidth();

        int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
        int xMarkRight = itemView.getRight() - xMarkMargin;
        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int xMarkBottom = xMarkTop + intrinsicHeight;


        //Setting Swipe Icon
        deleteIcon.setBounds(xMarkLeft, xMarkTop + 16, xMarkRight, xMarkBottom);
        deleteIcon.draw(c);

        //Setting Swipe Text
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(48);
        paint.setTextAlign(Paint.Align.CENTER);
        c.drawText(getLeftSwipeLable(), xMarkLeft + 40, xMarkTop + 10, paint);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
    */

    public String getLeftSwipeLable() {
        return leftSwipeLable;
    }

    public void setLeftSwipeLable(String leftSwipeLable) {
        this.leftSwipeLable = leftSwipeLable;
    }

    public int getLeftcolorCode() {
        return leftcolorCode;
    }

    public void setLeftcolorCode(int leftcolorCode) {
        this.leftcolorCode = leftcolorCode;
    }
}
