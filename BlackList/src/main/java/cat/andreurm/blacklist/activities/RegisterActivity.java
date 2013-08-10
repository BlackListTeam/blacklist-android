package cat.andreurm.blacklist.activities;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    InputMethodManager imm;
    EditText register_name;
    EditText register_email;
    EditText register_birth;
    ProgressDialog pdl=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ws=new WebService(this);
        u=new Utils(this);
        imm  = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);

        TextView txtCompleta = (TextView) findViewById(R.id.textViewCompleta);
        TextView txtAutoriza = (TextView) findViewById(R.id.textViewAutoriza);
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txtCompleta.setTypeface(font);
        txtCompleta.setPaintFlags(txtCompleta.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtAutoriza.setTypeface(font);
        txtAutoriza.setPaintFlags(txtAutoriza.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);





        register_name = (EditText) this.findViewById(R.id.register_name);

        register_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    register_name.setHint("");
                    register_name.setCursorVisible(true);
                    imm.showSoftInput(register_name, 0);
                }else{
                    register_name.setHint(R.string.name_and_surnames);
                    register_name.setCursorVisible(false);
                    imm.hideSoftInputFromWindow(register_name.getWindowToken(), 0);
                }
            }
        });

        register_email = (EditText) this.findViewById(R.id.register_email);

        register_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    register_email.setHint("");
                    register_email.setCursorVisible(true);
                    imm.showSoftInput(register_email, 0);
                }else{
                    register_email.setHint(R.string.email);
                    register_email.setCursorVisible(false);
                    imm.hideSoftInputFromWindow(register_email.getWindowToken(), 0);
                }

            }
        });

        register_birth = (EditText) this.findViewById(R.id.register_birth_year);

        register_birth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    register_birth.setHint("");
                    register_birth.setCursorVisible(true);
                    imm.showSoftInput(register_birth, 0);
                }else{
                    register_birth.setHint(R.string.birth_year);
                    register_birth.setCursorVisible(false);
                    imm.hideSoftInputFromWindow(register_birth.getWindowToken(), 0);
                }
            }
        });
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
        pdl= ProgressDialog.show(this, null, getString(R.string.loading), true, false);
        ws.addUser(user,u.retrivePromoterCode());
    }

    public void goToLogin(View view){
        startActivity(new Intent(this,LoginActivity.class));
    }



    @Override
    public void webServiceReady(Hashtable result) {
        Boolean auth_error= (Boolean) result.get("authError");
        if(auth_error){
            pdl.dismiss();
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
            Toast.makeText(getApplicationContext(), (CharSequence) result.get("errorMessage"), Toast.LENGTH_SHORT).show();
        }
        pdl.dismiss();
    }

    @Override
    public void onBackPressed() {
    }
}
