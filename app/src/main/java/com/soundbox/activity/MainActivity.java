package com.soundbox.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundbox.R;
import com.soundbox.adapter.ListAdapter;
import com.soundbox.common.SharedPreferenceManager;
import com.soundbox.model.Song;
import com.soundbox.model.User;
import com.soundbox.network.MyService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

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
    ListAdapter adapter;
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
    //end
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
    private String TAG = "MainActivity debug";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clickTabLayout();
        Log.e(TAG, "clickTabLayout" );

        loadMusicOncreate();
        Log.e(TAG, "loadMusicOncreate" );
        clickMusicOncreate();
        Log.e(TAG, "clickMusicOncreate" );
        uploadOncreate();
        Log.e(TAG, "uploadOncreate" );
        loginOncreate();
        Log.e(TAG, "loginOncreate" );
        registerOncreate();
        Log.e(TAG, "registerOncreate" );

        logOutOncreate();
        Log.e(TAG, "logOutOncreate" );
        retrieveInfo();
    }

    private void clickTabLayout() {
        buttonTabHome = findViewById(R.id.button_tab_home);
        buttonTabUpload = findViewById(R.id.button_tab_upload);
        buttonTabUser = findViewById(R.id.button_tab_user);

        layoutHome = findViewById(R.id.layout_home);
        layoutUpload = findViewById(R.id.layout_upload);
        layoutUser = findViewById(R.id.layout_user);
        layoutClickToLogin = findViewById(R.id.layout_login_to_upload);
        layoutUserTrue = findViewById(R.id.layout_logout_user);

        buttonTabHome.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                setVisibilityLayout();
                setColorButton();
                buttonTabHome.setTextColor(Color.RED);
                layoutHome.setVisibility(View.VISIBLE);
            }
        });

        buttonTabUpload.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                setVisibilityLayout();
                setColorButton();
                buttonTabUpload.setTextColor(Color.RED);
                if (isLogin) {
                    layoutUpload.setVisibility(View.VISIBLE);
                } else {
                    layoutClickToLogin.setVisibility(View.VISIBLE);
                }
            }
        });
        buttonTabUser.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                setVisibilityLayout();
                setColorButton();

                buttonTabUser.setTextColor(Color.RED);

                if (isLogin) {
                    layoutUserTrue.setVisibility(View.VISIBLE);
                } else {
                    layoutUser.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void setColorButton() {
        buttonTabUser.setTextColor(Color.WHITE);
        buttonTabUpload.setTextColor(Color.WHITE);
        buttonTabHome.setTextColor(Color.WHITE);
    }


    @SuppressLint("ResourceAsColor")

    private void loadMusicOncreate() {
        auth = FirebaseAuth.getInstance();
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

    private void uploadOncreate() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        selectSongNameEditText = findViewById(R.id.selectSong);
        selectImage = findViewById(R.id.selectImage);
        uploadButton = findViewById(R.id.uploadSongButton);
        artistName = findViewById(R.id.artistNameEditText);
        selectSong = findViewById(R.id.selectSongButton);

        selectSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickSong();
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

    }

    public void loginOncreate() {
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.processBar);
        textViewUserLoginTrue = findViewById(R.id.textViewuser_login);
        textViewPassLoginTrue = findViewById(R.id.textView_pass_login);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf((editTextPassword.getText()));
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    isLogin = true;
                                    setVisibilityLayout();
                                    setColorButton();
                                    buttonTabUpload.setTextColor(Color.RED);
                                    layoutUpload.setVisibility(View.VISIBLE);
                                    textViewUserLoginTrue.setText(editTextEmail.getText());
                                    textViewPassLoginTrue.setText(editTextPassword.getText());
                                    SharedPreferenceManager manager = SharedPreferenceManager.getInstance(getApplicationContext());
                                    Toast.makeText(MainActivity.this, "Login success",
                                            Toast.LENGTH_SHORT).show();
                                    manager.saveUserInfo(email, password);
                                } else {
                                    Toast.makeText(MainActivity.this, "Login fail",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }


        });
    }

    public void registerOncreate() {
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        btnReg = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.processBar);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.processBar);


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf((editTextPassword.getText()));
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    Toast.makeText(MainActivity.this, "Account created. Click Login to upload file",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Account fail",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }

    public void logOutOncreate() {
        logOutButton = findViewById(R.id.btnLogout);
        SharedPreferenceManager manager = SharedPreferenceManager.getInstance(this.getApplicationContext());

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLogin = false;
                manager.logOut();
                setVisibilityLayout();
                layoutUser.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setVisibilityLayout() {
        layoutUserTrue.setVisibility(View.GONE);
        layoutHome.setVisibility(View.GONE);
        layoutUser.setVisibility(View.GONE);
        layoutUpload.setVisibility(View.GONE);
        layoutClickToLogin.setVisibility(View.GONE);
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
                adapter = new ListAdapter(getApplicationContext(), songsNameList, thumbnail, songsArtistList, songsDurationList, songsUrlList);


                jcPlayerView.initPlaylist(jcAudios, null);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "FAILED!", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
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

    private void pickSong() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, 1);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 1 && resultCode == RESULT_OK) {
                uriSong = data.getData();
//                Log.i("uri", songName.toString());
                fileName = getFileName(uriSong);
                selectSongNameEditText.setText(fileName);
                songLength = getSongDuration(uriSong);
                //Log.i("duration", songLength);
            }
            if (requestCode == 2 && resultCode == RESULT_OK) {
//                Log.i("image",data.toString());
                image = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                    selectImage.setImageBitmap(bitmap);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    bytes = byteArrayOutputStream.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public String getSongDuration(Uri song) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(getApplicationContext(), song);
        String durationString = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long time = Long.parseLong(durationString);
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(time);
        int totalSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(time);
        int seconds = totalSeconds - (minutes * 60);
        if (String.valueOf(seconds).length() == 1) {
            return minutes + ":0" + seconds;
        } else {
            return minutes + ":" + seconds;
        }
    }

    public void upload(View view) {
//        if (uriSong == null){
//            Toast.makeText(this, "Please select a song", Toast.LENGTH_SHORT).show();
//        }
//        else if (selectSongNameEditText.getText().toString().equals("")){
//            Toast.makeText(this, "Song name cannot be empty!", Toast.LENGTH_SHORT).show();
//        }
//        else if(artistName.getText().toString().equals("")){
//            Toast.makeText(this, "Please add Artist, album name", Toast.LENGTH_SHORT).show();
//        }
//        else if (image == null){
//            Toast.makeText(this, "Please select a Thumbnail", Toast.LENGTH_SHORT).show();
//        }
//        else {
        fileName = selectSongNameEditText.getText().toString();
        String artist = artistName.getText().toString();
        uploadImageToServer(bytes, fileName);
        uploadFileToServer(uriSong, fileName, artist, songLength);
        //}
    }

    public void uploadImageToServer(byte[] image, String fileName) {
        UploadTask uploadTask = storageReference.child("images/" + UUID.randomUUID().toString()).putBytes(image);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                while (!task.isComplete()) ;
                Uri urlsong = task.getResult();
                imageUrl = urlsong.toString();
//                Log.i("image url", imageUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("image url", "failed");
            }
        });
    }

    public void uploadFileToServer(Uri uri, final String songName, final String artist, final String duration) {
        StorageReference filePath = storageReference.child("Audios").child(songName);
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Log.i("success", "upload");
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlSong = uriTask.getResult();
                songUrl = urlSong.toString();

                Log.i("success url ", songUrl);
                uploadDetailsToDatabase(fileName, songUrl, imageUrl, artist, duration, currentFirebaseUser.getUid());
                progressDialog.dismiss();
            }

        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                int currentProgress = (int) progress;
                progressDialog.setMessage("Uploading: " + currentProgress + "%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.i("success", "upload");
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Upload Failed! Please Try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadDetailsToDatabase(String songName, String songUrl, String imageUrl, String artistName, String songDuration, String idArtist) {
        Song song = new Song(songName, songUrl, imageUrl, artistName, songDuration, idArtist);
        FirebaseDatabase.getInstance().getReference("Songs")
                .push().setValue(song).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i("database", "upload success");
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Song Uploaded to Database", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void retrieveInfo() {
        try {
            SharedPreferenceManager manager = SharedPreferenceManager.getInstance(this.getApplicationContext());
            if (manager.isLoggedIn()) {
                User user = manager.getUserInfo();
                mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword());
                isLogin = true;
                textViewUserLoginTrue.setText(manager.getUserInfo().getEmail());
                textViewPassLoginTrue.setText(manager.getUserInfo().getPassword());
                Toast.makeText(this, "Login with email " + manager.getUserInfo().getEmail(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Toast.makeText(this, "Retrieve User Info FAILED", Toast.LENGTH_SHORT).show();
        }
    }

}


