package cat.andreurm.blacklist.activities;

import android.app.Service;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Hashtable;

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
        ws.login(input_name.getText().toString(), input_pass.getText().toString());
    }

    @Override
    public void webServiceReady(Hashtable result) {

        Boolean auth_error= (Boolean) result.get("authError");
        if(auth_error){
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
    }

    @Override
    public void onBackPressed() {
    }

}