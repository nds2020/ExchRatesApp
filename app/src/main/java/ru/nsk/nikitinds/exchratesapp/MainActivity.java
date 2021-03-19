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

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private SharedViewModel viewModel;
    private SharedPreferences savedData;
    private Timer timer;
    private Context context;
    private ProgressBar loadingIndicator;
    private Handler handler;

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

        TextView ratesLastUpdate = findViewById(R.id.tv_notice);
        viewModel.getRatesLastUpdate().observe(this, ratesLastUpdate::setText);

        ViewPager viewPager = findViewById(R.id.view_pager);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), 0);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton update = findViewById(R.id.fab);
        update.setOnClickListener(view -> {
            timer.cancel();
            loadRates();

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        loadRates();
                        viewModel.setTimerStartTimeMilliseconds(Calendar.getInstance().getTimeInMillis());
                    });
                }
            }, 60000, 60000);
        });

        context = this;

        handler = new Handler(Looper.getMainLooper()) {
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

        if (timer != null) {
            timer.cancel();
        }

        savedData.edit().putString("ratesLastUpdate", viewModel.getRatesLastUpdate().getValue()).apply();
        savedData.edit().putString("rubValue", viewModel.getRubValue()).apply();
        savedData.edit().putString("currencyValue", viewModel.getCurrencyValue().getValue()).apply();
        savedData.edit().putInt("spinnerItemPosition", viewModel.getSelectedSpinnerItemPosition()).apply();
        savedData.edit().putString("jsonResponse", viewModel.getJsonResponse().getValue()).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        timer = new Timer();

        long timerLastStart = viewModel.getTimerStartTimeMilliseconds();
        long currentTime = Calendar.getInstance().getTimeInMillis();
        int delay = 10000;

        if (timerLastStart != 0) {
            long delta = currentTime - timerLastStart;

            if (delta > 60000) {
                delay = 0;
            } else if (delta >= 0 && delta <= 60000) {
                delay = 60000 - (int) delta;
            }
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    loadRates();
                    viewModel.setTimerStartTimeMilliseconds(Calendar.getInstance().getTimeInMillis());
                });
            }
        }, delay, 60000);
    }

    private void loadRates() {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null || !info.isConnected()) {
            Toast.makeText(context, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        } else {
            loadingIndicator.setVisibility(View.VISIBLE);
            viewModel.loadData(context, handler);
        }
    }
}