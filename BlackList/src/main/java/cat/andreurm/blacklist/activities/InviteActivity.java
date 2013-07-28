package cat.andreurm.blacklist.activities;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

import cat.andreurm.blacklist.R;

public class InviteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        TextView txtIntroduceMails = (TextView) findViewById(R.id.textViewIntroduceMails);
        TextView txtInvitacionesProximaFiesta= (TextView) findViewById(R.id.textViewInvitacionesProximaFiesta);
        TextView txtInvitarOtrasPersonas= (TextView) findViewById(R.id.textViewInvitarOtrasPersonas);


        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txtIntroduceMails.setTypeface(font);
        txtIntroduceMails.setPaintFlags(txtIntroduceMails.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtInvitacionesProximaFiesta.setTypeface(font);
        txtInvitacionesProximaFiesta.setPaintFlags(txtInvitacionesProximaFiesta.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtInvitarOtrasPersonas.setTypeface(font);
        txtInvitarOtrasPersonas.setPaintFlags(txtInvitarOtrasPersonas.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.invite, menu);
        return true;
    }
    
}
