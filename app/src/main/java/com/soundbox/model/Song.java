package com.soundbox.model;

import java.io.Serializable;

public class Song implements Serializable {
    public static final String CHANNEL_ID = "Channel_Service_Exapple";
    private String songName,songUrl;
    private String imageUrl, songArtist, songDuration;

    private String idArtist;

    public String getIdArtist() {
        return idArtist;
    }

    public void setIdArtist(String idArtist) {
        this.idArtist = idArtist;
    }

    public Song() {
    }

    public Song(String songName, String songUrl, String imageUrl, String songArtist, String songDuration, String idArtist) {
        this.songName = songName;
        this.songUrl = songUrl;
        this.imageUrl = imageUrl;
        this.songArtist = songArtist;
        this.songDuration = songDuration;
        this.idArtist =idArtist;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }
}
