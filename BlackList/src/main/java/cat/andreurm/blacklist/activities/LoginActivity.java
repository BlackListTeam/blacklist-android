package cat.andreurm.blacklist.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.utils.Utils;
import cat.andreurm.blacklist.utils.WebService;

/**
 * Created by air on 24/07/13.
 */
public class LoginActivity extends Activity {

    Utils   u;
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

    @Override
    public void onBackPressed() {
    }

}