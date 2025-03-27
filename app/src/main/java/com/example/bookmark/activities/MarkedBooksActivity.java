package com.example.bookmark.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmark.R;
import com.example.bookmark.adapters.BookAdapter;
import com.example.bookmark.models.BookInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * MarkedBooksActivity displays the list of books marked by the user.
 * It loads the marked books from SharedPreferences, allows the user to sort them
 * based on published date, author, or marking time, and provides bottom navigation
 * to return to the search screen (MainActivity).
 */
public class MarkedBooksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private Spinner spinnerSort;
    private List<BookInfo> markedBooksList;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marked_books);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewMarkedBooks);
        spinnerSort = findViewById(R.id.spinnerSort);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load marked books from SharedPreferences
        markedBooksList = loadMarkedBooks();
        if (markedBooksList.isEmpty()) {
            Log.d("BookMarking", "No books to display.");
            Toast.makeText(this, "No marked books to display.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("BookMarking", "Books loaded: " + markedBooksList.size());
        }

        // Default sorting: Latest Marked (Index 0)
        sortBooks(0);

        // Set up RecyclerView using BookAdapter for consistent behavior
        bookAdapter = new BookAdapter(new ArrayList<>(markedBooksList), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookAdapter);

        // Set up Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_marked_books);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_search) {
                    Intent intent = new Intent(MarkedBooksActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0); // Smooth transition
                    return true;
                }
                return false;
            }
        });

        // Set up Spinner for sorting options
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(spinnerAdapter);

        // Set the default selected spinner position to "Latest Marked" (Index 0)
        spinnerSort.setSelection(0);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                sortBooks(position);
                bookAdapter.updateBooks(markedBooksList);
                bookAdapter.notifyDataSetChanged();
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
        sortBooks(0); // Ensure sorting remains "Latest Marked"
        bookAdapter.updateBooks(markedBooksList);
        bookAdapter.notifyDataSetChanged();
        Log.d("BookMarking", "onResume - Books loaded: " + markedBooksList.size());

        // Ensure the bottom navigation shows the 'Marked Books' tab
        bottomNavigationView.setSelectedItemId(R.id.nav_marked_books);
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
     * Loads marked books from SharedPreferences.
     *
     * @return A List of BookInfo objects representing the marked books.
     */
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

    /**
     * Sorts the list of marked books based on the selected option.
     *
     * @param position The index of the selected sorting option.
     */
    private void sortBooks(int position) {
        switch (position) {
            case 0: // Latest Marked
                Collections.sort(markedBooksList, new Comparator<BookInfo>() {
                    @Override
                    public int compare(BookInfo book1, BookInfo book2) {
                        return Long.compare(book2.getMarkedTime(), book1.getMarkedTime());
                    }
                });
                break;
            case 1: // Oldest Marked
                Collections.sort(markedBooksList, new Comparator<BookInfo>() {
                    @Override
                    public int compare(BookInfo book1, BookInfo book2) {
                        return Long.compare(book1.getMarkedTime(), book2.getMarkedTime());
                    }
                });
                break;
            case 2: // Published Date Ascending
                Collections.sort(markedBooksList, new Comparator<BookInfo>() {
                    @Override
                    public int compare(BookInfo book1, BookInfo book2) {
                        return book1.getPublishedDate().compareTo(book2.getPublishedDate());
                    }
                });
                break;
            case 3: // Published Date Descending
                Collections.sort(markedBooksList, new Comparator<BookInfo>() {
                    @Override
                    public int compare(BookInfo book1, BookInfo book2) {
                        return book2.getPublishedDate().compareTo(book1.getPublishedDate());
                    }
                });
                break;
            case 4: // Author Ascending
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
            case 5: // Author Descending
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
        }
    }
}
