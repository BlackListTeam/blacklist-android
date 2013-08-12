package cat.andreurm.blacklist.activities;

import android.app.ProgressDialog;
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

public class PromoterCodeActivity extends Activity implements WebServiceCaller {

    WebService ws;
    Utils u;
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

    String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ws=new WebService(this);
        u=new Utils(this);

        context = getApplicationContext();
        regid = getRegistrationId(context);

        if (regid.length() == 0) {
            registerBackground();
        }
        gcm = GoogleCloudMessaging.getInstance(this);

        if(u.userAllowedToUseApp()){
            startActivity(new Intent(this,LoginActivity.class));
        }

        setContentView(R.layout.promoter);

        TextView txt = (TextView) findViewById(R.id.textView);
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txt.setTypeface(font);
        txt.setPaintFlags(txt.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(u.userAllowedToUseApp()){
            startActivity(new Intent(this,LoginActivity.class));
        }
    }


    public void validateCode(View view){
        EditText editText = (EditText) findViewById(R.id.promoInput);
        String message = editText.getText().toString();
        pdl= ProgressDialog.show(this, null, getString(R.string.loading), true, false);
        ws.validatePromoterCode(message);
    }

    @Override
    public void webServiceReady(Hashtable result) {

        Boolean auth_error= (Boolean) result.get("authError");
        if(auth_error){
            pdl.dismiss();
            Toast.makeText(getApplicationContext(), getString(R.string.ws_connection_error), Toast.LENGTH_SHORT).show();
            return;
        }

        Boolean is_valid= (Boolean) result.get("valid");
        if(is_valid){
            EditText editText = (EditText) findViewById(R.id.promoInput);
            String message = editText.getText().toString();
            u.allowUserToUseApp(message);
            startActivity(new Intent(this,RegisterActivity.class));
        }else{
            String strJunk=getString(R.string.error_promo_code);
            Toast.makeText(getApplicationContext(), strJunk, Toast.LENGTH_SHORT).show();
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
        /*new AsyncTask() {
            @Override
            protected String doInBackground(Void... params) {
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
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);*/
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
