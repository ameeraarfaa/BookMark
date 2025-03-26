package com.example.bookmark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MarkedBooksAdapter extends RecyclerView.Adapter<MarkedBooksAdapter.MarkedBooksViewHolder> {
    private List<MarkedBooks> markedBooksList;
    private Context context;

    public MarkedBooksAdapter(List<MarkedBooks> markedBooksList, Context context) {
        this.markedBooksList = markedBooksList;
        this.context = context;
    }

    @NonNull
    @Override
    public MarkedBooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_marked_book, parent, false);
        return new MarkedBooksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkedBooksViewHolder holder, int position) {
        MarkedBooks book = markedBooksList.get(position);
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        holder.publishedDateTextView.setText(book.getPublishedDate());
        Glide.with(context).load(book.getThumbnailUrl()).into(holder.thumbnailImageView);
    }


    @Override
    public int getItemCount() {
        return markedBooksList.size();
    }

    public void updateBooks(List<MarkedBooks> newBooks) {
        markedBooksList.clear();
        markedBooksList.addAll(newBooks);
        notifyDataSetChanged();
    }

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
