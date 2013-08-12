package cat.andreurm.blacklist.activities;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.readystatesoftware.viewbadger.BadgeView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.carousel.Carousel;
import cat.andreurm.blacklist.model.Party;
import cat.andreurm.blacklist.utils.Utils;
import cat.andreurm.blacklist.utils.WebService;
import cat.andreurm.blacklist.utils.WebServiceCaller;

public class EventsActivity extends Activity implements WebServiceCaller {

    private Carousel carousel;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private ImageAdapter adapter;
    private int[] musicCover = { R.drawable.button_problemas, R.drawable.button_ok_gris,
            R.drawable.button_ok, R.drawable.bg_events, R.drawable.and_bkg_title_invitations};
    private Intent i;
    private TextView txtTitulo;
    WebService ws;
    Utils u;
    ArrayList<Party> parties;
    Boolean gettingBadges=false;
    ProgressDialog pdl=null;

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

    @Override
    protected void onResume(){
        super.onResume();
        ((EventsTabGroupActivity)getParent()).back_id=0;
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

        // change the first selected position. (optional)

        carousel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent i = new Intent(getParent(), EventInfoActivity.class);
                TabGroupActivity parentActivity = (TabGroupActivity)getParent();
                i.putExtra("Party", parties.get(position));
                parentActivity.startChildActivity("Precio", i);
            }
        });


        carousel.setOnItemSelectionUpdatedListener(new Carousel.OnItemSelectionUpdatedListener() {
            @Override
            public void onItemSelectionUpdated(AdapterView<?> adapterView, View view, int i) {

                Party p= parties.get(i);
                try {
                    Date dateParty = dateFormat.parse(p.date);
                    if(p.es_actual){
                        txtTitulo.setText(getString(R.string.proximo_evento));
                    }
                    else if(dateParty.before(new Date())){
                        txtTitulo.setText(getString(R.string.evento_pasado));
                    }
                    else{
                        txtTitulo.setText(getString(R.string.evento_futuro));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        );

        int index=1;
        for(Party p:parties){
            if(p.es_actual){
                carousel.setCenterPosition(index);
            }
            index++;
        }

        RelativeLayout rL = (RelativeLayout) findViewById(R.id.relativeLayoutBackGround);
        rL.addView(carousel);
    }

    private void badges(int new_msjs){
        if(new_msjs>0){
            TabGroupActivity tabs = (TabGroupActivity) getParent();
            TabHostActivity target = (TabHostActivity) tabs.getParent();
            target.badge= new BadgeView(getParent(), target.getTabWidget(),1);
            target.badge.setText(Integer.toString(new_msjs));
            target.badge.show();

        }
    }

    @Override
    public void webServiceReady(Hashtable result) {

        Boolean auth_error= (Boolean) result.get("authError");

        if(auth_error){

            Toast.makeText(getApplicationContext(), (String) result.get("errorMessage"), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,LoginActivity.class));
            return;
        }

        if(!gettingBadges){
            parties= (ArrayList<Party>) result.get("parties");
            if(!parties.isEmpty()){
                try {

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
                        }
                    }

                    init();

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
            gettingBadges=true;
            ws.getNewMessages(u.getSessionId());
        }else{
            gettingBadges=false;
            badges((Integer)result.get("new_messages"));
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
            return parties.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return parties.get(position).party_id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {


                view = LayoutInflater.from(mContext).inflate(R.layout.carousel_item, parent, false);
                Display display = getWindowManager().getDefaultDisplay();
                int width = display.getWidth();
                Log.v("WIDTH VIEW","Width "+width);
                if(width<350){
                    view.setLayoutParams(new Carousel.LayoutParams(150, 150));
                }
                else if(width<400){
                        view.setLayoutParams(new Carousel.LayoutParams(200, 200));
                }
                else if(width<500){
                    view.setLayoutParams(new Carousel.LayoutParams(250, 250));
                }
                else if(width<600){
                    view.setLayoutParams(new Carousel.LayoutParams(350, 350));
                }
                else{
                    Log.v("WIDTH VIEW","Width2 "+width);
                    view.setLayoutParams(new Carousel.LayoutParams(400, 400));
                }
                ViewHolder holder = new ViewHolder();
                holder.imageView = (ImageView)view.findViewById(R.id.itemImage);
                view.setTag(holder);
            }

            ViewHolder holder = (ViewHolder)view.getTag();
            holder.imageView.setTag(parties.get(position));

            parties.get(position).id_view = position;

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
            ImageLoader.getInstance().init(config);


            ImageLoader.getInstance().displayImage(parties.get(position).cover, holder.imageView,defaultOptions, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    if(pdl==null){
                        pdl= ProgressDialog.show(getParent(), null, getString(R.string.loading), true, false);
                    }
                    else if(!pdl.isShowing()){
                        pdl= ProgressDialog.show(getParent(), null, getString(R.string.loading), true, false);
                    }
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    if(pdl.isShowing()){
                        pdl.dismiss();
                    }
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if(pdl.isShowing()){
                        pdl.dismiss();
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    if(pdl.isShowing()){
                        pdl.dismiss();
                    }
                }
            });
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            return view;
        }

        private class ViewHolder {
            ImageView imageView;
        }
    }

}