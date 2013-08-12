package cat.andreurm.blacklist.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Hashtable;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.model.Party;
import cat.andreurm.blacklist.model.Reservation;
import cat.andreurm.blacklist.utils.Utils;
import cat.andreurm.blacklist.utils.WebService;
import cat.andreurm.blacklist.utils.WebServiceCaller;

public class ReservationActivity extends Activity implements WebServiceCaller {

    Party p;
    WebService ws;
    Utils u;
    Reservation r;
    TabActivity tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        p = getIntent().getParcelableExtra("Party");

        ws=new WebService(this);
        u=new Utils(this);

        TextView txtTitulo = (TextView) findViewById(R.id.textViewTituloEvent);
        TextView txtAcompanantes = (TextView) findViewById(R.id.textViewAcompanantes);
        TextView txtVip= (TextView) findViewById(R.id.textViewVip);
        TextView txtHabitaciones= (TextView) findViewById(R.id.textViewHabitaciones);
        final CheckBox vip = (CheckBox) findViewById(R.id.checkBoxVip);
        final TextView numeroHabitaciones = (TextView) findViewById(R.id.edit_text_rooms);
        final TextView numeroAcompanantes = (TextView) findViewById(R.id.edit_text_acompanantes);
        Button buttonContacto = (Button) findViewById(R.id.buttonContactoEspecial);
        ImageButton buttonReservar = (ImageButton) findViewById(R.id.buttonReservar);
        Button buttonMinusAcompanantes = (Button) findViewById(R.id.btn_minus_acompanantes);
        Button buttonPlusAcompanantes = (Button) findViewById(R.id.btn_plus_acompanantes);
        Button buttonMinusHabitaciones = (Button) findViewById(R.id.btn_minus_rooms);
        Button buttonPlusHabitaciones = (Button) findViewById(R.id.btn_plus_rooms);
        LinearLayout lHabitaciones = (LinearLayout) findViewById(R.id.linearLayoutHabitaciones);
        LinearLayout lAcompanantes = (LinearLayout) findViewById(R.id.linearLayoutAcompanantes);
        LinearLayout lVip= (LinearLayout) findViewById(R.id.linearLayoutVip);

        ImageView imageEvent= (ImageView) findViewById(R.id.textViewImageEvent);

        tabs = (TabActivity) getParent().getParent()    ;

        buttonContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType(getString(R.string.email_mime_type));
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{getString(R.string.email_to)});
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact));
                i.putExtra(Intent.EXTRA_TEXT   , getString(R.string.empty_string));
                try {
                    startActivity(Intent.createChooser(i, getString(R.string.email_chooser_title)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ReservationActivity.this, getString(R.string.no_email_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(p.max_escorts==0){
            lAcompanantes.removeAllViews();
        }
        else{
            buttonMinusAcompanantes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer i = Integer.parseInt(numeroAcompanantes.getText().toString());
                    i--;
                    if(i<0){
                        numeroAcompanantes.setText("0",TextView.BufferType.EDITABLE);
                    }
                    else{
                        numeroAcompanantes.setText(i.toString(),TextView.BufferType.EDITABLE);
                    }
                }
            });

            buttonPlusAcompanantes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer i = Integer.parseInt(numeroAcompanantes.getText().toString());
                    i++;
                    if(i>p.max_escorts){
                        numeroAcompanantes.setText(Integer.toString(p.max_escorts),TextView.BufferType.EDITABLE);
                    }
                    else{
                        numeroAcompanantes.setText(i.toString(),TextView.BufferType.EDITABLE);
                    }
                }
            });


        }
        if(p.max_rooms==0){
            lHabitaciones.removeAllViews();
        }
        else{
            buttonMinusHabitaciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer i = Integer.parseInt(numeroHabitaciones.getText().toString());
                    i--;
                    if(i<0){
                        numeroHabitaciones.setText("0",TextView.BufferType.EDITABLE);
                    }
                    else{
                        numeroHabitaciones.setText(i.toString(),TextView.BufferType.EDITABLE);
                    }
                }
            });

            buttonPlusHabitaciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer i = Integer.parseInt(numeroHabitaciones.getText().toString());
                    i++;
                    if(i>p.max_rooms){
                        numeroHabitaciones.setText(Integer.toString(p.max_rooms),TextView.BufferType.EDITABLE);
                    }
                    else{
                        numeroHabitaciones.setText(i.toString(),TextView.BufferType.EDITABLE);
                    }
                }
            });
        }
        if(!p.vip_allowed){
            lVip.removeAllViews();
        }

        // ImageLoader class instance
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().displayImage(p.image, imageEvent);

        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));

        txtTitulo.setTypeface(font);
        txtTitulo.setPaintFlags(txtTitulo.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtTitulo.setText(p.name);
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
    protected void onResume(){
        super.onResume();
        ((EventsTabGroupActivity)getParent()).back_id=3;
        ((EventsTabGroupActivity)getParent()).p=this.p;
    }


    @Override
    public void webServiceReady(Hashtable result) {
        Boolean auth_error= (Boolean) result.get("authError");
        if(auth_error){
            startActivity(new Intent(this,LoginActivity.class));
            return;
        }
        Boolean is_reservated= (Boolean) result.get("reservated");
        if(is_reservated){

            Log.v("Reservation Activity ","  Tabs "+tabs.toString());
            tabs.getTabHost().setCurrentTab(2);

            Intent i = new Intent(getParent(), CodeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            TabGroupActivity parentActivity = (TabGroupActivity)getParent();
            parentActivity.startChildActivity("Code", i);
        }else{
            Toast.makeText(getApplicationContext(), (String) result.get("errorMessage"), Toast.LENGTH_SHORT).show();
        }
    }


    public void makeReservation(View view){

        final CheckBox vip = (CheckBox) findViewById(R.id.checkBoxVip);
        final TextView numeroHabitaciones = (TextView) findViewById(R.id.edit_text_rooms);
        final TextView numeroAcompanantes = (TextView) findViewById(R.id.edit_text_acompanantes);
        r = new Reservation();
        if(numeroAcompanantes!=null){
            r.escorts = Integer.parseInt(numeroAcompanantes.getText().toString());
        }
        else{
            r.escorts =0;
        }
        if(numeroHabitaciones!=null){
            r.rooms = Integer.parseInt(numeroHabitaciones.getText().toString());
        }
        else{
            r.rooms=0;
        }
        if(vip!=null){
            r.vip = vip.isChecked();
        }
        else{
            r.vip = false;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(getParent())
                .setTitle(getString(R.string.warning))
                .setPositiveButton(getString(R.string.reservar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v("MAKE RESERVATION", "MAKE RESERVATION" + r.escorts);
                        ws.makeReservation(r, u.getSessionId());
                    }
                })
                .setNegativeButton(getString(R.string.cancelar), null);
        TextView textView = new TextView(getParent());
        textView.setText(getString(R.string.confirm_reservation));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(12);
        dialog.setView(textView);
        dialog.show();
    }
}
