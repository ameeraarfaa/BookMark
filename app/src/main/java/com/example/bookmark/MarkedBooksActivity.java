package com.example.bookmark;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MarkedBooksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MarkedBooksAdapter markedBooksAdapter;
    private Spinner spinnerSort;
    private List<MarkedBooks> markedBooksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marked_books);

        recyclerView = findViewById(R.id.recyclerViewMarkedBooks);
        spinnerSort = findViewById(R.id.spinnerSort);

        markedBooksList = loadMarkedBooks();

        // Set up RecyclerView with dynamic grid layout
        int numberOfColumns = calculateNoOfColumns();
        markedBooksAdapter = new MarkedBooksAdapter(markedBooksList, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setAdapter(markedBooksAdapter);

        // Set up Spinner for sorting options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Assume spinner positions:
                // 0: Published Date Ascending
                // 1: Published Date Descending
                // 2: Author Ascending
                // 3: Author Descending
                if (position == 0) {
                    Collections.sort(markedBooksList, new Comparator<MarkedBooks>() {
                        @Override
                        public int compare(MarkedBooks book1, MarkedBooks book2) {
                            return book1.getPublishedDate().compareTo(book2.getPublishedDate());
                        }
                    });
                } else if (position == 1) {
                    Collections.sort(markedBooksList, new Comparator<MarkedBooks>() {
                        @Override
                        public int compare(MarkedBooks book1, MarkedBooks book2) {
                            return book2.getPublishedDate().compareTo(book1.getPublishedDate());
                        }
                    });
                } else if (position == 2) {
                    Collections.sort(markedBooksList, new Comparator<MarkedBooks>() {
                        @Override
                        public int compare(MarkedBooks book1, MarkedBooks book2) {
                            return book1.getAuthor().compareTo(book2.getAuthor());
                        }
                    });
                } else if (position == 3) {
                    Collections.sort(markedBooksList, new Comparator<MarkedBooks>() {
                        @Override
                        public int compare(MarkedBooks book1, MarkedBooks book2) {
                            return book2.getAuthor().compareTo(book1.getAuthor());
                        }
                    });
                }
                markedBooksAdapter.updateBooks(markedBooksList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
    }

    private List<MarkedBooks> loadMarkedBooks() {
        List<MarkedBooks> markedBooksList = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("MarkedBooksPrefs", MODE_PRIVATE);
        String json = sharedPreferences.getString("markedBooks", "[]");

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject bookObject = jsonArray.getJSONObject(i);
                String title = bookObject.getString("title");
                String author = bookObject.getString("author");
                // Use "publishedDate" key instead of "year"
                String publishedDate = bookObject.getString("publishedDate");
                String thumbnailUrl = bookObject.getString("thumbnailUrl");

                MarkedBooks book = new MarkedBooks(title, author, publishedDate, thumbnailUrl);
                markedBooksList.add(book);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return markedBooksList;
    }

    private int calculateNoOfColumns() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int itemWidth = getResources().getDimensionPixelSize(R.dimen.grid_item_width);
        return Math.max(1, screenWidth / itemWidth);
    }
}
