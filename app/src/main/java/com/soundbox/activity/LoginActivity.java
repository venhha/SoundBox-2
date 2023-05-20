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

public class LoginActivity extends AppCompatActivity {
    EditText et_username, et_password;
    Button btn_login;
    ProgressBar pb_login;
    TextView tv_regHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveLogin(); // check login information
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title not the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//int flag, int mask

        setContentView(R.layout.activity_login);
        bindViews();
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
    private void retrieveLogin() {
        try {
            if (SharedPreferenceManager.getInstance(this).isLoggedIn()) {
                finish();
                startActivity(new Intent(this, HomeActivity.class));
                Toast.makeText(getApplicationContext(), "Chào mừng trở lại " + "Ven", Toast.LENGTH_SHORT);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT);
        }
    }
}