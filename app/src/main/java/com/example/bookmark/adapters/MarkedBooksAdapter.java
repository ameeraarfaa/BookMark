package com.example.bookmark.adapters;

import android.content.Context;
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
import com.example.bookmark.models.BookInfo;
import com.example.bookmark.R;
import java.util.List;

public class MarkedBooksAdapter extends RecyclerView.Adapter<MarkedBooksAdapter.MarkedBooksViewHolder> {
    private List<BookInfo> bookList;
    private Context context;

    public MarkedBooksAdapter(List<BookInfo> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
    }

    @NonNull
    @Override
    public MarkedBooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the book_rv_item.xml layout for each item
        View view = LayoutInflater.from(context).inflate(R.layout.book_rv_item, parent, false);
        return new MarkedBooksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkedBooksViewHolder holder, int position) {
        BookInfo book = bookList.get(position);
        // Bind title
        holder.titleTextView.setText(book.getTitle() != null ? book.getTitle() : "Unknown Title");
        // Bind author (first author if available)
        String author = (book.getAuthors() != null && !book.getAuthors().isEmpty())
                ? book.getAuthors().get(0)
                : "Unknown Author";
        holder.authorTextView.setText(author);
        // Bind published date
        holder.publishedDateTextView.setText(book.getPublishedDate() != null ? book.getPublishedDate() : "Unknown Date");
        // Load thumbnail image
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
    public void updateBooks(List<BookInfo> newBooks) {
        bookList.clear();
        bookList.addAll(newBooks);
        notifyDataSetChanged();
    }


    // ViewHolder class
    public class MarkedBooksViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView titleTextView, authorTextView, publishedDateTextView;
        ImageView thumbnailImageView;

        public MarkedBooksViewHolder(View itemView) {
            super(itemView);
            // Use IDs from book_rv_item.xml
            titleTextView = itemView.findViewById(R.id.idTVBookTitle);
            authorTextView = itemView.findViewById(R.id.idTVpublisher);
            publishedDateTextView = itemView.findViewById(R.id.idTVDate);
            thumbnailImageView = itemView.findViewById(R.id.idIVbook);
            // Register context menu listener on the itemView
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            // Use getAdapterPosition() as group ID so we know which item was pressed
            int position = getAdapterPosition();
            // Menu items: use arbitrary IDs (121 for "Unmark Book", 122 for "View Details")
            menu.setHeaderTitle("Select Action");
            menu.add(position, 121, 0, "Unmark Book");
            menu.add(position, 122, 1, "View Details");
        }
    }
}
