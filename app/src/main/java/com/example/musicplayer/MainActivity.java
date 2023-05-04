package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView musicRecyclerView;
    private ArrayList<Song> songs = new ArrayList<>();
    private LinearLayoutManager lManager;
    private MusicAdapter adapter;
    private BroadcastReceiver songClickedReceiver;
    private TextView songName, artistName;
    private ImageView play, next, previous, search, back;
    private MediaPlayer mediaPlayer;
    private int currentSongIndex = 0;

    private View player;
    private EditText searchInput;
    private InputMethodManager iManager;

    private EvelynProgressBar loadingsongs,playingSongs;
private TextView searching;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mediaPlayer = new MediaPlayer();// initialising the media player
        //finding views
        {
            searching = findViewById(R.id.searching_text);
            playingSongs = findViewById(R.id.playing);
            loadingsongs = findViewById(R.id.loading_songs);
            musicRecyclerView = findViewById(R.id.music_recycler_view);
            songName = findViewById(R.id.song_name);
            artistName = findViewById(R.id.artist_name);
            play = findViewById(R.id.play);
            next = findViewById(R.id.next);
            previous = findViewById(R.id.previous);
            search = findViewById(R.id.search);
            player = findViewById(R.id.player);
            searchInput = findViewById(R.id.search_edit_text);
            search = findViewById(R.id.search);
            back = findViewById(R.id.back);
        }
        //setting onclick listeners
        {
            back.setOnClickListener(b -> {
                back.setVisibility(View.INVISIBLE);
                searchInput.setVisibility(View.GONE);
                search.setVisibility(View.VISIBLE);
                adapter.clearSearch();
            });
            search.setOnClickListener(s -> {
                search.setVisibility(View.INVISIBLE);
                back.setVisibility(View.VISIBLE);
                searchInput.setVisibility(View.VISIBLE);
                if (iManager != null) {
                    iManager.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
                }
            });
            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.filter(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            play.setOnClickListener(p -> {
                if (currentSongIndex < 0 || currentSongIndex >= songs.size())
                    return;
                play(songs.get(currentSongIndex));
            });
            next.setOnClickListener(n -> {
                int nextIndex = currentSongIndex - 1;
                if (nextIndex < 0 || nextIndex >= songs.size())
                    return;
                play(songs.get(nextIndex));
            });
            previous.setOnClickListener(p -> {
                int previousIndex = currentSongIndex + 1;
                if (previousIndex < 0 || previousIndex >= songs.size())
                    return;
                play(songs.get(previousIndex));
            });
        }
        //setting up recycler view
        {
            lManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            adapter = new MusicAdapter(songs);
            musicRecyclerView.setLayoutManager(lManager);
            musicRecyclerView.setAdapter(adapter);
        }
        //listening to when a song is clicked
        {
            songClickedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int songIndex = intent.getIntExtra(Constants.SONG, -1);
                    if (songIndex == -1)
                        return;
                    play(songs.get(songIndex));
                }
            };
            registerReceiver(songClickedReceiver, new IntentFilter(Constants.ACTION_SONG_CLICKED));
        }
        //generating test songs
        {
            String[] testSongs = {"Love costs", "Am all alone", "without you equals pie R sqaured"};
            for (int i = 0; i < 0; i++) {
                Song s = new Song(testSongs[i % 3]);
                s.setPlaying(i == 3);
                songs.add(s);
                adapter.notifyItemChanged(i);
            }
        }
        loadSong();
        //display progress as songs are being loaded
        loadingsongs.setIndeterminate();
    }

    private void loadSong() {
        //dealing with file read permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            return;
        }
        //searching for songs on a different thread since its a blocking process
        new Thread(() -> {
            File externalStorage = Environment.getExternalStorageDirectory();
            list(externalStorage.listFiles());
            //returning to UI thread after
            new Handler(Looper.getMainLooper()).post(()->{
                adapter.notifyItemRangeChanged(0,songs.size());
                loadingsongs.stop();
                loadingsongs.setVisibility(View.GONE);
                searching.setVisibility(View.GONE);
            });
        }).start();
    }

    private void list(File[] files) {
        if (files == null)
            return;
        for (File f : files) {
            if (f.getName().endsWith(".mp3") || f.getName().endsWith(".opus") || f.getName().endsWith(".wav") || f.getName().endsWith(".acc")) {
                Song song = new Song(f);
                songs.add(song);
            }
            if (f.isDirectory())
                list(f.listFiles());
        }
    }

    private void play(Song song) {
        //normal media player stuff
        {
            try {
                mediaPlayer.setDataSource(song.getPath());
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(mp -> {
                    mp.start();
                    showProgress();
                    //changing the player controls
                    {
                        if (mediaPlayer.isPlaying()) {
                            player.setVisibility(View.VISIBLE);
                            play.setImageResource(R.drawable.pause);
                            songName.setText(song.getName());
                            artistName.setText(song.getArtist());
                            //un play previous song
                            for (Song s : songs) {
                                if (s.isPlaying()) {
                                    s.setPlaying(false);
                                    adapter.notifyItemChanged(songs.indexOf(s));
                                }
                            }
                            song.setPlaying(true);
                            currentSongIndex = songs.indexOf(song);
                            adapter.notifyItemChanged(currentSongIndex);
                            //scroll to the current song if its off screen
                            if (currentSongIndex > lManager.findLastCompletelyVisibleItemPosition())
                                musicRecyclerView.smoothScrollToPosition(currentSongIndex);
                        } else {
                            play.setImageResource(R.drawable.play);
                            songName.setText("");
                            artistName.setText("");
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, "error !!"+e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showProgress() {
        //todo implement this
        if(!mediaPlayer.isPlaying())
            return;
        float progress = (float)mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration();
        playingSongs.setProgress(progress);
        new Handler().postDelayed(this::showProgress,1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            loadSong();
    }
}