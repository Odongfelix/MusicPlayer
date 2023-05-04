package com.example.musicplayer;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MusicHolder extends RecyclerView.ViewHolder {
    private TextView song,artist;
    private int playingColor,songColor,artistColor,playingBackground;
    private final int defaultColor = Color.TRANSPARENT;
    private View iv;
    public MusicHolder(@NonNull View itemView) {
        super(itemView);
        song = itemView.findViewById(R.id.song);
        artist = itemView.findViewById(R.id.artist);
        iv = itemView;
        playingColor = ResourcesCompat.getColor(itemView.getResources(),R.color.purple_500,null);
        playingBackground = ResourcesCompat.getColor(itemView.getResources(),R.color.teal_700,null);
        songColor = ResourcesCompat.getColor(itemView.getResources(),R.color.purple_700,null);
        artistColor = playingColor;
    }
    public void set(Song song){
        this.song.setText(song.getName());
        artist.setText(song.getArtist());
        if(song.isPlaying()){
            iv.setBackgroundColor(playingBackground);
            this.song.setTextColor(Color.WHITE);
            artist.setTextColor(Color.WHITE);
        }else{
            iv.setBackgroundColor(defaultColor);
            this.song.setTextColor(songColor);
            artist.setTextColor(artistColor);
        }
    }
}
