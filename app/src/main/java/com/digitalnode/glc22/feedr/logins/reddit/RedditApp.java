package com.digitalnode.glc22.feedr.logins.reddit;

import android.app.Application;

import net.dean.jraw.http.oauth.Credentials;

import java.util.UUID;

public class RedditApp extends Application {
    private static final String CLIENT_ID = "yFjlO_gh1e2uBw";
    private static final String REDIRECT_URL = "https://github.com/HalfMillennium/Feedr";

    private static Credentials installedAppCredentials;
    private static Credentials userlessAppCredentials;

    public Credentials getUserlessAppCredentials() {
        if (userlessAppCredentials == null) {
            userlessAppCredentials = Credentials.userlessApp(CLIENT_ID, UUID.randomUUID());
        }
        return userlessAppCredentials;
    }

    public static Credentials getInstalledAppCredentials() {
        if (installedAppCredentials == null) {
            installedAppCredentials = Credentials.installedApp(CLIENT_ID, REDIRECT_URL);
        }
        return installedAppCredentials;
    }
}
