package cat.andreurm.blacklist.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.viewbadger.BadgeView;

import java.util.Hashtable;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.utils.Utils;
import cat.andreurm.blacklist.utils.WebService;
import cat.andreurm.blacklist.utils.WebServiceCaller;

public class InviteActivity extends Activity implements WebServiceCaller {

    WebService ws;
    Utils u;
    ProgressDialog pdl=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        ws=new WebService(this);
        u=new Utils(this);

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

    public void sendInvitation(View view){
        EditText input= (EditText) findViewById(R.id.inputInvite);

        pdl= ProgressDialog.show(this, null, getString(R.string.loading), true, false);
        ws.sendInvitation(input.getText().toString(),u.getSessionId());
    }

    @Override
    public void webServiceReady(Hashtable result) {

        Boolean auth_error= (Boolean) result.get("authError");
        if(auth_error){
            pdl.dismiss();
            startActivity(new Intent(this,LoginActivity.class));
            return;
        }

        Boolean is_sended= (Boolean) result.get("sended");
        if(is_sended){
            EditText input= (EditText) findViewById(R.id.inputInvite);
            input.setText(R.string.empty_string);
            Toast.makeText(getApplicationContext(), getString(R.string.invitation_sended), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), (String) result.get("errorMessage"), Toast.LENGTH_SHORT).show();
        }
        pdl.dismiss();
    }

    @Override
    public void onBackPressed() {
    }
}
