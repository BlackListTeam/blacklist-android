package cat.andreurm.blacklist.activities;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

import cat.andreurm.blacklist.R;

public class CodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        TextView txtPartyName= (TextView) findViewById(R.id.textViewPartyName);
        TextView txtReservaRealizadaEvento= (TextView) findViewById(R.id.textViewReservaRealizada);
        TextView txtInfo= (TextView) findViewById(R.id.textViewInfo);
        TextView txtTituloCodigoAutorizacion= (TextView) findViewById(R.id.textViewCodigoAutorizacion);

        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txtPartyName.setTypeface(font);
        txtPartyName.setPaintFlags(txtPartyName.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtReservaRealizadaEvento.setTypeface(font);
        txtReservaRealizadaEvento.setPaintFlags(txtReservaRealizadaEvento.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtInfo.setTypeface(font);
        txtInfo.setPaintFlags(txtInfo.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtTituloCodigoAutorizacion.setTypeface(font);
        txtTituloCodigoAutorizacion.setPaintFlags(txtTituloCodigoAutorizacion.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.code, menu);
        return true;
    }
    
}
