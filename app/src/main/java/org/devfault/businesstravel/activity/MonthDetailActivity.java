package org.devfault.businesstravel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.devfault.businesstravel.R;
import org.devfault.businesstravel.data.BusinessTravelContract;
import org.devfault.businesstravel.util.CommonUtil;


public class MonthDetailActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_detail);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MonthDetailFragment())
                    .commit();
        }
        Log.d(MonthDetailActivity.class.getSimpleName(), " CREATE ");

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            Log.d(CommonUtil.TAG, "HO CLICCATO HOME");

            int currentYear = BusinessTravelContract.ExpenseEntry.getYearFromUri(getIntent().getData());

            Intent upIntent = new Intent(this, YearDetailActivity.class);
            upIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            upIntent.setData(BusinessTravelContract.ExpenseEntry.buildUriByYear(currentYear));
            startActivity(upIntent);

        }

        return super.onOptionsItemSelected(item);
    }

    public void addWorkDay(View view) {
        Intent workDayEditActivity = new Intent(this, ExpenseEdit.class);
        startActivity(workDayEditActivity);
    }
}
