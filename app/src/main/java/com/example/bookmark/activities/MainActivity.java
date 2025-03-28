package com.example.bookmark.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * MainActivity is the primary activity in the app. It allows the user to search for books
 * using the Google Books API. The results are displayed in a RecyclerView. It also provides
 * navigation options to the "Marked Books" activity via an options menu.
 *
 * This activity handles:
 * - Search functionality using the Google Books API
 * - Display of search results in a RecyclerView
 * - Navigation to the "Marked Books" activity
 */
public class MainActivity extends AppCompatActivity {

    private RequestQueue mRequestQueue;
    private ArrayList<BookInfo> bookInfoArrayList;
    private ProgressBar progressBar;
    private EditText searchEdt;
    private ImageButton searchBtn;
    private RecyclerView mRecyclerView;
    private BookAdapter bookAdapter;

    /**
     * Called when the activity is created.
     * Initializes UI components, sets up RecyclerView.
     */
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

        // Set up click listener for search button
        searchBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String query = searchEdt.getText().toString().trim();
            if (query.isEmpty()) {
                // Show error if search query is empty
                searchEdt.setError("Please enter search query");
                progressBar.setVisibility(View.GONE);
            } else {
                // Fetch book information if query is valid
                getBooksInfo(query);
            }
        });
    }

    /**
     * Inflates the options menu from the XML resource.
     * @param menu The options menu in which items are placed.
     * @return true to display the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    /**
     * Handles the selection of an item from the options menu.
     * @param item The selected menu item.
     * @return true if the selection was handled, otherwise calls the superclass implementation.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            // Navigate to MainActivity (Search)
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.action_marked_books) {
            // Navigate to MarkedBooksActivity
            Intent intent = new Intent(this, MarkedBooksActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles the selection of an item from the context menu.
     * @param item The selected menu item.
     * @return true if the selection was handled, otherwise calls the superclass implementation.
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 101) {
            if (bookAdapter != null) {
                bookAdapter.shareBook();
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Fetches book information from the Google Books API based on the search query.
     * @param query Search query entered by the user
     */
    private void getBooksInfo(String query) {
        bookInfoArrayList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        mRequestQueue.getCache().clear();

        // URL for Google Books API to search books based on the query
        String url = "https://www.googleapis.com/books/v1/volumes?q=" + query;

        // Create a new JSON request to fetch book data
        JsonObjectRequest booksObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE); // Hide loading indicator after response
                        try {
                            // Parse the response to extract book information
                            JSONArray itemsArray = response.getJSONArray("items");
                            for (int i = 0; i < itemsArray.length(); i++) {
                                JSONObject itemsObj = itemsArray.getJSONObject(i);
                                JSONObject volumeObj = itemsObj.getJSONObject("volumeInfo");

                                // Extract relevant book details
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

                                // Convert authors array to a list of strings
                                ArrayList<String> authorsArrayList = new ArrayList<>();
                                if (authorsArray != null) {
                                    for (int j = 0; j < authorsArray.length(); j++) {
                                        authorsArrayList.add(authorsArray.optString(j));
                                    }
                                }

                                // Add book info to the list
                                bookInfoArrayList.add(new BookInfo(title, subtitle, authorsArrayList, publisher,
                                        publishedDate, description, pageCount, thumbnail, previewLink, infoLink, buyLink));
                            }


                            bookAdapter = new BookAdapter(bookInfoArrayList, MainActivity.this);
                            mRecyclerView.setAdapter(bookAdapter);

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
