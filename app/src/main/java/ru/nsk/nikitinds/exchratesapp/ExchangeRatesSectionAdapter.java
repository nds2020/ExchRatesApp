package ru.nsk.nikitinds.exchratesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ExchangeRatesSectionAdapter extends RecyclerView.Adapter<ExchangeRatesSectionAdapter.ExchangeRatesItemViewHolder>{
    private final List<Currency> currencies;
    private final Context context;


    public ExchangeRatesSectionAdapter(List<Currency> currencies, Context context) {
        this.currencies = currencies;
        this.context = context;
    }

    @NonNull
    @Override
    public ExchangeRatesItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.exchange_rates_section_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new ExchangeRatesItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExchangeRatesItemViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

    class ExchangeRatesItemViewHolder extends RecyclerView.ViewHolder {
        ImageView flag;
        TextView nominalAndCharCode;
        TextView name;
        TextView currentRate;

        public ExchangeRatesItemViewHolder(@NonNull View itemView) {
            super(itemView);

            flag = itemView.findViewById(R.id.iv_country_flag);
            nominalAndCharCode = itemView.findViewById(R.id.tv_nominal_and_char_code);
            name = itemView.findViewById(R.id.tv_name);
            currentRate = itemView.findViewById(R.id.tv_current_rate);
        }

        void bind(int listIndex) {
            Currency currency = currencies.get(listIndex);
            flag.setImageResource(currency.getFlagResource());
            nominalAndCharCode.setText(context.getString(R.string.currency_nominal_and_char_code, currency.getNominal(), currency.getCharCode()));
            name.setText(currency.getName());
            currentRate.setText(String.format(Locale.ROOT,"%.4f", currency.getCurrentValue()));
        }
    }
}
