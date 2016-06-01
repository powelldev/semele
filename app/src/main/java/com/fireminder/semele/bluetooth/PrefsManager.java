package com.fireminder.semele.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
  SharedPreferences preferences;

  public static final String CURRENT_USER_AGE = "current_user_age";
  public static final int USER_AGE_DEFAULT = 25;

  public PrefsManager(Context c) {
    preferences = c.getSharedPreferences("shared-prefs", Context.MODE_PRIVATE);
  }

  public int getCurrentUserAge() {
    return preferences.getInt(CURRENT_USER_AGE, USER_AGE_DEFAULT);
  }

}
