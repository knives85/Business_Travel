package org.devfault.businesstravel.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.devfault.businesstravel.data.BusinessTravelContract.ExpenseEntry;
import org.devfault.businesstravel.util.CommonUtil;

import java.util.Calendar;

public class BusinessTravelDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "business_travel.db";

    public BusinessTravelDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_EXPENSE_TABLE = "CREATE TABLE " + ExpenseEntry.TABLE_NAME + " (" +
                ExpenseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                ExpenseEntry.COLUMN_YEAR + " INTEGER NOT NULL, " +
                ExpenseEntry.COLUMN_MONTH + " INTEGER NOT NULL, " +
                ExpenseEntry.COLUMN_DAY + " INTEGER NOT NULL, " +
                ExpenseEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                ExpenseEntry.COLUMN_IS_REFUNDABLE + " BOOLEAN NOT NULL, " +
                ExpenseEntry.COLUMN_AMOUNT + " REAL NOT NULL, " +
                ExpenseEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL " +
            ");";

        sqLiteDatabase.execSQL(SQL_CREATE_EXPENSE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("ALTER TABLE " + ExpenseEntry.TABLE_NAME + " ADD COLUMN " + ExpenseEntry.COLUMN_YEAR+ " INTEGER NOT NULL DEFAULT 0");
        sqLiteDatabase.execSQL("ALTER TABLE " + ExpenseEntry.TABLE_NAME + " ADD COLUMN " + ExpenseEntry.COLUMN_MONTH+ " TEXT NOT NULL DEFAULT 0");
        sqLiteDatabase.execSQL("ALTER TABLE " + ExpenseEntry.TABLE_NAME + " ADD COLUMN " + ExpenseEntry.COLUMN_DAY+ " TEXT NOT NULL DEFAULT 0");


        Cursor allRecords = sqLiteDatabase.rawQuery("SELECT * FROM " + ExpenseEntry.TABLE_NAME, null);

        allRecords.moveToFirst();

        while (!allRecords.isAfterLast()) {

            long currdate = allRecords.getLong(ExpenseEntry.COLUMN_DATE_IDX);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(currdate);

            ContentValues values = new ContentValues();
            values.put(ExpenseEntry.COLUMN_AMOUNT, allRecords.getDouble(ExpenseEntry.COLUMN_AMOUNT_IDX));
            values.put(ExpenseEntry.COLUMN_DESCRIPTION, allRecords.getString(ExpenseEntry.COLUMN_DESCRIPTION_IDX));
            values.put(ExpenseEntry.COLUMN_IS_REFUNDABLE, allRecords.getInt(ExpenseEntry.COLUMN_IS_REFUNDABLE_IDX));
            values.put(ExpenseEntry.COLUMN_TYPE, allRecords.getString(ExpenseEntry.COLUMN_TYPE_IDX));
            values.put(ExpenseEntry.COLUMN_YEAR, c.get(Calendar.YEAR));
            values.put(ExpenseEntry.COLUMN_MONTH, c.get(Calendar.MONTH));
            values.put(ExpenseEntry.COLUMN_DAY, c.get(Calendar.DAY_OF_MONTH));


            int updated = sqLiteDatabase.update(ExpenseEntry.TABLE_NAME, values, ExpenseEntry._ID + " = ?", new String[]{allRecords.getLong(ExpenseEntry.COLUMN_ID_IDX) + ""});

            Log.d(CommonUtil.TAG, "Update DB Structure, updated row: " + updated);

            allRecords.moveToNext();
        }

    }
}
