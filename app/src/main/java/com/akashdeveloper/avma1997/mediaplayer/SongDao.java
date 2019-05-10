package com.akashdeveloper.avma1997.mediaplayer;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SongDao {
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     void insert(Audio audio);

     @Update
    void update(Audio audio);

     @Delete
    void delete(Audio audio);

    @Query("Delete FROM songs_table")
    void deleteAllSongs();

    @Query("Select * from songs_table WHERE flag =0")
    LiveData<List<Audio>> getAllSongs();

    @Query("Select * from songs_table WHERE flag =1")
    LiveData<List<Audio>> getAllSongsFavourites();



}
