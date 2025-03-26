package com.example.bookmark.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookmark.models.BookInfo;
import com.example.bookmark.models.MarkedBooks;
import com.example.bookmark.R;

import java.util.List;

public class MarkedBooksAdapter extends RecyclerView.Adapter<MarkedBooksAdapter.MarkedBooksViewHolder> {

    private List<BookInfo> bookList;
    private Context context;

    // Constructor
    public MarkedBooksAdapter(List<BookInfo> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
    }

    // This method inflates the layout for each book item
    @NonNull
    @Override
    public MarkedBooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate book_rv_item.xml for each item in the RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.book_rv_item, parent, false);
        return new MarkedBooksViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MarkedBooksViewHolder holder, int position) {
        // Get the current book info from the list
        BookInfo book = bookList.get(position);
        holder.titleTextView.setText(book.getTitle());

        // Set author (handling null or empty cases)
        String author = (book.getAuthors() != null && !book.getAuthors().isEmpty())
                ? book.getAuthors().get(0)
                : "Unknown Author";
        holder.authorTextView.setText(author);

        holder.publishedDateTextView.setText(book.getPublishedDate());

        // Load image with Glide (use a placeholder image if no thumbnail is available)
        if (book.getThumbnail() != null && !book.getThumbnail().isEmpty()) {
            Glide.with(context).load(book.getThumbnail()).into(holder.thumbnailImageView);
        } else {
            Glide.with(context).load(R.drawable.error_image).into(holder.thumbnailImageView);
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    // Update the book list (used when the list is sorted)
    public void updateBooks(List<BookInfo> newBooks) {
        bookList.clear();
        bookList.addAll(newBooks);
        notifyDataSetChanged();
    }

    // ViewHolder class to hold each item view
    public class MarkedBooksViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, authorTextView, publishedDateTextView;
        ImageView thumbnailImageView;

        public MarkedBooksViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.bookTitle);
            authorTextView = itemView.findViewById(R.id.bookAuthor);
            publishedDateTextView = itemView.findViewById(R.id.bookPublishedDate);
            thumbnailImageView = itemView.findViewById(R.id.bookThumbnail);
        }
    }
}
