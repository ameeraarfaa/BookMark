package com.example.bookmark.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookmark.models.BookInfo;
import com.example.bookmark.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * BookDetails is an activity that displays detailed information about a selected book.
 * It retrieves book details from an Intent and populates the UI with this data.
 * It also provides options to preview or purchase the book via external links.
 */
public class BookDetails extends AppCompatActivity {

    // Variables for book details
    String title, subtitle, publisher, publishedDate, description, thumbnail, previewLink, infoLink, buyLink;
    int pageCount;
    private ArrayList<String> authors;

    // UI components
    TextView titleTV, subtitleTV, publisherTV, descTV, pageTV, publishDateTV;
    Button previewBtn, buyBtn, markBtn;
    private ImageView bookIV;

    /**
     * Called when the activity is first created. This method initializes UI components,
     * retrieves book data from the intent, and sets up event listeners.
     * @param savedInstanceState If the activity is being re-initialized after being previously shut down,
     *                           this contains the most recent data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        // Initializing UI components
        titleTV = findViewById(R.id.idTVTitle);
        subtitleTV = findViewById(R.id.idTVSubTitle);
        publisherTV = findViewById(R.id.idTVpublisher);
        descTV = findViewById(R.id.idTVDescription);
        pageTV = findViewById(R.id.idTVNoOfPages);
        publishDateTV = findViewById(R.id.idTVPublishDate);
        previewBtn = findViewById(R.id.idBtnPreview);
        buyBtn = findViewById(R.id.idBtnBuy);
        markBtn = findViewById(R.id.idBtnMark); // Initialize the Mark button
        bookIV = findViewById(R.id.idIVbook);

        // Retrieving book data from Intent extras
        title = getIntent().getStringExtra("title");
        subtitle = getIntent().getStringExtra("subtitle");
        publisher = getIntent().getStringExtra("publisher");
        publishedDate = getIntent().getStringExtra("publishedDate");
        description = getIntent().getStringExtra("description");
        pageCount = getIntent().getIntExtra("pageCount", 0);
        thumbnail = getIntent().getStringExtra("thumbnail");
        previewLink = getIntent().getStringExtra("previewLink");
        infoLink = getIntent().getStringExtra("infoLink");
        buyLink = getIntent().getStringExtra("buyLink");

        // Setting retrieved data to UI components
        titleTV.setText(title);
        subtitleTV.setText(subtitle);
        publisherTV.setText(publisher);
        publishDateTV.setText("Published On : " + publishedDate);
        descTV.setText(description);
        pageTV.setText("No Of Pages : " + pageCount);

        // Load the book's thumbnail image using Picasso
        Picasso.get().load(thumbnail).into(bookIV);

        // Set Mark button's click listener
        markBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBookMarked(title)) {
                    unmarkBook(title);
                    markBtn.setText("Mark This Book");
                } else {
                    markBook(new BookInfo(title, subtitle, authors, publisher, publishedDate, description, pageCount, thumbnail, previewLink, infoLink, buyLink));
                    markBtn.setText("Unmark This Book");
                }
            }
        });

        // Set Preview button's click listener
        previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previewLink.isEmpty()) {
                    Toast.makeText(BookDetails.this, "No preview link present", Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri uri = Uri.parse(previewLink);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });

        // Set Buy button's click listener
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buyLink.isEmpty()) {
                    Toast.makeText(BookDetails.this, "No buy page present for this book on Google Play", Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri uri = Uri.parse(buyLink);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });

        // Set the Mark button text based on whether the book is already marked
        if (isBookMarked(title)) {
            markBtn.setText("Unmark Book");
        } else {
            markBtn.setText("Mark Book");
        }
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
     * Marks a book by saving it to SharedPreferences.
     * @param book The book to be marked.
     */
    private void markBook(BookInfo book) {
        SharedPreferences preferences = getSharedPreferences("MarkedBooksPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();

        // Get existing list
        String json = preferences.getString("markedBooks", "[]");
        Type type = new TypeToken<List<BookInfo>>() {}.getType();
        List<BookInfo> bookList = gson.fromJson(json, type);

        // Add new book if it's not already in the list
        for (BookInfo b : bookList) {
            if (b.getTitle().equals(book.getTitle())) {
                Log.d("BookMarking", "Book is already marked: " + book.getTitle());
                return; // Avoid duplicates
            }
        }
        bookList.add(book);

        // Save updated list
        String updatedJson = gson.toJson(bookList);
        editor.putString("markedBooks", updatedJson);
        editor.apply();

        Log.d("BookMarking", "Book marked: " + book.getTitle());
    }


    /**
     * Unmarks a book by removing it from SharedPreferences.
     * @param title The title of the book to be unmarked.
     */
    private void unmarkBook(String title) {
        SharedPreferences preferences = getSharedPreferences("MarkedBooksPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();

        // Get existing list
        String json = preferences.getString("markedBooks", "[]");
        Type type = new TypeToken<List<BookInfo>>() {}.getType();
        List<BookInfo> bookList = gson.fromJson(json, type);

        // Remove book by title
        bookList.removeIf(book -> book.getTitle().equals(title));

        // Save updated list
        String updatedJson = gson.toJson(bookList);
        editor.putString("markedBooks", updatedJson);
        editor.apply();

        Log.d("BookMarking", "Book unmarked: " + title);
    }


    /**
     * Checks if a book is marked (exists in SharedPreferences).
     * @param title The title of the book to check.
     * @return true if the book is marked, false otherwise.
     */
    private boolean isBookMarked(String title) {
        SharedPreferences preferences = getSharedPreferences("MarkedBooksPrefs", MODE_PRIVATE);
        Gson gson = new Gson();

        // Get existing list
        String json = preferences.getString("markedBooks", "[]");
        Type type = new TypeToken<List<BookInfo>>() {}.getType();
        List<BookInfo> bookList = gson.fromJson(json, type);

        // Check if book exists in list
        for (BookInfo book : bookList) {
            if (book.getTitle().equals(title)) {
                Log.d("BookMarking", "Book is marked: " + title);
                return true;
            }
        }
        Log.d("BookMarking", "Book is not marked: " + title);
        return false;
    }

}
