package ru.nsk.nikitinds.exchratesapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExchangeRatesSection extends Fragment {
    private SharedViewModel viewModel;

    public ExchangeRatesSection() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exchange_rates_section, container, false);

        RecyclerView currenciesList = view.findViewById(R.id.rv_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        currenciesList.setLayoutManager(layoutManager);

        viewModel.getCurrenciesList(getContext()).observe(this, list -> {
            ExchangeRatesSectionAdapter adapter = new ExchangeRatesSectionAdapter(list, getContext());
            currenciesList.setAdapter(adapter);
        });

        return view;
    }
}
