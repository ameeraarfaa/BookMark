package com.example.bookmark.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class RefreshReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Call method to refresh the marked books
        if (context instanceof MarkedBooksActivity) {
            ((MarkedBooksActivity) context).refreshMarkedBooks();
        } else {
            Toast.makeText(context, "Activity context is null", Toast.LENGTH_SHORT).show();
        }
    }
}
