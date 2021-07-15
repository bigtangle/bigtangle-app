package net.bigtangle.wallet.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DBname = "wallet.db"; //像控点数据库
    private static final int version = 1;
    private String tag = "MySQLDBHelper";
    private Context mycontext;


    public MySQLiteOpenHelper(Context context) {
        super(context, DBname, null, version);
        mycontext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sqlString = "create table  walletdata  (username varchar(64),file_data blob)";
        db.execSQL(sqlString);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
