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

public class RegisterOkActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_ok);

        TextView txtPeticion = (TextView) findViewById(R.id.textViewPeticion);
        TextView txtBienvenido = (TextView) findViewById(R.id.textViewBienvenido);
        TextView txtMailEnviado = (TextView) findViewById(R.id.textViewMailEnviado);
        TextView txtRevisalo = (TextView) findViewById(R.id.textViewRevisalo);
        TextView txtPasadosMinutos = (TextView) findViewById(R.id.textViewPasadosMinutos);
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txtPeticion.setTypeface(font);
        txtPeticion.setPaintFlags(txtPeticion.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtBienvenido.setTypeface(font);
        txtBienvenido.setPaintFlags(txtBienvenido.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtMailEnviado.setTypeface(font);
        txtMailEnviado.setPaintFlags(txtMailEnviado.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtRevisalo.setTypeface(font);
        txtRevisalo.setPaintFlags(txtRevisalo.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtPasadosMinutos.setTypeface(font);
        txtPasadosMinutos.setPaintFlags(txtPasadosMinutos.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public void onBackPressed() {
    }

    public void goToLogin(View view){
        startActivity(new Intent(this,LoginActivity.class));
    }

    public void contact(View view){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType(getString(R.string.email_mime_type));
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{getString(R.string.email_to)});
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact));
        i.putExtra(Intent.EXTRA_TEXT   , getString(R.string.empty_string));
        try {
            startActivity(Intent.createChooser(i, getString(R.string.email_chooser_title)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(RegisterOkActivity.this, getString(R.string.no_email_error), Toast.LENGTH_SHORT).show();
        }
    }
    
}
