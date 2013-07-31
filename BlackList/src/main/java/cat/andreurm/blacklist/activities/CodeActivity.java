package cat.andreurm.blacklist.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.Hashtable;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.model.Reservation;
import cat.andreurm.blacklist.utils.Utils;
import cat.andreurm.blacklist.utils.WebService;
import cat.andreurm.blacklist.utils.WebServiceCaller;

public class CodeActivity extends Activity implements WebServiceCaller {

    WebService ws;
    Utils u;
    Boolean delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        ws=new WebService(this);
        u=new Utils(this);
        delete=false;

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

        ws.getCurrentReservation(u.getSessionId());
    }

    @Override
    public void webServiceReady(Hashtable result) {

        Boolean auth_error= (Boolean) result.get("authError");
        if(auth_error){
            Toast.makeText(getApplicationContext(), (String) result.get("errorMessage"), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,LoginActivity.class));
            return;
        }

        if(delete){
            Boolean is_deleted=(Boolean) result.get("deleted");

            if(is_deleted){
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.warning))
                        .setMessage(getString(R.string.reservation_canceled))
                        .setPositiveButton(getString(R.string.see_parties), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getBaseContext(), TabHostActivity.class));
                            }

                        })
                        .show();
            }else{
                Toast.makeText(getApplicationContext(), (String) result.get("errorMessage"), Toast.LENGTH_SHORT).show();
            }

        }else{
            Reservation res= (Reservation) result.get("reservation");
            if(res != null){
                try {
                    ImageView img= (ImageView) findViewById(R.id.textViewQrImage);

                    URL thumb_u = new URL(res.qr);
                    Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
                    img.setImageDrawable(thumb_d);

                    TextView p_name=(TextView) findViewById(R.id.textViewPartyName);
                    TextView p_info=(TextView) findViewById(R.id.textViewInfo);


                    p_name.setText(res.party_name);

                    String aux=getString(R.string.you);


                    if(res.escorts>0){
                        aux+=" "+getString(R.string.plus)+" "+res.escorts;
                        if(res.escorts==1){
                            aux = aux + (" " + getString(R.string.escort));
                        }else{
                            aux = aux + (" " + getString(R.string.escorts));
                        }


                    }
                    if(res.vip){
                        aux+= " "+getString(R.string.vip_space);
                    }
                    if(res.rooms>0){
                        aux+=" "+getString(R.string.plus)+" "+res.rooms;

                        if(res.rooms==1){
                            aux+=" "+getString(R.string.room);
                        }else{
                            aux+=" "+getString(R.string.rooms);
                        }

                    }

                    p_info.setText(aux);


                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.img_download_error), Toast.LENGTH_SHORT).show();
                }

            }else{

                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.warning))
                        .setMessage(getString(R.string.no_active_reservation))
                        .setPositiveButton(getString(R.string.see_parties), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getBaseContext(), TabHostActivity.class));
                            }

                        })
                        .show();
            }
        }

        delete=false;
    }

    @Override
    public void onBackPressed() {
    }

    public void cancelReservation(View view){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.confirm_cancel_reservation))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete=true;
                        ws.deleteReservation(u.getSessionId());
                    }

                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    
}
