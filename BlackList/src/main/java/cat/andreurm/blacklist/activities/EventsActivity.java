package cat.andreurm.blacklist.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.controls.Carousel;
import cat.andreurm.blacklist.controls.CarouselAdapter;
import cat.andreurm.blacklist.controls.CarouselItem;

public class EventsActivity extends Activity {

    ImageButton buttonEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        TextView txtTitulo = (TextView) findViewById(R.id.textViewTituloEvent);
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txtTitulo.setTypeface(font);
        txtTitulo.setPaintFlags(txtTitulo.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        /*buttonEvent = (ImageButton) findViewById(R.id.buttonAnarFesta);
        buttonEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getParent(), EventInfoActivity.class);
                TabGroupActivity parentActivity = (TabGroupActivity)getParent();
                parentActivity.startChildActivity("Precio", i);
            }
        });*/

        RelativeLayout r = (RelativeLayout) findViewById(R.id.relativeLayoutBackGround);

        CoverFlow coverFlow;
        coverFlow = new CoverFlow(this);
        coverFlow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        coverFlow.setAdapter(new ImageAdapter(this));

        ImageAdapter coverImageAdapter =  new ImageAdapter(this);

        coverFlow.setAdapter(coverImageAdapter);

        coverFlow.setSpacing(-25);
        coverFlow.setSelection(4, true);
        coverFlow.setAnimationDuration(1000);

        setContentView(coverFlow);

        //r.addView(coverFlow);


        /*Carousel carousel = (Carousel)findViewById(R.id.carousel);
        carousel.setOnItemClickListener(new CarouselAdapter.OnItemClickListener(){

            public void onItemClick(CarouselAdapter<?> parent, View view,
                                    int position, long id) {

                Toast.makeText(EventsActivity.this,
                        String.format("%s has been clicked",
                                ((CarouselItem)parent.getChildAt(position)).getName()),
                        Toast.LENGTH_SHORT).show();
            }

        });

        carousel.setOnItemSelectedListener(new CarouselAdapter.OnItemSelectedListener(){

            public void onItemSelected(CarouselAdapter<?> parent, View view,
                                       int position, long id) {

                final TextView txt = (TextView)(findViewById(R.id.selected_item));

                switch(position){
                    case 0:
                        txt.setText("The cat (Felis catus), also known as the domestic cat or housecat to distinguish it from other felids and felines, is a small, usually furry, domesticated, carnivorous mammal that is valued by humans for its companionship and for its ability to hunt vermin and household pests. Cats have been associated with humans for at least 9,500 years, and are currently the most popular pet in the world. Owing to their close association with humans, cats are now found almost everywhere in the world.");
                        break;
                    case 1:
                        txt.setText("The hippopotamus (Hippopotamus amphibius), or hippo, from the ancient Greek for \"river horse\" (ἱπποπόταμος), is a large, mostly herbivorous mammal in sub-Saharan Africa, and one of only two extant species in the family Hippopotamidae (the other is the Pygmy Hippopotamus.) After the elephant, the hippopotamus is the third largest land mammal and the heaviest extant artiodactyl.");
                        break;
                    case 2:
                        txt.setText("A monkey is a primate, either an Old World monkey or a New World monkey. There are about 260 known living species of monkey. Many are arboreal, although there are species that live primarily on the ground, such as baboons. Monkeys are generally considered to be intelligent. Unlike apes, monkeys usually have tails. Tailless monkeys may be called \"apes\", incorrectly according to modern usage; thus the tailless Barbary macaque is called the \"Barbary ape\".");
                        break;
                    case 3:
                        txt.setText("A mouse (plural: mice) is a small mammal belonging to the order of rodents. The best known mouse species is the common house mouse (Mus musculus). It is also a popular pet. In some places, certain kinds of field mice are also common. This rodent is eaten by large birds such as hawks and eagles. They are known to invade homes for food and occasionally shelter.");
                        break;
                    case 4:
                        txt.setText("The giant panda, or panda (Ailuropoda melanoleuca, literally meaning \"black and white cat-foot\") is a bear native to central-western and south western China.[4] It is easily recognized by its large, distinctive black patches around the eyes, over the ears, and across its round body. Though it belongs to the order Carnivora, the panda's diet is 99% bamboo.");
                        break;
                    case 5:
                        txt.setText("Rabbits (or, colloquially, bunnies) are small mammals in the family Leporidae of the order Lagomorpha, found in several parts of the world. There are eight different genera in the family classified as rabbits, including the European rabbit (Oryctolagus cuniculus), cottontail rabbits (genus Sylvilagus; 13 species), and the Amami rabbit (Pentalagus furnessi, an endangered species on Amami Ōshima, Japan)");
                        break;
                }

            }

            public void onNothingSelected(CarouselAdapter<?> parent) {
            }

        }
        );
*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.events, menu);
        return true;
    }

    public class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;

        private FileInputStream fis;

        private String[] myRemoteImages = {
                "http://disenograficodos.files.wordpress.com/2010/10/absolution-muse-cd-cover-art.jpg?w=200",
                "http://disenograficodos.files.wordpress.com/2010/10/absolution-muse-cd-cover-art.jpg?w=200",
                "http://disenograficodos.files.wordpress.com/2010/10/absolution-muse-cd-cover-art.jpg?w=200",
                "http://disenograficodos.files.wordpress.com/2010/10/absolution-muse-cd-cover-art.jpg?w=200",
                "http://disenograficodos.files.wordpress.com/2010/10/absolution-muse-cd-cover-art.jpg?w=200",
                "http://disenograficodos.files.wordpress.com/2010/10/absolution-muse-cd-cover-art.jpg?w=200"
        };

        private ImageView[] mImages;

        public ImageAdapter(Context c) {
            mContext = c;
            mImages = new ImageView[myRemoteImages.length];
        }


        public int getCount() {
            return myRemoteImages.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            //Use this code if you want to load from resources
            final ImageView i = new ImageView(mContext);

            //TODO aquí hauríem d'agafar la url de manera sincrona
            i.setTag("http://disenograficodos.files.wordpress.com/2010/10/absolution-muse-cd-cover-art.jpg?w=200");

            i.setOnLongClickListener(new View.OnLongClickListener(){
                public boolean onLongClick(View v){
                    if(v.equals(i)){
                        Log.v("Events ACtivity","Click Carousel!!!");
                        Intent i = new Intent(getParent(), EventInfoActivity.class);
                        TabGroupActivity parentActivity = (TabGroupActivity)getParent();
                        parentActivity.startChildActivity("Info", i);
                    }
                    return false;
                }
            });

            new DownloadImagesTask().execute(i);

            i.setLayoutParams(new CoverFlow.LayoutParams(130, 130));
            i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            return i;

            //return mImages[position];
        }
        /** Returns the size (0.0f to 1.0f) of the views
         * depending on the 'offset' to the center. */
        public float getScale(boolean focused, int offset) {
        /* Formula: 1 / (2 ^ offset) */
            return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset)));
        }


    }

    public class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {

        ImageView imageView = null;

        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {
            this.imageView = imageViews[0];
            return download_Image((String)imageView.getTag());
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }

        private Bitmap download_Image(String url) {

            Bitmap bmp =null;
            try{
                URL ulrn = new URL(url);
                HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
                InputStream is = con.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
                imageView.setImageBitmap(bmp);
                imageView.setLayoutParams(new CoverFlow.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                drawable.setAntiAlias(true);

                if (null != bmp)
                    return bmp;

            }catch(Exception e){}
            return bmp;
        }
    }
}