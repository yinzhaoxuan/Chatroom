package com.gavin.imsoftware.db;

/**
 * Created by gavin on 2017/5/4.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class MessageDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database";
    private static final String MESSAGE_TABLE_NAME = "user";
    private static final int MESSAGE_DATABASE_VISION = 1;

    public MessageDbHelper(Context context) {
        super(context, DATABASE_NAME, null, MESSAGE_DATABASE_VISION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_table = "create table " + MESSAGE_TABLE_NAME + " ("
                + MessageColumns._ID + " integer primary key autoincrement,"
                + MessageColumns.MSG_ID + " text not null,"
                + MessageColumns.SEND_PERSON + " text not null,"
                + MessageColumns.SEND_CTN + " text not null,"
                + MessageColumns.SEND_DATE + " text not null)";
        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE_NAME);
        onCreate(db);
    }

    public static final class MessageColumns implements BaseColumns {
        public static final String ID = "id";
        public static final String MSG_ID = "msg_id";
        public static final String SEND_CTN = "send_ctn";
        public static final String SEND_DATE = "send_date";
        public static final String SEND_PERSON = "send_person";
    }
}