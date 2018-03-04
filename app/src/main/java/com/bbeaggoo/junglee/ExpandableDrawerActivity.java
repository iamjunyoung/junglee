package com.bbeaggoo.junglee;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableDrawerActivity extends AppCompatActivity {
    Cursor cursor;
    CustomGridAdapter adapter; //cursorAdaptor


    final static String DB_NAME = "MySave.db";
    final static String DB_TABLE_NAME = "MyTable";
    final static int DB_VERSION = 1;
    static boolean longTouched = false;

    SQLiteDatabase mDB = null;

    ArrayList<ListItem> listData;

    public DrawerLayout dl;
    public ExpandableListView xl;
    //public ActionBarDrawerToggle adt;
    public List<String> alkitab;
    public HashMap<String, List<String>> data_alkitab;
    public CharSequence title;
    private int lastExpandPosition = -1;
    private MenuItem menuItem;
    private ExpandableDrawerAdapter adapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_for_drawer);

        listData = getListData();

        loadData();
        dl = (DrawerLayout)findViewById(R.id.drawer_layout);
        xl = (ExpandableListView)findViewById(R.id.left_drawer);
        Log.i("JYN", "xl : " + xl);
        adapt = new ExpandableDrawerAdapter(this, alkitab, data_alkitab, xl);
        xl.setAdapter(adapt);
        xl.setTextFilterEnabled(true);
        xl.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        xl.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandPosition != -1 && groupPosition != lastExpandPosition) {
                    xl.collapseGroup(lastExpandPosition);
                }
                lastExpandPosition = groupPosition;
            }
        });

        xl.setOnGroupCollapseListener(new OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {}
        });

        xl.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                int grup_pos = (int)adapt.getGroupId(groupPosition);
                int child_pos = (int)adapt.getChildId(groupPosition, childPosition);
                if(grup_pos == 1){
                    switch (child_pos) {
                        case 0:
                            Toast.makeText(getApplicationContext(), "Child 1 Group 1", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(getApplicationContext(), "Child 2 Group 1", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(getApplicationContext(), "Child 3 Group 1", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(getApplicationContext(), "Child 4 Group 1", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        final GridView gridView = (GridView) findViewById(R.id.gridView1);

        //open();
        if (mDB != null) {
            cursor = mDB.rawQuery("SELECT * FROM " + DB_TABLE_NAME, null);
        }
        adapter = new CustomGridAdapter(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, listData);
        gridView.setAdapter(adapter);
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

        AdapterView.OnItemLongClickListener listener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                getSupportActionBar().show();
                //openOptionsMenu();
                //getMenuInflater().inflate(R.menu.menu, menu);

                longTouched = true;
                ListItem newsData = (ListItem) gridView.getItemAtPosition(position);

                Toast.makeText(ExpandableDrawerActivity.this, "Long touched(" + position + ") : " + newsData, Toast.LENGTH_SHORT).show();
                listData.get(position).setChecked(true);
                int size = parent.getCount();
                Log.i("JYN", "parent size : " + size);

                adapter.notifyDataSetChanged();
                return true;
            }
        };

        gridView.setOnItemLongClickListener(listener);

        //Window w = getWindow(); // in Activity's onCreate() for instance
        //w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    }

    public void loadData(){

        alkitab = new ArrayList<String>();
        data_alkitab = new HashMap<String, List<String>>();

        alkitab.add("Group 1");
        alkitab.add("Group 2");
        alkitab.add("Group 3");
        alkitab.add("Group 4");

        List<String> kitab_perjanjian_lama = new ArrayList<String>();
        kitab_perjanjian_lama.add("Child 1 Of Group 1");
        kitab_perjanjian_lama.add("Child 2 Of Group 1");
        kitab_perjanjian_lama.add("Child 3 Of Group 1");
        kitab_perjanjian_lama.add("Child 4 Of Group 1");

        List<String> kitab_perjanjian_baru = new ArrayList<String>();
        kitab_perjanjian_baru.add("Child 1 Of Group 2");
        kitab_perjanjian_baru.add("Child 2 Of Group 2");
        kitab_perjanjian_baru.add("Child 3 Of Group 2");
        kitab_perjanjian_baru.add("Child 4 Of Group 2");

        List<String> kidung_jemaat = new ArrayList<String>();
        kidung_jemaat.add("Child 1 Of Group 3");
        kidung_jemaat.add("Child 2 Of Group 3");
        kidung_jemaat.add("Child 3 Of Group 3");
        kidung_jemaat.add("Child 4 Of Group 3");

        List<String> gita_bakti = new ArrayList<String>();
        gita_bakti.add("Child 1 Of Group 4");
        gita_bakti.add("Child 2 Of Group 4");
        gita_bakti.add("Child 3 Of Group 4");
        gita_bakti.add("Child 4 Of Group 4");

        data_alkitab.put(alkitab.get(0), kitab_perjanjian_lama);
        data_alkitab.put(alkitab.get(1), kitab_perjanjian_baru);
        data_alkitab.put(alkitab.get(2), kidung_jemaat);
        data_alkitab.put(alkitab.get(3), gita_bakti);
    }

    @SuppressWarnings("unused")
    private void displayViewExpandableListview(int position){
        android.app.Fragment frag = null;
        switch (position) {
            case 0:
                Toast.makeText(this, "test 1", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(this, "test 2", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "Test 3", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        if(frag != null){
            android.app.FragmentManager frag_mgr = getFragmentManager();
            frag_mgr.beginTransaction().replace(R.id.content_frame, frag).commit();
            xl.setItemChecked(position, true);
            dl.closeDrawer(xl);
        } else {
            Log.d("Error 1", "Error creating fragment");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        /*
        int id = item.getItemId();
        ArrayList<ListItem> selectedData = new ArrayList<>();
        for (int i = 0; i < listData.size(); i++) {
            if (listData.get(i).getChecked()) {
                selectedData.add(listData.get(i));
            }
        }
        if (id == R.id.plus) {
            Toast.makeText(this, "plus 이벤트", Toast.LENGTH_SHORT).show();
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
        */
        return true;
    }

    private ArrayList<ListItem> getListData() {
        return CilpboardListenerService.mySaveList;
    }

    private void startClickedItem(ListItem listItem) {
        Log.i("JYN", "startClickedItem() getUri : " + listItem.getUri());
        if (listItem.getUrl() != null && !"".equals(listItem.getUrl())) {
            //if ( !listItem.getUri().toString().contains("content://")) {
            //http case
            Toast.makeText(ExpandableDrawerActivity.this, "Selected url:" + " " + listItem, Toast.LENGTH_LONG).show();
            if (listItem != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(listItem.getUrl()));
                startActivity(intent);
            }
        } else {
            Toast.makeText(ExpandableDrawerActivity.this, "Selected image:" + " " + listItem.getUri(), Toast.LENGTH_LONG).show();
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
            longTouched = false;
        } else {
            Toast.makeText(this, "Back button pressed. longTouched false", Toast.LENGTH_SHORT).show();

            super.onBackPressed();
        }
    }

    /*
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        adt.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adt.onConfigurationChanged(newConfig);
    }
    */
}
