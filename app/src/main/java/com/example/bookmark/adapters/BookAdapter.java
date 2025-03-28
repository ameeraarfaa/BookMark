package com.example.bookmark.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookmark.R;
import com.example.bookmark.activities.BookDetails;
import com.example.bookmark.models.BookInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * BookAdapter handles displaying book information in a RecyclerView.
 * It allows users to mark/unmark books, open book details and share the book.
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private ArrayList<BookInfo> bookInfoArrayList;
    private Context mcontext;
    private int selectedPosition = -1; // Stores selected item position for context menu

    /**
     * Constructor for BookAdapter.
     * @param bookInfoArrayList List of books to display.
     * @param mcontext Context of the activity.
     */
    public BookAdapter(ArrayList<BookInfo> bookInfoArrayList, Context mcontext) {
        this.bookInfoArrayList = bookInfoArrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_rv_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookInfo bookInfo = bookInfoArrayList.get(position);

        holder.nameTV.setText(bookInfo.getTitle());
        holder.publisherTV.setText(bookInfo.getPublisher());
        holder.pageCountTV.setText("No of Pages : " + bookInfo.getPageCount());
        holder.dateTV.setText(bookInfo.getPublishedDate());

        // Load thumbnail using Glide
        if (bookInfo.getThumbnail() != null && !bookInfo.getThumbnail().isEmpty()) {
            Glide.with(holder.bookIV.getContext())
                    .load(bookInfo.getThumbnail())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.bookIV);
        } else {
            holder.bookIV.setImageResource(R.drawable.placeholder_image);
        }

        // Handle "Mark" icon state
        ImageView markIcon = holder.itemView.findViewById(R.id.idMarkIcon);
        if (isBookMarked(bookInfo.getTitle())) {
            markIcon.setImageResource(R.drawable.ic_bookmark_filled);
        } else {
            markIcon.setImageResource(R.drawable.ic_bookmark_border);
        }

        // Toggle Mark/Unmark on Click
        markIcon.setOnClickListener(v -> {
            if (isBookMarked(bookInfo.getTitle())) {
                unmarkBook(bookInfo.getTitle());
                markIcon.setImageResource(R.drawable.ic_bookmark_border);
            } else {
                markBook(bookInfo);
                markIcon.setImageResource(R.drawable.ic_bookmark_filled);
            }
            notifyDataSetChanged();
        });


        // Open BookDetails on Click
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(mcontext, BookDetails.class);
            i.putExtra("title", bookInfo.getTitle());
            i.putExtra("subtitle", bookInfo.getSubtitle());
            i.putExtra("authors", bookInfo.getAuthors());
            i.putExtra("publisher", bookInfo.getPublisher());
            i.putExtra("publishedDate", bookInfo.getPublishedDate());
            i.putExtra("description", bookInfo.getDescription());
            i.putExtra("pageCount", bookInfo.getPageCount());
            i.putExtra("thumbnail", bookInfo.getThumbnail());
            i.putExtra("previewLink", bookInfo.getPreviewLink());
            i.putExtra("infoLink", bookInfo.getInfoLink());
            i.putExtra("buyLink", bookInfo.getBuyLink());
            mcontext.startActivity(i);
        });

        // Set up context menu click
        holder.moreOptionsIV.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            v.showContextMenu(); // Show the context menu
        });
    }

    @Override
    public int getItemCount() {
        return bookInfoArrayList.size();
    }

    /**
     * ViewHolder class for BookAdapter.
     */
    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView nameTV, publisherTV, pageCountTV, dateTV;
        ImageView bookIV, moreOptionsIV;

        public BookViewHolder(View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.idTVBookTitle);
            publisherTV = itemView.findViewById(R.id.idTVpublisher);
            pageCountTV = itemView.findViewById(R.id.idTVPageCount);
            dateTV = itemView.findViewById(R.id.idTVDate);
            bookIV = itemView.findViewById(R.id.idIVbook);
            moreOptionsIV = itemView.findViewById(R.id.idIVMoreOptions);

            // Register context menu for the three-dot button
            moreOptionsIV.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Options");
            menu.add(this.getAdapterPosition(), 101, 0, "Share Book");
        }
    }

    /**
     * Updates the book list and refreshes the adapter.
     * @param newBooks New list of books.
     */
    public void updateBooks(List<BookInfo> newBooks) {
        bookInfoArrayList.clear();
        bookInfoArrayList.addAll(newBooks);
        notifyDataSetChanged();
    }

    /**
     * Checks if a book is marked.
     * @param title Book title.
     * @return True if marked, false otherwise.
     */
    // Method to handle marking books
    private boolean isBookMarked(String title) {
        SharedPreferences preferences = mcontext.getSharedPreferences("MarkedBooksPrefs", Context.MODE_PRIVATE);
        String json = preferences.getString("markedBooks", "[]");
        List<BookInfo> bookList = new Gson().fromJson(json, new TypeToken<List<BookInfo>>() {}.getType());
        return bookList != null && bookList.stream().anyMatch(book -> book.getTitle().equals(title));
    }

    /**
     * Marks a book and saves it to SharedPreferences.
     * @param book BookInfo object.
     */
    private void markBook(BookInfo book) {
        SharedPreferences preferences = mcontext.getSharedPreferences("MarkedBooksPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String json = preferences.getString("markedBooks", "[]");
        List<BookInfo> bookList = new Gson().fromJson(json, new TypeToken<List<BookInfo>>() {}.getType());

        if (bookList == null) {
            bookList = new ArrayList<>();
        }

        for (BookInfo b : bookList) {
            if (b.getTitle().equals(book.getTitle())) {
                return;
            }
        }

        bookList.add(book);
        editor.putString("markedBooks", new Gson().toJson(bookList));
        editor.apply();

        // Send broadcast to refresh after marking
        Intent intent = new Intent("com.example.bookmark.ACTION_REFRESH");
        mcontext.sendBroadcast(intent);
    }

    /**
     * Removes a book from the marked books list and updates SharedPreferences.
     * Also sends a broadcast to notify the activity to refresh the book list.
     * @param title The title of the book to be unmarked.
     */
    private void unmarkBook(String title) {
        SharedPreferences preferences = mcontext.getSharedPreferences("MarkedBooksPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String json = preferences.getString("markedBooks", "[]");
        List<BookInfo> bookList = new Gson().fromJson(json, new TypeToken<List<BookInfo>>() {}.getType());

        if (bookList != null) {
            bookList.removeIf(book -> book.getTitle().equals(title));
            editor.putString("markedBooks", new Gson().toJson(bookList));
            editor.apply();

            // Send a broadcast to notify the activity to refresh
            Intent intent = new Intent("com.example.bookmark.ACTION_REFRESH");
            mcontext.sendBroadcast(intent);
        }
    }

    /**
     * Shares the selected book's details via an intent.
     * Opens Android's Sharesheet where the user can choose an app to share the book information.
     */
    public void shareBook() {
        if (selectedPosition != -1 && selectedPosition < bookInfoArrayList.size()) {
            BookInfo bookInfo = bookInfoArrayList.get(selectedPosition);
            String shareText = "Check out this book: " + bookInfo.getTitle() + "\nPreview here: " + bookInfo.getPreviewLink();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            mcontext.startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
    }
}
