package cat.andreurm.blacklist.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import java.util.Hashtable;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.model.MessageThread;
import cat.andreurm.blacklist.utils.Utils;
import cat.andreurm.blacklist.utils.WebService;
import cat.andreurm.blacklist.utils.WebServiceCaller;

public class WriteMessageActivity extends Activity implements OnTouchListener, WebServiceCaller{

    ImageButton bVolver;
    EditText msj;
    RelativeLayout layout;
    Utils u;
    WebService ws;
    int mt_id=0;
    ProgressDialog pdl=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_write_message);

        ws=new WebService(this);
        u=new Utils(this);

        TextView txtTitulo = (TextView) findViewById(R.id.textViewTituloEscribirMensaje);
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txtTitulo.setTypeface(font);
        txtTitulo.setPaintFlags(txtTitulo.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        bVolver = (ImageButton) findViewById(R.id.buttonVolver);
        bVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getParent(), ListMessagesActivity.class);
                TabGroupActivity parentActivity = (TabGroupActivity)getParent();
                parentActivity.startChildActivity("ListMessagesActivity", i);
            }
        });


        msj = (EditText) findViewById(R.id.editTextMessage);

        layout = (RelativeLayout)findViewById(R.id.relativeLayoutGeneral);
        layout.setOnTouchListener(this);

        Bundle b = getIntent().getExtras();
        mt_id = b.getInt("mt_id",0);
    }

    @Override
    protected void onNewIntent(Intent i){
        Bundle b = i.getExtras();
        mt_id = b.getInt("mt_id",0);

        msj.clearFocus();
        msj.setText(null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v==layout){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(msj.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    public void sendMessage(View v){
        pdl= ProgressDialog.show(getParent(), null, getString(R.string.loading), true, false);
        if(mt_id!=0){
            ws.replyMessage(msj.getText().toString(), mt_id, u.getSessionId());
        }else{
            ws.addMessage(msj.getText().toString(),u.getSessionId());
        }
    }



    @Override
    public void webServiceReady(Hashtable result) {
        Boolean auth_error= (Boolean) result.get("authError");

        if(auth_error){
            pdl.dismiss();
            Toast.makeText(getApplicationContext(), (String) result.get("errorMessage"), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,LoginActivity.class));
            return;
        }

        Boolean is_added= (Boolean) result.get("added");
        if(is_added){
            Toast.makeText(getApplicationContext(), getString(R.string.message_sended), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getParent(), ListMessagesActivity.class);
            TabGroupActivity parentActivity = (TabGroupActivity)getParent();
            parentActivity.startChildActivity("ListMessagesActivity", i);
        }else{
            Toast.makeText(getApplicationContext(), (String) result.get("errorMessage"), Toast.LENGTH_SHORT).show();
        }
        pdl.dismiss();
    }

    @Override
    public void onBackPressed() {
    }
}



