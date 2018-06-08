package vkpp.flippy.ru.vkpp.data;

import android.content.Context;
import android.content.SharedPreferences;

import vkpp.flippy.ru.vkpp.App;

public class Prefs {
    private static final Prefs ourInstance = new Prefs();

    public static Prefs getInstance() {
        return ourInstance;
    }

    private SharedPreferences sp = App.context.getSharedPreferences(Prefs.class.getSimpleName(), Context.MODE_PRIVATE);

    private Prefs() {
    }

    private final String TOKEN = "token";

    public void setToken(String token) {
        sp.edit().putString(TOKEN, token).apply();
    }

    public String getToken() {
        return sp.getString(TOKEN, null);
    }
}
