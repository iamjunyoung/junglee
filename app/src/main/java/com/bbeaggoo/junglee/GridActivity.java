package com.bbeaggoo.junglee;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.Toast;

import com.bbeaggoo.junglee.category.CategoryManager;
import com.bbeaggoo.junglee.db.DbOpenHelper;

import java.io.File;
import java.util.ArrayList;

public class GridActivity extends AppCompatActivity {
    //AppCompatActivity는 android.support.v7.app.ActionBar 를 쓸 수 있도록 해주는 확장된 액티비티 클래스
    Cursor cursor;
    CustomGridAdapter adapter; //cursorAdaptor

    GridView gridView;

    public DrawerLayout dl;
    public ExpandableListView xl;

    final static String DB_NAME = "MySave.db";
    final static String DB_TABLE_NAME = "MyTable";
    final static int DB_VERSION = 1;
    static boolean longTouched = false;
    SQLiteDatabase mDB = null;

    ArrayList<ListItem> listData;

    View view;
    GridView gridViewForMoreAction;
    CategoryManager categoryManager;

    int mLastFirstVisibleItem = 0;


    void open() {
        if (mDB == null) {
            mDB = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
            mDB.execSQL("CREATE TABLE IF NOT EXISTS " + DB_TABLE_NAME + "(" +
                    "_id            INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "number         TEXT, " +
                    "name           TEXT, " +
                    "departure      TEXT, " +
                    "grade          INTEGER );"); //execSQL()로 테이블을 생성한다.

            Log.i("JYN", "DB create");
        }
        //execSQL() 함수내에 SQL 언어를 넣어 실행할 수 있다.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        setContentView(R.layout.grid_layout);
        Log.i("JYN", "onCreate() GridActivity");

        listData = getListData();
        /*
        for (int i = 0 ; i < listData.size() ; i++) {
            Log.i("JYN", "item  " + i + " : " + listData.get(i) + "   " + listData.get(i).getUri());
            if (listData.get(i).getUri().toString().contains("jpg")) {
                Uri uri = getImageContentUri(this, new File(listData.get(i).getUri().toString()));
                Log.i("JYN", "new uri : " + uri);
                listData.get(i).setUri(uri);
            }
        }
        */

        gridView = (GridView) findViewById(R.id.gridView1);
        gridViewForMoreAction = gridView;

        //open();
        if (mDB != null) {
            cursor = mDB.rawQuery("SELECT * FROM " + DB_TABLE_NAME, null);
        }
        adapter = new CustomGridAdapter(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, listData);
        gridView.setAdapter(adapter);

        //final boolean mIsScrollingUp;

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            boolean mIsScrollingUp;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: // 스크롤이 정지되어 있는 상태야.
                        //정지되어 있는 상태일 때 해야 할 일들을 써줘.
                        Log.i("JYN", "SCROLL_STATE_IDLE");
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: // 스크롤이 터치되어 있을 때 상태고,
                        //스크롤이 터치되어 있는 상태일 때 해야 할 일들을 써줘.
                        Log.i("JYN", "SCROLL_STATE_TOUCH_SCROLL");
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING: // 이건 스크롤이 움직이고 있을때 상태야.
                        Log.i("JYN", "SCROLL_STATE_FLING");
                        if (mIsScrollingUp) {
                            getSupportActionBar().show();
                        } else {
                            getSupportActionBar().hide();
                        }
                        break;
                }

            }

            //Good
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getId() == gridView.getId()) {
                    final int currentFirstVisibleItem = gridView.getFirstVisiblePosition();
                    Log.i("JYN", "onScroll() mLastFirstVisibleItem : " + mLastFirstVisibleItem + "    currentFirstVisibleItem : " + currentFirstVisibleItem);
                    if (currentFirstVisibleItem > mLastFirstVisibleItem && getSupportActionBar().isShowing()) {
                        mIsScrollingUp = false;
                        //getSupportActionBar().hide();
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem && !getSupportActionBar().isShowing()) {
                        mIsScrollingUp = true;
                        //getSupportActionBar().show();
                    }
                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            }
        });
        /*
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                if (!longTouched) {
                    ListItem newsData = (ListItem) gridView.getItemAtPosition(position);
                    startClickedItem(newsData);
                } else {
                    if (!listData.get(position).getChecked()) {
                        listData.get(position).setChecked(true);
                    } else {
                        listData.get(position).setChecked(false);
                    }
                    adapter.notifyDataSetChanged();
                }

            }
        });
        */

        /*
        AdapterView.OnItemLongClickListener listener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //getSupportActionBar().show();
                //openOptionsMenu();
                //getMenuInflater().inflate(R.menu.menu, menu);

                longTouched = true;
                ListItem newsData = (ListItem) gridView.getItemAtPosition(position);

                Toast.makeText(GridActivity.this, "Long touched(" + position + ") : " + newsData, Toast.LENGTH_SHORT).show();
                listData.get(position).setChecked(true);
                int size = parent.getCount();
                Log.i("JYN", "parent size : " + size);

                adapter.notifyDataSetChanged();
                return true;
            }
        };

        gridView.setOnItemLongClickListener(listener);
        */

        /*
        getSupportActionBar().hide();
        Window w = getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        */

        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#330000ff")));
        //getSupportActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#550000ff")));

    }

    @Override
    protected void onResume() {
        super.onResume();

        dl = (DrawerLayout) findViewById((R.id.drawer_layout));
        xl = (ExpandableListView)findViewById(R.id.left_drawer);
        categoryManager = new CategoryManager(this, dl, xl);

    }

    private ArrayList<ListItem> getListData() {
        return CilpboardListenerService.mySaveList;
    }

    //CustomGridAdapter에 구현이 완료되면 얘는 쓰지 않을 예정
    private void startClickedItem(ListItem listItem) {
        Log.i("JYN", "startClickedItem() getUri : " + listItem.getUri());
        if (listItem.getUrl() != null && !"".equals(listItem.getUrl())) {
            //if ( !listItem.getUri().toString().contains("content://")) {
            //http case
            Toast.makeText(GridActivity.this, "Selected url:" + " " + listItem, Toast.LENGTH_LONG).show();
            if (listItem != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(listItem.getUrl()));
                startActivity(intent);
            }
        } else {
            Toast.makeText(GridActivity.this, "Selected image:" + " " + listItem.getUri(), Toast.LENGTH_LONG).show();
            if (listItem != null) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(listItem.getUri(), "image/*");
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (longTouched) {
            Toast.makeText(this, "Back button pressed. longTouched true", Toast.LENGTH_SHORT).show();
            //remove all touched state(another words set default state)
            for (int i = 0; i < listData.size(); i++) {
                Boolean checked = listData.get(i).getChecked();
                if (checked) {
                    Toast.makeText(this, "checked item i : " + i, Toast.LENGTH_SHORT).show();
                    Log.i("JYN", "checked to unchecked");
                    listData.get(i).setChecked(false);
                }
            }
            //모든 check되어있는 상태를 원복

            adapter.notifyDataSetChanged();
            //getSupportActionBar().hide();
            longTouched = false;
        } else {
            Toast.makeText(this, "Back button pressed. longTouched false", Toast.LENGTH_SHORT).show();

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.findItem(R.menu.menu2).setVisible(false);
        Log.i("JYN", "onPrepareOptionsMenu");
        if (longTouched) {
            menu.findItem(R.id.action_settings).setVisible(false);
            menu.findItem(R.id.plus).setVisible(false);
            menu.findItem(R.id.share).setVisible(false);
            menu.findItem(R.id.trash).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ArrayList<ListItem> selectedData = new ArrayList<>();
        for (int i = 0; i < listData.size(); i++) {
            if (listData.get(i).getChecked()) {
                selectedData.add(listData.get(i));
            }
        }
        if (id == R.id.plus) {
            Toast.makeText(this, "plus 이벤트", Toast.LENGTH_SHORT).show();
            categoryManager.showDialogForCategoryList(this);
            return true;
        } else if (id == R.id.share) {
            Toast.makeText(this, "share 이벤트", Toast.LENGTH_SHORT).show();
            shareremoveSelectedItem(selectedData);
            return true;
        } else if (id == R.id.trash) {
            Toast.makeText(this, "trash 이벤트", Toast.LENGTH_SHORT).show();
            removeSelectedItem(selectedData);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public void shareremoveSelectedItem(ArrayList<ListItem> selectedData) {
        ArrayList<Uri> uris = new ArrayList<Uri>();

        for (int i = 0; i < selectedData.size(); i++) {
            Log.i("JYN", selectedData.get(i) + "    : " + selectedData.get(i).getUri() + "    " + selectedData.get(i).getUrl());
            String url = selectedData.get(i).getUrl();
            //Uri uri = Uri.parse(selectedData.get(i).getUrl());
            if (url != null && !url.isEmpty()) {
                //url 이 있는 webPage case
                uris.add(Uri.parse(selectedData.get(i).getUrl()));
            } else {
                uris.add(selectedData.get(i).getUri());
            }
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        shareIntent.setType("*/*");
        startActivity(Intent.createChooser(shareIntent, "Share images to.."));
    }

    public void removeSelectedItem(ArrayList<ListItem> selectedData) {
        if (selectedData != null) {
            for (int i = 0; i < selectedData.size(); i++) {
                DbOpenHelper mDbOpenHelper = new DbOpenHelper(this);
                boolean delete = mDbOpenHelper.deleteColumnById(selectedData.get(i).getId());
                Log.i("JYN", "delete : " + selectedData.get(i) + "    " + delete);

            }
            CilpboardListenerService.mySaveList.clear();
            CilpboardListenerService cls = new CilpboardListenerService();
            cls.doWhileCursorToArray(this);
            //we need refresh activity
            adapter.notifyDataSetChanged();
            Log.i("JYN", "notifyDataSetChanged()");
        }
    }

    /*
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view.getId() == gridView.getId()) {
            final int currentFirstVisibleItem = gridView.getFirstVisiblePosition();
            Log.i("JYN", "onScroll() mLastFirstVisibleItem : " + mLastFirstVisibleItem + "    currentFirstVisibleItem : " + currentFirstVisibleItem);
            if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                // getSherlockActivity().getSupportActionBar().hide();
                getSupportActionBar().hide();
            } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                // getSherlockActivity().getSupportActionBar().show();
                getSupportActionBar().show();
            }

            mLastFirstVisibleItem = currentFirstVisibleItem;
        }
    }
    */
}
