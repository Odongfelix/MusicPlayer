package com.example.musicplayer;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import java.io.File;

public class Song {
    private String name, path, artist = "unknown artist";
    private boolean isPlaying;

    public Song(String name) {
        this.name = name;//todo this is only for debug purposes
    }

    public Song(File file) {
        //todo this still contains the file extension
        name = file.getName();
        if (TextUtils.isEmpty(name)) {
            name = "Unknown song";
        }
        path = file.getAbsolutePath();
        try {
            MediaMetadataRetriever m = new MediaMetadataRetriever();
            m.setDataSource(path);
            artist = m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            artist = artist == null ? "un known artist" : artist;
        }catch (Exception e){
            artist = "unknown artist";
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
