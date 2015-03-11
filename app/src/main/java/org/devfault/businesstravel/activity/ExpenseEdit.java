package org.devfault.businesstravel.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.devfault.businesstravel.R;
import org.devfault.businesstravel.data.BusinessTravelContract;
import org.devfault.businesstravel.util.CommonUtil;

import static org.devfault.businesstravel.data.BusinessTravelContract.*;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by noi on 31/01/15.
 */
public class ExpenseEdit extends Activity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = ExpenseEdit.class.getSimpleName();
    DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
    Calendar currentDate = Calendar.getInstance();
    Long currId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workday_edit);

        Spinner spinner = (Spinner)findViewById(R.id.detail_expense_type_value);

        ArrayAdapter<CommonUtil.ExpenseType> adapter = new ArrayAdapter<>(this, R.layout.type_spinner, CommonUtil.ExpenseType.values());

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        updateDate(Calendar.getInstance());

        if (getIntent().getData() != null) {
            Cursor item = getContentResolver().query(getIntent().getData(), ExpenseEntry.COLUMN_PROJECTION, null, null, null);
            bindView(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void updateDate(Calendar updateDate) {
        currentDate = updateDate;

        TextView date = (TextView)findViewById(R.id.detail_expense_date);
        date.setText(formatter.format(updateDate.getTime()));
    }

    public void showDateDialog(View view) {
        TextView date = (TextView)findViewById(R.id.detail_expense_date);

        Calendar c = Calendar.getInstance();

        try {
            Date parsed = formatter.parse(date.getText().toString());
            c.setTime(parsed);
        } catch (ParseException pe) {
            Log.e(TAG, "Errore durante la parsificazione della data", pe);
        }

        DatePickerFragment dialog = new DatePickerFragment();
        dialog.setDate(c);
        dialog.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        CheckBox refundable = (CheckBox)findViewById(R.id.detail_expense_refundable);
        LinearLayout refundableRow = (LinearLayout)findViewById(R.id.detail_expense_refundable_row);

        boolean isIncome = parent.getAdapter().getItem(position).equals(CommonUtil.ExpenseType.INCOME);

        refundable.setChecked(isIncome ? false : refundable.isChecked());
        refundableRow.setVisibility(isIncome ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        Calendar currentDate = Calendar.getInstance();

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            int year = currentDate.get(Calendar.YEAR);
            int month = currentDate.get(Calendar.MONTH);
            int day = currentDate.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void setDate(Calendar newCalendar) {
            currentDate = newCalendar;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            ((ExpenseEdit)getActivity()).updateDate(calendar);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                return saveContent();
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void bindView(Cursor item) {
        item.moveToFirst();

        currId = item.getLong(ExpenseEntry.COLUMN_ID_IDX);

        currentDate.setTimeInMillis(item.getLong(ExpenseEntry.COLUMN_DATE_IDX));
        updateDate(currentDate);

        CommonUtil.ExpenseType type = CommonUtil.ExpenseType.valueOf(item.getString(ExpenseEntry.COLUMN_TYPE_IDX));
        ((Spinner) findViewById(R.id.detail_expense_type_value)).setSelection(type.ordinal());
        ((TextView) findViewById(R.id.detail_expense_amount))
                .setText(CommonUtil.NUMBER_FORMAT.format(
                        item.getDouble(ExpenseEntry.COLUMN_AMOUNT_IDX)));
        ((TextView) findViewById(R.id.detail_expense_description)).setText(item.getString(ExpenseEntry.COLUMN_DESCRIPTION_IDX));
        ((CheckBox) findViewById(R.id.detail_expense_refundable)).setChecked(item.getInt(ExpenseEntry.COLUMN_IS_REFUNDABLE_IDX) == 1);

    }

    private boolean saveContent() {

        Number amount = 0;

        try {
            amount = CommonUtil.NUMBER_FORMAT.parse(((TextView) findViewById(R.id.detail_expense_amount)).getText().toString());
        } catch (ParseException pe) {
            Log.e(TAG, "errore durante la parsificazione della somma", pe);
        }

        ContentValues values = new ContentValues();
        values.put(ExpenseEntry.COLUMN_DATE, currentDate.getTimeInMillis());
        values.put(ExpenseEntry.COLUMN_IS_REFUNDABLE, ((CheckBox) findViewById(R.id.detail_expense_refundable)).isChecked());
        values.put(ExpenseEntry.COLUMN_TYPE, ((Spinner) findViewById(R.id.detail_expense_type_value)).getSelectedItem().toString());
        values.put(ExpenseEntry.COLUMN_AMOUNT, amount.doubleValue());
        values.put(ExpenseEntry.COLUMN_DESCRIPTION, ((TextView) findViewById(R.id.detail_expense_description)).getText().toString());
        values.put(ExpenseEntry.COLUMN_YEAR, currentDate.get(Calendar.YEAR));
        values.put(ExpenseEntry.COLUMN_MONTH, currentDate.get(Calendar.MONTH));
        values.put(ExpenseEntry.COLUMN_DAY, currentDate.get(Calendar.DAY_OF_MONTH));


        if (currId != null) {
            getContentResolver().update(BusinessTravelContract.ExpenseEntry.buildEntryUri(currId) , values, ExpenseEntry._ID + " = ?", new String[]{currId.toString()});
        } else {
            getContentResolver().insert(ExpenseEntry.CONTENT_URI, values);
        }


        Intent backIntent = new Intent(this, DayDetailActivity.class);
        backIntent.setData(ExpenseEntry.buildUriByDay(
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
        ));
        startActivity(backIntent);
        finish();


        return true;
    }

}
