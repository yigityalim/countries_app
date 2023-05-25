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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final List<String> ulkeListesi = new ArrayList<>();
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
        String url = "https://restcountries.com/v3.1/all?fields=name";
        apiTalep(url);
    }

    protected void apiTalep(String url) {
        RequestQueue talepSirasi = Volley.newRequestQueue(this);
        StringRequest talep = new StringRequest(Request.Method.GET, url, response -> {
            Log.v("test", "Tüm cevap :" + response);
            jsonCevabi = response;
            parseCountries();
        }, error -> Log.v("test", "Hata mesajı :" + error.toString())) {
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
                String ulke = countryJO.getJSONObject("name").getString("common");
                ulkeListesi.add(convertToUTF8(ulke));
            }
            ulkeListesi.sort(String::compareToIgnoreCase);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ulkeListesi);
            ListView listView = findViewById(R.id.countryListView);
            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private String convertToUTF8(@NonNull String input) {
        byte[] isoBytes = input.getBytes(StandardCharsets.ISO_8859_1);
        return new String(isoBytes, StandardCharsets.UTF_8);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = ulkeListesi.get(position);
        Intent intent = new Intent(this, CountryActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }
}
