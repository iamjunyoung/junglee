package com.bbeaggoo.junglee.menudb;

public final class Sql {
	public static final String DatabaseName = "contact.db";
	public static final int DatabaseVersion = 3;

	public static final String createTable =
			"create table if not exists contact(" +
					"_id integer primary key autoincrement, " +
					"name text not null, " +
					"phone text not null, " +
					"hotkey integer not null);";
	public static final String createDeleteTable =
			"create table if not exists deletecontact(" +
					"_id integer primary key autoincrement, " +
					"name text not null, " +
					"phone text not null, " +
					"hotkey integer not null);";

	public static final String dropTable = "drop table if exists contact;";
	public static final String dropDeleteTable = "drop table if exists deletecontact;";
	
	public static final String createTriggerContactLog = 
			" CREATE TRIGGER tr_Contact_delete " +
			" BEFORE DELETE ON contact " +
			" BEGIN " +
			" INSERT INTO deletecontact(_id, name, phone, hotkey) " +
			" VALUES(OLD._id, OLD.name, OLD.phone, OLD.hotkey); " +
			" END; ";
	public static final String dropContactTrigger = "drop Trigger if exists tr_Contact_delete;";
	
	public static final String allSelect = "select name, phone, hotkey from contact;";
	public static final String nameSelect = "select name, phone, hotkey from contact where name = ?;";
	public static final String hotKeySelect = "select name, phone, hotkey from contact where hotkey = ?;";
	public static final String nameDelete = "delete from contact where name = ?;";
	public static final String hotKeyDelete = "delete from contact where hotkey = ?;";
	public static final String update = "update contact set phone = ?, hotkey = ? where name = ?;";
	public static final String insert = "insert into contact(name, phone, hotkey) values(?,?,?);";
}
