package com.soundbox.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.soundbox.model.User;


// Singleton Pattern
public class SharedPreferenceManager {
    private static SharedPreferenceManager mInstance;
    private static Context ctx;

    //test
    private static final String _NAME = "testSharedPreference";
    private static final String PREF_LOGIN = "loginPref";

    private SharedPreferenceManager(Context context) {
        ctx = context;
    }

    // synchronized: đồng bộ hóa các phương thức và khối mã để tránh tình trạng xung đột giữa các luồng thực thi,
    // chỉ có một luồng thực thi được phép truy cập vào phương thức hoặc khối mã đó tại một thời điểm.
    public static synchronized SharedPreferenceManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferenceManager(context);
        }
        return mInstance;
    }

    // save user info for the next login --without type email pass
    public void saveUserInfo(String email, String password) {
        SharedPreferences.Editor editor = getSharedPreferences(PREF_LOGIN).edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
        System.out.println("saveUserInfo SUCCESS");
    }

    public User getUserInfo() {
        String email = getSharedPreferences(PREF_LOGIN).getString("email", null);
        String password = getSharedPreferences(PREF_LOGIN).getString("password", null);
        return new User(email, password);
    }

    public boolean isLoggedIn() {
        return getSharedPreferences(PREF_LOGIN).getString("email", null) != null;
    }

    public void logOut() {
        getSharedPreferences(PREF_LOGIN).edit().clear().apply();
        System.out.println("logOut SUCCESS");
    }

    private SharedPreferences getSharedPreferences(String prefName) {
        return ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }
}
