package com.gavin.imsoftware.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class UserDbHelper extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "database";
	private static final int USER_DATABASE_VISION = 1;
	public UserDbHelper(Context context) {
		super(context, DATABASE_NAME, null, USER_DATABASE_VISION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String create_table = "create table "+ UserColumns.USER_TABLE_NAME+" ("+ UserColumns._ID+" integer primary key,"
				+ UserColumns.IP+" text not null,"+ UserColumns.PORT+" text not null,"+ UserColumns.NAME+" text not null,"
				+ UserColumns.IMG+" text not null,"+ UserColumns.FALG+" integer not null)";
		db.execSQL(create_table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+ UserColumns.USER_TABLE_NAME);
		onCreate(db);
	}
	
	public static final class UserColumns implements BaseColumns{
		public UserColumns() {}
		public static final String USER_TABLE_NAME = "user";
		public static final String IP = "ip";
		public static final String PORT = "port";
		public static final String NAME = "name";
		public static final String IMG = "img";
		public static final String FALG = "flag";
	}  
}
