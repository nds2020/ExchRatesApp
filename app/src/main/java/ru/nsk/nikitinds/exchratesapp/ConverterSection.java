package ru.nsk.nikitinds.exchratesapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ConverterSection extends Fragment {
    private SharedViewModel viewModel;
    private Spinner spinner;

    public ConverterSection() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_converter_section, container, false);

        spinner = view.findViewById(R.id.sp_currencies_list);

        viewModel.getCurrenciesList(getContext()).observe(this, list -> {
            spinner.setAdapter(new SpinnerAdapter(getContext(), R.layout.converter_section_spiner_item, list));
            spinner.setSelection(viewModel.getSelectedSpinnerItemPosition());
        });

        EditText rubValue = view.findViewById(R.id.et_source_value);
        rubValue.setText(viewModel.getRubValue());

        TextView currencyValue = view.findViewById(R.id.tv_result_value);
        viewModel.getCurrencyValue().observe(this, currencyValue::setText);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setSelectedSpinnerItemPosition(position);
                viewModel.convertRubles();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        rubValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    viewModel.setRubValue(s.toString());
                } else {
                    viewModel.setRubValue("");
                }

                viewModel.convertRubles();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }
}
