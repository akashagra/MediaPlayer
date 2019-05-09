package com.akashdeveloper.avma1997.mediaplayer;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> {

    private ArrayList<Audio> audioList;
    private SongsClickListener mListener;
    private Context mContext;

    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.single_row_view, parent, false);
        return new SongsViewHolder(itemView, mListener);

    }

    @Override
    public void onBindViewHolder(@NonNull final SongsViewHolder songsViewHolder, final int position) {

        songsViewHolder.title_tv.setText(audioList.get(position).getTitle());
        Log.i("music",audioList.get(position).getTitle());
        songsViewHolder.album_tv.setText(audioList.get(position).getAlbum());
        songsViewHolder.artist_tv.setText(audioList.get(position).getArtist());
        songsViewHolder.buttonViewOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songsViewHolder.mClickListener.onItemClick(v,position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public interface SongsClickListener {
        void onItemClick(View view, int position);
       // void onTextItemClick(View view,int position);
    }

    public SongsAdapter( Context mContext,ArrayList<Audio> audioList, SongsClickListener mListener) {

        this.audioList = audioList;
        this.mListener = mListener;
        this.mContext = mContext;
    }

    public static class SongsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title_tv, album_tv, artist_tv;
        TextView buttonViewOptions;
        ImageView play_pause;
        SongsClickListener mClickListener;


        public SongsViewHolder(@NonNull View itemView, SongsClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(this);
            mClickListener = listener;
            title_tv = itemView.findViewById(R.id.title_tv_song);
            album_tv = itemView.findViewById(R.id.album_tv_song);
            artist_tv = itemView.findViewById(R.id.artist_tv_song);
            play_pause=itemView.findViewById(R.id.play_pause_iv);
            buttonViewOptions=itemView.findViewById(R.id.textViewOptions);

        }

        @Override
        public void onClick(View view) {

            int id = view.getId();
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {

                 if (id == R.id.song_view) {
                    mClickListener.onItemClick(view, position);

                }




            }
        }


    }
}







