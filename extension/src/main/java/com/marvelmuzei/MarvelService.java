package com.marvelmuzei;

/**
 * Created by james on 2/17/14.
 */

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;

interface MarvelService {

    @GET("/")
    Character getCharacter();

    @PUT("/character/")
    Comic getComicByCharacter(@Body String CharacterID);

    @PUT("/artist/")
    Comic getComicByArtist(@Body String ArtistID);

    static class Character {
        int id;
        String name;
        Image thumbnail;
        String description;
        List<Url> urls;
    }

    static class Image {
        String path;
        String extension;
    }

    static class Url {
        String url;
    }

    static class Comic {
        int id;
        String title;
        List<Url> urls;
        Image thumbnail;
        String description;
        Creators creators;
    }

    static class Creators {
        Creator[] items;
    }
    static class Creator {
        String name;
        String role;
    }
}