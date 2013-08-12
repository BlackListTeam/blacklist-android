package cat.andreurm.blacklist.activities;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Parcelable;
import android.view.Menu;
import android.view.Window;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.model.Party;

public class EventsTabGroupActivity extends TabGroupActivity {

    public int back_id=0;
    public Party p=null;


    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startChildActivity("Principal", new Intent(this, EventsActivity.class));
    }

    @Override
    public void  onBackPressed  () {
        switch(back_id){
            case 0: //No fer res
                break;

            case 1: //Anar al carrouse principal
                startActivity(new Intent(getBaseContext(), TabHostActivity.class));
                break;

            case 2: //Recarregar la activity (pel mapa i els countdown)
                this.startChildActivity("test", this.getCurrentActivity().getIntent());
                break;

            case 3: //Anar a sitio "net" per la galeria i reserva
                Intent i = new Intent(this, EventsSitioActivity.class);
                i.putExtra("Party", p);
                this.startChildActivity("Sitio", i);
                break;
        }
    }




    
}
