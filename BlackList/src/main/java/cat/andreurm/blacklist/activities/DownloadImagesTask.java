package cat.andreurm.blacklist.activities;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import cat.andreurm.blacklist.R;

public class DownloadImagesTask extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_images);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.download_images_task, menu);
        return true;
    }
    
}
