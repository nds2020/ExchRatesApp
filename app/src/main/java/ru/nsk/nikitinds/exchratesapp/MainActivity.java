package ru.nsk.nikitinds.exchratesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private SharedViewModel viewModel;
    private SharedPreferences savedData;
    private Timer mTimer;
    private Context context;
    private ProgressBar loadingIndicator;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        savedData = getSharedPreferences("data", MODE_PRIVATE);

        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        viewModel.setSavedData(savedData);

        loadingIndicator = findViewById(R.id.pb_loading_indicator);

        TextView currentDate = findViewById(R.id.tv_current_date);
        currentDate.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date()));

        TextView notice = findViewById(R.id.tv_notice);
        notice.setText(viewModel.getNoticeText().getValue());
        viewModel.getNoticeText().observe(this, notice::setText);

        ViewPager viewPager = findViewById(R.id.view_pager);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> loadRates());

        context = this;

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 0) {
                    Toast.makeText(context, getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, getString(R.string.successful_load), Toast.LENGTH_LONG).show();
                }

                loadingIndicator.setVisibility(View.INVISIBLE);
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mTimer != null) {
            mTimer.cancel();
        }

        savedData.edit().putString("noticeText", viewModel.getNoticeText().getValue()).apply();
        savedData.edit().putString("rubValue", viewModel.getRubValue()).apply();
        savedData.edit().putString("currencyValue", viewModel.getCurrencyValue().getValue()).apply();
        savedData.edit().putInt("spinnerItemPosition", viewModel.getSelectedSpinnerItemPosition()).apply();
        savedData.edit().putString("jsonResponse", viewModel.getJsonResponse().getValue()).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> loadRates());
            }
        }, 10000, 60000);
    }

    private void loadRates() {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null || !info.isConnected()) {
            Toast.makeText(context, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        } else {
            loadingIndicator.setVisibility(View.VISIBLE);
            viewModel.loadData(context, mHandler);
        }
    }
}