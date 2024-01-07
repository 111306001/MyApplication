package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class  MainActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private Button buttonSearch;
    private TextView textViewResults;
    private RequestQueue requestQueue;
    CheckBox[] checkBoxes = new CheckBox[8];
    StringBuilder resultText = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        textViewResults = findViewById(R.id.textViewResults);
        requestQueue = Volley.newRequestQueue(this);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = editTextSearch.getText().toString().trim();
                if (!keyword.isEmpty()) {
                    performSearch(keyword);
                }
            }
        });

        checkBoxes[0] = findViewById(R.id.checkBox);
        checkBoxes[1] = findViewById(R.id.checkBox2);
        checkBoxes[2] = findViewById(R.id.checkBox3);
        checkBoxes[3] = findViewById(R.id.checkBox4);
        checkBoxes[4] = findViewById(R.id.checkBox5);
        checkBoxes[5] = findViewById(R.id.checkBox6);
        checkBoxes[6] = findViewById(R.id.checkBox7);
        checkBoxes[7] = findViewById(R.id.checkBox8);

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

    }

    private void performSearch(String keyword) {
        String apiKey = "AIzaSyBuklRqxCBbXGoBcYeJ2Mo-cNet7zPPCRY"; // Replace with your API key
        String customSearchEngineID = "b6b062cffaef84cff"; // Replace with your custom search engine ID
        String searchUrl = "https://www.googleapis.com/customsearch/v1?key=" + apiKey +
                "&cx=" + customSearchEngineID + "&q=" + keyword;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, searchUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                         Log.d("SearchResponse", response.toString()); // Log the response for debugging
                        handleResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        textViewResults.setText("Error fetching results: " + error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void handleResponse(JSONObject response) {
        try {
            JSONArray items = response.getJSONArray("items");
            StringBuilder searchResults = new StringBuilder();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String title = item.getString("title");
                String link = item.getString("link");
                searchResults.append("<a href='").append(link).append("'>").append(title).append("</a><br>");
            }
            if (searchResults.length() > 0) {
                textViewResults.setText(android.text.Html.fromHtml(searchResults.toString()));
                textViewResults.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
            } else {
                textViewResults.setText("No results found");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            textViewResults.setText("Error parsing results");
        }
    }
    private void updateKeywordInput(String text) {
        resultText.setLength(0);

        resultText.append(editTextSearch.getText().toString().trim());
        if (!resultText.toString().isEmpty()) {
            resultText.append(" ");
        }

        resultText.append(text);
        editTextSearch.setText(resultText.toString().trim());
    }

    private void removeKeywordFromInput(String text) {
        String currentText = editTextSearch.getText().toString().trim();
        if (currentText.contains(text)) {
            currentText = currentText.replace(text, "").trim();
            editTextSearch.setText(currentText);
        }
    }
}
