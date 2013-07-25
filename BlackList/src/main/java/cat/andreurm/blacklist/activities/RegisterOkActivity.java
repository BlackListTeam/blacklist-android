package cat.andreurm.blacklist.activities;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register_ok, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
    }
    
}
