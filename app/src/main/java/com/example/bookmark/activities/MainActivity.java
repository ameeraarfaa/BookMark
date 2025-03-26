package com.example.bookmark.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookmark.adapters.BookAdapter;
import com.example.bookmark.models.BookInfo;
import com.example.bookmark.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RequestQueue mRequestQueue;
    private ArrayList<BookInfo> bookInfoArrayList;
    private ProgressBar progressBar;
    private EditText searchEdt;
    private ImageButton searchBtn;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        progressBar = findViewById(R.id.idLoadingPB);
        searchEdt = findViewById(R.id.idEdtSearchBooks);
        searchBtn = findViewById(R.id.idBtnSearch);
        mRecyclerView = findViewById(R.id.idRVBooks);

        // Set up RecyclerView layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // Set up Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_search);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_marked_books) {
                Intent intent = new Intent(MainActivity.this, MarkedBooksActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Smooth transition
                return true;
            }
            return false;
        });

        // Set up click listener for search button
        searchBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String query = searchEdt.getText().toString().trim();
            if (query.isEmpty()) {
                searchEdt.setError("Please enter search query");
                progressBar.setVisibility(View.GONE);
            } else {
                getBooksInfo(query);
            }
        });
    }

    private void getBooksInfo(String query) {
        bookInfoArrayList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        mRequestQueue.getCache().clear(); // Ensure fresh data

        String url = "https://www.googleapis.com/books/v1/volumes?q=" + query;

        JsonObjectRequest booksObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONArray itemsArray = response.getJSONArray("items");
                            for (int i = 0; i < itemsArray.length(); i++) {
                                JSONObject itemsObj = itemsArray.getJSONObject(i);
                                JSONObject volumeObj = itemsObj.getJSONObject("volumeInfo");

                                String title = volumeObj.optString("title");
                                String subtitle = volumeObj.optString("subtitle");
                                JSONArray authorsArray = volumeObj.optJSONArray("authors");
                                String publisher = volumeObj.optString("publisher");
                                String publishedDate = volumeObj.optString("publishedDate");
                                String description = volumeObj.optString("description");
                                int pageCount = volumeObj.optInt("pageCount");
                                JSONObject imageLinks = volumeObj.optJSONObject("imageLinks");
                                String thumbnail = (imageLinks != null) ? imageLinks.optString("thumbnail") : "";
                                String previewLink = volumeObj.optString("previewLink");
                                String infoLink = volumeObj.optString("infoLink");
                                JSONObject saleInfoObj = itemsObj.optJSONObject("saleInfo");
                                String buyLink = (saleInfoObj != null) ? saleInfoObj.optString("buyLink") : "";

                                ArrayList<String> authorsArrayList = new ArrayList<>();
                                if (authorsArray != null) {
                                    for (int j = 0; j < authorsArray.length(); j++) {
                                        authorsArrayList.add(authorsArray.optString(j));
                                    }
                                }

                                bookInfoArrayList.add(new BookInfo(title, subtitle, authorsArrayList, publisher,
                                        publishedDate, description, pageCount, thumbnail, previewLink, infoLink, buyLink));
                            }

                            // Set adapter for RecyclerView
                            BookAdapter adapter = new BookAdapter(bookInfoArrayList, MainActivity.this);
                            mRecyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "No Data Found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        mRequestQueue.add(booksObjRequest);
    }
}
