package cat.andreurm.blacklist.utils;

import android.content.SharedPreferences;

import android.app.Activity;
import android.util.Log;

import java.util.Date;

/**
 * Created by air on 18/07/13.
 */
public class Utils {

    public static final String PREFS_NAME = "configuration";

    public Activity act;

    public Utils(Activity a){
        act=a;
    }

    public Boolean userAllowedToUseApp(){
        SharedPreferences settings = act.getSharedPreferences(PREFS_NAME, 0);
        Boolean ret=settings.getBoolean("allowed", false);
        return ret;
    }

    public void allowUserToUseApp(String  promoterCode){
        SharedPreferences settings = act.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("allowed", true);
        editor.putString("promoterCode",promoterCode);
        editor.commit();
    }

    public String retrivePromoterCode(){
        SharedPreferences settings = act.getSharedPreferences(PREFS_NAME, 0);
        String ret=settings.getString("promoterCode", "");
        return ret;
    }

    public String retriveUserName(){
        SharedPreferences settings = act.getSharedPreferences(PREFS_NAME, 0);
        String ret=settings.getString("userName", "Nombre");
        return ret;
    }

    public void saveUserName(String name){
        SharedPreferences settings = act.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userName",name);
        editor.commit();
    }

    public String prettyDate(Date date){
        String ret="todo";
        return ret;
    }
}
