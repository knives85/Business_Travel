package org.devfault.businesstravel.adapters;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.devfault.businesstravel.R;
import org.devfault.businesstravel.model.WorkDay;
import org.devfault.businesstravel.util.CommonUtil;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.devfault.businesstravel.data.BusinessTravelContract.ExpenseEntry;

/**
 * {@link DateAggregatedAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class DateAggregatedAdapter extends CursorAdapter {

    public static String TAG = DateAggregatedAdapter.class.getSimpleName();

    List<WorkDay> aggregatedItems;
    private DateFormat mFormatter;
    private Cursor mCursor;
    private Context mContext;
    private int lastCursorPosition = 0;
    private int mAggregatorField;
    private NumberFormat mNumberFormatter = NumberFormat.getCurrencyInstance();

    public DateAggregatedAdapter(Context context, Cursor c, int flags, int aggregatorField, DateFormat formatter) {
        super(context, c, flags);
        mCursor = c;
        mContext = context;
        aggregatedItems = new ArrayList<>();
        mAggregatorField = aggregatorField;
        mFormatter = formatter;
        lastCursorPosition = 0;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mCursor == null) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (mCursor.getCount() > 0 && !mCursor.moveToPosition(lastCursorPosition)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        View v;
        if (convertView == null) {
            v = newView(mContext, mCursor, parent);
        } else {
            v = convertView;
        }
        bindView(v, mContext, mCursor);
        return v;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        double expenseTot = 0;
        double incomeTot = 0;
        long lastAggregatorValue = -1;
        Calendar elemReferenceValue = Calendar.getInstance();
        Calendar bufferCalendar = Calendar.getInstance();
        bufferCalendar.setTimeInMillis(cursor.getLong(ExpenseEntry.COLUMN_DATE_IDX));
        elemReferenceValue.setTimeInMillis(cursor.getLong(ExpenseEntry.COLUMN_DATE_IDX));

        Log.d(TAG, "INIZIO IL CICLO");

        while (!cursor.isAfterLast() && (lastAggregatorValue == -1 || bufferCalendar.get(mAggregatorField) == lastAggregatorValue )) {

            Log.d(TAG, "Parsifico la spesa id " + cursor.getLong(ExpenseEntry.COLUMN_ID_IDX));

            lastAggregatorValue = bufferCalendar.get(mAggregatorField);
            double amount = cursor.getDouble(ExpenseEntry.COLUMN_AMOUNT_IDX);

            if (CommonUtil.ExpenseType.EXPENSE.toString().equals(cursor.getString(ExpenseEntry.COLUMN_TYPE_IDX))) {
                expenseTot += amount;
            } else {
                incomeTot += amount;
            }

            lastCursorPosition++;
            cursor.moveToNext();
            if (!cursor.isAfterLast()) {
                bufferCalendar.setTimeInMillis(cursor.getLong(ExpenseEntry.COLUMN_DATE_IDX));
            }
        }

        cursor.moveToPrevious();

        WorkDay elem = new WorkDay();
        elem.setWorkDate(elemReferenceValue.getTime());
        elem.setExpensesTot(expenseTot);
        elem.setIncomeTot(incomeTot);

        aggregatedItems.add(elem);

        TextView dayNumber = (TextView) view.findViewById(R.id.day_number_textview);
        dayNumber.setText(mFormatter.format(elem.getWorkDate()));

        TextView expenses = (TextView) view.findViewById(R.id.expense_value_textview);
        expenses.setText(mNumberFormatter.format(expenseTot) + "");

        TextView incomes = (TextView) view.findViewById(R.id.income_value_textview);
        incomes.setText(mNumberFormatter.format(incomeTot) + "");

        double balanceValue = incomeTot - expenseTot;
        int amountColor = R.color.green;
        if (balanceValue < 0)
        {
            amountColor = R.color.red;
        }


        TextView balance = (TextView) view.findViewById(R.id.balance_value_textview);
        balance.setText(mNumberFormatter.format(Math.abs(balanceValue)) + "");
        balance.setTextColor(context.getResources().getColor(amountColor));

    }

    @Override
    public Object getItem(int position) {
        return aggregatedItems.get(position);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        lastCursorPosition = 0;
        return super.swapCursor(newCursor);
    }

    @Override
    public int getCount() {
        if (mCursor != null) {

            Set<Integer> distinctDate = new HashSet<>();
            mCursor.moveToFirst();
            Calendar calendar = Calendar.getInstance();

            while (!mCursor.isAfterLast()) {

                calendar.setTimeInMillis(mCursor.getLong(ExpenseEntry.COLUMN_DATE_IDX));
                distinctDate.add(calendar.get(mAggregatorField));
                mCursor.moveToNext();

            }

            mCursor.moveToFirst();

            return distinctDate.size();

        } else {
            return 0;
        }
    }
}
