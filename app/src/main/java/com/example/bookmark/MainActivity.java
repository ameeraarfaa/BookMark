package com.example.bookmark;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * MainActivity handles user interactions for searching books using the Google Books API.
 * It fetches book details based on the user's query and displays them in a RecyclerView.
 */
public class MainActivity extends AppCompatActivity {

    // Declare required variables
    private RequestQueue mRequestQueue;
    private ArrayList<BookInfo> bookInfoArrayList;
    private ProgressBar progressBar;
    private EditText searchEdt;
    private ImageButton searchBtn;

    /**
     * Called when the activity is first created.
     * Initializes views and sets up click listeners.
     * @param savedInstanceState Saved instance state for restoring activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Ensure layout file is correctly referenced

        // Initialize UI components
        progressBar = findViewById(R.id.idLoadingPB);
        searchEdt = findViewById(R.id.idEdtSearchBooks);
        searchBtn = findViewById(R.id.idBtnSearch);

        // Set up click listener for search button
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {
                progressBar.setVisibility(View.VISIBLE);
                if (searchEdt.getText().toString().isEmpty()) {
                    searchEdt.setError("Please enter search query");
                    return;
                }
                // Call API to fetch book details
                getBooksInfo(searchEdt.getText().toString());
            }
        });
    }

    /**
     * Fetches book information from the Google Books API based on the search query.
     *
     * @param query The search term entered by the user.
     */
    private void getBooksInfo(String query) {
        bookInfoArrayList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(MainActivity.this);

        // Clear cache to ensure fresh data retrieval
        mRequestQueue.getCache().clear();

        // Construct the API URL for fetching book details
        String url = "https://www.googleapis.com/books/v1/volumes?q=" + query;

        // Initialize a new request queue
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        // Create a JSON request for book data
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

                                // Extract book details
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

                                // Convert authors JSON array to ArrayList
                                ArrayList<String> authorsArrayList = new ArrayList<>();
                                if (authorsArray != null) {
                                    for (int j = 0; j < authorsArray.length(); j++) {
                                        authorsArrayList.add(authorsArray.optString(j));
                                    }
                                }

                                // Create a BookInfo object and add to the list
                                BookInfo bookInfo = new BookInfo(title, subtitle, authorsArrayList, publisher,
                                        publishedDate, description, pageCount, thumbnail,
                                        previewLink, infoLink, buyLink);
                                bookInfoArrayList.add(bookInfo);
                            }

                            // Set up RecyclerView with book data
                            RecyclerView mRecyclerView = findViewById(R.id.idRVBooks);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this,
                                    RecyclerView.VERTICAL, false);
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            BookAdapter adapter = new BookAdapter(bookInfoArrayList, MainActivity.this);
                            mRecyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "No Data Found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the queue
        queue.add(booksObjRequest);
    }
}

