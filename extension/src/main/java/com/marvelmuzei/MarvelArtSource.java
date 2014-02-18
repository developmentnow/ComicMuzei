package com.marvelmuzei;

import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import android.content.Intent;
import android.net.Uri;

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

    private static final int ROTATE_TIME_MILLIS = 24 * 60 * 60 * 1000; // rotate every 3 hours

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

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer("http://kubetown.com")
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError retrofitError) {
                        int statusCode = retrofitError.getResponse().getStatus();
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
        MarvelService.Character character = service.getCharacter();


        if (character == null ) {
            throw new RetryException();
        }

        publishArtwork(new Artwork.Builder()
                .title(character.name)
                .byline(character.description)
                .imageUri(Uri.parse(character.thumbnail.path + ".jpg"))
                .token(Integer.toString(character.id))
                .viewIntent(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(character.urls.get(0).url)))
                .build());

        scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
    }


}
