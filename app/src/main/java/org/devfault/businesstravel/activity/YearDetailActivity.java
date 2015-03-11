package org.devfault.businesstravel.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.devfault.businesstravel.R;
import org.devfault.businesstravel.adapters.DateAggregatedAdapter;
import org.devfault.businesstravel.data.BusinessTravelContract;
import org.devfault.businesstravel.model.WorkDay;
import org.devfault.businesstravel.util.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class YearDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year_detail);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_month_detail, menu);
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

        DateAggregatedAdapter mAdapter;
        private static final int LOADER_ID = 1;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_year_detail, container, false);

            mAdapter = new DateAggregatedAdapter(getActivity(), null, 0, Calendar.MONTH, new SimpleDateFormat("MMM"));

            ListView listView = (ListView) rootView.findViewById(R.id.year_detail_list);
            listView.setAdapter(mAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WorkDay selectedItem = (WorkDay)mAdapter.getItem(position);

                    Calendar c = Calendar.getInstance();
                    c.setTime(selectedItem.getWorkDate());

                    Intent dayIntent = new Intent(getActivity(), MonthDetailActivity.class);
                    dayIntent.setData(BusinessTravelContract.ExpenseEntry.buildUriByMonth(c.get(Calendar.YEAR), c.get(Calendar.MONTH)));

                    startActivity(dayIntent);

                }
            });

            int year;
            if (getActivity().getIntent().getData() != null) {
                year = BusinessTravelContract.ExpenseEntry.getYearFromUri(getActivity().getIntent().getData());
            } else {
                year = Calendar.getInstance().get(Calendar.YEAR);
            }

            String[] years = CommonUtil.getYearArray(getActivity());

            Spinner spinner = (Spinner)rootView.findViewById(R.id.year_spinner);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.header_spinner,
                    years);

            spinner.setAdapter(adapter);
            spinner.setSelection(Arrays.binarySearch(years, year + ""));

            return rootView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            String sortOrder = BusinessTravelContract.ExpenseEntry.COLUMN_DATE + " ASC";

            return new CursorLoader(getActivity(), BusinessTravelContract.ExpenseEntry.buildUriByYear(2015),
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
