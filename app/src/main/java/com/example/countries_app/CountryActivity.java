package com.example.countries_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.palette.graphics.Palette;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.countries_app.databinding.ActivityCountryBinding;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class CountryActivity extends AppCompatActivity {
    private ActivityCountryBinding binding;
    private String url = "https://restcountries.com/v3.1/capital/";
    private String jsonCevabi = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCountryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.backButton.setOnClickListener(v -> onBackPressed());
        String capital = getIntent().getStringExtra("capital");
        url += capital;
        apiTalep(url);
    }

    private int getTextColorForBackground(int backgroundColor) {
        double luminance = (0.299 * Color.red(backgroundColor) + 0.587 * Color.green(backgroundColor) + 0.114 * Color.blue(backgroundColor)) / 255;
        if (luminance > 0.5) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }

    protected void apiTalep(String url) {
        RequestQueue talepSirasi = Volley.newRequestQueue(this);
        StringRequest talep = new StringRequest(Request.Method.GET, url, response -> {
            Log.v("test", "Tüm cevap :" + response);
            jsonCevabi = response;
            jsonAyikla();
        }, error -> Log.v("test", "Hata mesajı :" + error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<>();
            }
        };
        talepSirasi.add(talep);
    }

    protected void jsonAyikla() {
        try {
            JSONArray root = new JSONArray(jsonCevabi);
            JSONObject countryJO = root.getJSONObject(0);
            String flags = countryJO.getJSONObject("flags").getString("png");
            String coatOfArms = countryJO.getJSONObject("coatOfArms").getString("png");

            loadFlag(flags, coatOfArms);

            // Ülke ismi
            JSONArray altSpellings = countryJO.getJSONArray("altSpellings");
            int lastIndex = altSpellings.length() - 1;
            String name = altSpellings.getString(lastIndex);
            binding.countryNameTextView.setText(convertToUTF8(name));

            // Başkent
            JSONArray capital = countryJO.getJSONArray("capital");
            String capitalName = convertToUTF8(capital.getString(0));
            binding.capitalTextView.setText("Başkent: " + convertToUTF8(capitalName));

            // Bölge
            String region = countryJO.getString("region");
            binding.regionTextView.setText("Bölge: " + convertToUTF8(region));

            // Alt bölge
            String subregion = countryJO.getString("subregion");
            binding.subregionTextView.setText("Alt Bölge: " + convertToUTF8(subregion));

            // Dil
            JSONObject languages = countryJO.getJSONObject("languages");
            StringBuilder languagesText = new StringBuilder();
            Iterator<String> diller = languages.keys();
            while (diller.hasNext()) {
                String key = diller.next();
                String value = languages.getString(key);
                languagesText.append(value);
                if (diller.hasNext()) languagesText.append(", ");
            }
            binding.languageTextView.setText("Konuşulan Diller: " + languagesText);

            // para birimi
            JSONObject currencies = countryJO.getJSONObject("currencies");
            Iterator<String> paraBirimleri = currencies.keys();
            while (paraBirimleri.hasNext()) {
                String currencyCode = paraBirimleri.next();
                JSONObject currency = currencies.getJSONObject(currencyCode);
                String currencyName = currency.getString("name");
                String currencySymbol = currency.getString("symbol");

                String currencyInfo = currencyName + " (" + currencySymbol + ")";
                binding.currencyTextView.setText("Para Birimi: " + convertToUTF8(currencyInfo));
            }

            // Nüfus
            long population = countryJO.getLong("population");
            String populationText = String.format(Locale.getDefault(), "%,d", population);
            binding.populationTextView.setText("Nüfus: " + populationText);

            // Zaman Dilimleri
            JSONArray timezones = countryJO.getJSONArray("timezones");
            StringBuilder timezonesText = new StringBuilder();
            for (int i = 0; i < timezones.length(); i++) {
                String timezone = timezones.getString(i);
                timezonesText.append(timezone);
                if (i < timezones.length() - 1) {
                    timezonesText.append(", ");
                }
            }
            binding.timezonesTextView.setText("Zaman Dilimleri: " + timezonesText);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private String convertToUTF8(@NonNull String input) {
        byte[] isoBytes = input.getBytes(StandardCharsets.ISO_8859_1);
        return new String(isoBytes, StandardCharsets.UTF_8);
    }

    public void loadFlag(String png, String coatOfArms) {
        Picasso.get().load(coatOfArms).into(binding.coatOfArmsImageView);
        Picasso.get().load(png).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                binding.flagImageView.setImageBitmap(bitmap);

                Palette.from(bitmap).generate(palette -> {
                    if (palette != null) {
                        Palette.Swatch backgroundSwatch = palette.getDominantSwatch();
                        if (backgroundSwatch != null) {
                            int textColor = getTextColorForBackground(backgroundSwatch.getRgb());
                            binding.backButton.setBackgroundTintList(ColorStateList.valueOf(backgroundSwatch.getRgb()));
                            binding.backButton.setTextColor(textColor);

                            Drawable[] drawables = binding.backButton.getCompoundDrawablesRelative();
                            Drawable startDrawable = drawables[0];
                            if (startDrawable != null) {
                                startDrawable = DrawableCompat.wrap(startDrawable);
                                DrawableCompat.setTint(startDrawable, textColor);
                                binding.backButton.setCompoundDrawablesRelativeWithIntrinsicBounds(startDrawable, null, null, null);
                            }
                        }
                    }
                });
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }
}