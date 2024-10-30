package com.example.asyncapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private Button buttonFetchData;
    private ProgressBar progressBar;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        buttonFetchData = findViewById(R.id.buttonFetchData);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);

        buttonFetchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchDataTask().execute("https://jsonplaceholder.typicode.com/photos/1");
            }
        });
    }

    private class FetchDataTask extends AsyncTask<String, Void, String> {

        private String imageUrl;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            buttonFetchData.setEnabled(false);
            textView.setText("Fetching data...");
        }

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                // Parse JSON for title and URL
                JSONObject jsonObject = new JSONObject(result.toString());
                imageUrl = jsonObject.getString("url");
                return jsonObject.getString("title");
            } catch (Exception e) {
                e.printStackTrace();
                return "Error fetching data!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            buttonFetchData.setEnabled(true);
            textView.setText(result);

            // Load image using Picasso
            if (imageUrl != null) {
                Picasso.get().load(imageUrl).into(imageView);
            }
        }
    }
}
