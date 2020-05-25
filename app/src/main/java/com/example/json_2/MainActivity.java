package com.example.json_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // BUILDING WHAT'S THE WEATHER APP

    TextView textWeather, textTemperature, textDate, textRange;
    ProgressBar progressBar;

    public class DownloadJSON extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls) {
            String jsonResult = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(inputStream);
                int data = streamReader.read();

                while(data != -1)
                {
                    char currentChar = (char) data;
                    jsonResult += currentChar;
                    data = streamReader.read();
                }
                return jsonResult;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String message = "";

                String infoWeather = jsonObject.getString("weather");

                JSONArray arrayWeather = new JSONArray(infoWeather);
                for(int i = 0; i < arrayWeather.length(); i++)
                {
                    JSONObject object = arrayWeather.getJSONObject(i);

                    String main = object.getString("main");
                    message += main + " ";
                    textWeather.setText(message);
                }


                String infoTemperature = jsonObject.getString("main");

                // BECAUSE 'MAIN' IS A OBJECT AND NOT ARRAY
                JSONObject objectTemperature = new JSONObject(infoTemperature);

                String temp = objectTemperature.getString("temp");
                String feels_like = objectTemperature.getString("feels_like");
                String temp_min = objectTemperature.getString("temp_min");
                String temp_max = objectTemperature.getString("temp_max");

                textTemperature.setText(temp + "\u00B0");
                textRange.setText(temp_max + "\u00B0" + "/" + temp_min + "\u00B0" + "  Feels Like " + feels_like + "\u00B0");

                DateFormat dateFormat = new SimpleDateFormat("MMM dd, HH:mm");
                Date date = new Date();
                textDate.setText(dateFormat.format(date));

                progressBar.setVisibility(View.GONE);
                textDate.setVisibility(View.VISIBLE);
                textTemperature.setVisibility(View.VISIBLE);
                textRange.setVisibility(View.VISIBLE);
                textWeather.setVisibility(View.VISIBLE);
            }
            catch (Exception e) {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
                Snackbar.make(findViewById(R.id.constraintLayout), "UNABLE TO FETCH INFORMATION", BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textWeather = findViewById(R.id.textWeather);
        textRange = findViewById(R.id.textRange);
        textTemperature = findViewById(R.id.textTemperature);
        textDate = findViewById(R.id.textDate);
        progressBar = findViewById(R.id.progressBar);
        final EditText inputCity = findViewById(R.id.inputCity);
        final FloatingActionButton btnGetWeather = findViewById(R.id.btnGetWeather);

        btnGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textDate.setVisibility(View.GONE);
                textTemperature.setVisibility(View.GONE);
                textRange.setVisibility(View.GONE);
                textWeather.setVisibility(View.GONE);

                String city = inputCity.getText().toString();

                if(city.isEmpty())
                    Snackbar.make(findViewById(R.id.constraintLayout), "ENTER CITY NAME!", BaseTransientBottomBar.LENGTH_SHORT).show();

                else {
                    progressBar.setVisibility(View.VISIBLE);
                    DownloadJSON downloadJSON = new DownloadJSON();
                    downloadJSON.execute("https://openweathermap.org/data/2.5/weather?q=" + city + "&appid=439d4b804bc8187953eb36d2a8c26a02");
                }

                //HIDES KEYBOARD ON BUTTON CLICK
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(inputCity.getWindowToken(), 0);
            }
        });
    }
}
