package com.bbeaggoo.junglee.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

/**
 * Created by junyoung on 16. 10. 23..
 */
public class DbOpenHelper {

    private static final String DATABASE_NAME = "my.db";
    private static final int DATABASE_VERSION = 2;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        // 생성자
        public DatabaseHelper(Context context, String name,
                              CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // 최초 DB를 만들때 한번만 호출된다.
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DataBases.CreateDB._CREATE);

        }

        // 버전이 업데이트 되었을 경우 DB를 다시 만들어 준다.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DataBases.CreateDB._TABLENAME);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context) {
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDB.close();
    }

    /**
     * 데이터베이스에 사용자가 입력한 값을 insert하는 메소드
     *
     * @param name    이름
     * @param contact 전화번호
     * @param email   이메일
     * @return SQLiteDataBase에 입력한 값을 insert
     * (data.getTitle(), data.getUrl(), data.getDescription(), path, "", getCurrentTime());
     */
    public long insertColumn(String title, String url, String desc, String path,
                             String category, String folder, String date, String isCategory, String isFolder) {
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.TITLE, title);
        values.put(DataBases.CreateDB.URL, url);
        values.put(DataBases.CreateDB.DESC, desc);
        values.put(DataBases.CreateDB.IMAGE, path);
        values.put(DataBases.CreateDB.CATEGORY, category);
        values.put(DataBases.CreateDB.FOLDER, folder);
        values.put(DataBases.CreateDB.DATE, date);
        values.put(DataBases.CreateDB.ISCATEGORY, isCategory);
        values.put(DataBases.CreateDB.ISFOLDER, isFolder);
        return mDB.insert(DataBases.CreateDB._TABLENAME, null, values);
    }

    /**
     * 기존 데이터베이스에 사용자가 변경할 값을 입력하면 값이 변경됨(업데이트)
     *
     * @param id      데이터베이스 아이디
     * @param name    이름
     * @param contact 전화번호
     * @param email   이메일
     * @return SQLiteDataBase에 입력한 값을 update
     */
    public boolean updateColumn(long id, String title, String url, String desc, String path,
                                String category, String folder, String date, String isCategory, String isFolder) {
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.TITLE, title);
        values.put(DataBases.CreateDB.URL, url);
        values.put(DataBases.CreateDB.DESC, desc);
        values.put(DataBases.CreateDB.IMAGE, path);
        values.put(DataBases.CreateDB.CATEGORY, category);
        values.put(DataBases.CreateDB.FOLDER, folder);
        values.put(DataBases.CreateDB.DATE, date);
        values.put(DataBases.CreateDB.ISCATEGORY, isCategory);
        values.put(DataBases.CreateDB.ISFOLDER, isFolder);
        return mDB.update(DataBases.CreateDB._TABLENAME, values, "_id=" + id, null) > 0;
    }

    public boolean updateColumn2(long id, String path) {
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.IMAGE, path);
        return mDB.update(DataBases.CreateDB._TABLENAME, values, "_id=" + id, null) > 0;
    }

    public boolean updateCategoryColumn(String origCategoryName, String newCategoryName) {
        mDB.execSQL("UPDATE " + DataBases.CreateDB._TABLENAME + " SET category = '" + newCategoryName + "' WHERE category = '" + origCategoryName + "'");
        return true;
        //return mDB.update(DataBases.CreateDB._TABLENAME, values, "category='" + origCategoryName + "'", null) > 0;
    }

    public boolean setCategoryColumn(String url, String newCategoryName) {
        mDB.execSQL("UPDATE " + DataBases.CreateDB._TABLENAME + " SET category = '" + newCategoryName + "' WHERE url = '" + url + "'");
        return true;
        //return mDB.update(DataBases.CreateDB._TABLENAME, values, "category='" + origCategoryName + "'", null) > 0;
    }

    public boolean updateFolderColumn(String origFolderName, String newFolderName) {
        mDB.execSQL("UPDATE " + DataBases.CreateDB._TABLENAME + " SET folder = '" + newFolderName + "' WHERE folder = '" + origFolderName + "'");
        return true;
        //return mDB.update(DataBases.CreateDB._TABLENAME, values, "category='" + origCategoryName + "'", null) > 0;
    }

    public boolean updateFolderColumn(long id, String folderName) {
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.FOLDER, folderName);
        values.put(DataBases.CreateDB.ISFOLDER, "yes");
        return mDB.update(DataBases.CreateDB._TABLENAME, values, "folder='" + folderName + "'", null) > 0;
    }

    //입력한 id값을 가진 DB를 지우는 메소드
    public boolean deleteColumnById(long id) {
        int a = (int) id;
        //return mDB.delete(DataBases.CreateDB._TABLENAME, "_id=?" + id, new String[] { String.valueOf(id) }) > 0;
        return mDB.delete(DataBases.CreateDB._TABLENAME, "_id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public void deleteCategory(String category) {
        //folderName이 일치하고 "isFolder"가 true인 row를 delete한다.
        mDB.execSQL("DELETE FROM " + DataBases.CreateDB._TABLENAME + " WHERE category='" + category + "' AND iscategory='yes'");
        //return mDB.delete(DataBases.CreateDB._TABLENAME, "folder=?" && "isFolder==true", new String[]{String.valueOf(folder)}) > 0;
        Log.i("JYN", "[deleteCategoryr()] Delelte category : " + category + " from DB");

        //카테고리 삭제시 해당 카테고리의 하위 폴더들을 전부 삭제한다.
        mDB.execSQL("DELETE FROM " + DataBases.CreateDB._TABLENAME + " WHERE category='" + category + "' AND isfolder='yes'");
    }

    public void deleteFolder(String folder) {
        //folderName이 일치하고 "isFolder"가 true인 row를 delete한다.
        mDB.execSQL("DELETE FROM " + DataBases.CreateDB._TABLENAME + " WHERE folder='" + folder + "' AND isfolder='yes'");
        //mDB.delete(DataBases.CreateDB._TABLENAME, "folder=?", null);
        //return mDB.delete(DataBases.CreateDB._TABLENAME, "folder=?" && "isFolder==true", new String[]{String.valueOf(folder)}) > 0;
        Log.i("JYN", "[deleteFolder()] Delelte folder : " + folder + " from DB");
    }

    //입력한 전화번호 값을 가진 DB를 지우는 메소드
    /*
    public boolean deleteColumn(String number) {
        return mDB.delete(DataBases.CreateDB._TABLENAME, "contact="+number, null) > 0;
    }
    */

    public boolean deleteColumn(String title) {

        Log.i("DbOpenHelper", "deleteColumn : " + title);
        return mDB.delete(DataBases.CreateDB._TABLENAME, "title=?", new String[]{title}) > 0;

        /*
        String sql = "delete from " + DataBases.CreateDB._TABLENAME + " where title = "+title+";";
        mDB.execSQL(sql);
        return true;
        */
    }

    /*
    // Data 삭제
    public void removeData(int index){
        String sql = "delete from " + tableName + " where id = "+index+";";
        db.execSQL(sql);
    }
*/

    //커서 전체를 선택하는 메소드
    public Cursor getAllColumns() {
        return mDB.query(DataBases.CreateDB._TABLENAME, null, null, null, null, null, null);
    }

    //특정 column을 갖고오기
    //public

    //ID 컬럼 얻어오기
    public Cursor getColumn(long id) {
        Cursor c = mDB.query(DataBases.CreateDB._TABLENAME, null,
                "_id=" + id, null, null, null, null);
        //받아온 컬럼이 null이 아니고 0번째가 아닐경우 제일 처음으로 보냄
        if (c != null && c.getCount() != 0)
            c.moveToFirst();
        return c;
    }

    //이름으로 검색하기 (rawQuery)
    public Cursor getMatchName(String name) {
        Cursor c = mDB.rawQuery("Select * from my where name" + "'" + name + "'", null);
        return c;
    }

    public Cursor getColumsForCategory() {
        Cursor c = mDB.query(DataBases.CreateDB._TABLENAME,
                new String[] {DataBases.CreateDB._ID,
                        DataBases.CreateDB.ISCATEGORY,
                        DataBases.CreateDB.ISFOLDER,
                        DataBases.CreateDB.CATEGORY,
                        DataBases.CreateDB.FOLDER},
                null, null, null, null, null);
        //받아온 컬럼이 null이 아니고 0번째가 아닐경우 제일 처음으로 보냄
        if (c != null && c.getCount() != 0)
            c.moveToFirst();
        return c;
    }
}
