package cat.andreurm.blacklist.activities;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.utils.Utils;
import cat.andreurm.blacklist.utils.WebService;
import cat.andreurm.blacklist.utils.WebServiceCaller;

/**
 * Created by air on 24/07/13.
 */
public class LoginActivity extends Activity implements WebServiceCaller{

    Utils u;
    WebService ws;
    ProgressDialog pdl=null;





    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME =
            "onServerExpirationTimeMs";
    /**
     * Default lifespan (7 days) of a reservation until it is considered expired.
     */
    public static final long REGISTRATION_EXPIRY_TIME_MS = 1000 * 3600 * 24 * 7;

    /**
     * Substitute you own sender ID here.
     */
    String SENDER_ID = "631277367083";


    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        u=new Utils(this);
        ws=new WebService(this);

        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));

        TextView txt = (TextView) findViewById(R.id.login_name);
        txt.setTypeface(font);
        txt.setPaintFlags(txt.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txt.setText(u.retriveUserName());

        TextView txt2 = (TextView) findViewById(R.id.login_password);
        txt2.setTypeface(font);
        txt2.setPaintFlags(txt2.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);


        context = getApplicationContext();
        regid = getRegistrationId(context);

        if (regid.length() == 0) {
            registerBackground();
        }
        gcm = GoogleCloudMessaging.getInstance(this);
    }

    public void goToRegister(View view){
        startActivity(new Intent(this,RegisterActivity.class));
    }

    public void problems(View view){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType(getString(R.string.email_mime_type));
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{getString(R.string.email_to)});
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact));
        i.putExtra(Intent.EXTRA_TEXT   , getString(R.string.empty_string));
        try {
            startActivity(Intent.createChooser(i, getString(R.string.email_chooser_title)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(LoginActivity.this, getString(R.string.no_email_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void login (View view){
        EditText input_name= (EditText) findViewById(R.id.login_name);
        EditText input_pass= (EditText) findViewById(R.id.login_password);
        pdl= ProgressDialog.show(this, null, getString(R.string.loading), true, false);
        ws.login(input_name.getText().toString(), input_pass.getText().toString(),regid);
    }

    @Override
    public void webServiceReady(Hashtable result) {

        Boolean auth_error= (Boolean) result.get("authError");
        if(auth_error){
            pdl.dismiss();
            Toast.makeText(getApplicationContext(), (String) result.get("errorMessage"), Toast.LENGTH_SHORT).show();
            return;
        }

        Boolean logged= (Boolean) result.get("logged");
        if(logged){
            u.setSessionId((String) result.get("sessionId"));
            TextView txt = (TextView) findViewById(R.id.login_name);
            u.saveUserName(txt.getText().toString());
            startActivity(new Intent(this, TabHostActivity.class));
        }else{
            Toast.makeText(getApplicationContext(), (String) result.get("errorMessage"), Toast.LENGTH_SHORT).show();
        }
        pdl.dismiss();
    }

    @Override
    public void onBackPressed() {
    }








    /**
     * Gets the current registration id for application on GCM service.
     * <p>
     * If result is empty, the registration has failed.
     *
     * @return registration id, or empty string if the registration is not
     *         complete.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.length() == 0) {
            Log.v("AND-PUSH", "Registration not found.");
            return "";
        }
        // check if app was updated; if so, it must clear registration id to
        // avoid a race condition if GCM sends a message
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion || isRegistrationExpired()) {
            Log.v("AND-PUSH", "App version changed or registration expired.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(PromoterCodeActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }


    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Checks if the registration has expired.
     *
     * <p>To avoid the scenario where the device sends the registration to the
     * server but the server loses it, the app developer may choose to re-register
     * after REGISTRATION_EXPIRY_TIME_MS.
     *
     * @return true if the registration has expired.
     */
    private boolean isRegistrationExpired() {
        final SharedPreferences prefs = getGCMPreferences(context);
        // checks if the information is not stale
        long expirationTime =
                prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
        return System.currentTimeMillis() > expirationTime;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration id, app versionCode, and expiration time in the
     * application's shared preferences.
     */
    private void registerBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration id=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the message
                    // using the 'from' address in the message.

                    // Save the regid - no need to register again.
                    setRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                Log.v("AND-PUSH", msg);
                return msg;
            }



        }.execute(null, null, null);
    }

    /**
     * Stores the registration id, app versionCode, and expiration time in the
     * application's {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration id
     */
    private void setRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.v("AND-PUSH", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;

        Log.v("AND-PUSH", "Setting registration expiry time to " +
                new Timestamp(expirationTime));
        editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
        editor.commit();
    }

}