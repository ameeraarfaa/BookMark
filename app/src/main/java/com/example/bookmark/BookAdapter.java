package com.example.bookmark;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookmark.BookInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * BookAdapter is a RecyclerView Adapter class that handles the display of book information
 * in a RecyclerView. It binds book data from an ArrayList<BookInfo> to the corresponding views.
 * It also handles item click events, launching the BookDetails activity with detailed book data.
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private ArrayList<BookInfo> bookInfoArrayList;  // List of books to be displayed
    private Context mcontext;  // Context for handling UI-related operations

    /**
     * Constructor to initialize BookAdapter with a list of books and context.
     * @param bookInfoArrayList List of BookInfo objects to be displayed.
     * @param mcontext Context of the activity or fragment where the adapter is used.
     */
    public BookAdapter(ArrayList<BookInfo> bookInfoArrayList, Context mcontext) {
        this.bookInfoArrayList = bookInfoArrayList;
        this.mcontext = mcontext;
    }

    /**
     * Creates and returns a new ViewHolder by inflating the item layout for the RecyclerView.
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new View.
     * @return A new instance of BookViewHolder.
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_rv_item, parent, false);
        return new BookViewHolder(view);
    }

    /**
     * Binds data to the views inside the ViewHolder at the given position.
     * This method sets the book details (title, publisher, page count, and date) to the respective UI components.
     * It also loads the book's thumbnail image using Picasso and sets up a click listener for each item.
     * @param holder The ViewHolder that should be updated with new data.
     * @param position The position of the item in the dataset.
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookInfo bookInfo = bookInfoArrayList.get(position);

        // Set book details to respective TextViews
        holder.nameTV.setText(bookInfo.getTitle());
        holder.publisherTV.setText(bookInfo.getPublisher());
        holder.pageCountTV.setText("No of Pages : " + bookInfo.getPageCount());
        holder.dateTV.setText(bookInfo.getPublishedDate());

        // Print thumbnail URL to Logcat for debugging
        Log.d("BookAdapter", "Thumbnail URL: " + bookInfo.getThumbnail());

        // Load book thumbnail using Picasso with error handling
        if (bookInfo.getThumbnail() != null && !bookInfo.getThumbnail().isEmpty()) {
            Glide.with(holder.bookIV.getContext())
                    .load(bookInfo.getThumbnail())
                    .placeholder(R.drawable.placeholder_image)  // Placeholder image while loading
                    .error(R.drawable.error_image)             // Image to show if loading fails
                    .into(holder.bookIV);

        } else {
            // Set placeholder image if URL is invalid
            holder.bookIV.setImageResource(R.drawable.placeholder_image);
        }

        // Set click listener to open BookDetails activity with book data
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                // Start BookDetails activity with the selected book details
                mcontext.startActivity(i);
            }
        });
    }


    /**
     * Returns the total number of items in the book list.
     * @return The size of bookInfoArrayList.
     */
    @Override
    public int getItemCount() {
        return bookInfoArrayList.size();
    }

    /**
     * ViewHolder class to hold references to the views for each item in the RecyclerView.
     * It contains TextViews for book details and an ImageView for the book thumbnail.
     */
    public class BookViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV, publisherTV, pageCountTV, dateTV;
        ImageView bookIV;

        /**
         * Constructor to initialize the ViewHolder with item views.
         * @param itemView The view representing a single item in the RecyclerView.
         */
        public BookViewHolder(View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.idTVBookTitle);
            publisherTV = itemView.findViewById(R.id.idTVpublisher);
            pageCountTV = itemView.findViewById(R.id.idTVPageCount);
            dateTV = itemView.findViewById(R.id.idTVDate);
            bookIV = itemView.findViewById(R.id.idIVbook);
        }
    }
}