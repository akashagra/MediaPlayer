package com.akashdeveloper.avma1997.mediaplayer;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {Audio.class}, version = 1)
public abstract class SongsDatabase extends RoomDatabase {

    private static SongsDatabase instance;





}
