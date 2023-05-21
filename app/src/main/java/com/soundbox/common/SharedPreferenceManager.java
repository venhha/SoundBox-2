package com.soundbox.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.soundbox.model.User;


// Singleton Pattern
public class SharedPreferenceManager {
    private static SharedPreferenceManager mInstance;
    private static Context ctx;

    // single pattern
    SharedPreferences mSharedPreferences;

    // Constants || Pref name
    private static final String PREF_LOGIN = "loginPref";
    private static final String PREF_STATE_PLAYER = "state_playerPref";

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
        SharedPreferences.Editor editor = getSharedPreferences_LOGIN().edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    public User getUserInfo() {
        String email = getSharedPreferences_LOGIN().getString("email", null);
        String password = getSharedPreferences_LOGIN().getString("password", null);
        return new User(email, password);
    }

    public boolean isLoggedIn() {
        return getSharedPreferences_LOGIN().getString("email", null) != null;
    }

    public void logOut() {
        getSharedPreferences_LOGIN().edit().clear().apply();
    }

    private SharedPreferences getSharedPreferences_LOGIN() {
        if (mSharedPreferences == null) {
            mSharedPreferences = ctx.getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE);
            return mSharedPreferences;
        }
        return mSharedPreferences;
    }

}
