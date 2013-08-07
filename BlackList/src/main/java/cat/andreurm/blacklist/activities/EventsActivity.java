package cat.andreurm.blacklist.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.model.Party;
import cat.andreurm.blacklist.utils.Utils;
import cat.andreurm.blacklist.utils.WebService;
import cat.andreurm.blacklist.utils.WebServiceCaller;

public class EventsActivity extends Activity implements WebServiceCaller {

    private com.digitalaria.gama.carousel.Carousel carousel;
    private ImageAdapter adapter;
    private int[] musicCover;
    private Intent i;
    private TextView txtTitulo;
    WebService ws;
    Utils u;
    ArrayList<Party> parties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_events);
        txtTitulo = (TextView) findViewById(R.id.textViewTituloEvent);
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txtTitulo.setTypeface(font);
        txtTitulo.setPaintFlags(txtTitulo.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        ws=new WebService(this);
        u=new Utils(this);

        ws.getPartyCovers(u.getSessionId());
    }

    private void init()
    {
        Log.v("INIT", "INIT PRINCIPIO");

        // create the carousel object.
        /*carousel = (com.digitalaria.gama.carousel.Carousel) findViewById(R.id.carousel);

        // configurations for the carousel.
        carousel.setType(com.digitalaria.gama.carousel.Carousel.TYPE_COVERFLOW);
        carousel.setOverScrollBounceEnabled(true);
        carousel.setInfiniteScrollEnabled(false);
        carousel.setItemRearrangeEnabled(true);

        // set images for the carousel.
        adapter = new ImageAdapter(this);
        carousel.setAdapter(adapter);

        // change the first selected position. (optional)
        //TODO Escoger el próximo evento
        carousel.setCenterPosition(3);

        carousel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent i = new Intent(getParent(), EventsPrecioActivity.class);
                TabGroupActivity parentActivity = (TabGroupActivity)getParent();
                parentActivity.startChildActivity("Precio", i);

            }
        });

        carousel.setOnItemSelectionUpdatedListener(new com.digitalaria.gama.carousel.Carousel.OnItemSelectionUpdatedListener() {
            @Override
            public void onItemSelectionUpdated(AdapterView<?> adapterView, View view, int i) {
                //TODO Escoger el título dependiendo del date
                txtTitulo.setText("Evento Pasado");
            }
        }
        );

        Log.v("INIT", "INIT FINAL");*/
    }

    @Override
    public void webServiceReady(Hashtable result) {
        parties= (ArrayList<Party>) result.get("parties");
        if(!parties.isEmpty()){
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                int id_actual=0;
                for(Party p:parties){
                    p.es_actual = false;
                    Date dateParty = null;
                    try {
                        dateParty = dateFormat.parse(p.date);
                    } catch (ParseException e) {
                        Log.e("EventInfoActivity","Error de Format en fechas");
                        e.printStackTrace();
                    }
                    //La primera fiesta que sea después de la fecha actual le ponemos la variable es actual a true
                    if(dateParty.after(new Date())&&id_actual==0){
                        id_actual = p.party_id;
                        p.es_actual = true;
                        Intent i = new Intent(getParent(), EventInfoActivity.class);
                        i.putExtra("Party", p);
                        TabGroupActivity parentActivity = (TabGroupActivity)getParent();
                        parentActivity.startChildActivity("InfoFromEvents", i);
                    }
                }

                /*Button buttonInfo = (Button) findViewById(R.id.buttonAnarFesta);

                buttonInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getParent(), EventInfoActivity.class);
                        i.putExtra("Party", parties.get(1));
                        TabGroupActivity parentActivity = (TabGroupActivity)getParent();
                        parentActivity.startChildActivity("InfoFromEvents", i);
                    }
                });

                musicCover = new int[parties.size()];
                for(int i =0;i<=parties.size();i++){
                    musicCover[i]=parties.get(i).party_id;
                }*/
                //init();
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

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c)
        {
            mContext = c;
        }

        @Override
        public int getCount() {
            return musicCover.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return musicCover[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            Log.v("LOG", "PARTIES   "+position+"   "+musicCover.length+"   "+parties.get(position).cover);
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.carousel_item, parent, false);
                view.setLayoutParams(new com.digitalaria.gama.carousel.Carousel.LayoutParams(250, 250));
                ViewHolder holder = new ViewHolder();
                holder.imageView = (ImageView)view.findViewById(R.id.itemImage);
                view.setTag(holder);
            }

            ViewHolder holder = (ViewHolder)view.getTag();
            Log.v("LOG", "PARTIES   "+position+"   "+musicCover.length+"   "+parties.get(position).cover);

            //holder.imageView.setTag(parties.get(position).cover);
            //holder.imageView.setTag("http://www.blacklistmeetings.com/files/party/cover/5/iphone_FESTA1.jpg");

            /*DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
            ImageLoader.getInstance().init(config);
            ImageLoader.getInstance().displayImage(parties.get(position).cover, holder.imageView);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);*/
            Bitmap bmp =null;
            try{
                URL ulrn = new URL("http://www.blacklistmeetings.com/files/party/cover/5/iphone_FESTA1.jpg");
                HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
                InputStream is = con.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
                holder.imageView.setImageBitmap(bmp);
                BitmapDrawable drawable = (BitmapDrawable) holder.imageView.getDrawable();
                drawable.setAntiAlias(true);

            }catch(Exception e){}


            return view;
        }

        private class ViewHolder {
            ImageView imageView;
        }
    }

}