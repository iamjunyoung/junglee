package com.bbeaggoo.junglee;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends CursorAdapter {
    private ArrayList<ListItem> listData;
    private LayoutInflater layoutInflater;

    public CustomListAdapter(Context context, Cursor c, boolean autoRequery, ArrayList<ListItem> listData) {
        super(context, c, autoRequery);
        this.listData = listData;
    }

    public CustomListAdapter(Context context, Cursor c, int flags, ArrayList<ListItem> listData) {
        super(context, c, flags);
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
            holder = new ViewHolder();
            holder.headlineView = (TextView) convertView.findViewById(R.id.title);
            holder.reporterNameView = (TextView) convertView.findViewById(R.id.reporter);
            holder.reportedDateView = (TextView) convertView.findViewById(R.id.date);
            holder.imageView = (ImageView) convertView.findViewById(R.id.thumbImage);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListItem newsItem = listData.get(position);
        holder.imageView.setImageDrawable(newsItem.getImg());

        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        holder.headlineView.setText(newsItem.getTitle());
        holder.reportedDateView.setText(newsItem.getDate());

        convertView.setBackgroundResource(R.drawable.image_border);

        if (holder.imageView != null) {
            //new ImageDownloaderTask(holder.imageView).execute(newsItem.getUrl());
        }

        return convertView;
    }

    static class ViewHolder {
        TextView headlineView;
        TextView reporterNameView;
        TextView reportedDateView;
        ImageView imageView;
    }


    //for CursorAdaptor
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
