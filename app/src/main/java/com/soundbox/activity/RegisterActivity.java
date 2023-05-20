package com.soundbox.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class RegisterActivity extends AppCompatActivity {
    EditText et_username, et_password, et_password_rep;
    Button btn_reg;
    ProgressBar pb_reg;
    TextView tv_loginHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title not the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//int flag, int mask

        setContentView(R.layout.activity_register);
        bindViews();
    }

    private void bindViews() {
        et_username = findViewById(R.id.et_username_reg);
        et_password = findViewById(R.id.et_password_reg);
        et_password_rep = findViewById(R.id.et_password_reg_repeat);
        btn_reg = findViewById(R.id.btn_register);
        pb_reg = findViewById(R.id.pb_reg);
        tv_loginHere = findViewById(R.id.btn_loginHere);

        btn_reg.setOnClickListener(v -> {
            handleRegister();
        });
        tv_loginHere.setOnClickListener(v -> {
            finish();
        });
    }

    private void handleRegister() {
        String email = et_username.getText().toString();
        String password = et_password.getText().toString();
        String password_rep = et_password_rep.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Nhập Email", Toast.LENGTH_SHORT).show();
            et_username.clearFocus();
            return;
        } else if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password_rep)) {
            Toast.makeText(RegisterActivity.this, "Nhập mật khẩu", Toast.LENGTH_SHORT).show();
            et_password.clearFocus();
            return;
        } else if (!TextUtils.equals(password, password_rep)) {
            System.out.println(password_rep + "|||" + password);
            Toast.makeText(RegisterActivity.this, "Lặp lại không đúng", Toast.LENGTH_SHORT).show();
            et_password_rep.clearFocus();
            return;
        }
        MyUtils.getProgressBarUtil().set(pb_reg, btn_reg).show();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            MyUtils.getProgressBarUtil().set(pb_reg, btn_reg).hide();
                            SharedPreferenceManager.getInstance(RegisterActivity.this).saveUserInfo(email,password);
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            MyUtils.getProgressBarUtil().set(pb_reg, btn_reg).hide();
                            et_password.setText("");
                            et_password_rep.setText("");
                            et_username.clearFocus();
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        //todo
    }
}