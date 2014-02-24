package com.marvelmuzei;

import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.android.apps.muzei.api.Artwork;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by james on 2/17/14.
 */
public class MarvelArtSource extends RemoteMuzeiArtSource {
    private static final String TAG = "Marvel";
    private static final String SOURCE_NAME = "MarvelArtSource";

    private static final int ROTATE_TIME_MILLIS = 24 * 60 * 60 * 1000; // Rotate Every Day
    private static final int RETRY_TIME_MILLIS = 60 * 60 * 1000; // Retry in an hour

    public MarvelArtSource() {
        super(SOURCE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUserCommands(BUILTIN_COMMAND_ID_NEXT_ARTWORK);
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer("http://marvel.kubetown.com")
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        if (retrofitError == null || retrofitError.getResponse() == null) {
                            scheduleUpdate(System.currentTimeMillis() + RETRY_TIME_MILLIS);
                            return new RetryException();
                        }
                        Integer statusCode = retrofitError.getResponse().getStatus();
                        if (retrofitError.isNetworkError()
                                || (500 <= statusCode && statusCode < 600)) {
                            return new RetryException();
                        }
                        scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
                        return retrofitError;
                    }
                })
                .build();

        MarvelService service = restAdapter.create(MarvelService.class);

        MarvelService.Comic comic = null;
        MarvelService.Character character = null;
        if (prefs.getBoolean(SettingsActivity.KEY_PREF_BY_CHARACTER, false)) {
            String[] raw = TextUtils.split(prefs.getString(SettingsActivity.KEY_PREF_CHARACTERS, ""), SettingsActivity.SEPARATOR);
            String chars = TextUtils.join(",", raw);
            comic = service.getComicByCharacter(chars);
        } else if (prefs.getBoolean(SettingsActivity.KEY_PREF_BY_ARTIST, false)) {
            String[] raw = TextUtils.split(prefs.getString(SettingsActivity.KEY_PREF_ARTISTS, ""), SettingsActivity.SEPARATOR);
            String artists = TextUtils.join(",", raw);
            comic = service.getComicByArtist(artists);
        } else {
            character = service.getCharacter();
        }

        if (character == null && comic == null) {
            throw new RetryException();
        }
        if (character != null) {
            publishArtwork(new Artwork.Builder()
                    .title(character.name)
                    .byline(character.description)
                    .imageUri(Uri.parse(character.thumbnail.path + ".jpg"))
                    .token(Integer.toString(character.id))
                    .viewIntent(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(character.urls.get(0).url)))
                    .build());
        } else {
            String description = null;
            for (int i = 0; i < comic.creators.items.length; i++) {
                if (comic.creators.items[i].role.equals("penciller (cover)")) {
                    description =  comic.creators.items[i].name;
                    break;
                }
            }

            publishArtwork(new Artwork.Builder()
                    .title(comic.title)
                    .byline(description)
                    .imageUri(Uri.parse(comic.thumbnail.path + ".jpg"))
                    .token(Integer.toString(comic.id))
                    .viewIntent(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(comic.urls.get(0).url)))
                    .build());
        }
        scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
    }


}
