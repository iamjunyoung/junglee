package com.bbeaggoo.junglee.menudb;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/* ContentObserver�� �̿��� ContactProject
 * 4.ContactProject_RS_v4�� ContactMain.java�ȿ� onChange�޼ҵ���� showList()�� �����ϰ� ���̸� ����  */

/** �?�� ����Ÿ�� ��, �����ϴ� Ŭ���� */
public class CustomerDAO extends ContentProvider {
	private SQLiteDatabase mDb;
	private DbOpenHelper mHelper;
	private Context co;

	/** Context�� �޾� CustomerDAO ��ü�� ���Ѵ�. */
	public CustomerDAO(Context co) {
		this.co = co;
		mHelper = new DbOpenHelper(co, Sql.DatabaseName, Sql.DatabaseVersion);
	}

	public CustomerDAO() {
	};

	/** �����ͺ��̽��� ��, �����Ѵ�. */
	private class DbOpenHelper extends SQLiteOpenHelper {

		public DbOpenHelper(Context context, String name, int version) {
			super(context, name, null, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(Sql.createTable);
			db.execSQL(Sql.createDeleteTable);
			db.execSQL(Sql.createTriggerContactLog);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL(Sql.dropTable);
			db.execSQL(Sql.dropDeleteTable);
			db.execSQL(Sql.dropContactTrigger);

			onCreate(db);
		}

	}
	
	private static final int ContactMain = 1;
	private static final int ContactDel = 2;

	public static final String CONTENT_CONTACT = "edu.jaen.dir/"
			+ Contact.AUTHORITY;
	public static final String CONTENT_CONTACT_DEL = "edu.jaen.item/"
			+ Contact.AUTHORITY;

	private static UriMatcher matcher;
	static {
		matcher = new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(Contact.AUTHORITY, "contact", ContactMain);
		matcher.addURI(Contact.AUTHORITY, "deletecontact", ContactDel);
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		this.co = getContext();
		mHelper = new DbOpenHelper(co, Sql.DatabaseName, Sql.DatabaseVersion);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		mDb = mHelper.getReadableDatabase();
		Cursor c = mDb.query(uri.getPathSegments().get(0), projection,
				selection, selectionArgs, null, null, sortOrder);
		c.moveToFirst();
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		mDb = mHelper.getWritableDatabase();
		long i = mDb.insert(uri.getPathSegments().get(0), null, values);
		getContext().getContentResolver().notifyChange(uri, null);
		return uri.withAppendedPath(uri, i + "");
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		mDb = mHelper.getWritableDatabase();
		int count = mDb.delete(uri.getPathSegments().get(0), selection,
				selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		mDb = mHelper.getWritableDatabase();
		int count = mDb.update(uri.getPathSegments().get(0), values, selection,
				selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (matcher.match(uri)) {
		case ContactMain:
			Log.e("Provider", "Contact");
			return CONTENT_CONTACT;
		case ContactDel:
			Log.e("Provider", "ContactDel");
			return CONTENT_CONTACT_DEL;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
}
