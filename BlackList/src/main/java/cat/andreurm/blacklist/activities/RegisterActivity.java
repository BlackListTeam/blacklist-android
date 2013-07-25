package cat.andreurm.blacklist.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Hashtable;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.model.User;
import cat.andreurm.blacklist.utils.Utils;
import cat.andreurm.blacklist.utils.WebService;
import cat.andreurm.blacklist.utils.WebServiceCaller;

public class RegisterActivity extends Activity implements WebServiceCaller {

    WebService ws;
    Utils u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ws=new WebService(this);
        u=new Utils(this);

        TextView txtCompleta = (TextView) findViewById(R.id.textViewCompleta);
        TextView txtAutoriza = (TextView) findViewById(R.id.textViewAutoriza);
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txtCompleta.setTypeface(font);
        txtCompleta.setPaintFlags(txtCompleta.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtAutoriza.setTypeface(font);
        txtAutoriza.setPaintFlags(txtAutoriza.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public void addUser(View view){
        EditText nameText = (EditText) findViewById(R.id.register_name);
        String name = nameText.getText().toString();
        EditText emailText = (EditText) findViewById(R.id.register_email);
        String email = emailText.getText().toString();
        EditText birth_dateText = (EditText) findViewById(R.id.register_birth_year);
        String birth_date = birth_dateText.getText().toString();

        User user= new User();
        user.name=name;
        user.email=email;
        user.birth_year=birth_date;

        ws.addUser(user,u.retrivePromoterCode());
        //Log.d("AND", "OK");
    }

    public void goToLogin(View view){
        startActivity(new Intent(this,LoginActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public void webServiceReady(Hashtable result) {
        Boolean auth_error= (Boolean) result.get("authError");
        Log.d("AND-Register",result.toString());
        if(auth_error){
            Toast.makeText(getApplicationContext(), getString(R.string.ws_connection_error), Toast.LENGTH_SHORT).show();
            return;
        }

        Boolean added= (Boolean) result.get("added");
        if(added){
            EditText editText = (EditText) findViewById(R.id.register_name);
            String message = editText.getText().toString();
            u.saveUserName(message);
            startActivity(new Intent(this,RegisterOkActivity.class));
        }else{
            //String strJunk=getString(R.string.error_promo_code);
            Toast.makeText(getApplicationContext(), (CharSequence) result.get("errorMessage"), Toast.LENGTH_SHORT).show();
        }
    }
}
