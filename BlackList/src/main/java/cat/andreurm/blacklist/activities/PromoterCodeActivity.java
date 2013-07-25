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
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.utils.Utils;
import cat.andreurm.blacklist.utils.WebService;
import cat.andreurm.blacklist.utils.WebServiceCaller;

public class PromoterCodeActivity extends Activity implements WebServiceCaller {

    WebService ws;
    Utils u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ws=new WebService(this);
        u=new Utils(this);

        if(u.userAllowedToUseApp()){
            //startActivity(new Intent(this,LoginActivity.class));
        }

        setContentView(R.layout.promoter);

        TextView txt = (TextView) findViewById(R.id.textView);
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txt.setTypeface(font);
        txt.setPaintFlags(txt.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public void validateCode(View view){
        EditText editText = (EditText) findViewById(R.id.promoInput);
        String message = editText.getText().toString();
        ws.validatePromoterCode(message);
    }

    @Override
    public void webServiceReady(Hashtable result) {

        Boolean auth_error= (Boolean) result.get("authError");
        if(auth_error){
            Toast.makeText(getApplicationContext(), getString(R.string.ws_connection_error), Toast.LENGTH_SHORT).show();
            return;
        }

        Boolean is_valid= (Boolean) result.get("valid");
        if(is_valid){
            EditText editText = (EditText) findViewById(R.id.promoInput);
            String message = editText.getText().toString();
            u.allowUserToUseApp(message);
            startActivity(new Intent(this,RegisterActivity.class));
        }else{
            String strJunk=getString(R.string.error_promo_code);
            Toast.makeText(getApplicationContext(), strJunk, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


}
