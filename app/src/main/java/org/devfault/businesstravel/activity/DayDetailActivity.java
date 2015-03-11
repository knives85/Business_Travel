package org.devfault.businesstravel.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.devfault.businesstravel.R;
import org.devfault.businesstravel.adapters.DayExpensesAdapter;
import org.devfault.businesstravel.data.BusinessTravelContract;
import static org.devfault.businesstravel.data.BusinessTravelContract.*;

import org.devfault.businesstravel.util.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DayDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_detail);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_day_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            int currentYear;
            int currentMonth;

            if (getIntent().getData() != null) {
                currentYear = ExpenseEntry.getYearFromUri(getIntent().getData());
                currentMonth = ExpenseEntry.getMonthFromUri(getIntent().getData());
            } else {
                currentYear = Calendar.getInstance().get(Calendar.YEAR);
                currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            }
            Intent upIntent = new Intent(this, MonthDetailActivity.class);
            upIntent.setData(ExpenseEntry.buildUriByMonth(currentYear, currentMonth));
            startActivity(upIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void addWorkDay(View view) {
        Log.d(CommonUtil.TAG, "Ho cliccato Add");

        Intent workDayEditActivity = new Intent(this, ExpenseEdit.class);
        startActivity(workDayEditActivity);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        DayExpensesAdapter mAdapter;


        private static final int LOADER_ID = 3;


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_day_detail, container, false);
            mAdapter = new DayExpensesAdapter(getActivity(), null, 0);

            ListView listView = (ListView)rootView.findViewById(R.id.list_day_expenses);
            listView.setAdapter(mAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor record = (Cursor)mAdapter.getItem(position);

                    Uri uri = ExpenseEntry.buildEntryUri(record.getLong(ExpenseEntry.COLUMN_ID_IDX));

                    Intent detailIntent = new Intent(getActivity(), ExpenseEdit.class);
                    detailIntent.setData(uri);
                    startActivity(detailIntent);
                }
            });

            Uri uri = getActivity().getIntent().getData();

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, BusinessTravelContract.ExpenseEntry.getYearFromUri(uri));
            c.set(Calendar.MONTH, BusinessTravelContract.ExpenseEntry.getMonthFromUri(uri));
            c.set(Calendar.DAY_OF_MONTH, BusinessTravelContract.ExpenseEntry.getDayFromUri(uri));

            TextView title = (TextView)rootView.findViewById(R.id.title_textview);
            title.setText(new SimpleDateFormat("dd MMM yyyy").format(c.getTime()));


            return rootView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            String sortOrder = BusinessTravelContract.ExpenseEntry._ID + " ASC";

            return new CursorLoader(getActivity(), getActivity().getIntent().getData(),
                    BusinessTravelContract.ExpenseEntry.COLUMN_PROJECTION, null, null, sortOrder);

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }


    }
}
