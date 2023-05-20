package com.soundbox.adapter;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.soundbox.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    List<String> songNames;
    List<String> thumbnails;
    List<String> songArtist;
    List<String> songDuration;
    Context context;
    ImageView imgView;
    FirebaseStorage storage;
    List<String> songsUrlList;

    public ListAdapter(Context context, List<String> songNames, List<String> thumbnails, List<String> songArtist, List<String> songDuration, List<String> songUrl) {
        this.context = context;
        this.songNames = songNames;
        this.thumbnails = thumbnails;
        this.songArtist = songArtist;
        this.songDuration = songDuration;
        this.songsUrlList =songUrl;
    }

    @Override
    public int getCount() {
        return songNames.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        storage = FirebaseStorage.getInstance();
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.songs_list_layout, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        imgView = view.findViewById(R.id.currentlyPlaying);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference httpsReference = storage.getReferenceFromUrl(songsUrlList.get(i));

                httpsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalFilesDir(context,DIRECTORY_DOWNLOADS,songNames.get(i)+".mpeg");
                        downloadManager.enqueue(request);
                    }
                });



            }
        });
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(15)
                .build();

        Picasso.get().load(thumbnails.get(i)).transform(transformation).into(viewHolder.thumbnail);
        viewHolder.songName.setText(songNames.get(i));
        viewHolder.artistName.setText(songArtist.get(i));
        viewHolder.songDuration.setText(songDuration.get(i));
        return view;
    }

    private static class ViewHolder{
       TextView songName;
       TextView artistName;
       TextView songDuration;
       ImageView thumbnail;
       CardView cardView;
       ImageView currentlyPlaying;

        ViewHolder(View view){
            songName = view.findViewById(R.id.songName);
            thumbnail = view.findViewById(R.id.songThumbnail);
            artistName = view.findViewById(R.id.artistName);
            songDuration = view.findViewById(R.id.songDuration);
            cardView = view.findViewById(R.id.cardView);
            currentlyPlaying = view.findViewById(R.id.currentlyPlaying);
        }
    }
}
