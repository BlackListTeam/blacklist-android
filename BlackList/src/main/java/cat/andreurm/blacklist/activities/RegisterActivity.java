package cat.andreurm.blacklist.activities;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

import cat.andreurm.blacklist.R;

public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView txtCompleta = (TextView) findViewById(R.id.textViewCompleta);
        TextView txtAutoriza = (TextView) findViewById(R.id.textViewAutoriza);
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txtCompleta.setTypeface(font);
        txtCompleta.setPaintFlags(txtCompleta.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtAutoriza.setTypeface(font);
        txtAutoriza.setPaintFlags(txtAutoriza.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }
    
}
