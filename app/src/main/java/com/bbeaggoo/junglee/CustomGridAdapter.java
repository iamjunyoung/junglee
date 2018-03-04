package com.bbeaggoo.junglee;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bbeaggoo.junglee.category.CategoryManager;
import com.bbeaggoo.junglee.db.DataBases;
import com.bumptech.glide.Glide;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.ArrayList;

public class CustomGridAdapter extends CursorAdapter {
    private ArrayList<ListItem> listData;
    private LayoutInflater layoutInflater;
    private Context mContext;

    private boolean longTouched = false;

    private GridActivity ga;

    public Handler handler = new Handler();

    // About Tess-two
    Bitmap image; //사용되는 이미지
    private TessBaseAPI mTess; //Tess API reference
    String datapath = "" ; //언어데이터가 있는 경로
    //[출처] [안드로이드 스튜디오] 안드로이드 OCR 앱 만들기|작성자 코스모스


    public CustomGridAdapter(Context context, Cursor c, boolean autoRequery, ArrayList<ListItem> listData) {
        super(context, c, autoRequery);
        this.listData = listData;
    }

    public CustomGridAdapter(Context context, Cursor c, int flags, ArrayList<ListItem> listData) {
        super(context, c, flags);

        mContext = context;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        ga = (GridActivity)mContext;

        String lang = "eng+kor";
        /*
        mTess = new TessBaseAPI();
        mTess.init(datapath, lang);
        //getListData
        //ListItem
        for (ListItem item : getListData()) {
            if (item.getUrl() == null && item.getTitle() == null) { //이미지 파일이고 현재 image processing결과가 null 일 때에만 진

            }
        }
        */
    }

    /*
    Runnable worker = new Runnable() {
        public void run() {
            runImageProcessing();
        }
    };
    */

    class MyThread implements Runnable {
        Uri uri;
        public MyThread(Uri uri) {
            this.uri = uri;
        }

        @Override
        public void run() {

        }

    }

    //Process an Image
    public String processImage(Bitmap bitmap) {
        Log.i("Tess-tow", "OCR processing start");
        long start = System.currentTimeMillis();
        mTess.setImage(bitmap);
        long end = System.currentTimeMillis();
        Log.i("Tess-tow", "OCR processing end. elapsed time : " + (end - start));
        return mTess.getUTF8Text();
    }

    public void updateListData(ListItem listItem) {
        long id = listItem.getId();
        Log.i("JYN", "updateListData() id : " + id + "    data : " + listItem);
        for (int i = 0 ; i < listData.size() ; i++) {
            if (id == listData.get(i).getId()) {
                int indexOfItem = listData.indexOf(listData.get(i));
                Log.i("JYN", "index of this item : " + indexOfItem);
                listData.set(indexOfItem, listItem);
                Log.i("JYN", "After set category : " + listData.get(listData.indexOf(listData.get(i))).getCategory());

            }
        }

    }

    private ArrayList<ListItem> getListData() {
        return CilpboardListenerService.mySaveList;
    }

    @Override
    public int getCount() {
        if (listData != null) {
            return listData.size();
        } else {
            return -1;
        }
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolderGrid {
        RelativeLayout containerView;
        LinearLayout contentsView;//gridItemContent
        TextView headlineView;
        ImageView imageView;
        ImageView imageViewCheck;
        ImageView imageViewCircle;

        TextView categoryOrFolder;
        ImageView imageViewForMore;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ListItem newsItem = listData.get(position);
        final ViewHolderGrid holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_grid, null);
            holder = new ViewHolderGrid();

            holder.containerView = (RelativeLayout)convertView.findViewById(R.id.itemgrid);
            holder.contentsView = (LinearLayout) convertView.findViewById((R.id.gridItemContent));
            holder.headlineView = (TextView) convertView.findViewById(R.id.title);

            holder.imageView = (ImageView) convertView.findViewById(R.id.thumbImage);
            holder.imageViewCheck = (ImageView) convertView.findViewById(R.id.imageView2);
            holder.imageViewCircle = (ImageView) convertView.findViewById(R.id.imageView3);

            holder.categoryOrFolder = (TextView) convertView.findViewById(R.id.textForCategory);
            holder.imageViewForMore = (ImageView) convertView.findViewById(R.id.imageForMore);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //holder.imageView.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.MULTIPLY);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderGrid) convertView.getTag();
        }
        holder.headlineView.setText(newsItem.getTitle());
        holder.categoryOrFolder.setText(newsItem.getCategory());
        //holder.imageView.setImageDrawable(newsItem.getImg());

        //holder.contentView
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listData != null) {
                    if (!GridActivity.longTouched) {

                        startClickedItem(newsItem);
                    } else {
                        if (!newsItem.getChecked()) {
                            newsItem.setChecked(true);
                        } else {
                            newsItem.setChecked(false);
                        }
                        notifyDataSetChanged();
                    }
                } else {
                    Log.i("JYN", "listData is null. so ignore this case.");
                }
            }
        });

        holder.headlineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listData != null) {
                    if (!GridActivity.longTouched) {
                        Intent intentForDetail = new Intent();
                        intentForDetail.putExtra("result", newsItem.getTitle());
                        intentForDetail.setClass(mContext, com.bbeaggoo.junglee.screenshot.ScreenShotProcessingDetailActivity.class);
                        Toast.makeText(mContext, "Show detail text of image processing \ndata : " + newsItem.getTitle(), Toast.LENGTH_LONG).show();
                        mContext.startActivity(intentForDetail);
                    } else {
                        if (!newsItem.getChecked()) {
                            newsItem.setChecked(true);
                        } else {
                            newsItem.setChecked(false);
                        }
                        notifyDataSetChanged();
                    }
                } else {
                    Log.i("JYN", "listData is null. so ignore this case.");
                }
            }
        });

        final int pos = position;
        holder.containerView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                GridActivity.longTouched = true;

                ga.invalidateOptionsMenu();

                Toast.makeText(mContext, "Long touched " + newsItem, Toast.LENGTH_SHORT).show();
                listData.get(pos).setChecked(true);

                notifyDataSetChanged();

                return true;
            }
        });

        holder.imageViewForMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View temp = v;

                Rect r = new Rect();
                temp.getGlobalVisibleRect(r);
                int top = r.top;
                int left = r.left;
                int bottom = r.bottom;
                int right = r.right;
                Log.v("JYN", "top : " + top + "\tleft : " +  left + "\tbottom : " + bottom + "\tright : " + right);

                //View parent = (View)temp.getParent();
                addViewForMorePopup(holder.imageViewForMore, newsItem, right, bottom);
                //addViewForMore(right, bottom);
                //setToCategory();
            }
        });

        Glide.with(mContext)
                .load(newsItem.getUri())
                .into(holder.imageView);
        Log.i("CustomGridAdapter", "Title : " + newsItem.getTitle() + "  uri : " + newsItem.getUri());

        if (newsItem.getChecked()) {
            Log.i("", "newsItem : " + newsItem + " is checked true");
            holder.imageView.setColorFilter(Color.parseColor("#828080"), PorterDuff.Mode.MULTIPLY);

            holder.imageViewCheck.setVisibility(View.VISIBLE);

            holder.imageViewCircle.setVisibility(View.INVISIBLE);
        } else if ( GridActivity.longTouched ) {
            holder.imageViewCircle.setVisibility(View.VISIBLE);

            holder.imageView.clearColorFilter();
            holder.imageViewCheck.setVisibility(View.INVISIBLE);
        } else {
            holder.imageViewCircle.setVisibility(View.INVISIBLE);
            holder.imageView.clearColorFilter();
            holder.imageViewCheck.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }


    public void addViewForMorePopup(ImageView imgView, ListItem listItem, int x, int y) {
        // 팝업
        Resources lang_res = mContext.getResources();
        DisplayMetrics lang_dm = lang_res.getDisplayMetrics();
        int lang_width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, lang_dm);
        int lang_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, lang_dm);

        View pop_View = View.inflate(mContext, R.layout.layout_for_more_popup, null);
        //PopupWindow popupWindow = new PopupWindow(pop_View, lang_width, lang_height, true);
        PopupWindow popupWindow = new PopupWindow(pop_View, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);

        LinearLayout view_for_cate = (LinearLayout)pop_View.findViewById(R.id.layout_for_category);
        LinearLayout view_for_share = (LinearLayout)pop_View.findViewById(R.id.layout_for_shareto);
        LinearLayout view_for_memo = (LinearLayout)pop_View.findViewById(R.id.layout_for_takememo);
        //View temp = layoutInflater.inflate(R.layout.item_grid, null);
        final ListItem lt = listItem;
        final CustomGridAdapter customGridAdapter = this;
        view_for_cate.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                Log.i("JYN", "category");
                Log.i("JYN", "addView() :" + lt + "    id : " + v.getId());
                /*
                DbOpenHelper mDbOpenHelper = new DbOpenHelper(mContext);
                Cursor cursor = mDbOpenHelper.getColumsForCategory();
                getColumsForCategoryFromDBForDebugging(cursor);
                */
                //show dialog
                CategoryManager cm = new CategoryManager(mContext);
                cm.showDialogForCategoryList(mContext, lt, customGridAdapter);

            }
        });
        view_for_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("JYN", "share");
                Log.i("JYN", "addView() :" + lt + "    id : " + v.getId());
            }
        });
        view_for_memo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("JYN", "memo");
                Log.i("JYN", "addView() :" + lt + "    id : " + v.getId());
            }
        });

        //popupWindow.showAtLocation(pop_View, Gravity.CENTER, x, y);
        popupWindow.showAsDropDown(imgView, 50, 50);
    }

    private void getColumsForCategoryFromDBForDebugging(Cursor cursor) {
        Cursor mCursor = cursor;
        while (mCursor.moveToNext()) {
            Log.i("JYN", "isCategory : " + mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.ISCATEGORY)) +
                    "    category : " + mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.CATEGORY)) +
                "    folder : " + mCursor.getString(mCursor.getColumnIndex(DataBases.CreateDB.FOLDER)));
        }

    }

    private void addViewForMore(int x, int y) {

        WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        Display dis = wm.getDefaultDisplay();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.layout_for_more_popup, null);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        params.x = x;
        params.y = y;

        /*
        final View close = view.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
                windowManager.removeView(view);
            }
        });
        */
        wm.addView(view, params);
    }
    private void startClickedItem(ListItem listItem) {
        Log.i("JYN", "startClickedItem() getUri : " + listItem.getUri());
        if (listItem.getUrl() != null && !"".equals(listItem.getUrl())) {
            //if ( !listItem.getUri().toString().contains("content://")) {
            //http case
            Toast.makeText(mContext, "Selected url:" + " " + listItem, Toast.LENGTH_LONG).show();
            if (listItem != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(listItem.getUrl()));
                mContext.startActivity(intent);
            }
        } else {
            Toast.makeText(mContext, "Selected image:" + " " + listItem.getUri(), Toast.LENGTH_LONG).show();
            if (listItem != null) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(listItem.getUri(), "image/*");
                mContext.startActivity(intent);
            }
        }
    }

    public void setToCategory() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate( R.layout.layout_for_more_popup, null );

        AlertDialog.Builder listViewDialog = new AlertDialog.Builder(mContext);
        // 리스트뷰 설정된 레이아웃
        listViewDialog.setView(view);
        listViewDialog.show();
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
