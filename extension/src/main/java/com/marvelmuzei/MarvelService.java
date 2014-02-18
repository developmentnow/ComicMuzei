package com.marvelmuzei;

/**
 * Created by james on 2/17/14.
 */

import java.util.List;

import retrofit.http.GET;

interface MarvelService {

    @GET("/marvel/")
    Character getCharacter();



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


}