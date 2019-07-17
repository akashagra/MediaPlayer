package com.akashdeveloper.avma1997.mediaplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ramotion.circlemenu.CircleMenuView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 0;
    ArrayList<Audio> audioList;
    ArrayList<Audio> playList;
    private MediaPlayerService player;
    boolean serviceBound = false;
    RecyclerView recyclerView;
    SongsAdapter adapter;
    private SongDao songDao;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.akashdeveloper.avma1997.PlayNewAudio";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.songs_recycler_view);
        audioList=new ArrayList<>();
        playList=new ArrayList<>();
        SongsDatabase db = SongsDatabase.getInstance(this);
        songDao = db.songDao();
        adapter= new SongsAdapter(this,audioList, new SongsAdapter.SongsClickListener() {
            @Override
            public void onItemClick(View view,  final int position)  {
                if (view instanceof TextView) {
                    PopupMenu popup = new PopupMenu(MainActivity.this, view);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.options_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu1:
                                    Log.i("aa_rha", "playmenu");
                                    playAudio(position);
                                    break;
                                case R.id.menu2:
                                    if (position != audioList.size() - 1) {
                                        playAudio(position + 1);
                                    } else {
                                        playAudio(0);
                                    }
                                    break;
                                case R.id.menu3:
                                    Audio audio = audioList.get(position);
                                    audio.flag=0;
                                    new insertAsyncTask(songDao).execute(audioList.get(position));
                                    break;
                                case R.id.menu4:
                                    Audio audio2 = audioList.get(position);
                                    audio2.flag=1;
                                    new insertAsyncTask(songDao).execute(audioList.get(position));
                                    break;
                                case R.id.menu5:
                                    Uri si= Uri.parse(audioList.get(position).getData());
                                    String s = si.getPath();
                                    File k = new File(s);  // set File from path
                                    if (s != null) {      // file.exists
                                        Log.i("ringtone","cv");

                                        ContentValues values = new ContentValues();
                                        values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
                                        values.put(MediaStore.MediaColumns.TITLE, "ring");
                                        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                                        values.put(MediaStore.MediaColumns.SIZE, k.length());
                                        values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
                                        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                                        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                                        values.put(MediaStore.Audio.Media.IS_ALARM, true);
                                        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

                                        Uri uri = MediaStore.Audio.Media.getContentUriForPath(k
                                                .getAbsolutePath());
//                                        getContentResolver().delete(
//                                                uri,
//                                                MediaStore.MediaColumns.DATA + "=\""
//                                                        + k.getAbsolutePath() + "\"", null);
                                        Uri newUri = getContentResolver().insert(uri, values);

                                        try {
                                            Log.i("ringtone","set");
                                            RingtoneManager.setActualDefaultRingtoneUri(
                                                    MainActivity.this, RingtoneManager.TYPE_RINGTONE,
                                                    newUri);
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                            Log.i("ringtone","problem");

                                        }
                                    }

                                    //handle menu5 click
                                    break;
                                case R.id.menu6:
                                    stopAudio(position);

                                    break;
                                case R.id.menu7:

                                    break;

                                case R.id.menu8:
                                    Uri del=Uri.parse(audioList.get(position).getData());
                                    String path= del.getPath();
                                    File file= new File(path);
                                    if(path!=null){

                                        Uri uri = MediaStore.Audio.Media.getContentUriForPath(file
                                                .getAbsolutePath());
                                        getContentResolver().delete(
                                                uri,
                                                MediaStore.MediaColumns.DATA + "=\""
                                                        + file.getAbsolutePath() + "\"", null);
                                        loadAudio();

                                    }
                                    break;

                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();

                } else{
                    playAudio(position);

                }
            }


        });





        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(decoration);
        if(grantPermission())
            loadAudio();

        final CircleMenuView menu = (CircleMenuView) findViewById(R.id.circle_menu);
        menu.setEventListener(new CircleMenuView.EventListener() {
            @Override
            public void onMenuOpenAnimationStart(@NonNull CircleMenuView view) {
                Log.d("D", "onMenuOpenAnimationStart");
            }

            @Override
            public void onMenuOpenAnimationEnd(@NonNull CircleMenuView view) {
                Log.d("D", "onMenuOpenAnimationEnd");
            }

            @Override
            public void onMenuCloseAnimationStart(@NonNull CircleMenuView view) {
                Log.d("D", "onMenuCloseAnimationStart");
            }

            @Override
            public void onMenuCloseAnimationEnd(@NonNull CircleMenuView view) {
                Log.d("D", "onMenuCloseAnimationEnd");
            }

            @Override
            public void onButtonClickAnimationStart(@NonNull CircleMenuView view, int index) {

                if(index==0){

                    try {
                        ArrayList<Audio> audio = new selectAsyncTask(songDao).execute().get();
                        audioList.clear();
                        audioList.addAll(audio);
                        adapter.notifyDataSetChanged();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                  ArrayList<Audio> audio = (ArrayList<Audio>) songViewModel.getAllSongs();


//                 songViewModel.getAllSongs().observe(MainActivity.this, new Observer<List<Audio>>() {
//                     @Override
//                     public void onChanged(List<Audio> audio) {
//                         audioList.clear();
//                         audioList.addAll(audio);
//                         adapter.notifyDataSetChanged();
//
//
//                     }
//                 });

                }

                if(index==1){

                    try {
                        ArrayList<Audio> audio = new selectFavAsyncTask(songDao).execute().get();
                        audioList.clear();
                        audioList.addAll(audio);
                        adapter.notifyDataSetChanged();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }




                if(index==4){
                    loadAudio();
                }



            }

            @Override
            public void onButtonClickAnimationEnd(@NonNull CircleMenuView view, int index) {
                Log.d("D", "onButtonClickAnimationEnd| index: " + index);
            }



        });









    }

//
//    public class SectionsPagerAdapter extends FragmentPagerAdapter {
//
//        public SectionsPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            // getItem is called to instantiate the fragment for the given page.
//            // Return a PlaceholderFragment (defined as a static inner class below).
//            // contact fragment on the first tab is used to display the list of contacts
//            // message fragment is used to display the list of messages
//            switch (position) {
//                case 0:
//                    SongsFragment songsFragment= new SongsFragment();
//                    return songsFragment;
////                    ContactFragment contactFragment =new ContactFragment();
////                    return contactFragment;
//                case 1:
//                    Fragment fragment2= new Fragment();
//                    return fragment2;
////                    SentMessageFragment messageFragment= new SentMessageFragment();
////                    return messageFragment;
//                case 2:
//                    Fragment fragment3= new Fragment();
//                    return fragment3;
//
//
//                default:
//                    return null;
//            }
//        }
//
//        @Override
//        public int getCount() {
//            // Show 3 total pages.
//            return 3;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "Songs";
//                case 1:
//                    return "PlayList";
//                case 2:
//                     return "Favourites";
//            }
//            return null;
//        }
//    }




    private boolean grantPermission() {

        if (SDK_INT >= Build.VERSION_CODES.M) {
            int permissionReadPhoneState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
            int permissionStorage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int permissionWriteStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permissionWriteSettings=ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS);
            List<String> listPermissionsNeeded = new ArrayList<>();

            if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
            }

            if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if(permissionWriteStorage != PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(getApplicationContext())) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 200);
                }
            }
//            if(permissionWriteSettings != PackageManager.PERMISSION_GRANTED){
//                listPermissionsNeeded.add(Manifest.permission.WRITE_SETTINGS);
//            }


            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 0);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        String TAG = "LOG_PERMISSION";
        Log.d(TAG, "Permission callback called-------");
        Log.d(TAG,requestCode+"");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.WRITE_SETTINGS,PackageManager.PERMISSION_GRANTED);


                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions

                    if (perms.get(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE)  == PackageManager.PERMISSION_GRANTED && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
//                            perms.get(Manifest.permission.WRITE_SETTINGS)==PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d(TAG, "Phone state and storage permissions granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        loadAudio();
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                      //shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                            showDialogOK("Phone state and storage permissions required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    grantPermission();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }
    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
            Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;

        }
    };

    private void playAudio(int audioIndex) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudio(audioList);
            storage.storeAudioIndex(audioIndex);
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    private void stopAudio(int audioindex) {

        if (serviceBound) {
            unbindService(serviceConnection);
            player.stopSelf();
        }
    }


    private void loadAudio() {
        ContentResolver contentResolver = getContentResolver();
        // The MediaStore class is a content provider
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            if(audioList.size()>0)
            {audioList.clear();}
            while (cursor.moveToNext()) {
                int id =cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                Log.d("songs",title);

                // Save to audioList
                audioList.add(new Audio(id,data, title, album, artist));
                //adapter.notifyDataSetChanged();
            }
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

