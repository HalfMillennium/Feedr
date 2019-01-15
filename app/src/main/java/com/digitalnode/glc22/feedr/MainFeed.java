package com.digitalnode.glc22.feedr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.digitalnode.glc22.feedr.logins.reddit.RedditApp;
import com.digitalnode.glc22.feedr.logins.reddit.RedditTokenStore;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.RefreshTokenHandler;
import net.dean.jraw.auth.TokenStore;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;
import net.dean.jraw.paginators.TimePeriod;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by User on 4/9/2017.
 */

public class MainFeed extends Fragment {
    private static final String TAG = "MainFeed";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RedditClient redditClient;

    private ArrayList<Entry> entryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.main_feed_layout, container, false);
        Log.d(TAG, "onCreateView: started.");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        redditClient = new RedditClient(UserAgent.of("android",
                "com.digitalnode.feedr", "v0.0.1", "TheFiveHundred"));
        TokenStore store = new RedditTokenStore(
                PreferenceManager.getDefaultSharedPreferences(getContext()));
        RefreshTokenHandler refreshTokenHandler = new RefreshTokenHandler(store, redditClient);

        AuthenticationManager manager = AuthenticationManager.get();
        manager.init(redditClient, refreshTokenHandler);

        Entry[] arr = new Entry[entryList.size()];

        for(int i = 0; i < arr.length; i++)
        {
            arr[i] = entryList.get(i);
        }
        // specify an adapter (see also next example)
        Log.d("posts", "" + entryList);
        mAdapter = new EntryAdapter(arr);
        mRecyclerView.setAdapter(mAdapter);

        if(redditIsLoggedIn())
            new SetUpReddit().execute();

        return view;
    }

    private void setUpReddit()
    {
        SubredditPaginator frontPage = new SubredditPaginator(redditClient);
        // Adjust the request parameters
        frontPage.setLimit(50);                    // Default is 25 (Paginator.DEFAULT_LIMIT)
        frontPage.setTimePeriod(TimePeriod.MONTH); // Default is DAY (Paginator.DEFAULT_TIME_PERIOD)
        frontPage.setSorting(Sorting.TOP);         // Default is HOT (Paginator.DEFAULT_SORTING)
        // This Paginator is now set up to retrieve the highest-scoring links submitted within the past
        // month, 50 at a time

        // Since Paginator implements Iterator, you can use it just how you would expect to, using next() and hasNext()
/*        List<Submission> submissions = frontPage.next();
        for (Submission s : submissions) {
            // Print some basic stats about the posts
            Entry e = new Entry("RED", s.getTitle(), s.getAuthor(), s.getThumbnail(), s.getCreated(), s.getSelftext(), s.getSubredditName());
            entryList.add(e);
        }*/

        for(int i = 0; i < 10; i++)
        {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            Date out = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

            Entry e;
            if(i % 2 == 0)
                e = new Entry("RED", "title", "author", "https://pbs.twimg.com/profile_images/630285593268752384/iD1MkFQ0.png", out, "hello there", "r/bestsubreddit");
            else
                e = new Entry("TWIT", "Twitter Name", "@twitterhandle", "https://pbs.twimg.com/profile_images/630285593268752384/iD1MkFQ0.png", out, "tweet content", "@twitterhandle");
            entryList.add(e);
        }
    }

    public static boolean redditIsLoggedIn()
    {
        return AuthenticationManager.get().getRedditClient().hasActiveUserContext();
    }

    private class SetUpReddit extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            setUpReddit();
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            Entry[] arr = new Entry[entryList.size()];

            for(int i = 0; i < arr.length; i++)
            {
                arr[i] = entryList.get(i);
            }
            // specify an adapter (see also next example)
            Log.d("posts", "" + entryList);
            mAdapter = new EntryAdapter(arr);
            mRecyclerView.setAdapter(mAdapter);

            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //do something with your id
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        //if(redditIsLoggedIn())
        //    new SetUpReddit().execute();
    }
}