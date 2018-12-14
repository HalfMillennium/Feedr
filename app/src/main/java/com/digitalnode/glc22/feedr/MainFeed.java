package com.digitalnode.glc22.feedr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import net.dean.jraw.RedditClient;
import net.dean.jraw.Version;
import net.dean.jraw.android.AndroidHelper;
import net.dean.jraw.android.AppInfoProvider;
import net.dean.jraw.android.ManifestAppInfoProvider;
import net.dean.jraw.android.SharedPreferencesTokenStore;
import net.dean.jraw.android.SimpleAndroidLogAdapter;
import net.dean.jraw.http.LogAdapter;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.SimpleHttpLogger;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.oauth.AccountHelper;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.BarebonesPaginator;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by User on 4/9/2017.
 */

public class MainFeed extends Fragment {
    private static final String TAG = "MainFeed";
    private static AccountHelper accountHelper;
    private static SharedPreferencesTokenStore tokenStore;
    private DefaultPaginator<Submission> frontPage;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Entry> entryList;

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

        Entry[] arr = new Entry[entryList.size()];

        setUpReddit();

        for(int i = 0; i < arr.length; i++)
        {
            arr[i] = entryList.get(i);
        }
        // specify an adapter (see also next example)
        mAdapter = new EntryAdapter(arr);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private void setUpReddit()
    {
        // Get UserAgent and OAuth2 data from AndroidManifest.xml
        AppInfoProvider provider = new ManifestAppInfoProvider(getContext());

        // Ideally, this should be unique to every device
        UUID deviceUuid = UUID.randomUUID();

        // Store our access tokens and refresh tokens in shared preferences
        tokenStore = new SharedPreferencesTokenStore(getContext());
        // Load stored tokens into memory
        tokenStore.load();
        // Automatically save new tokens as they arrive
        tokenStore.setAutoPersist(true);

        // An AccountHelper manages switching between accounts and into/out of userless mode.
        accountHelper = AndroidHelper.accountHelper(provider, deviceUuid, tokenStore);

        // Every time we use the AccountHelper to switch between accounts (from one account to
        // another, or into/out of userless mode), call this function
        accountHelper.onSwitch(redditClient -> {
            // By default, JRAW logs HTTP activity to System.out. We're going to use Log.i()
            // instead.
            LogAdapter logAdapter = new SimpleAndroidLogAdapter(Log.INFO);

            // We're going to use the LogAdapter to write down the summaries produced by
            // SimpleHttpLogger
            redditClient.setLogger(
                    new SimpleHttpLogger(SimpleHttpLogger.DEFAULT_LINE_LENGTH, logAdapter));
            frontPage = redditClient.frontPage()
                    .sorting(SubredditSort.TOP)
                    .timePeriod(TimePeriod.DAY)
                    .limit(30)
                    .build();

            return null;
        });

        // You'll want to change this for your specific OAuth2 app
        Credentials credentials = Credentials.script("<username>", "<password>", "<client ID>", "<client secret>");

        // Construct our NetworkAdapter
        UserAgent userAgent = new UserAgent("bot", "net.dean.jraw.example.script", Version.get(), "thatJavaNerd");
        NetworkAdapter http = new OkHttpNetworkAdapter(userAgent);

        // Authenticate our client
        RedditClient reddit = OAuthHelper.automatic(http, credentials);

        // Browse through the top posts of the last month, requesting as much data as possible per request
        DefaultPaginator<Submission> paginator = reddit.frontPage()
                .limit(Paginator.RECOMMENDED_MAX_LIMIT)
                .sorting(SubredditSort.TOP)
                .timePeriod(TimePeriod.MONTH)
                .build();

        // Request the first page
        Listing<Submission> firstPage = paginator.next();

        for (Submission post : firstPage) {
            if (post.getDomain().contains("imgur.com")) {
                System.out.println(String.format("%s (/r/%s, %s points) - %s",
                        post.getTitle(), post.getSubreddit(), post.getScore(), post.getUrl()));
            }
        }

        /** This will be used for getting all subreddits the person is subscribed to **/
        /*
        // Make sure we have a logged-in user or this call will fail!
        BarebonesPaginator<Subreddit> paginator = redditClient.me().subreddits("subscriber")
                // Request as many items as possible
                .limit(Paginator.RECOMMENDED_MAX_LIMIT)
                .build();

        List<Subreddit> subscribed = new ArrayList<Subreddit>();

        // Paginator implements Iterable, so we can use the enhanced for loop to iterate the Paginator until reddit
        // can't give us anything else. Don't do this for posts on a subreddit or the front page!
        for (Listing<Subreddit> page : paginator) {
            subscribed.addAll(page);
        } */
        /***************************************************************************/
    }
}