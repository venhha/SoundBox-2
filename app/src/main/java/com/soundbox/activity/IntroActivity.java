package com.soundbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.soundbox.R;
import com.soundbox.common.SharedPreferenceManager;
import com.soundbox.model.User;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title not the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//int flag, int mask

        setContentView(R.layout.activity_intro);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                retrieveLogin();
                Intent intent = new Intent(IntroActivity.this, HomeActivity.class);
                finish();
                startActivity(intent);
            }
        }, 1500); // milliseconds
    }

    private void retrieveLogin() {
        try {
            if (SharedPreferenceManager.getInstance(this).isLoggedIn()) {
                User user = SharedPreferenceManager.getInstance(this).getUserInfo();
                // to-do check login before signIn again
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(user.getEmail(), user.getPassword());
                }
                Toast.makeText(getApplicationContext(), "Chào mừng trở lại\n" + user.getEmail(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}