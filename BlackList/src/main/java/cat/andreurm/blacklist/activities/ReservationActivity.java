package cat.andreurm.blacklist.activities;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import cat.andreurm.blacklist.R;

public class ReservationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        TextView txtTitulo = (TextView) findViewById(R.id.textViewTituloEvent);
        TextView txtAcompanantes = (TextView) findViewById(R.id.textViewAcompanantes);
        TextView txtVip= (TextView) findViewById(R.id.textViewVip);
        TextView txtHabitaciones= (TextView) findViewById(R.id.textViewHabitaciones);
        Button buttonContacto = (Button) findViewById(R.id.buttonContactoEspecial);

        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));

        txtTitulo.setTypeface(font);
        txtTitulo.setPaintFlags(txtTitulo.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtAcompanantes.setTypeface(font);
        txtAcompanantes.setPaintFlags(txtAcompanantes.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtVip.setTypeface(font);
        txtVip.setPaintFlags(txtVip.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtHabitaciones.setTypeface(font);
        txtHabitaciones.setPaintFlags(txtHabitaciones.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        buttonContacto.setTypeface(font);
        buttonContacto.setPaintFlags(buttonContacto.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reservation, menu);
        return true;
    }
    
}
