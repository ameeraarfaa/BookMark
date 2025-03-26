package com.example.bookmark.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmark.R;
import com.example.bookmark.adapters.MarkedBooksAdapter;
import com.example.bookmark.models.BookInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MarkedBooksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MarkedBooksAdapter markedBooksAdapter;
    private Spinner spinnerSort;
    private List<BookInfo> markedBooksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marked_books);

        recyclerView = findViewById(R.id.recyclerViewMarkedBooks);
        spinnerSort = findViewById(R.id.spinnerSort);

        // Load marked books from SharedPreferences
        markedBooksList = loadMarkedBooks();
        if (markedBooksList.isEmpty()) {
            Log.d("BookMarking", "No books to display.");
            Toast.makeText(this, "No marked books to display.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("BookMarking", "Books loaded: " + markedBooksList.size());
        }

        // Use LinearLayoutManager for a simple vertical list
        markedBooksAdapter = new MarkedBooksAdapter(markedBooksList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(markedBooksAdapter);

        // Set up Spinner for sorting options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                sortBooks(position);
                markedBooksAdapter.updateBooks(markedBooksList);
                markedBooksAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload marked books when the activity resumes
        markedBooksList = loadMarkedBooks();
        markedBooksAdapter.updateBooks(markedBooksList);
        markedBooksAdapter.notifyDataSetChanged();
        Log.d("BookMarking", "onResume - Books loaded: " + markedBooksList.size());
    }

    // Load marked books from SharedPreferences
    private List<BookInfo> loadMarkedBooks() {
        SharedPreferences preferences = getSharedPreferences("MarkedBooksPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("markedBooks", "[]");
        Type type = new TypeToken<List<BookInfo>>() {}.getType();
        List<BookInfo> bookList = gson.fromJson(json, type);
        if (bookList == null) {
            bookList = new ArrayList<>();
        }
        return bookList;
    }

    // Sort books based on the selected option in Spinner
    private void sortBooks(int position) {
        switch (position) {
            case 0: // Published Date Ascending
                Collections.sort(markedBooksList, new Comparator<BookInfo>() {
                    @Override
                    public int compare(BookInfo book1, BookInfo book2) {
                        return book1.getPublishedDate().compareTo(book2.getPublishedDate());
                    }
                });
                break;
            case 1: // Published Date Descending
                Collections.sort(markedBooksList, new Comparator<BookInfo>() {
                    @Override
                    public int compare(BookInfo book1, BookInfo book2) {
                        return book2.getPublishedDate().compareTo(book1.getPublishedDate());
                    }
                });
                break;
            case 2: // Author Ascending
                Collections.sort(markedBooksList, new Comparator<BookInfo>() {
                    @Override
                    public int compare(BookInfo book1, BookInfo book2) {
                        String author1 = (book1.getAuthors() != null && !book1.getAuthors().isEmpty())
                                ? book1.getAuthors().get(0) : "";
                        String author2 = (book2.getAuthors() != null && !book2.getAuthors().isEmpty())
                                ? book2.getAuthors().get(0) : "";
                        return author1.compareTo(author2);
                    }
                });
                break;
            case 3: // Author Descending
                Collections.sort(markedBooksList, new Comparator<BookInfo>() {
                    @Override
                    public int compare(BookInfo book1, BookInfo book2) {
                        String author1 = (book1.getAuthors() != null && !book1.getAuthors().isEmpty())
                                ? book1.getAuthors().get(0) : "";
                        String author2 = (book2.getAuthors() != null && !book2.getAuthors().isEmpty())
                                ? book2.getAuthors().get(0) : "";
                        return author2.compareTo(author1);
                    }
                });
                break;
            case 4: // Oldest Marked
                Collections.sort(markedBooksList, new Comparator<BookInfo>() {
                    @Override
                    public int compare(BookInfo book1, BookInfo book2) {
                        return Long.compare(book1.getMarkedTime(), book2.getMarkedTime());
                    }
                });
                break;
            case 5: // Latest Marked
                Collections.sort(markedBooksList, new Comparator<BookInfo>() {
                    @Override
                    public int compare(BookInfo book1, BookInfo book2) {
                        return Long.compare(book2.getMarkedTime(), book1.getMarkedTime());
                    }
                });
                break;
        }
    }
}
