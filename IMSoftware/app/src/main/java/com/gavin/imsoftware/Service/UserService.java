package com.gavin.imsoftware.Service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;

import com.gavin.imsoftware.bean.User;
import com.gavin.imsoftware.db.UserDbHelper.UserColumns;

import com.gavin.imsoftware.db.UserDbHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gavin on 2017/5/6.
 */

public class UserService {
    private UserDbHelper mUserDbHelper;
    private SQLiteDatabase db;
    private Context mContext;
    private String columns[] = {UserColumns._ID, UserColumns.IP, UserColumns.PORT,
            UserColumns.NAME, UserColumns.IMG};
    public UserService(Context context) {
        this.mContext = context;
        mUserDbHelper = new UserDbHelper(this.mContext);
    }

    /**
     * 查询当前标示用户
     *
     * */
    public User queryUser() {
        int curFalg = 1;
        db = mUserDbHelper.getReadableDatabase();
        Cursor cursor = db.query(UserColumns.USER_TABLE_NAME, columns, UserColumns.FALG + "=" +
            curFalg, null, null, null, null);
        if (cursor.moveToFirst()) {
            String ip = cursor.getString(cursor.getColumnIndex(UserColumns.IP));
            String port = cursor.getString(cursor.getColumnIndex(UserColumns.PORT));
            String img = cursor.getString(cursor.getColumnIndex(UserColumns.IMG));
            String name = cursor.getString(cursor.getColumnIndex(UserColumns.NAME));
            int id = cursor.getInt(cursor.getColumnIndex(UserColumns._ID));
            User user = new User();
            user.setIp(ip);
            user.setPort(port);
            user.setImg(img);
            user.setName(name);
            user.setId(id);

            cursor.close();
            db.close();
            return user;
        }
        cursor.close();
        db.close();
        return null;
    }

    /*
    * 插入用户
    * */
    public long insertUser(User user) throws FileNotFoundException {
        db = mUserDbHelper.getWritableDatabase();
        File fileDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            fileDir = new File(Environment.getExternalStorageDirectory() + "/userImage");
        } else {
            fileDir = new File(mContext.getFilesDir()+"/userImage");
        }
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        String fileName = System.currentTimeMillis()+".png";
        File imageFile = new File(fileDir.getAbsoluteFile()+"/"+fileName);
        OutputStream outputStream = new FileOutputStream(imageFile);
        Bitmap bitmap = user.getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 60, outputStream);
        ContentValues values = new ContentValues();
        values.put(UserColumns._ID, user.getId());
        values.put(UserColumns.IP, user.getIp());
        values.put(UserColumns.PORT, user.getPort());
        values.put(UserColumns.NAME, user.getName());
        values.put(UserColumns.IMG, user.getImg());
        values.put(UserColumns.FALG, user.getFlag());
        db.beginTransaction();
        long rowId = 0;
        try {
            db.execSQL("update "+UserColumns.USER_TABLE_NAME+" set "+UserColumns.FALG+"='0'");
            rowId = db.insert(UserColumns.USER_TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
        return rowId;
    }
    /**
     *
     * 切换账号
     * */
    public long convertUser(User user) {
        db = mUserDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserColumns.IP, user.getIp());
        values.put(UserColumns.PORT, user.getPort());
        values.put(UserColumns.IP, user.getFlag());
        db.beginTransaction();
        long rowId = 0;
        try {
            db.execSQL("update "+UserColumns.USER_TABLE_NAME+" set "+UserColumns.FALG+"='0'");
            rowId = db.update(UserColumns.USER_TABLE_NAME, values, UserColumns._ID+"=?",
                    new String[]{String.valueOf(user.getId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
        return rowId;
    }

    /**
     * 删除用户
     * */

    public int deleteUser(int rowId) {
        db = mUserDbHelper.getWritableDatabase();
        String where = null;
        if (rowId != 0) {
            where = UserColumns._ID + "=" + rowId;
        }
        int rows = db.delete(UserColumns.USER_TABLE_NAME, where, null);
        db.close();
        return rows;
    }

    /**
     *
     * 更新用户
     *
     * */
    public int Updateuser(User user) {
        db = mUserDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserColumns.IP, user.getIp());
        values.put(UserColumns.PORT, user.getPort());
        values.put(UserColumns.NAME, user.getName());
        values.put(UserColumns.IMG, user.getImg());
        values.put(UserColumns.FALG, user.getFlag());
        int rowId = db.update(UserColumns.USER_TABLE_NAME, values,
                UserColumns._ID+"="+user.getId(), null);
        db.close();
        return rowId;
    }

    /**
     * 查询已注册的用户
     * */
    public List<User> queryResigterUser() {
        db = mUserDbHelper.getWritableDatabase();
        Cursor cursor = db.query(UserColumns.USER_TABLE_NAME, columns,
                null, null, null, null, UserColumns.FALG+" DESC");
        List<User> list = new ArrayList<User>();
        while(cursor.moveToNext()) {
            String ip = cursor.getString(cursor.getColumnIndex(UserColumns.IP));
            String port = cursor.getString(cursor.getColumnIndex(UserColumns.PORT));
            String img = cursor.getString(cursor.getColumnIndex(UserColumns.IMG));
            String name = cursor.getString(cursor.getColumnIndex(UserColumns.NAME));
            int id = cursor.getInt(cursor.getColumnIndex(UserColumns._ID));
            User user = new User(id, ip, port, name, img);
            list.add(user);
        }
        cursor.close();
        db.close();
        return list;

    }
}
