package com.akashdeveloper.avma1997.mediaplayer;

import android.app.Application;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;

//public class SongsRepository {
//
//    private SongDao songDao;
//    private List<Audio> mSongs;
//    private List<Audio> mFavourites;
//
//    public SongsRepository(Application application){
//
//        SongsDatabase db = SongsDatabase.getInstance(application);
//        songDao = db.songDao();
//        mSongs = songDao.getAllSongs();
//        mFavourites=songDao.getAllSongsFavourites();
//
//    }
//
//    List<Audio> getAllSongs(){
//        return mSongs;
//    }
//    List<Audio> getAllSongsFavourites(){
//        return mFavourites;
//    }





  public class insertAsyncTask extends AsyncTask<Audio, Void, Void> {

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

    class selectAsyncTask extends AsyncTask<Void,ArrayList<Audio>,ArrayList<Audio>>{

      private SongDao mSongDao;
      selectAsyncTask(SongDao dao)
        {
         mSongDao= dao;
        }



        @Override
        protected ArrayList<Audio> doInBackground(Void... voids) {
             ArrayList<Audio> audio = (ArrayList<Audio>) mSongDao.getAllSongs();
            return audio;
        }
    }
class selectFavAsyncTask extends AsyncTask<Void,ArrayList<Audio>,ArrayList<Audio>>{

    private SongDao mSongDao;
    selectFavAsyncTask(SongDao dao)
    {
        mSongDao= dao;
    }



    @Override
    protected ArrayList<Audio> doInBackground(Void... voids) {
        ArrayList<Audio> audio = (ArrayList<Audio>) mSongDao.getAllSongsFavourites();
        return audio;
    }
}







