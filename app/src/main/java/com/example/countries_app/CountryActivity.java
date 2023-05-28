package com.example.countries_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.palette.graphics.Palette;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.countries_app.databinding.ActivityCountryBinding;
import com.example.countries_app.enums.Regions;
import com.example.countries_app.enums.Subregions;
import com.example.countries_app.enums.Weeks;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class CountryActivity extends AppCompatActivity {
    private ActivityCountryBinding binding;
    private String url = "https://restcountries.com/v3.1/name/";
    private String jsonCevabi = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCountryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.backButton.setOnClickListener(v -> onBackPressed());
        // binding.scrollView.getBackground().setAlpha(76);
        String name = getIntent().getStringExtra("name");
        url += name;
        apiTalep(url);
    }

    protected int getTextColorForBackground(int backgroundColor) {
        double luminance = (0.299 * Color.red(backgroundColor) + 0.587 * Color.green(backgroundColor) + 0.114 * Color.blue(backgroundColor)) / 255;
        if (luminance > 0.5) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }

    protected void apiTalep(String url) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.countryLinearLayout.setVisibility(View.GONE);
        RequestQueue talepSirasi = Volley.newRequestQueue(this);
        StringRequest talep = new StringRequest(Request.Method.GET, url, response -> {
            Log.v("cevap", "Tüm cevap :" + response);
            binding.progressBar.setVisibility(View.GONE);
            binding.countryLinearLayout.setVisibility(View.VISIBLE);
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

            // Bayrak ve armalar
            String flags = countryJO.getJSONObject("flags").getString("png");
            String coatOfArms = countryJO.getJSONObject("coatOfArms").getString("png");
            loadFlag(flags, coatOfArms);

            // Ülke ismi
            JSONObject nameObject = countryJO.getJSONObject("name");
            if (nameObject.has("nativeName")) {
                JSONObject nativeNames = nameObject.getJSONObject("nativeName");
                Iterator<String> ulkeIsimleri = nativeNames.keys();
                if (ulkeIsimleri.hasNext()) {
                    String key = ulkeIsimleri.next();
                    JSONObject nativeName = nativeNames.getJSONObject(key);
                    if (nativeName.has("official")) {
                        String ulkeIsmi = nativeName.getString("official");
                        binding.countryNameTextView.setText(convertToUTF8(ulkeIsmi));
                    }
                }

                String engName = nameObject.getString("common");
                binding.commonNameTextView.setText(convertToUTF8(engName));
            } else {
                if (nameObject.has("common")) {
                    String ulkeIsmi = nameObject.getString("common");
                    binding.countryNameTextView.setText(convertToUTF8(ulkeIsmi));
                }
            }

            // Başkent
            if (countryJO.has("capital")) {
                JSONArray capital = countryJO.getJSONArray("capital");
                if (capital.length() > 0) {
                    String capitalName = convertToUTF8(capital.getString(capital.length() - 1));
                    binding.capitalTextView.setText(convertToUTF8(capitalName));
                }
            }

            // Bölge
            String region = countryJO.getString("region");
            binding.regionTextView.setText(convertToUTF8(Regions.valueOf(region.toUpperCase()).getRegionName()));

            // Alt bölge
            if (countryJO.has("subregion")) {
                String subregion = countryJO.getString("subregion").toUpperCase().replace(" ", "_");
                binding.subregionTextView.setText(Subregions.valueOf(subregion).getSubregionName());
            }

            // Dil
            if (countryJO.has("languages")) {
                JSONObject languages = countryJO.getJSONObject("languages");
                StringBuilder languagesText = new StringBuilder();
                Iterator<String> diller = languages.keys();
                while (diller.hasNext()) {
                    String key = diller.next();
                    String value = languages.getString(key);
                    languagesText.append(value);
                    if (diller.hasNext()) languagesText.append(", ");
                }
                if (languages.length() == 1) {
                    binding.languageTitleTextView.setText("Konuşulan Dil: ");
                } else {
                    binding.languageLinearLayout.setOrientation(LinearLayout.VERTICAL);
                    binding.languageTitleTextView.setText("Konuşulan Diller: ");
                }
                binding.languageTextView.setText(languagesText);
            }

            // para birimi
            if (countryJO.has("currencies")) {
                JSONObject currencies = countryJO.getJSONObject("currencies");
                Iterator<String> paraBirimleri = currencies.keys();
                while (paraBirimleri.hasNext()) {
                    String currencyCode = paraBirimleri.next();
                    JSONObject currency = currencies.getJSONObject(currencyCode);
                    String currencyName = currency.getString("name");
                    String currencySymbol = currency.getString("symbol");

                    String currencyInfo = currencyName + " (" + currencySymbol + ")";
                    binding.currencyTextView.setText(convertToUTF8(currencyInfo));
                }
            }

            // Nüfus
            long population = countryJO.getLong("population");
            String populationText = String.format(Locale.getDefault(), "%,d", population);
            binding.populationTextView.setText(populationText);

            // Zaman Dilimleri
            JSONArray timezones = countryJO.getJSONArray("timezones");
            StringBuilder timezonesText = new StringBuilder();
            if (timezones.length() == 1) {
                binding.timezonesTitleTextView.setText("Zaman Dilimi: ");
            } else {
                binding.timezonesTitleTextView.setText("Zaman Dilimleri: ");
                binding.timezonesLinearLayout.setOrientation(LinearLayout.VERTICAL);
            }
            for (int i = 0; i < timezones.length(); i++) {
                String timezone = timezones.getString(i);
                timezonesText.append(timezone);
                if (i < timezones.length() - 1) {
                    timezonesText.append(", ");
                }
            }
            binding.timezonesTextView.setText(timezonesText);

            // haftanın başlangıcı
            if (countryJO.has("startOfWeek")) {
                String startOfWeek = countryJO.getString("startOfWeek");
                Weeks week = Weeks.valueOf(startOfWeek.toUpperCase(Locale.getDefault()));
                String startOfWeekText = week.getDay();
                binding.startOfWeekTextView.setText(convertToUTF8(startOfWeekText));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    protected String convertToUTF8(@NonNull String input) {
        return new String(input.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }


    protected void loadFlag(String png, String coatOfArms) {
        Picasso.get().load(coatOfArms).into(binding.coatOfArmsImageView);
        Picasso.get().load(png).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                binding.flagImageView.setImageBitmap(bitmap);
                float ratio = (float) bitmap.getWidth() / bitmap.getHeight();
                if (ratio >= 1.0f) {
                    binding.flagImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    binding.flagImageView.setScaleType(ImageView.ScaleType.FIT_START);
                }

                Palette.from(bitmap).generate(palette -> {
                    if (palette != null) {
                        Palette.Swatch backgroundSwatch = palette.getDominantSwatch();
                        if (backgroundSwatch != null) {
                            int textColor = getTextColorForBackground(backgroundSwatch.getRgb());
                            int backgroundColor = ColorUtils.blendARGB(backgroundSwatch.getRgb(), textColor, 0.2f);

                            updateButtonAppearance(backgroundSwatch.getRgb(), textColor);
                            updateStatusBarColor(backgroundColor);
                            updateScrollViewBackground(backgroundColor);
                            setTextColorForBackground(backgroundSwatch.getRgb());
                        }
                    }
                });
            }

            private void updateButtonAppearance(int backgroundColor, int textColor) {
                binding.backButton.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
                binding.backButton.setTextColor(textColor);

                Drawable[] drawables = binding.backButton.getCompoundDrawablesRelative();
                Drawable startDrawable = drawables[0];
                if (startDrawable != null) {
                    startDrawable = DrawableCompat.wrap(startDrawable);
                    DrawableCompat.setTint(startDrawable, textColor);
                    binding.backButton.setCompoundDrawablesRelativeWithIntrinsicBounds(startDrawable, null, null, null);
                }
            }

            private void updateStatusBarColor(int color) {
                getWindow().setStatusBarColor(color);
            }

            private void updateScrollViewBackground(int color) {
                binding.scrollView.setBackgroundColor(color);
            }

            private void setTextColorForBackground(int backgroundColor) {
                int textColor = getTextColorForBackground(backgroundColor);

                TextView[] textViews = {
                        binding.countryNameTextView,
                        binding.commonNameTextView,
                        binding.capitalTextView,
                        binding.regionTitleTextView,
                        binding.regionTextView,
                        binding.subregionTitleTextView,
                        binding.subregionTextView,
                        binding.languageTitleTextView,
                        binding.languageTextView,
                        binding.currencyTitleTextView,
                        binding.currencyTextView,
                        binding.populationTitleTextView,
                        binding.populationTextView,
                        binding.timezonesTitleTextView,
                        binding.timezonesTextView,
                        binding.startOfWeekTitleTextView,
                        binding.startOfWeekTextView
                };

                for (TextView textView : textViews) {
                    textView.setTextColor(textColor);
                }
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