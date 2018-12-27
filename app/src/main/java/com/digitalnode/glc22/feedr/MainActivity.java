package com.digitalnode.glc22.feedr;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.digitalnode.glc22.feedr.Sorts.SortByNewest;

import net.dean.jraw.RedditClient;
import net.dean.jraw.Version;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        Log.d(TAG, "onCreate: Started.");

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

        for (Submission post : firstPage)
            if (post.getDomain().contains("imgur.com")) {
                System.out.println(String.format("%s (/r/%s, %s points) - %s",
                        post.getTitle(), post.getSubreddit(), post.getScore(), post.getUrl()));
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.feed_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.conf_feed:
                // start activity for configuring the feed
                return true;
            case R.id.conf_accounts:
                // start activity for configuring accounts
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainFeed(), "MainFeed");
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);
    }
}
