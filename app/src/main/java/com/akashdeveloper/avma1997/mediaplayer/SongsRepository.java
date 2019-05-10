package com.akashdeveloper.avma1997.mediaplayer;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;

public class SongsRepository {

    private SongDao songDao;
    private LiveData<List<Audio>> mSongs;
    private LiveData<List<Audio>> mFavourites;

    public SongsRepository(Application application){

        SongsDatabase db = SongsDatabase.getInstance(application);
        songDao = db.songDao();
        mSongs = songDao.getAllSongs();
        mFavourites=songDao.getAllSongsFavourites();

    }

    LiveData<List<Audio>> getAllSongs(){
        return mSongs;
    }
    LiveData<List<Audio>> getAllSongsFavourites(){
        return mFavourites;
    }



    public void insert(Audio audio){
        new insertAsyncTask(songDao).execute(audio);
    }

    private static class insertAsyncTask extends AsyncTask<Audio, Void, Void> {

        private SongDao mSongDao;

        insertAsyncTask(SongDao dao) {
            mSongDao = dao;
        }

        @Override
        protected Void doInBackground(final Audio... params) {
            mSongDao.insert(params[0]);
            return null;
        }
    }




}
