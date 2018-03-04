package com.bbeaggoo.junglee;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by junyoung on 17. 2. 11..
 */
public class CategoryListAdapter implements ListAdapter {
    private Context mContext;
    public ArrayList<String> categoryListExpanded;
    private ViewHolder viewHolder = null;

    static class ViewHolder {
        TextView categoryNameText;
        ImageView categoryIconImage;
        ImageView indicatorImage;
    }

    public CategoryListAdapter(Context context, ArrayList<String> categoryListExpanded ) {
        mContext = context;
        this.categoryListExpanded = categoryListExpanded;
        for (int i = 0 ; i < categoryListExpanded.size() ; i++) {
            Log.i("JYN", "" + categoryListExpanded.get(i));
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return categoryListExpanded.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryListExpanded.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_row_level1, parent, false);
            viewHolder.categoryIconImage = (ImageView)convertView.findViewById(R.id.image_group);
            viewHolder.categoryNameText = (TextView)convertView.findViewById(R.id.text_group);
            viewHolder.indicatorImage = (ImageView)convertView.findViewById(R.id.indicator);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        String item = categoryListExpanded.get(position);
        viewHolder.categoryNameText.setText(item);

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
