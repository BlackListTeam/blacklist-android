package cat.andreurm.blacklist.activities;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.carousel.Carousel;
import cat.andreurm.blacklist.model.Party;

public class GalleryActivity extends Activity {

    RelativeLayout rL;
    private Carousel carousel;
    private ImageAdapter adapter;
    Party p;
    private int[] musicCover = { R.drawable.button_ok, R.drawable.button_problemas,
            R.drawable.button_anular, R.drawable.button_ok_gris, R.drawable.bg_events};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        p = getIntent().getParcelableExtra("Party");
        rL = (RelativeLayout) findViewById(R.id.relativeLayoutGeneral);

        init();


    }

    private void init()
    {
        carousel = new Carousel(this);

        // configurations for the carousel.
        carousel.setType(Carousel.TYPE_COVERFLOW);
        carousel.setOverScrollBounceEnabled(true);
        carousel.setInfiniteScrollEnabled(false);
        carousel.setItemRearrangeEnabled(true);
        carousel.setAngle(90);

        // set images for the carousel.
        adapter = new ImageAdapter(this);
        carousel.setAdapter(adapter);

        carousel.setCenterPosition(1);

        rL.addView(carousel);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gallery, menu);
        return true;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c)
        {
            mContext = c;
        }

        @Override
        public int getCount() {
            return p.gallery.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return p.gallery[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

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

            return view;
            /*View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.carousel_item, parent, false);
                view.setLayoutParams(new com.digitalaria.gama.carousel.Carousel.LayoutParams(250, 250));
                ViewHolder holder = new ViewHolder();
                holder.imageView = (ImageView)view.findViewById(R.id.itemImage);
                view.setTag(holder);
            }

            ViewHolder holder = (ViewHolder)view.getTag();
            holder.imageView.setImageResource(musicCover[position]);

            return view;*/
        }

        private class ViewHolder {
            ImageView imageView;
        }
    }
    
}
