package cat.andreurm.blacklist.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.model.Party;

public class EventsPrecioActivity extends Activity {

    Button buttonSitio;
    Button buttonInfo;
    ImageButton buttonReserva;
    Party p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_precio);

        p = getIntent().getParcelableExtra("Party");

        TextView txtTitulo = (TextView) findViewById(R.id.textViewTituloEvent);
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txtTitulo.setTypeface(font);
        txtTitulo.setPaintFlags(txtTitulo.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtTitulo.setText(p.name);

        TextView txtInfo = (TextView) findViewById(R.id.textViewPrecio);
        txtInfo.setText(p.price_info);
        txtInfo.setMovementMethod(new ScrollingMovementMethod());

        buttonReserva = (ImageButton) findViewById(R.id.buttonReservar);
        if(!p.es_actual){
            buttonReserva.setVisibility(View.INVISIBLE);
        }

        ImageView imageEvent= (ImageView) findViewById(R.id.textViewImageEvent);
        // ImageLoader class instance
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().displayImage(p.image, imageEvent);

        buttonSitio = (Button) findViewById(R.id.buttonSitio);
        buttonSitio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getParent(), EventsSitioActivity.class);
                i.putExtra("Party", p);
                TabGroupActivity parentActivity = (TabGroupActivity)getParent();
                parentActivity.startChildActivity("Sitio", i);
            }
        });
        buttonInfo = (Button) findViewById(R.id.buttonInfo);
        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getParent(), EventInfoActivity.class);
                i.putExtra("Party", p);
                TabGroupActivity parentActivity = (TabGroupActivity)getParent();
                parentActivity.startChildActivity("Info", i);
            }
        });
        buttonReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getParent(), ReservationActivity.class);
                i.putExtra("Party", p);
                TabGroupActivity parentActivity = (TabGroupActivity)getParent();
                parentActivity.startChildActivity("Reservar", i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.events_precio, menu);
        return true;
    }
    
}
