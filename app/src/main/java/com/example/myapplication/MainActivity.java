package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SearchView;
import java.io.IOException;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public CheckBox[] checkBoxes = new CheckBox[8];
    public EditText keywordInput;
    public Button button;
    public GoogleQuery google;
    public MainActivity() {
        super();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keywordInput = findViewById(R.id.keyword);

        checkBoxes[0] = findViewById(R.id.checkBox);
        checkBoxes[1] = findViewById(R.id.checkBox2);
        checkBoxes[2] = findViewById(R.id.checkBox3);
        checkBoxes[3] = findViewById(R.id.checkBox4);
        checkBoxes[4] = findViewById(R.id.checkBox5);
        checkBoxes[5] = findViewById(R.id.checkBox6);
        checkBoxes[6] = findViewById(R.id.checkBox7);
        checkBoxes[7] = findViewById(R.id.checkBox8);

        button = (Button) findViewById(R.id.button);

        for (int i = 0; i < checkBoxes.length; i++) {
            final int index = i;
            checkBoxes[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        updateKeywordInput(checkBoxes[index].getText().toString());
                    } else {
                        removeKeywordFromInput(checkBoxes[index].getText().toString());
                    }
                }
            });
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = keywordInput.getText().toString().trim();
                if (!keyword.isEmpty()) {
                    try {
                        performSearch(keyword);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a keyword", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void performSearch(String keyword) throws IOException {
        String apiKey = "AIzaSyBuklRqxCBbXGoBcYeJ2Mo-cNet7zPPCRY"; // Replace with your API key
        String customSearchEngineID = "b6b062cffaef84cff"; // Replace with your custom search engine ID
        String searchUrl = "https://www.googleapis.com/customsearch/v1?key=" + apiKey + "&cx=" + customSearchEngineID + "&q=" + keyword;

        try {
            GoogleQuery google = new GoogleQuery(keyword);
            HashMap<String, String> query = google.query();
            if (query == null) {
                System.out.print("query == null");
            } else {
                String[][] s = new String[query.size()][2];
                int num = 0;
                for (Map.Entry<String, String> entry : query.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    s[num][0] = key;
                    s[num][1] = value;
                    num++;
                }
                // Set the query attribute in the request
            }
            displaySearchResults(query);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error in performing search", Toast.LENGTH_SHORT).show();
        }

        new HttpRequestTask().execute(searchUrl);
    }

    private class HttpRequestTask extends AsyncTask<String, Void, String> {

        private HttpRequestTask() throws IOException {
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
                response = "ERROR";
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("ERROR")) {
                Toast.makeText(MainActivity.this, "Error in performing search", Toast.LENGTH_SHORT).show();
            } else {
                handleResponse(result);
            }

        }
    }

    private void handleResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray items = jsonResponse.getJSONArray("items");

            // Create a StringBuilder to store formatted results
            StringBuilder formattedResults = new StringBuilder();

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String title = item.getString("title");
                String link = item.getString("link");

                formattedResults.append("<a href='").append(link).append("'>").append(title).append("</a><br>");
            }

            TextView resultTextView = findViewById(R.id.textView2);
            resultTextView.setText(Html.fromHtml(formattedResults.toString()));

            resultTextView.setMovementMethod(LinkMovementMethod.getInstance());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateKeywordInput(String text) {
        String currentText = keywordInput.getText().toString().trim();
        if (!currentText.isEmpty()) {
            currentText += " " + text; // Add a space before appending the new text
        } else {
            currentText = text;
        }
        keywordInput.setText(currentText);
    }

    private void removeKeywordFromInput(String text) {
        String currentText = keywordInput.getText().toString().trim(); // Use getText() method to retrieve text from EditText
        if (currentText.contains(text)) {
            currentText = currentText.replace(text, "").trim();
            keywordInput.setText(currentText); // Update the EditText with modified text
        }
    }

    private void displaySearchResults(HashMap<String, String> results) {
        for (String title : results.keySet()) {
            String link = results.get(title);
            Log.d("Search Results", title + " - " + link);
        }
    }
}
