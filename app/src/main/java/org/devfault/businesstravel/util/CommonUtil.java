package org.devfault.businesstravel.util;

import android.content.Context;
import android.database.Cursor;

import org.devfault.businesstravel.data.BusinessTravelContract;
import static org.devfault.businesstravel.data.BusinessTravelContract.*;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by noi on 31/01/15.
 */
public class CommonUtil {

    public static final String TAG = "BusinessTravel";

    public static final NumberFormat NUMBER_FORMAT;

    static {
        NUMBER_FORMAT = NumberFormat.getNumberInstance();
        NUMBER_FORMAT.setMaximumFractionDigits(2);
        NUMBER_FORMAT.setMinimumFractionDigits(2);
        NUMBER_FORMAT.setGroupingUsed(false);
    }

    public enum ExpenseType {
        EXPENSE, INCOME
    }

    public static String[] getMonthArray() {
        String[] months = new String[12];
        Calendar c = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMMM");

        for (int i = 0; i < 12; i++) {
            c.set(Calendar.MONTH, i);
            months[i] = formatter.format(c.getTime());
        }

        return months;
    }

    public static String[] getYearArray(Context context) {
        List<String> years = new ArrayList<>();

        Cursor details = context.getContentResolver()
                .query(ExpenseEntry.buildUriYears(),
                        ExpenseEntry.COLUMN_PROJECTION_YEARS,
                        null,
                        null,
                        ExpenseEntry.COLUMN_YEAR + " ASC");

        details.moveToFirst();

        while (!details.isAfterLast()) {

            years.add(details.getString(0));

            details.moveToNext();
        }
        details.close();

        return years.toArray(new String[]{});
    }


}
