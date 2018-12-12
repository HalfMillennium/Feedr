package com.digitalnode.glc22.feedr;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {
    private Entry[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class EntryViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public String source;
        public TextView textContent, title, label, author;
        public ImageView thumbnail;
        public CardView topCard, mainCard;
        public EntryViewHolder(View v) {
            super(v);
            textContent = v.findViewById(R.id.textContent);
            topCard = v.findViewById(R.id.top_card);
            mainCard = v.findViewById(R.id.main_card);
            thumbnail = mainCard.findViewById(R.id.thumbnail);
           // author = mainCard.findViewById(R.id.author);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public EntryAdapter(Entry[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EntryAdapter.EntryViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.standard_entry_item, parent, false);

        EntryViewHolder vh = new EntryViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(EntryViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setText(mDataset[position].getTitle());
        holder.label.setText(mDataset[position].getLabel());
        holder.textContent.setText(mDataset[position].getTextContent());
        holder.thumbnail.setImageDrawable(LoadImageFromWebOperations(mDataset[position].getThumbnail()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}