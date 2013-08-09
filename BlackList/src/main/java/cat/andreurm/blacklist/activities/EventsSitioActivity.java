package cat.andreurm.blacklist.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/*import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;*/
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.carousel.Carousel;
import cat.andreurm.blacklist.model.Party;
import cat.andreurm.blacklist.model.Reservation;
import cat.andreurm.blacklist.utils.Utils;
import cat.andreurm.blacklist.utils.WebService;
import cat.andreurm.blacklist.utils.WebServiceCaller;

public class EventsSitioActivity extends android.support.v4.app.FragmentActivity implements WebServiceCaller {

    Button buttonPrecio;
    Button buttonInfo;
    ImageButton buttonReserva;
    Party p;
    RelativeLayout rContent;
    Date dateLocation;
    WebService ws;
    Utils u;
    RelativeLayout rL;
    private Carousel carousel;
    private ImageAdapter adapter;
    private int[] musicCover = { R.drawable.button_ok, R.drawable.button_problemas,
            R.drawable.button_anular, R.drawable.button_ok_gris, R.drawable.bg_events};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_sitio);

        p = getIntent().getParcelableExtra("Party");

        ws=new WebService(this);
        u=new Utils(this);

        ws.getCurrentReservation(u.getSessionId());
        TextView txtTitulo = (TextView) findViewById(R.id.textViewTituloEvent);
        TextView txtPlaceInfo = (TextView) findViewById(R.id.textViewPlaceInfo);

        rL = (RelativeLayout) findViewById(R.id.relativeLayoutContent);

        ImageButton buttonMap = (ImageButton) findViewById(R.id.buttonMap);
        ImageButton buttonPictures = (ImageButton) findViewById(R.id.buttonPictures);

        rContent = (RelativeLayout) findViewById(R.id.relativeLayoutContentMenu);

        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));

        txtTitulo.setTypeface(font);
        txtTitulo.setPaintFlags(txtTitulo.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        txtTitulo.setText(p.name);
        txtPlaceInfo.setText(p.place_text);
        txtPlaceInfo.setMovementMethod(new ScrollingMovementMethod());

        ImageView imageEvent= (ImageView) findViewById(R.id.textViewImageEvent);
        // ImageLoader class instance
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().displayImage(p.image, imageEvent);

        buttonReserva = (ImageButton) findViewById(R.id.buttonReservar);
        if(!p.es_actual){
            buttonReserva.setVisibility(View.INVISIBLE);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            dateLocation = dateFormat.parse(p.location_date);
        } catch (ParseException e) {
            Log.e("EventsSitioActivity","Error al parsear fechas");
            e.printStackTrace();
        }

        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showMap(rContent,p.latitude,p.longitude,p.address);
            }
        });

        buttonPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictures();
            }
        });

        if(p.gallery.length == 0 && dateLocation.after(new Date())){
            //Countdown
            showCountdown(rContent, dateLocation);
        }
        else if((p.gallery.length == 0) && dateLocation.before(new Date()))
        {

            //Només icone Mapa
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.rightMargin=100;
            params.topMargin=20;
            buttonMap.setLayoutParams(params);
            buttonPictures.setVisibility(View.INVISIBLE);
            rContent.removeView(buttonPictures);
        }
        else if(p.gallery.length > 0 && dateLocation.after(new Date())){
            //Dos icones per el location es mostra el countdown
            buttonMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCountdown(rContent, dateLocation);
                }
            });
        }

        buttonPrecio = (Button) findViewById(R.id.buttonPrecio);
        buttonPrecio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getParent(), EventsPrecioActivity.class);
                i.putExtra("Party", p);
                TabGroupActivity parentActivity = (TabGroupActivity) getParent();
                parentActivity.startChildActivity("Precio", i);
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

    private void showCountdown(RelativeLayout r, Date location_date){
        Date currentDate = new Date();
        long numericalDifference = location_date.getTime() - currentDate.getTime();
        numericalDifference = numericalDifference / 1000;
        numericalDifference = numericalDifference / 60;
        int minutes = (int) numericalDifference % 60;
        numericalDifference = numericalDifference / 60;
        int hours = (int) numericalDifference % 24;
        numericalDifference = numericalDifference / 24;
        int days = (int) numericalDifference % 365;
        View item = getLayoutInflater().inflate(R.layout.countdown,null);
        TextView dias = (TextView) item.findViewById(R.id.textViewDias);
        TextView horas = (TextView) item.findViewById(R.id.textViewHoras);
        TextView minutos = (TextView) item.findViewById(R.id.textViewMinutos);
        TextView labelDias = (TextView) item.findViewById(R.id.labelDias);
        TextView labelHoras = (TextView) item.findViewById(R.id.labelHoras);
        TextView labelMinutos = (TextView) item.findViewById(R.id.labelMinutos);
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));

        dias.setText(String.valueOf(days));
        horas.setText(String.valueOf(hours));
        minutos.setText(String.valueOf(minutes));

        dias.setTypeface(font);
        horas.setTypeface(font);
        minutos.setTypeface(font);
        labelDias.setTypeface(font);
        labelHoras.setTypeface(font);
        labelMinutos.setTypeface(font);

        r.removeAllViewsInLayout();
        r.setGravity(Gravity.CENTER_HORIZONTAL);
        r.addView(item);


    }

    private void showMap(RelativeLayout r, float latitude, float longitude,String address){
        //Eliminem totes les vistes que hi han al Layout on s'ha de mostrar el mapa
        r.removeAllViewsInLayout();
        //Centrem la info del Layout per a que el mapa quedi centrat
        r.setGravity(Gravity.CENTER_HORIZONTAL);
         //MapView map = new MapView(this,10)

        // get an instance of FragmentTransaction from your Activity
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //add a fragment
        MapFragment myFragment = new MapFragment(latitude,longitude,address);
        fragmentTransaction.add(R.id.relativeLayoutContentMenu, myFragment);
        fragmentTransaction.commit();
    }

    public void showPictures(){
        /*RelativeLayout rL = (RelativeLayout) findViewById(R.id.relativeLayoutGeneral);
        rL.removeAllViews();
        init();*/
        Intent i = new Intent(getParent(), GalleryActivity.class);
        i.putExtra("Party", p);
        TabGroupActivity parentActivity = (TabGroupActivity)getParent();
        parentActivity.startChildActivity("Gallery", i);
    }


    private void init()
    {
        carousel = new Carousel(this);

        // configurations for the carousel.
        carousel.setType(Carousel.TYPE_COVERFLOW);
        carousel.setOverScrollBounceEnabled(true);
        carousel.setInfiniteScrollEnabled(false);
        carousel.setItemRearrangeEnabled(true);

        // set images for the carousel.
        adapter = new ImageAdapter(this);
        carousel.setAdapter(adapter);

        carousel.setCenterPosition(0);

        Log.v("RELATIVE LAYOUT","   "+rL+"    "+carousel);
        rL.addView(carousel);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.events_sitio, menu);
        return true;
    }


    @Override
    public void webServiceReady(Hashtable result) {
        Boolean auth_error= (Boolean) result.get("authError");
        if(auth_error){
            Toast.makeText(getApplicationContext(), (String) result.get("errorMessage"), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,LoginActivity.class));
            return;
        }
        Reservation res= (Reservation) result.get("reservation");
        if(res == null && p.es_actual){
            buttonReserva.setVisibility(View.VISIBLE);
        }
    }


    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c)
        {
            mContext = c;
        }

        @Override
        public int getCount() {
            Log.v("RELATIVE LAYOUT2","   "+p.gallery.length);
            return musicCover.length;
            //return p.gallery.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return musicCover[position];
            //return p.gallery[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            /*View view = convertView;

            Log.v("POSITION IMAGE GALLERY","      "+p.gallery[position]);

            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.carousel_item, parent, false);
                view.setLayoutParams(new Carousel.LayoutParams(250, 250));
                ViewHolder holder = new ViewHolder();
                holder.imageView = (ImageView)view.findViewById(R.id.itemImage);
                view.setTag(holder);
            }

            ViewHolder holder = (ViewHolder)view.getTag();
            holder.imageView.setTag(p.gallery[position]);

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
            ImageLoader.getInstance().init(config);

            ImageLoader.getInstance().displayImage(p.gallery[position], holder.imageView);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            return view;*/
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.carousel_item, parent, false);
                view.setLayoutParams(new Carousel.LayoutParams(250, 250));
                ViewHolder holder = new ViewHolder();
                holder.imageView = (ImageView)view.findViewById(R.id.itemImage);
                view.setTag(holder);
            }

            ViewHolder holder = (ViewHolder)view.getTag();
            holder.imageView.setImageResource(musicCover[position]);

            return view;
        }

        private class ViewHolder {
            ImageView imageView;
        }
    }

}
