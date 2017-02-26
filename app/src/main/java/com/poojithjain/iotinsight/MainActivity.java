package com.poojithjain.iotinsight;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.poojithjain.iotinsight.util.app.APIService;
import com.poojithjain.iotinsight.util.app.AppConstants;
import com.poojithjain.iotinsight.util.app.AppSharedPreferences;
import com.poojithjain.iotinsight.util.net.api.FitbitAPITask;
import com.poojithjain.iotinsight.util.net.data.RequestType;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Context context = this;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            // Collect auth code and store it
            SharedPreferences.Editor editor = AppSharedPreferences.getInstance().editor(context);
            editor.putString("authCode", uri.getQueryParameter("code").trim());
            editor.commit();
            Log.i("Auth code", AppSharedPreferences.getInstance().getSharedPreferences(context).getString("authCode", ""));
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(getApplicationContext(), APIService.class), PendingIntent.FLAG_NO_CREATE) != null);

        if (!alarmUp) {
            Calendar cal = Calendar.getInstance();
            Intent serviceIntent = new Intent(MainActivity.this, APIService.class);
            PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, serviceIntent, 0);
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            // schedule every minute
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 60 * 1000, pintent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            new FitbitAPITask(context, RequestType.Devices).execute();
            return true;

        } else if (id == R.id.action_login) {
            if (!AppSharedPreferences.getInstance().getSharedPreferences(context).getBoolean("loginStatus", false)) {
                String url = "https://www.fitbit.com/oauth2/authorize?response_type=code&client_id=2285P6&scope=settings profile heartrate";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse(url));

                new FitbitAPITask(context, RequestType.AccessToken).execute();
                return true;
            } else {
                Toast.makeText(context, "You are already logged in", Toast.LENGTH_LONG).show();
                return true;
            }
        }

        else if (id == R.id.action_alarm) {
            new FitbitAPITask(context, RequestType.Alarm).execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_battery, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return AppConstants.tabTitles[position];
        }
    }
}
