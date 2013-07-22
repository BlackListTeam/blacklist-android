package cat.andreurm.blacklist.activities;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

import cat.andreurm.blacklist.R;

public class InterfaceExample extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promoter);

        //PROMOTER
        TextView txt = (TextView) findViewById(R.id.textView);
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txt.setTypeface(font);
        txt.setPaintFlags(txt.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        //ACTIVITY MAIN
        /*Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));

        TextView txt = (TextView) findViewById(R.id.editText);
        txt.setTypeface(font);
        txt.setPaintFlags(txt.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        TextView txt2 = (TextView) findViewById(R.id.editText2);
        txt2.setTypeface(font);
        txt2.setPaintFlags(txt2.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
