package com.bbeaggoo.junglee.menudb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bbeaggoo.junglee.R;

public class ContactMain extends Activity implements View.OnClickListener {
	private EditText etName;
	private EditText etPhone;
	private EditText etHotkey;
	private ListView list;
	private LayoutInflater Inflater;
	private CustomerAdapter cAdapter;
	private Cursor listCursor;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_db_main);
		Inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		findViewById(R.id.btnInsert).setOnClickListener(this);
		findViewById(R.id.btnUpdate).setOnClickListener(this);
		findViewById(R.id.btnDelete).setOnClickListener(this);
		findViewById(R.id.btnSearch).setOnClickListener(this);
		findViewById(R.id.btnClear).setOnClickListener(this);
		findViewById(R.id.btnExit).setOnClickListener(this);

		etName = (EditText) findViewById(R.id.etName);
		etPhone = (EditText) findViewById(R.id.etPhone);
		etHotkey = (EditText) findViewById(R.id.etHotkey);
		list = (ListView) findViewById(R.id.list);
		View view = Inflater.inflate(R.layout.list_header, null);
		list.addHeaderView(view);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				if(position == 0) return ;
				listCursor.moveToPosition(position-1);
				etName.setText(listCursor.getString(listCursor.getColumnIndex("name")));
				etPhone.setText(listCursor.getString(listCursor.getColumnIndex("phone")));
				etHotkey.setText(listCursor.getString(listCursor.getColumnIndex("hotkey")));
			}
		});
		
		cAdapter = new CustomerAdapter(this);
		list.setAdapter(cAdapter);
		showList();
		
		getContentResolver().registerContentObserver(Contact.contact_uri, true, observer);
	}
	
	private ContentObserver observer = new ContentObserver(new Handler()) {

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			showList();
		}
	};
	
	class CustomerAdapter extends BaseAdapter{
		private Context context;
		public CustomerAdapter(Context context){
			this.context = context;
			listCursor = getContentResolver().query(Contact.contact_uri, null, null, null, null);
			startManagingCursor(listCursor);
			
		}
		
		public int getCount() {
			// TODO Auto-generated method stub
			return listCursor.getCount();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView == null){
				convertView = Inflater.inflate(R.layout.list_row, null);
			}
			
			TextView name = (TextView) convertView.findViewById(R.id.name);
			TextView phone = (TextView) convertView.findViewById(R.id.phone);
			TextView hotkey = (TextView) convertView.findViewById(R.id.hotkey);
			
			listCursor.moveToPosition(position);
			name.setText(listCursor.getString(listCursor.getColumnIndex("name")));
			phone.setText(listCursor.getString(listCursor.getColumnIndex("phone")));
			hotkey.setText(listCursor.getString(listCursor.getColumnIndex("hotkey")));
			
			return convertView;
		}
	}
	public void showList() {
		listCursor = getContentResolver().query(Contact.contact_uri, null, null, null, null);
		cAdapter.notifyDataSetChanged();
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnInsert:
			insert();
			break;
		case R.id.btnUpdate:
			update();
			break;
		case R.id.btnDelete:
			delete();
			break;
		case R.id.btnSearch:
			search();
			break;
		case R.id.btnClear:
			clear();
			break;
		default:
			System.exit(0);
		}
	}

	/** TextView�� ������ �����. */
	public void clear() {
		etName.setText("");
		etPhone.setText("");
		etHotkey.setText("");
		etName.setEnabled(true);
		etHotkey.setEnabled(true);
	}

	/** Dialog�� ���÷����Ѵ�. */
	public void showDialog(String msg) {
		new AlertDialog.Builder(this).setMessage(msg)
				.setPositiveButton("Ȯ��", null).show();
	}

	/** Insert Button�� ������ �� ȣ��ȴ�. */
	public void insert() {
		String name = etName.getText().toString().trim();
		String phone = etPhone.getText().toString().trim();
		String hotkey = etHotkey.getText().toString().trim();
		if (name.equals("") || phone.equals("") || hotkey.equals("")) {
			showDialog("����ִ� �׸��� �ֽ��ϴ�");
			return;
		}
		Cursor c = getContentResolver().query(Contact.contact_uri, null, "hotKey = ?", new String[]{hotkey}, null);
		if (c.getCount() > 0) {
			showDialog("�̹� �ִ� ����Ű�Դϴ�.");
			return;
		}
		ContentValues cv = new ContentValues();
		cv.put("name", etName.getText().toString());
		cv.put("phone", etPhone.getText().toString());
		cv.put("hotkey", etHotkey.getText().toString());
		
		getContentResolver().insert(Contact.contact_uri, cv);
		
		c.close();
		showList();
		clear();
	}

	/** Delete Button�� ������ �� ȣ��ȴ�. */
	public void delete() {
		Cursor c = null;
		String name = etName.getText().toString().trim();
		String hotkey = etHotkey.getText().toString().trim();
		if (name.equals("") && hotkey.equals("")) {
			showDialog("������ �߸� �Ǿ���ϴ�");
			return;
		}
		if (!name.equals("")) {
			c = getContentResolver().query(Contact.contact_uri, null, "name = ?", new String[]{name}, null);
			if (c.getCount() == 0) {
				showDialog("�������� �ʴ� �̸��Դϴ�.");
				return;
			} else {
				getContentResolver().delete(Contact.contact_uri, "name = ?", new String[]{name});
			}
		} else {
			c = getContentResolver().query(Contact.contact_uri, null, "hotKey = ?", new String[]{hotkey}, null);
			if (c.getCount() == 0) {
				showDialog("���� ���� �ʴ� ����Ű�Դϴ�.");
				return;
			} else {
				getContentResolver().delete(Contact.contact_uri, "hotkey = ?", new String[]{hotkey});
			}
		}
		c.close();
		showList();
		clear();
	}

	/** Update Button�� ������ �� ȣ��ȴ�. */
	public void update() {
		Cursor c = null;
		String name = etName.getText().toString().trim();
		String phone = etPhone.getText().toString().trim();
		String hotkey = etHotkey.getText().toString().trim();
		if (phone.equals("") || name.equals("") || hotkey.equals("")) {
			showDialog("����ִ� �׸��� �ֽ��ϴ�");
			return;
		}
		c = getContentResolver().query(Contact.contact_uri, null, "name = ?", new String[]{name}, null);
		if (c.getCount() == 0) {
			showDialog("�����ϴ� �̸��� ����ϴ�.");
			return;
		}
		ContentValues cv = new ContentValues();
		cv.put("name", etName.getText().toString());
		cv.put("phone", etPhone.getText().toString());
		cv.put("hotkey", etHotkey.getText().toString());
		getContentResolver().update(Contact.contact_uri, cv, "name = ?", new String[]{name});
		c.close();
		showList();
		clear();
	}

	/** Search Button�� ������ �� ActionPerformed Method�� ���� ȣ��ȴ�. */
	public void search() {
		//Customer c = null;
		String hotkey = etHotkey.getText().toString().trim();
		String name = etName.getText().toString().trim();
		Cursor cursor = null;
		if (!(name.equals(""))) {
			cursor = getContentResolver().query(Contact.contact_uri, null, "name = ?", new String[]{name}, null);
		} else if (!(hotkey.equals(""))) {
			cursor = getContentResolver().query(Contact.contact_uri, null, "hotKey = ?", new String[]{hotkey}, null);
		} else {
			showDialog("������ �߸�Ǿ���ϴ�");
			return;
		}
		if (cursor.getCount()==0) {
			showDialog("ã�� �� ����ϴ�");
			return;
		}else{
			cursor.moveToFirst();
			etName.setText(cursor.getString(1));
			etPhone.setText(cursor.getString(2));
			etHotkey.setText(cursor.getString(3));// String<=int
			etName.setEnabled(false);
		}
		cursor.close();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add("���� �ּҷ�");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, DeleteCustomer.class));
		return super.onOptionsItemSelected(item);
	}
}