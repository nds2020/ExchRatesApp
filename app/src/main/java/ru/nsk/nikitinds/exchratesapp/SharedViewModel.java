package ru.nsk.nikitinds.exchratesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static ru.nsk.nikitinds.exchratesapp.Utils.convertFromJson;
import static ru.nsk.nikitinds.exchratesapp.Utils.getResponseFromURL;

public class SharedViewModel extends ViewModel {
    private SharedPreferences savedData;
    private final MutableLiveData<String> jsonResponse = new MutableLiveData<>();
    private final MutableLiveData<List<Currency>> currenciesList = new MutableLiveData<>();
    private final MutableLiveData<String> ratesLastUpdate = new MutableLiveData<>();
    private final MutableLiveData<String> rubValue = new MutableLiveData<>();
    private final MutableLiveData<String> currencyValue = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedSpinnerItemPosition = new MutableLiveData<>();
    private final MutableLiveData<Long> timerStartTimeMilliseconds = new MutableLiveData<>();

    public void setSavedData(SharedPreferences savedData) {
        this.savedData = savedData;
    }

    public LiveData<String> getJsonResponse() {
        if (jsonResponse.getValue() != null) {
            return jsonResponse;
        }

        jsonResponse.setValue(savedData.getString("jsonResponse", ""));
        return jsonResponse;
    }

    public LiveData<List<Currency>> getCurrenciesList(Context context) {
        if (currenciesList.getValue() != null) {
            return currenciesList;
        }

        currenciesList.setValue(convertFromJson(context, savedData.getString("jsonResponse", "")));
        return currenciesList;
    }

    public LiveData<String> getRatesLastUpdate() {
        if (ratesLastUpdate.getValue() != null) {
            return ratesLastUpdate;
        }

        ratesLastUpdate.setValue(savedData.getString("ratesLastUpdate", "Курсы валют " +
                "будут запрошены через 10 секунд. Чтобы не ждать, нажмите кнопку внизу экрана"));
        return ratesLastUpdate;
    }

    public String getRubValue() {
        if (rubValue.getValue() != null) {
            return rubValue.getValue();
        }

        rubValue.setValue(savedData.getString("rubValue", ""));
        return rubValue.getValue();
    }

    public void setRubValue(String value) {
        rubValue.setValue(value);
    }

    public LiveData<String> getCurrencyValue() {
        if (currencyValue.getValue() != null) {
            return currencyValue;
        }

        currencyValue.setValue(savedData.getString("currencyValue", ""));
        return currencyValue;
    }

    public void setCurrencyValue(String value) {
        currencyValue.setValue(value);
    }

    public Integer getSelectedSpinnerItemPosition() {
        if (selectedSpinnerItemPosition.getValue() != null) {
            return selectedSpinnerItemPosition.getValue();
        }

        selectedSpinnerItemPosition.setValue(savedData.getInt("spinnerItemPosition", 0));
        return selectedSpinnerItemPosition.getValue();
    }

    public void setSelectedSpinnerItemPosition(Integer position) {
        selectedSpinnerItemPosition.setValue(position);
    }

    public Long getTimerStartTimeMilliseconds() {
        if (timerStartTimeMilliseconds.getValue() == null) {
            return 0L;
        }

        return timerStartTimeMilliseconds.getValue();
    }

    public void setTimerStartTimeMilliseconds(Long milliseconds) {
        timerStartTimeMilliseconds.setValue(milliseconds);
    }

    public void loadData(Context context, Handler handler) {
        new Thread(() -> {
            try {
                URL url = new URL(context.getResources().getString(R.string.base_url) + context.getString(R.string.data_format));

                String response = getResponseFromURL(url);

                if (response == null || response.length() == 0) {
                    Message message = handler.obtainMessage(0);
                    message.sendToTarget();
                } else {
                    String currentData = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                    ratesLastUpdate.postValue(context.getString(R.string.rates_last_update, currentData));
                    jsonResponse.postValue(response);
                    currenciesList.postValue(convertFromJson(context, response));
                    Message message = handler.obtainMessage(1);
                    message.sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void convertRubles() {
        if (currenciesList.getValue() != null && getRubValue().length() != 0) {
            Currency currency = currenciesList.getValue().get(getSelectedSpinnerItemPosition());
            double rate = currency.getCurrentValue();
            int nominal = currency.getNominal();
            String sourceValue = getRubValue();
            String resultValue = String.format(Locale.ROOT, "%.4f",
                    (Double.parseDouble(sourceValue) * nominal / rate));
            setCurrencyValue(resultValue);
        } else {
            setCurrencyValue("");
        }
    }
}
