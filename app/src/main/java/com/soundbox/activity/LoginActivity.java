package com.soundbox.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.soundbox.R;
import com.soundbox.common.MyUtils;
import com.soundbox.common.SharedPreferenceManager;
import com.soundbox.model.User;

public class LoginActivity extends AppCompatActivity {
    EditText et_username, et_password;
    Button btn_login;
    ProgressBar pb_login;
    TextView tv_regHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title not the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//int flag, int mask

        setContentView(R.layout.activity_login);
        bindViews();
        bindNavigationBar();
    }

    private void bindViews() {
        et_username = findViewById(R.id.et_username_login);
        et_password = findViewById(R.id.et_password_login);
        btn_login = findViewById(R.id.btn_login);
        pb_login = findViewById(R.id.pb_login);
        tv_regHere = findViewById(R.id.btn_regHere);

        btn_login.setOnClickListener(v -> {
            handleLogin();
        });
        tv_regHere.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void handleLogin() {
        String email = et_username.getText().toString();
        String password = et_password.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Nhập Email", Toast.LENGTH_SHORT).show();
            et_username.clearFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Nhập mật khẩu", Toast.LENGTH_SHORT).show();
            et_password.clearFocus();
            return;
        }
        MyUtils.getProgressBarUtil().set(pb_login, btn_login).show();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // login successful

                            //save user info for the next login without type again
                            SharedPreferenceManager.getInstance(getApplicationContext()).saveUserInfo(email, password);
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công\n" + email, Toast.LENGTH_SHORT).show();
                        } else {
                            // login fail
                            Toast.makeText(LoginActivity.this, "Tài khoản không đúng", Toast.LENGTH_SHORT).show();
                        }
                        MyUtils.getProgressBarUtil().set(pb_login, btn_login).hide();
                    }
                }
        );

    }
    private LinearLayout btn_toHome, btn_toUpload, btn_toProfile;

    private void bindNavigationBar() {
        btn_toHome = findViewById(R.id.btn_toHome);
        btn_toUpload = findViewById(R.id.btn_toUpload);
        btn_toProfile = findViewById(R.id.btn_toProfile);

        btn_toHome.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        });

        btn_toUpload.setOnClickListener(v -> {
            if (SharedPreferenceManager.getInstance(getApplicationContext()).isLoggedIn()){
                startActivity(new Intent(LoginActivity.this, UploadSongActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Bạn cần đăng nhập để sử dụng chức năng này", Toast.LENGTH_SHORT).show();
            }        });

//        btn_toProfile.setOnClickListener(v -> {
//            recreate();
//        });
    }
}