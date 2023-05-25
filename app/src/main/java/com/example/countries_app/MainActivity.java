package com.example.countries_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final ArrayList<String> baskentListesi = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.countryListView);
        listView.setOnItemClickListener(this);
        ulkeAyikla();
    }
    private void ulkeAyikla() {
        try {
            String json = dosyaOku();
            JSONArray jsonArray = new JSONArray(json);
            List<String> ulkeListesi = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String ulkeAdi = jsonObject.getString("common");
                String baskent = jsonObject.getString("capital");
                baskentListesi.add(baskent);
                ulkeListesi.add(ulkeAdi);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ulkeListesi);
            ListView listView = findViewById(R.id.countryListView);
            listView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @NonNull
    private String dosyaOku() {
        StringBuilder icerik = new StringBuilder();
        try (InputStream is = getAssets().open("ulkeAdlari.json");
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            String satir = br.readLine();
            while (satir != null) {
                icerik.append(satir);
                satir = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return icerik.toString();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String capital = baskentListesi.get(position);
        Intent intent = new Intent(this, CountryActivity.class);
        intent.putExtra("capital", capital);
        startActivity(intent);
    }
}
