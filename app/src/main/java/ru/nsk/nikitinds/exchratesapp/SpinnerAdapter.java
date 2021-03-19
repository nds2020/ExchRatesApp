package ru.nsk.nikitinds.exchratesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<Currency> {
    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Currency> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    public View getCustomView(int position, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.converter_section_spiner_item, parent, false);

        ImageView flag = view.findViewById(R.id.iv_country_flag);
        flag.setImageResource(getItem(position).getFlagResource());

        TextView charCode = view.findViewById(R.id.tv_char_code);
        charCode.setText(getItem(position).getCharCode());

        return view;
    }
}
