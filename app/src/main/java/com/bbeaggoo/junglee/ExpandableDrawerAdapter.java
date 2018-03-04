package com.bbeaggoo.junglee;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by junyoung on 16. 12. 24..
 */
public class ExpandableDrawerAdapter extends BaseExpandableListAdapter {
    public final Context _context;
    public List<String> _alkitab, tempchild;
    public HashMap<String, List<String>> _data_alkitab;
    public ExpandableListView xl;
    private ViewHolder viewHolder = null;

    int lastExpandPosition = -1;

    static class ViewHolder {
        TextView groupNameText;
        ImageView groupIconImage;
        ImageView indicatorImage;
        ImageButton indicatorButton;
        //holder.imageView.setColorFilter(Color.parseColor("#828080"), PorterDuff.Mode.MULTIPLY);
    }

    public ExpandableDrawerAdapter(Context context, List<String> alkitab, HashMap<String, List<String>> data_alkitab, ExpandableListView xl){
        this._context = context;
        this._alkitab = alkitab;
        this._data_alkitab = data_alkitab;
        this.xl = xl;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._data_alkitab.get(this._alkitab.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String)getChild(groupPosition, childPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_level2, null);
        }
        TextView a = (TextView)convertView.findViewById(R.id.lblListItem);
        a.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._data_alkitab.get(this._alkitab.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._alkitab.get(groupPosition);
    }
    // _alkitab 는 List<String> _alkitab

    @Override
    public int getGroupCount() {
        return this._alkitab.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String)getGroup(groupPosition);
        Log.i("JYN", "HeaderTitle : " + headerTitle + "    " + _data_alkitab.get(getGroup(groupPosition)).size());
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_row_level1, parent, false);
            viewHolder.groupIconImage = (ImageView)convertView.findViewById(R.id.image_group);
            viewHolder.groupNameText = (TextView)convertView.findViewById(R.id.text_group);
            viewHolder.indicatorImage = (ImageView)convertView.findViewById(R.id.indicator);
            //viewHolder.indicatorButton = (ImageButton)convertView.findViewById(R.id.indicator_arrow);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        /*
        viewHolder.indicatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int holderPosition = ((ParentItemVH)v.getTag()).getAdapterPosition();
                if(((ParentItem)visibleItems.get(holderPosition)).visibilityOfChildItems){
                    collapseChildItems(holderPosition);
                }else{
                    expandChildItems(holderPosition);
                }
            }
        });
        */

        if (_data_alkitab != null && _data_alkitab.get(getGroup(groupPosition)).size() != 0) {
            if (isExpanded) {
                viewHolder.indicatorImage.setBackgroundColor(Color.GREEN);
            } else {
                //
                viewHolder.indicatorImage.setImageResource(R.drawable.arrow_below);
                viewHolder.indicatorImage.setBackgroundColor(Color.WHITE);
            }
        }

        if (getChildrenCount(groupPosition) == 4) {
            viewHolder.indicatorImage.setBackgroundColor(Color.RED);
        }

        if (groupPosition != _data_alkitab.size()-1 ) {
            viewHolder.groupIconImage.setImageResource(R.drawable.check_1_icon);
        } else {
            viewHolder.groupIconImage.setImageResource(R.drawable.icon_setting);
        }
        viewHolder.groupNameText.setText(headerTitle);

        final ViewGroup parentView = parent;
        final int groupPos = groupPosition;
        viewHolder.indicatorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    ((ExpandableListView) parentView).collapseGroup(groupPos);
                }
                else {
                    ((ExpandableListView) parentView).expandGroup(groupPos, true);
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
