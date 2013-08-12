package cat.andreurm.blacklist.activities;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.Hashtable;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.model.User;
import cat.andreurm.blacklist.utils.WebService;
import cat.andreurm.blacklist.utils.WebServiceCaller;

public class MainActivity extends Activity implements WebServiceCaller {

    Boolean is_logged=false;
    WebService ws;
    User u;
    EditText et;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*Utils u=new Utils(this);
        Log.d("1.",u.userAllowedToUseApp().toString());
        Log.d("2.",u.retrivePromoterCode());
        Log.d("3.",u.retriveUserName());
        u.allowUserToUseApp("PROMOTER");
        u.saveUserName("USERNAME");
        Log.d("4.",u.userAllowedToUseApp().toString());
        Log.d("5.",u.retrivePromoterCode());
        Log.d("6.",u.retriveUserName());*/
    }

    public void btnGetWeather(View view) {
        ws=new WebService(this);

        //ws.validatePromoterCode("TEST123");

        /*User u=new User();
        EditText et= (EditText) findViewById(R.id.txtLat);
        u.name=et.getText().toString();
        u.birth_year="1986";
        u.email="android@test.com";
        ws.addUser(u,"test123");*/

        u=new User();
        et= (EditText) findViewById(R.id.txtLat);
        //ws.login(et.getText().toString(),"1986");
        //ws.getPartyCovers("sessionID");
    }

    public void webServiceReady(Hashtable result){

        if(is_logged){
            Log.d("AAA-afterLogin",result.toString());
        }else{
            Log.d("AAA-beforeLogin",result.toString());
            is_logged= (Boolean) result.get("logged");
            if(is_logged){
                //ws.getPartyCovers((String) result.get("sessionId"));
            }
        }

    }
}