package com.example.bookmark.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * MarkedBooksActivity displays a list of books that have been marked by the user.
 * It uses a RecyclerView with a BookAdapter to show the marked books,
 * which are loaded from SharedPreferences. It also provides sorting options via a Spinner,
 * and listens for a broadcast (ACTION_REFRESH) to refresh its content automatically when
 * a book is marked or unmarked.
 *
 */
public class MarkedBooksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private Spinner spinnerSort;
    private List<BookInfo> markedBooksList;

    /**
     * BroadcastReceiver that listens for ACTION_REFRESH broadcasts to refresh the list
     * of marked books.
     */
    private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MarkedBooksActivity", "Broadcast received: " + intent.getAction());
            refreshMarkedBooks();
        }
    };

    /**
     * Sets up the UI by initializing the RecyclerView, Spinner, and loads the marked books.
     * The default sort order is "Latest Marked". It also sets up the Spinner's item selected
     * listener to re-sort the list when an option is chosen.
     * @param savedInstanceState the saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marked_books);

        // Initialize views
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

        // Default sorting: Latest Marked (Index 0)
        sortBooks(0);

        // Set up RecyclerView using BookAdapter for consistent behavior
        bookAdapter = new BookAdapter(new ArrayList<>(markedBooksList), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookAdapter);

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

    /**
     * Reloads the marked books, applies the default "Latest Marked" sort order,
     * and updates the RecyclerView.
     */
    @Override
    protected void onResume() {
        super.onResume();
        markedBooksList = loadMarkedBooks();
        sortBooks(0); // Ensure sorting remains "Latest Marked"
        bookAdapter.updateBooks(markedBooksList);
        bookAdapter.notifyDataSetChanged();
        Log.d("BookMarking", "onResume - Books loaded: " + markedBooksList.size());
    }

    /**
     * Called when the activity becomes visible.
     * Registers the broadcast receiver to listen for ACTION_REFRESH events.
     *
     */
    @SuppressLint("MissingReceiverExport")
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MarkedBooksActivity", "onStart: Registering refresh receiver");
        IntentFilter filter = new IntentFilter("com.example.bookmark.ACTION_REFRESH");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(refreshReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            //noinspection ReceiverNotExported
            registerReceiver(refreshReceiver, filter);
        }
    }

    /**
     * Unregisters the broadcast receiver.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MarkedBooksActivity", "onStop: Unregistering refresh receiver");
        unregisterReceiver(refreshReceiver);
    }

    /**
     * This method reloads the marked books from SharedPreferences, applies the default
     * sort order, and updates the RecyclerView adapter.
     *
     */
    protected void refreshMarkedBooks() {
        Log.d("MarkedBooksActivity", "Refreshing marked books...");
        markedBooksList = loadMarkedBooks();
        sortBooks(0); // Sort the books after refreshing
        bookAdapter.updateBooks(markedBooksList);
        bookAdapter.notifyDataSetChanged();
    }

    /**
     * Inflates the options menu.
     * @param menu the menu to inflate.
     * @return true if the menu is inflated successfully.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    /**
     * Handles selections on the options menu. Navigates to MainActivity when the search option is selected, or prevents reopening
     * MarkedBooksActivity if it's already open.
     * @param item the selected menu item.
     * @return true if the menu item is handled.
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
            // Prevent opening the MarkedBooksActivity if it's already open
            if (!(this instanceof MarkedBooksActivity)) {
                Intent intent = new Intent(this, MarkedBooksActivity.class);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles context menu selections.
     * @param item the selected context menu item.
     * @return true if the selection is handled.
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
     * Loads the marked books from SharedPreferences.
     * The books are stored as a JSON array under the key "markedBooks" in SharedPreferences.
     * If no books are found, an empty list is returned.
     *
     * @return a List of BookInfo objects representing the marked books.
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
     * Sorts the marked books list based on the provided position.
     * <p>
     * The sorting options are:
     * <ul>
     *     <li>0: Latest Marked (Descending by marked time)</li>
     *     <li>1: Oldest Marked (Ascending by marked time)</li>
     *     <li>2: Published Date Ascending</li>
     *     <li>3: Published Date Descending</li>
     *     <li>4: Author Ascending</li>
     *     <li>5: Author Descending</li>
     * </ul>
     * </p>
     *
     * @param position the index corresponding to the selected sort option.
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
