package com.example.countries_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CountryAdapter extends ArrayAdapter<Country> {
    private final Context context;
    private List<Country> countryList;

    public CountryAdapter(Context context, List<Country> countryList) {
        super(context, 0, countryList);
        this.context = context;
        this.countryList = countryList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.country_list_item, parent, false);
        }

        Country currentCountry = countryList.get(position);

        ImageView flagImageView = listItemView.findViewById(R.id.flagImageView);
        TextView nameTextView = listItemView.findViewById(R.id.nameTextView);

        Picasso.get().load(currentCountry.getFlagUrl()).into(flagImageView);
        nameTextView.setText(currentCountry.getName());

        return listItemView;
    }

    public void filterList(List<Country> filteredList) {
        countryList = filteredList;
        notifyDataSetChanged();
    }

}
