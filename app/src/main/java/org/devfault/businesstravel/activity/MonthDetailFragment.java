package org.devfault.businesstravel.activity;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import org.devfault.businesstravel.R;
import org.devfault.businesstravel.adapters.DateAggregatedAdapter;
import org.devfault.businesstravel.data.BusinessTravelContract;
import org.devfault.businesstravel.model.WorkDay;
import org.devfault.businesstravel.util.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by noi on 09/02/15.
 */
public class MonthDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {

    DateAggregatedAdapter mAdapter;
    private static final int LOADER_ID = 2;

    public MonthDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_month_detail, container, false);

        mAdapter = new DateAggregatedAdapter(getActivity(), null, 0, Calendar.DAY_OF_MONTH, new SimpleDateFormat("d\nEEE"));

        ListView listView = (ListView) rootView.findViewById(R.id.my_recycler_view);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WorkDay selectedItem = (WorkDay)mAdapter.getItem(position);

                Calendar c = Calendar.getInstance();
                c.setTime(selectedItem.getWorkDate());


                Intent dayIntent = new Intent(getActivity(), DayDetailActivity.class);
                dayIntent.setData(BusinessTravelContract.ExpenseEntry
                        .buildUriByDay(c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)));

                startActivity(dayIntent);

            }
        });

        int month = BusinessTravelContract.ExpenseEntry.getMonthFromUri(getActivity().getIntent().getData());


        Spinner spinner = (Spinner)rootView.findViewById(R.id.month_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                        R.layout.header_spinner,
                                        CommonUtil.getMonthArray());

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(month);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = BusinessTravelContract.ExpenseEntry.COLUMN_DATE + " ASC";

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //update month detail uri
        Uri oldUri = getActivity().getIntent().getData();
        int year = BusinessTravelContract.ExpenseEntry.getYearFromUri(oldUri);

        getActivity().getIntent().setData(BusinessTravelContract.ExpenseEntry.buildUriByMonth(year, position));
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
