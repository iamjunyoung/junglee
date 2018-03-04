package com.bbeaggoo.junglee.menudb;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleCursorAdapter;

import com.bbeaggoo.junglee.R;

public class DeleteCustomer extends ListActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		getListView().addHeaderView(View.inflate(this, R.layout.list_header, null));
		
		Cursor cursor = getContentResolver().query(Contact.deleteContact_Uri, null, null, null, null);
		startManagingCursor(cursor);
		String[] from = {Contact.name, Contact.tel, Contact.hotkey};
		int[] to = {R.id.name, R.id.phone, R.id.hotkey};
		SimpleCursorAdapter sca = new SimpleCursorAdapter(this, R.layout.list_row, cursor, from, to,1);
		setListAdapter(sca);
	}
}
