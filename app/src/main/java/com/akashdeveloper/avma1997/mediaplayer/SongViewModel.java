package com.akashdeveloper.avma1997.mediaplayer;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class SongViewModel extends AndroidViewModel {
    private SongsRepository songRepository;
    private LiveData<List<Audio>> mSongs;
    private LiveData<List<Audio>> mSongsFavourites;



    public SongViewModel(@NonNull Application application) {
        super(application);
        songRepository = new SongsRepository(application);
        mSongs = songRepository.getAllSongs();
        mSongsFavourites=songRepository.getAllSongsFavourites();
    }

    public LiveData<List<Audio>> getAllSongs() { return mSongs; }

    public LiveData<List<Audio>> getAllSongsFavourites() { return mSongsFavourites; }


    public void insert(Audio audio) { songRepository.insert(audio); }

}
