package com.soundbox.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.soundbox.R;
import com.soundbox.adapter.ListSongAdapter;
import com.soundbox.common.MyService;
import com.soundbox.model.Song;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private RelativeLayout layoutBottom;

    LinearLayout layoutHome, layoutUpload, layoutUser, layoutClickToLogin, layoutUserTrue;

    Button buttonTabHome, buttonTabUpload, buttonTabUser;
    TextView textView;
    FirebaseUser user;
    ProgressDialog progressDialog;
    ListView listView;

    List<String> songsNameList;
    List<String> songsUrlList;
    List<String> songsArtistList;
    List<String> songsDurationList;
    ListSongAdapter adapter;
    List<String> thumbnail;
    private Song newMusic;
    private boolean isPlaying;

    private ImageView imgMusic, imgPlayOrPause, imgClear;
    private TextView textViewTitleMusic, textViewSingleMusic;
    JcPlayerView jcPlayerView;
    List<JcAudio> jcAudios;

    // upload Music
    Uri uriSong, image;
    byte[] bytes;
    String fileName, songUrl, imageUrl;
    String songLength;
    FirebaseStorage storage;
    StorageReference storageReference;
    Boolean isLogin = false;
    EditText selectSongNameEditText;
    EditText artistName;
    TextView textViewUserLoginTrue, textViewPassLoginTrue, textUser;
    ImageView selectImage;
    Button uploadButton;
    ImageButton selectSong;
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    //end

    //Login - register
    TextInputEditText editTextEmail, editTextPassword;
    Button btnLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    Button btnReg;
    //end

    //logout
    Button logOutButton;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }

            newMusic = (Song) bundle.get("object_music");
            isPlaying = bundle.getBoolean("status_player");
            int actionMusic = bundle.getInt("action_music");
            handleLayoutMusic(actionMusic);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title not the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//int flag, int mask
        setContentView(R.layout.activity_home);
        bindNavigationBar();

        loadMusicOncreate();
        clickMusicOncreate();
    }

    @SuppressLint("ResourceAsColor")
    private void loadMusicOncreate() {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data_to_activity"));

        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setMessage("Please Wait...");

        jcPlayerView = findViewById(R.id.jcplayer);
        imgMusic = findViewById(R.id.img_music);
        imgPlayOrPause = findViewById(R.id.button_play_pause);
        imgClear = findViewById(R.id.button_clear);
        textViewTitleMusic = findViewById(R.id.text_view_title_music);
        textViewSingleMusic = findViewById(R.id.text_view_single_music);
        listView = findViewById(R.id.songsList);
        retrieveSongs();
    }
    public void retrieveSongs() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Songs");
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                songsNameList = new ArrayList<>();
                songsUrlList = new ArrayList<>();
                songsArtistList = new ArrayList<>();
                songsDurationList = new ArrayList<>();
                thumbnail = new ArrayList<>();
                jcAudios = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Song song = ds.getValue(Song.class);
                    songsNameList.add(song.getSongName());
                    songsUrlList.add(song.getSongUrl());
                    songsArtistList.add(song.getSongArtist());
                    songsDurationList.add(song.getSongDuration());
                    thumbnail.add(song.getImageUrl());

                    jcAudios.add(JcAudio.createFromURL(song.getSongName(), song.getSongUrl()));
                }
                adapter = new ListSongAdapter(getApplicationContext(), songsNameList, thumbnail, songsArtistList, songsDurationList, songsUrlList);


                jcPlayerView.initPlaylist(jcAudios, null);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "FAILED!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleLayoutMusic(int action) {
        switch (action) {
            case MyService.ACTION_START:
                layoutBottom.setVisibility(View.VISIBLE);
                showInforMusic();
                setStartusButtonPlayOrPause();
                break;
            case MyService.ACTION_PAUSE:
                setStartusButtonPlayOrPause();
                break;
            case MyService.ACTION_RESUME:
                setStartusButtonPlayOrPause();
                break;
            case MyService.ACTION_CLEAR:
                layoutBottom.setVisibility(View.GONE);
                break;

        }
    }
    private void showInforMusic() {
        if (newMusic == null) {
            return;
        }
        textViewTitleMusic.setText(newMusic.getSongName());
        textViewSingleMusic.setText(newMusic.getSongArtist());

        imgPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    sendActionToService(MyService.ACTION_PAUSE);
                } else {
                    sendActionToService(MyService.ACTION_RESUME);
                }
            }
        });


        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActionToService(MyService.ACTION_CLEAR);
            }
        });
    }
    private void setStartusButtonPlayOrPause() {
        if (isPlaying) {
            imgPlayOrPause.setImageResource(R.drawable.ic_pause_20);
        } else {
            imgPlayOrPause.setImageResource(R.drawable.ic_play_20);
        }
    }
    private void sendActionToService(int action) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("action_music_service", action);
        startService(intent);

    }


    private void clickMusicOncreate() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                jcPlayerView.playAudio(jcAudios.get(i));
                jcPlayerView.setVisibility(View.VISIBLE);
                jcPlayerView.createNotification();
                adapter.notifyDataSetChanged();
            }
        });
    }
    private LinearLayout btn_toHome, btn_toUpload, btn_toProfile;
    private void bindNavigationBar() {
        btn_toHome = findViewById(R.id.btn_toHome);
        btn_toUpload = findViewById(R.id.btn_toUpload);
        btn_toProfile = findViewById(R.id.btn_toProfile);

        btn_toHome.setOnClickListener(v -> {
            recreate();
        });

        btn_toUpload.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, UploadSongActivity.class));
            finish();
        });

        btn_toProfile.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            finish();
        });
    }
}
