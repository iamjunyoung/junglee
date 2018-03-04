package com.bbeaggoo.junglee.screenshot;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbeaggoo.junglee.R;

/**
 * Created by junyoung on 2017. 8. 21..
 */

public class ScreenshotItemVH extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView textView;

    public ScreenshotItemVH(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.imageView1);
        textView = (TextView) itemView.findViewById(R.id.textView1);
    }
}