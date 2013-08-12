package cat.andreurm.blacklist.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Display;
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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

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
    ProgressDialog pdl=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        p = getIntent().getParcelableExtra("Party");
        rL = (RelativeLayout) findViewById(R.id.relativeLayoutGeneral);

        init();


    }

    @Override
    protected void onResume(){
        super.onResume();
        ((EventsTabGroupActivity)getParent()).back_id=3;
        ((EventsTabGroupActivity)getParent()).p=this.p;
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

                Display display = getWindowManager().getDefaultDisplay();
                int width = display.getWidth();
                if(width<350){
                    view.setLayoutParams(new Carousel.LayoutParams(250, 250));
                }
                else if(width<400){
                    view.setLayoutParams(new Carousel.LayoutParams(300, 300));
                }
                else if(width<500){
                    view.setLayoutParams(new Carousel.LayoutParams(400, 400));
                }
                else if(width<600){
                    view.setLayoutParams(new Carousel.LayoutParams(500, 500));
                }
                else{
                    view.setLayoutParams(new Carousel.LayoutParams(550, 550));
                }

                ViewHolder holder = new ViewHolder();
                holder.imageView = (ImageView)view.findViewById(R.id.itemImage);
                view.setTag(holder);
            }

            ViewHolder holder = (ViewHolder)view.getTag();
            holder.imageView.setTag(p.gallery[position]);

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
            ImageLoader.getInstance().init(config);

            ImageLoader.getInstance().displayImage(p.gallery[position], holder.imageView);

            ImageLoader.getInstance().displayImage(p.gallery[position], holder.imageView,defaultOptions, new ImageLoadingListener() {
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
