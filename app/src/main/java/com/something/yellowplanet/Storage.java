package com.something.yellowplanet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

/**
 * Created by Geoff on 5/11/2016.
 */
public final class Storage {
    public Storage() {}

    public static abstract class DataEntry implements BaseColumns {
        public static final String TABLE_NAME = "rawData";
        public static final String COL_TIMESTAMP = "timestamp";
        public static final String COL_TIMESLICE = "timeslice";
        public static final String COL_BATTERY = "battery";
        public static final String COL_CPU = "cpu";
        public static final String COL_NETIN = "netin";
        public static final String COL_NETOUT = "netout";
        public static final String COL_MEMORY = "memory";
    }

    public class StorageHelper extends SQLiteOpenHelper {

        private static final String TEXT_TYPE = " TEXT";
        private static final String REAL_TYPE = " REAL";
        private static final String INT_TYPE = " INTEGER";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DataEntry.TABLE_NAME + " ("
                        + DataEntry._ID + "INTEGER PRIMARY KEY,"
                        + DataEntry.COL_TIMESTAMP + INT_TYPE + COMMA_SEP
                        + DataEntry.COL_TIMESLICE + INT_TYPE + COMMA_SEP
                        + DataEntry.COL_BATTERY + REAL_TYPE + COMMA_SEP
                        + DataEntry.COL_CPU + REAL_TYPE + COMMA_SEP
                        + DataEntry.COL_MEMORY + REAL_TYPE + COMMA_SEP
                        + DataEntry.COL_NETIN + REAL_TYPE + COMMA_SEP
                        + DataEntry.COL_NETOUT + REAL_TYPE + ")";


        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "DataStorage.db";

        public StorageHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {

        }


    }

}
