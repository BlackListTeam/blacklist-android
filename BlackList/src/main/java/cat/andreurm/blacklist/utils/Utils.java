package cat.andreurm.blacklist.utils;

import android.content.SharedPreferences;

import android.app.Activity;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import cat.andreurm.blacklist.R;

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

    public void setSessionId(String sessionId){
        SharedPreferences settings = act.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("sessionId",sessionId);
        editor.commit();
    }

    public String getSessionId(){
        SharedPreferences settings = act.getSharedPreferences(PREFS_NAME, 0);
        String ret=settings.getString("sessionId", "");
        return ret;
    }

    public String prettyDate(Date date){
        String ret="todo";
        return ret;
    }

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
}
