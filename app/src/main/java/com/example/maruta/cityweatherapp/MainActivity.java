package com.example.maruta.cityweatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private EditText cityText;
    private Button btn;
    private TextView weatherText;
    private String citySearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();

        btn.setOnClickListener((View event) -> {
            try {
                checkCity();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


    }

    private void checkCity() throws ExecutionException, InterruptedException {

        citySearch = cityText.getText().toString();

        String weatherWebsite = "http://api.openweathermap.org/data/2.5/weather?";
        String cityCmd = "q=";
        String apiKey = "&appid=7e33175539f5d5e31f8df6b25f241db9";

        WeatherDownload app = new WeatherDownload();

        app.execute(weatherWebsite + cityCmd + citySearch + apiKey).get();

    }

    private void initUi() {

        cityText = findViewById(R.id.cityText);
        btn = findViewById(R.id.btn);
        weatherText = findViewById(R.id.weatherText);

    }

    public class WeatherDownload extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String rs = "";

            try{

                URL url = new URL(strings[0]);
                HttpURLConnection urlCon = (HttpURLConnection)url.openConnection();

                InputStream in = urlCon.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){

                    char crr = (char)data;

                    rs += crr;

                    data = reader.read();

                }

                return rs;

            }catch (Exception ex){
                ex.printStackTrace();
                return "";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                String message = "", main = "", desc = "";

               JSONObject weather = new JSONObject(result);

                String temp = weather.getString("weather");

                JSONArray arr = new JSONArray(temp);

                for(int i = 0; i < arr.length(); i++){

                    JSONObject part = arr.getJSONObject(i);

                    main = part.getString("main");
                    desc = part.getString("description");

                    if (!main.equals("") && !desc.equals("")) {
                        message = main + desc;
                    }
                }

                if(!message.equals("")){

                    weatherText.setText("Weather:" + message);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
