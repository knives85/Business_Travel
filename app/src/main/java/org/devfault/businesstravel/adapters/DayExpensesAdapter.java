package org.devfault.businesstravel.adapters;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.devfault.businesstravel.R;
import org.devfault.businesstravel.util.CommonUtil;

import java.text.NumberFormat;

import static org.devfault.businesstravel.data.BusinessTravelContract.*;

/**
 * {@link DayExpensesAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class DayExpensesAdapter extends CursorAdapter {


    private NumberFormat mNumberFormatter = NumberFormat.getCurrencyInstance();

    public DayExpensesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_day_item, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String type = cursor.getString(ExpenseEntry.COLUMN_TYPE_IDX);

        int amountColor = R.color.green;
        if (CommonUtil.ExpenseType.valueOf(type).equals(CommonUtil.ExpenseType.EXPENSE))
        {
            amountColor = R.color.red;
        }
        TextView amountTextView = ((TextView)view.findViewById(R.id.expense_amount));

        ((TextView)view.findViewById(R.id.expense_type)).setText(type);
        ((TextView)view.findViewById(R.id.expense_description)).setText(cursor.getString(ExpenseEntry.COLUMN_DESCRIPTION_IDX));
        amountTextView.setTextColor(context.getResources().getColor(amountColor));
        amountTextView.setText(mNumberFormatter.format(cursor.getDouble(ExpenseEntry.COLUMN_AMOUNT_IDX)));

        int visibility = View.VISIBLE;

        if (cursor.getInt(ExpenseEntry.COLUMN_IS_REFUNDABLE_IDX) == 0) {
            visibility = View.INVISIBLE;
        }

        view.findViewById(R.id.expense_refundable_img).setVisibility(visibility);

    }
}
