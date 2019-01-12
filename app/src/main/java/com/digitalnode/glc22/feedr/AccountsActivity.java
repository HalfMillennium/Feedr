package com.digitalnode.glc22.feedr;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalnode.glc22.feedr.logins.reddit.RedditApp;
import com.digitalnode.glc22.feedr.logins.reddit.RedditLoginActivity;
import com.digitalnode.glc22.feedr.logins.reddit.RedditService;

import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.AuthenticationState;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.models.Submission;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class AccountsActivity extends AppCompatActivity {

    private static final int REQ_CODE_LOGIN = 0;
    private CompositeDisposable disposables = new CompositeDisposable();
    
    ConstraintLayout red, twit;
    TextView reddText, twitText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        if(MainFeed.redditIsLoggedIn())
            setRedditLoggedIn();
    }

    public void getRedditLogin(View view)
    {
        startActivityForResult(new Intent(this, RedditLoginActivity.class), REQ_CODE_LOGIN);
    }

    public static Intent makeIntent(Context context)
    {
        return new Intent(context, AccountsActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_LOGIN && resultCode == RESULT_OK) {
            Credentials credentials = RedditApp.getInstalledAppCredentials();

            disposables.add(RedditService.userAuthentication(
                    AuthenticationManager.get().getRedditClient(),
                    credentials,
                    data.getStringExtra("RESULT_URL"))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            String username = AuthenticationManager.get().getRedditClient()
                                    .getAuthenticatedUser();
                            setRedditLoggedIn();
                            Toast.makeText(AccountsActivity.this, "Logged in as " + username,
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(AccountsActivity.this, "Something went wrong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
            );
        }
    }

    /*** Not 100% on whether we'll want to use this ***/
    private void doUserlessAuth() {
        Credentials credentials = ((RedditApp) getApplication()).getUserlessAppCredentials();
        disposables.add(RedditService.userlessAuthentication(
                AuthenticationManager.get().getRedditClient(), credentials)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(AccountsActivity.this, "Authentication complete!",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(AccountsActivity.this, "Something went wrong",
                                Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    private void doUserAuth() {
        Intent intent = new Intent(this, RedditLoginActivity.class);
        startActivityForResult(intent, REQ_CODE_LOGIN);
    }

    private void checkStatus() {
        Toast.makeText(
                this,
                AuthenticationManager.get().checkAuthState().toString(),
                Toast.LENGTH_SHORT)
                .show();
    }

    private void refreshToken() {
        if (!AuthenticationManager.get().getRedditClient().hasActiveUserContext()) {
            Toast.makeText(AccountsActivity.this, "No need to refresh userless auth tokens",
                    Toast.LENGTH_SHORT).show();
        } else {
            Credentials credentials = ((RedditApp) getApplicationContext())
                    .getInstalledAppCredentials();
            disposables.add(RedditService.refreshToken(credentials)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            Toast.makeText(AccountsActivity.this, "Token refreshed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(AccountsActivity.this, "Something went wrong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
            );
        }
    }

    private void loadSubmission() {
        if (AuthenticationManager.get().checkAuthState() == AuthenticationState.READY) {
            disposables.add(RedditService
                    .getSubmission(AuthenticationManager.get().getRedditClient())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<Submission>() {
                        @Override
                        public void onSuccess(Submission submission) {
                            Toast.makeText(AccountsActivity.this,
                                    "Submission loaded with title: " + submission.getTitle(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(AccountsActivity.this, "Something went wrong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }));
        } else if (AuthenticationManager.get().checkAuthState() == AuthenticationState.NONE) {
            Toast.makeText(this, "Not authenticated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Token needs refresh", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        if (!AuthenticationManager.get().getRedditClient().hasActiveUserContext()) {
            Toast.makeText(AccountsActivity.this, "No need to logout of userless auth",
                    Toast.LENGTH_SHORT).show();
        } else {
            Credentials credentials = ((RedditApp) getApplicationContext())
                    .getInstalledAppCredentials();

            disposables.add(RedditService.logout(credentials)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            Toast.makeText(AccountsActivity.this, "Deauthenticated!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(AccountsActivity.this, "Something went wrong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
            );
        }
    }

    private void setRedditLoggedIn()
    {
        red = findViewById(R.id.reddit);
        red.setBackgroundColor(getResources().getColor(R.color.lightGray));

        reddText = findViewById(R.id.redditConf);
        reddText.setText(R.string.reconf_reddit);
    }
}
