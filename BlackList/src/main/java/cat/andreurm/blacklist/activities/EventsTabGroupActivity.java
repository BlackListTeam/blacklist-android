package cat.andreurm.blacklist.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cat.andreurm.blacklist.R;

public class EventsTabGroupActivity extends TabGroupActivity {



    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startChildActivity("Principal", new Intent(this, EventsActivity.class));
    }


    
}
