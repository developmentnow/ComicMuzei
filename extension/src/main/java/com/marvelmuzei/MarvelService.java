package com.marvelmuzei;

/**
 * Created by james on 2/17/14.
 */

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;

interface MarvelService {

    @GET("/")
    Character getCharacter();

    @GET("/character/{CharacterID}")
    Comic getComicByCharacter(@Path("CharacterID") String CharacterID);

    @GET("/artist/{ArtistID}")
    Comic getComicByArtist(@Path("ArtistID") String ArtistID);

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
    }

}