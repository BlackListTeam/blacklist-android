package cat.andreurm.blacklist.activities;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import cat.andreurm.blacklist.R;

public class PicturesGalleryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_gallery);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pictures_gallery, menu);
        return true;
    }
    
}
