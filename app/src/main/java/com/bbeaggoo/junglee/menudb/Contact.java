package com.bbeaggoo.junglee.menudb;

import android.net.Uri;

public class Contact {
	public static final Uri contact_uri = Uri.parse("content://com.bbeaggo.junglee.menudb/contact");
	public static final Uri deleteContact_Uri = Uri.parse("content://com.bbeaggo.junglee.menudb/deletecontact");
	public static final String AUTHORITY= "com.bbeaggo.junglee.menudb";
	public static final String name = "name";
	public static final String tel = "phone";
	public static final String hotkey = "hotkey";
}
