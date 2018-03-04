package com.bbeaggoo.junglee.screenshot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bbeaggoo.junglee.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by junyoung on 2017. 12. 30..
 */

public class ScreenshotAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private Context mContext;
    private ArrayList<ScreenshotItem> listViewItemList = new ArrayList<ScreenshotItem>() ;
    //출처: http://recipes4dev.tistory.com/43 [개발자를 위한 레시피]\

    //CustomGridAdapter 참고


    public ScreenshotAdaptor(Context context, ArrayList<ScreenshotItem> listViewItemList) {
        this.mContext = context;
        this.listViewItemList = listViewItemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_srceenshot, parent, false);

        RecyclerView.ViewHolder viewHolder;
        viewHolder = new ScreenshotItemVH(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ScreenshotItemVH itemVH = (ScreenshotItemVH)holder;
        Log.i("JYN", "onBindBiewHolder " + listViewItemList.get(position).getImg());
        Glide.with(mContext)
                .load(listViewItemList.get(position).getImg())
                .into(itemVH.imageView);
        itemVH.textView.setText(listViewItemList.get(position).getExtractedText());

        itemVH.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("JYN", "this is spartacus");
                //Intent intent = new Intent(mContext, );

                //mContext.startActivity();
            }
        });



    }

    @Override
    public int getItemCount() {
        return listViewItemList.size()  ;
    }



}
