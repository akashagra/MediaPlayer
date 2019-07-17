package com.akashdeveloper.avma1997.mediaplayer;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {Audio.class},version = 1,exportSchema = false)
public abstract class SongsDatabase extends RoomDatabase {

    private static SongsDatabase instance;

    public abstract SongDao songDao();

    public static synchronized SongsDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    SongsDatabase.class, "songs_database")
                    .fallbackToDestructiveMigration().addCallback(roomCallback).build();
        }
        return instance;
    }

        private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
            @Override
            public void onCreate(SupportSQLiteDatabase db) {
                new PopulateDbAsyncTask(instance).execute();
                super.onCreate(db);
            }
        };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private  SongDao songDao;

        private PopulateDbAsyncTask(SongsDatabase db) {
            songDao = db.songDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // This can be used to insert default values as well

            return null;
        }
    }


}








