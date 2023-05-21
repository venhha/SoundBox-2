package com.soundbox.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.soundbox.R;
import com.soundbox.common.SharedPreferenceManager;
import com.soundbox.model.User;

public class ProfileActivity extends AppCompatActivity {
    ImageView btn_logout;
    TextView tv_name, tv_name_profile, tv_email_profile, tv_phoneNumber_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title not the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//int flag, int mask
        setContentView(R.layout.activity_profile);
        bindNavigationBar();
        bindViews();
        loadData();
    }

    private void bindViews() {
//        tv_name = findViewById(R.id.tv_name);
//        tv_name_profile = findViewById(R.id.tv_name_profile);
//        tv_phoneNumber_profile = findViewById(R.id.tv_phoneNumber_profile);
        tv_email_profile = findViewById(R.id.tv_email_profile);
        btn_logout = findViewById(R.id.btn_logout_profile);

        btn_logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            SharedPreferenceManager.getInstance(ProfileActivity.this).logOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            Toast.makeText(ProfileActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadData() {
        tv_email_profile.setText(SharedPreferenceManager.getInstance(ProfileActivity.this).getUserInfo().getEmail());
    }

    private LinearLayout btn_toHome, btn_toUpload, btn_toProfile;

    private void bindNavigationBar() {
        btn_toHome = findViewById(R.id.btn_toHome);
        btn_toUpload = findViewById(R.id.btn_toUpload);
        btn_toProfile = findViewById(R.id.btn_toProfile);

        btn_toHome.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish();
        });

        btn_toUpload.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, UploadSongActivity.class));
            finish();
        });

//        btn_toProfile.setOnClickListener(v -> {
//            recreate();
//        });
    }
}