package com.bbeaggoo.junglee;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    Cursor cursor;
    ListView listview ;
    ListViewAdapter adapter; //cursorAdaptor

    final static String DB_NAME = "MySave.db";
    final static String DB_TABLE_NAME = "MyTable";
    final static int DB_VERSION = 1;

    SQLiteDatabase mDB = null;

    void open() {
        if ( mDB == null ) {
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
        setContentView(R.layout.activity_list_main);

        ArrayList<ListItem> listData = getListData();
        Log.i("ListActivity", "listData : " + listData);
        final ListView listView = (ListView) findViewById(R.id.custom_list);
        //open();
        if ( mDB != null ) {
            cursor = mDB.rawQuery("SELECT * FROM " + DB_TABLE_NAME, null);
        }
        listView.setAdapter(new CustomListAdapter(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, listData));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                ListItem newsData = (ListItem) listView.getItemAtPosition(position);
                Toast.makeText(ListActivity.this, "Selected :" + " " + newsData, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsData.getUrl()));
                startActivity(intent);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListItem newsData = (ListItem) listView.getItemAtPosition(position);
                Toast.makeText(ListActivity.this, "Long touched : " + newsData, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private ArrayList<ListItem> getListData() { return CilpboardListenerService.mySaveList; }
}
