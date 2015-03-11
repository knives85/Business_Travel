package org.devfault.businesstravel.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.devfault.businesstravel.data.BusinessTravelContract.ExpenseEntry;
import org.devfault.businesstravel.util.CommonUtil;

import java.util.Calendar;

public class BusinessTravelProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private BusinessTravelDbHelper mOpenHelper;

    static final int EXPENSE_BY_DAY = 100;
    static final int EXPENSE_BY_MONTH = 200;
    static final int EXPENSE_BY_ID = 300;
    static final int EXPENSE_BY_YEAR = 400;
    static final int EXPENSE_YEARS = 500;
    static final int EXPENSE = 600;

    public static final String URI_DETAIL_PATH = "DETAIL";
    public static final String URI_YEARS_PATH = "YEARS";

    private static final SQLiteQueryBuilder sExpenseQueryBuilder;

    static{
        sExpenseQueryBuilder = new SQLiteQueryBuilder();
        sExpenseQueryBuilder.setTables(ExpenseEntry.TABLE_NAME);
    }

    private static final String sExpenseByIdSelection =
            ExpenseEntry.TABLE_NAME + "." + ExpenseEntry._ID + " = ? ";
    private static final String sExpensesInYearSelection =
            ExpenseEntry.COLUMN_YEAR + " = ?";
    private static final String sExpensesInMonthSelection =
            ExpenseEntry.COLUMN_YEAR + " = ? AND " + ExpenseEntry.COLUMN_MONTH + " = ? ";
    private static final String sExpensesInDaySelection =
            ExpenseEntry.COLUMN_YEAR + " = ? AND " + ExpenseEntry.COLUMN_MONTH + " = ?  AND " +
            ExpenseEntry.COLUMN_DAY + " = ?";

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(ExpenseEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(ExpenseEntry.COLUMN_DATE);
            values.put(ExpenseEntry.COLUMN_DATE, BusinessTravelContract.normalizeDate(dateValue));
        }
    }

    private Cursor getExpensesByYear(Uri uri, String[] projection, String sortOrder) {
        int year = ExpenseEntry.getYearFromUri(uri);

        String[] selectionArgs = new String[]{year + ""};

        return executeQuery(projection, sExpensesInYearSelection, selectionArgs, null, null, sortOrder);

    }

    private Cursor getExpensesByMonth(Uri uri, String[] projection, String sortOrder) {
        int month = ExpenseEntry.getMonthFromUri(uri);
        int year = ExpenseEntry.getYearFromUri(uri);

        String[] selectionArgs = new String[]{
                year + "",
                month + ""};

        sExpenseQueryBuilder.setDistinct(false);

        return executeQuery(projection, sExpensesInMonthSelection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getExpensesByDay(Uri uri, String[] projection, String sortOrder) {
        int month = ExpenseEntry.getMonthFromUri(uri);
        int year = ExpenseEntry.getYearFromUri(uri);
        int day = ExpenseEntry.getDayFromUri(uri);

        String[] selectionArgs = new String[]{
                year + "",
                month + "",
                day + ""};

        sExpenseQueryBuilder.setDistinct(false);

        return executeQuery(projection, sExpensesInDaySelection, selectionArgs, null, null, sortOrder);

    }

    private Cursor getExpenseById(
            Uri uri, String[] projection) {

        sExpenseQueryBuilder.setDistinct(false);

        Long id = ExpenseEntry.getIdFromUri(uri);

        return executeQuery(projection, sExpenseByIdSelection, new String[] {id.toString()}, null, null, null);
    }

    private Cursor getExpenseYears(
            Uri uri, String[] projection) {

        sExpenseQueryBuilder.setDistinct(true);

        return executeQuery(projection, null, null, null, null, ExpenseEntry.COLUMN_YEAR + " ASC");

    }

    private Cursor executeQuery(String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder) {
        return sExpenseQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BusinessTravelContract.CONTENT_AUTHORITY;

        /*
        EXPENSE/YEAR/MONTH
        EXPENSE/ID
        EXPENSE?DATE=LONG

         */

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, BusinessTravelContract.PATH_EXPENSE, EXPENSE);
        matcher.addURI(authority, BusinessTravelContract.PATH_EXPENSE + "/" + URI_DETAIL_PATH +"/#", EXPENSE_BY_ID);
        matcher.addURI(authority, BusinessTravelContract.PATH_EXPENSE + "/#/#", EXPENSE_BY_MONTH);
        matcher.addURI(authority, BusinessTravelContract.PATH_EXPENSE + "/#/#/#", EXPENSE_BY_DAY);
        matcher.addURI(authority, BusinessTravelContract.PATH_EXPENSE + "/#", EXPENSE_BY_YEAR);
        matcher.addURI(authority, BusinessTravelContract.PATH_EXPENSE + "/" + URI_YEARS_PATH, EXPENSE_YEARS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new BusinessTravelDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case EXPENSE_BY_DAY:
                return ExpenseEntry.CONTENT_TYPE;
            case EXPENSE_BY_MONTH:
                return ExpenseEntry.CONTENT_TYPE;
            case EXPENSE_BY_YEAR:
                return ExpenseEntry.CONTENT_TYPE;
            case EXPENSE_YEARS:
                return ExpenseEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
//
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor = null;
        switch (sUriMatcher.match(uri)) {
            case EXPENSE_BY_MONTH:
                retCursor = getExpensesByMonth(uri, projection, sortOrder);
                break;
            case EXPENSE_BY_DAY:
                retCursor = getExpensesByDay(uri, projection, sortOrder);
                break;
            case EXPENSE_BY_YEAR:
                retCursor = getExpensesByYear(uri, projection, sortOrder);
                break;
            case EXPENSE_YEARS:
                retCursor = getExpenseYears(uri, projection);
                break;
            case EXPENSE_BY_ID:
                retCursor = getExpenseById(uri, projection);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }
//
//    /*
//        Student: Add the ability to insert Locations to the implementation of this function.
//     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case EXPENSE: {
                normalizeDate(values);
                long _id = db.insert(ExpenseEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ExpenseEntry.buildEntryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case EXPENSE_BY_DAY: {
                rowsDeleted = db.delete(ExpenseEntry.TABLE_NAME, selection, selectionArgs);
                Log.d(CommonUtil.TAG, "ContentProvider Deleted Rows: " + rowsDeleted);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;

        switch (match) {
            case EXPENSE_BY_ID: {
                normalizeDate(values);
                rowsUpdated = db.update(ExpenseEntry.TABLE_NAME, values, selection, selectionArgs);
                Log.d(CommonUtil.TAG, "ContentProvider Updated Rows: " + rowsUpdated);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
