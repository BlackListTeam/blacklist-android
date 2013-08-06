package cat.andreurm.blacklist.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.model.MessageThread;

public class MessageInfoActivity extends Activity {

    ImageButton bVolver;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_info);

        TextView txtTitulo = (TextView) findViewById(R.id.textViewTituloMensaje);
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txtTitulo.setTypeface(font);
        txtTitulo.setPaintFlags(txtTitulo.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);


        Bundle b = getIntent().getExtras();
        MessageThread object = b.getParcelable("message_thread");


        Log.d("AND-Info",object.from);

        bVolver = (ImageButton) findViewById(R.id.buttonVolver);
        bVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getParent(), ListMessagesActivity.class);
                TabGroupActivity parentActivity = (TabGroupActivity)getParent();
                parentActivity.startChildActivity("ListMessagesActivity", i);
            }
        });


    }

    @Override
    public void onBackPressed() {
    }
    
}
