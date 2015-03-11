package org.devfault.businesstravel.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by noi on 20/02/15.
 */
public class BusinessTravelContract {

    /*
        - SPESA
            * _ID
            * YEAR
            * MONTH
            * DAY
            * TYPE
            * IS_REFOUNDABLE
            * AMOUNT
            * DESCRIPTION
     */

    public static final String CONTENT_AUTHORITY = "org.devfault.businesstravel";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_EXPENSE = "expense";

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.setToNow();
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Inner class that defines the table contents of the location table */
    public static final class ExpenseEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXPENSE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSE;

        // Table name
        public static final String TABLE_NAME = "expense";

        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_IS_REFUNDABLE = "is_refoundable";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_MONTH = "month";
        public static final String COLUMN_DAY = "day";

        public static final int COLUMN_ID_IDX = 0;
        public static final int COLUMN_DATE_IDX = 1;
        public static final int COLUMN_TYPE_IDX = 2;
        public static final int COLUMN_AMOUNT_IDX = 3;
        public static final int COLUMN_IS_REFUNDABLE_IDX = 4;
        public static final int COLUMN_DESCRIPTION_IDX = 5;
        public static final int COLUMN_YEAR_IDX = 6;
        public static final int COLUMN_MONTH_IDX = 7;
        public static final int COLUMN_DAY_IDX = 8;

        public static final String[] COLUMN_PROJECTION = new String[] {
                _ID,
                COLUMN_DATE,
                COLUMN_TYPE,
                COLUMN_AMOUNT,
                COLUMN_IS_REFUNDABLE,
                COLUMN_DESCRIPTION,
                COLUMN_YEAR,
                COLUMN_MONTH,
                COLUMN_DAY
        };

        public static final String[] COLUMN_PROJECTION_YEARS = new String[] {
                COLUMN_YEAR
        };

        public static Uri buildEntryUri(Long id) {
            return CONTENT_URI.buildUpon().appendPath(BusinessTravelProvider.URI_DETAIL_PATH).appendPath(id.toString()).build();
        }

        public static Uri buildUriByDay(Integer year, Integer month, Integer day) {
            return CONTENT_URI.buildUpon()
                    .appendPath(year.toString())
                    .appendPath(month.toString())
                    .appendPath(day.toString())
                    .build();
        }

        public static Uri buildUriByMonth(Integer year, Integer month) {
            return CONTENT_URI.buildUpon().appendPath(year.toString()).appendPath(month.toString()).build();
        }

        public static Uri buildUriByYear(Integer year) {
            return CONTENT_URI.buildUpon().appendPath(year.toString()).build();
        }

        public static Uri buildUriYears() {
            return CONTENT_URI.buildUpon().appendPath(BusinessTravelProvider.URI_YEARS_PATH).build();
        }


        public static int getMonthFromUri(Uri uri) {
            return Integer.valueOf(uri.getPathSegments().get(2));
        }

        public static int getYearFromUri(Uri uri) {
            return Integer.valueOf(uri.getPathSegments().get(1));
        }

        public static int getDayFromUri(Uri uri) {
            return Integer.valueOf(uri.getPathSegments().get(3));
        }

        public static long getIdFromUri(Uri uri) {
            return Integer.valueOf(uri.getPathSegments().get(2));
        }


    }

}
