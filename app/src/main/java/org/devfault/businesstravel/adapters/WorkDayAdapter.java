package org.devfault.businesstravel.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.devfault.businesstravel.R;
import org.devfault.businesstravel.model.WorkDay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class WorkDayAdapter extends RecyclerView.Adapter<WorkDayAdapter.ViewHolder> {
    private WorkDay[] mDataset;
    private DateFormat format = new SimpleDateFormat("d\nEEE");

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mExpenseValue;
        public TextView mIncomeValue;
        public TextView mBalanceValue;
        public TextView mDayNumber;

        public ViewHolder(View v) {
            super(v);
            mExpenseValue = (TextView) v.findViewById(R.id.expense_value_textview);
            mIncomeValue = (TextView) v.findViewById(R.id.income_value_textview);
            mBalanceValue = (TextView) v.findViewById(R.id.balance_value_textview);
            mDayNumber = (TextView) v.findViewById(R.id.day_number_textview);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public WorkDayAdapter(WorkDay[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WorkDayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mDayNumber.setText(format.format(mDataset[position].getWorkDate()));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.length : 0;
    }
}
