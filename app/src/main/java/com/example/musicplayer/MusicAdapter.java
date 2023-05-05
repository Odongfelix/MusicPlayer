package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicHolder> {

    private final ArrayList<Song> songs;
    private ArrayList<Song> tempList;

    public MusicAdapter(ArrayList<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public MusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MusicHolder(
                LayoutInflater.from(parent.getContext()
                ).inflate(R.layout.row_song,
                        parent,
                        false));
    }

    private Context context;

    @Override
    public void onBindViewHolder(@NonNull MusicHolder holder, int position) {
        if (context == null)
            context = holder.itemView.getContext();
        holder.set(songs.get(position));
        holder.itemView.setOnClickListener(s -> {
            Intent songClickedIntent = new Intent(Constants.ACTION_SONG_CLICKED);
            songClickedIntent.putExtra(Constants.SONG, position);
            context.sendBroadcast(songClickedIntent);
        });
    }

    public void filter(String searchString) {
        if (tempList == null) {
            tempList = new ArrayList<>();
            tempList.addAll(songs);
        }
        songs.clear();
        notifyDataSetChanged();
        for (Song s : tempList) {
            if(s.getName() == null||s.getArtist() == null)
                continue;
            if (s.getName().contains(searchString) || s.getArtist().contains(searchString)) {
                songs.add(s);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void clearSearch() {
        if(tempList == null)
            return;
        songs.addAll(tempList);
        notifyItemRangeChanged(0,songs.size());
    }
}
