package com.digitalnode.glc22.feedr;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
        public TextView textContent, title, label, author, date, div;
        public ImageView thumbnail;
        public CardView topCard, mainCard;
        public EntryViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            textContent = v.findViewById(R.id.textContent);
            topCard = v.findViewById(R.id.top_card);
            mainCard = v.findViewById(R.id.main_card);
            thumbnail = mainCard.findViewById(R.id.thumbnail);
            author = mainCard.findViewById(R.id.author);
            label = topCard.findViewById(R.id.label);
            date = mainCard.findViewById(R.id.date_created);
            div = mainCard.findViewById(R.id.divider_date_author);
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
                .inflate(R.layout.entry_item_constraints, parent, false);

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
        //holder.thumbnail.setImageDrawable(LoadImageFromWebOperations(mDataset[position].getThumbnail()));
        holder.author.setText(mDataset[position].getAuthor());

        Glide.with(holder.thumbnail.getContext())
                .load(mDataset[position].getThumbnail())
                .into(holder.thumbnail);

        if(mDataset[position].getSource().equals("TWIT"))
        {
            setTwitterColors(holder);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "reddit_thumbnail");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public void setTwitterColors(EntryViewHolder holder)
    {
        int col = Color.parseColor("#1DA1F2");

        holder.topCard.setCardBackgroundColor(col);
        holder.thumbnail.setColorFilter(col);
        holder.author.setTextColor(col);
        holder.date.setTextColor(col);
        holder.div.setTextColor(col);
    }

    /*
    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;
        private Bitmap image;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                image = null;
            }
            return image;
        }

        @SuppressLint("NewApi")
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }*/
}