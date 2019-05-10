package com.akashdeveloper.avma1997.mediaplayer;

import java.io.Serializable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName="songs_table")
public class Audio implements Serializable {
    @PrimaryKey
    private int id;
    private String data;
    private String title;
    private String album;
    private String artist;
    int flag

    public Audio(int id,String data, String title, String album, String artist) {
        this.id=id;
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
