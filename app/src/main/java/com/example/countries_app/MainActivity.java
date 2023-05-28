package com.example.countries_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final List<Country> countryList = new ArrayList<>();
    private String jsonCevabi = "";
    private EditText countryEditText;
    private ListView countryListView;
    private CountryAdapter countryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        countryEditText = findViewById(R.id.countryEditText);
        countryListView = findViewById(R.id.countryListView);
        countryAdapter = new CountryAdapter(this, countryList);
        countryListView.setAdapter(countryAdapter);
        ulkeAyikla();
        countryListView.setOnItemClickListener(this);
        /*
        countryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                filterCountries(s.toString());
            }
        });
            */
        /*
        countryEditText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                filterCountries(countryEditText.getText().toString());
                return true;
            }
            return false;
        });
         */
    }
    private void filterCountries(String searchText) {
        List<Country> filteredList = new ArrayList<>();
        String filteredText = removeSpecialCharacters(searchText.toLowerCase());

        for (Country country : countryList) {
            String countryName = country.getName().toLowerCase();
            if (countryName.contains(filteredText)) {
                filteredList.add(country);
            }
        }

        countryAdapter.filterList(filteredList);
        countryAdapter.notifyDataSetChanged();
    }

    @NonNull
    private String removeSpecialCharacters(@NonNull String text) {
        return text.replaceAll("[^a-zA-Z0-9]", "");
    }

    protected void ulkeAyikla() {
        String url = "https://restcountries.com/v3.1/all?fields=name,flags";
        apiTalep(url);
    }
    protected void apiTalep(String url) {
        RequestQueue talepSirasi = Volley.newRequestQueue(this);
        StringRequest talep = new StringRequest(Request.Method.GET, url, response -> {
            Log.v("test", "Cevap: " + response);
            jsonCevabi = response;
            parseCountries();
        }, error -> Log.v("test", "Hata mesajı: " + error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<>();
            }
        };
        talepSirasi.add(talep);
    }
    protected void parseCountries() {
        try {
            JSONArray jsonArray = new JSONArray(jsonCevabi);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject countryJO = jsonArray.getJSONObject(i);
                JSONObject nameJO = countryJO.getJSONObject("name");
                String ulkeAdi = nameJO.getString("official");

                JSONObject flagsJO = countryJO.getJSONObject("flags");
                String bayrakUrl = flagsJO.getString("png");

                Country country = new Country(ulkeAdi, bayrakUrl);
                countryList.add(country);
            }
            countryAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = countryList.get(position).getName();
        Intent intent = new Intent(this, CountryActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }
}

