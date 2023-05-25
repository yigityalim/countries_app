package com.example.countries_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final List<Country> countryList = new ArrayList<>();
    private String jsonCevabi = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.countryListView);
        listView.setOnItemClickListener(this);
        ulkeAyikla();
    }

    protected void ulkeAyikla() {
        String url = "https://restcountries.com/v3.1/all?fields=name,flags";
        apiTalep(url);
    }

    protected void apiTalep(String url) {
        RequestQueue talepSirasi = Volley.newRequestQueue(this);
        StringRequest talep = new StringRequest(Request.Method.GET, url, response -> {
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

            CountryAdapter adapter = new CountryAdapter(this, countryList);
            ListView listView = findViewById(R.id.countryListView);
            listView.setAdapter(adapter);
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
