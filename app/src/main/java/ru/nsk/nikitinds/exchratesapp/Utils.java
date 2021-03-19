package ru.nsk.nikitinds.exchratesapp;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class Utils {
    public static String getResponseFromURL(URL url) throws IOException {
        HttpsURLConnection connection = null;
        InputStream inputStream = null;
        Scanner scanner = null;

        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.connect();

            inputStream = connection.getInputStream();

            scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (UnknownHostException e) {
            return null;
        } finally {
            if (inputStream != null) inputStream.close();
            if (connection != null) connection.disconnect();
            if (scanner != null) scanner.close();
        }
    }

    public static List<Currency> convertFromJson(Context context, String stringJson) {
        List<Currency> result = new ArrayList<>();

        try {
            JSONObject jsonResponse = new JSONObject(stringJson);
            JSONObject jsonCurrencies = jsonResponse.getJSONObject("Valute");
            Iterator<String> iterator = jsonCurrencies.keys();
            while (iterator.hasNext()) {
                Currency currency = new Currency();

                JSONObject jsonCurrency = jsonCurrencies.getJSONObject(iterator.next());
                currency.setId(jsonCurrency.getString("ID"));
                currency.setNumCode(jsonCurrency.getString("NumCode"));
                currency.setCharCode(jsonCurrency.getString("CharCode"));
                currency.setNominal(jsonCurrency.getInt("Nominal"));
                currency.setName(jsonCurrency.getString("Name"));
                currency.setCurrentValue(jsonCurrency.getDouble("Value"));
                currency.setPreviousValue(jsonCurrency.getDouble("Previous"));
                currency.setFlagResource(context.getResources().getIdentifier("drawable/_" +
                        currency.getCharCode().toLowerCase(), null, context.getPackageName()));
                result.add(currency);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}