package cat.andreurm.blacklist.activities;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.model.Message;
import cat.andreurm.blacklist.model.MessageThread;
import cat.andreurm.blacklist.utils.Utils;
import cat.andreurm.blacklist.utils.WebService;
import cat.andreurm.blacklist.utils.WebServiceCaller;

public class MessageInfoActivity extends Activity{

    ImageButton bVolver;
    Utils u;
    WebService ws;
    MessageThread mt;
    LinearLayout ll;
    String payLink="";
    TextView b_answer;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_info);
        ws=new WebService(this);
        u=new Utils(this);
        ll=(LinearLayout) findViewById(R.id.listMessages);

        TextView txtTitulo = (TextView) findViewById(R.id.textViewTituloMensaje);
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txtTitulo.setTypeface(font);
        txtTitulo.setPaintFlags(txtTitulo.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);


        TextView answer = (TextView) findViewById(R.id.answer);
        Typeface font2 = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        answer.setTypeface(font2);
        answer.setPaintFlags(txtTitulo.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        bVolver = (ImageButton) findViewById(R.id.buttonVolver);
        bVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getParent(), ListMessagesActivity.class);
                TabGroupActivity parentActivity = (TabGroupActivity)getParent();
                parentActivity.startChildActivity("ListMessagesActivity", i);
            }
        });

        b_answer= (TextView) findViewById(R.id.answer);
        b_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("AND-ANSWER","ANSWER");
                Intent i = new Intent(getParent(), WriteMessageActivity.class);
                i.putExtra("mt_id", mt.mt_id);
                TabGroupActivity parentActivity = (TabGroupActivity)getParent();
                parentActivity.startChildActivity("WriteMessageActivity", i);
            }
        });

        Bundle b = getIntent().getExtras();
        MessageThread object = b.getParcelable("message_thread");

        mt=object;

        printList();
        scrollToBottom();
    }

    @Override
    protected void onNewIntent(Intent i){
        ll.removeAllViews();

        Bundle b = i.getExtras();
        MessageThread object = b.getParcelable("message_thread");
        mt=object;

        b_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("AND-ANSWER","ANSWER");
                Intent i = new Intent(getParent(), WriteMessageActivity.class);
                i.putExtra("mt_id", mt.mt_id);
                TabGroupActivity parentActivity = (TabGroupActivity)getParent();
                parentActivity.startChildActivity("WriteMessageActivity", i);
            }
        });

        printList();
        scrollToBottom();
    }

    private void printList(){
        printHeader();

        Boolean first=true;
        for(Message m:mt.messages){
            if(!first){
                printItem(m);
            }
            first=false;
        }
    }

    private void printHeader(){
        //LAYOUT SUPERIOR
        RelativeLayout layout=new RelativeLayout(this);
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rlp.setMargins(7,0,7,0);
        layout.setLayoutParams(rlp);
        layout.setPadding(0,0,0,7);
        layout.setBackgroundColor(Color.parseColor("#000000"));

        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));

        //FROM
        TextView from = new TextView(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(7,9,0,0);
        from.setLayoutParams(lp);
        from.setTypeface(font);
        from.setPaintFlags(from.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        from.setText(getString(R.string.from));
        from.setTextSize(16);
        from.setTextColor(Color.parseColor("#444444"));
        from.setId(1);
        layout.addView(from);

        //FROM name
        TextView fromName = new TextView(this);
        RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp4.addRule(RelativeLayout.RIGHT_OF, 1);
        lp4.setMargins(3,7,0,0);
        fromName.setLayoutParams(lp4);
        fromName.setTypeface(font);
        fromName.setPaintFlags(fromName.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        fromName.setText(mt.from);
        fromName.setTextSize(20);
        fromName.setTextColor(Color.parseColor("#444444"));
        layout.addView(fromName);

        //SUBJECT
        TextView subject = new TextView(this);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp2.setMargins(7,7,7,0);
        lp2.addRule(RelativeLayout.BELOW,1);
        subject.setLayoutParams(lp2);
        subject.setTypeface(font);
        subject.setPaintFlags(subject.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        subject.setText(mt.subject);
        subject.setTextSize(22);
        subject.setTextColor(Color.parseColor("#ffffff"));
        subject.setId(2);
        layout.addView(subject);

        //DATE
        TextView date = new TextView(this);
        RelativeLayout.LayoutParams lp_date = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_date.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp_date.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp_date.setMargins(0, 7, 7, 7);
        date.setLayoutParams(lp_date);
        date.setTypeface(font);
        date.setPaintFlags(date.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        date.setText(getPrettyDate(mt.messages.get(0).date));
        date.setTextSize(16);
        date.setTextColor(Color.parseColor("#222222"));
        layout.addView(date);

        //LINE
        RelativeLayout line=new RelativeLayout(this);
        RelativeLayout.LayoutParams lp_line = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                1);
        lp_line.setMargins(7,7,7,0);
        lp_line.addRule(RelativeLayout.BELOW,2);
        line.setLayoutParams(lp_line);
        line.setBackgroundColor(Color.parseColor("#666666"));
        line.setId(3);
        layout.addView(line);

        //MSJ
        TextView msj = new TextView(this);
        RelativeLayout.LayoutParams lp_msj = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_msj.setMargins(7,7,7,0);
        lp_msj.addRule(RelativeLayout.BELOW,3);
        msj.setLayoutParams(lp_msj);
        msj.setText(mt.messages.get(0).text);
        msj.setTextSize(12);
        msj.setTextColor(Color.parseColor("#ffffff"));
        msj.setId(4);
        layout.addView(msj);

        //PAY
        if(mt.messages.get(0).pay_link.length()>1){
            payLink=mt.messages.get(0).pay_link;
            TextView pay = new TextView(this);
            RelativeLayout.LayoutParams lp_pay = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_pay.setMargins(7,7,7,0);
            lp_pay.addRule(RelativeLayout.BELOW,4);
            lp_pay.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            pay.setLayoutParams(lp_pay);
            pay.setTypeface(font);
            pay.setPaintFlags(pay.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            pay.setText("PAGAR");
            pay.setTextSize(20);
            pay.setTextColor(Color.parseColor("#008358"));
            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(payLink));
                    startActivity(browserIntent);
                }
            });
            layout.addView(pay);
        }


        //END
        ll.addView(layout);
    }

    private void printItem(Message m){
        //LAYOUT
        RelativeLayout layout=new RelativeLayout(this);
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        if(m.answer){
            rlp.setMargins(57,7,7,0);
        }else{
            rlp.setMargins(7,7,57,0);
        }
        layout.setLayoutParams(rlp);
        layout.setPadding(0,0,0,7);
        layout.setBackgroundColor(Color.parseColor("#000000"));

        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));

        //DATE
        TextView date = new TextView(this);
        RelativeLayout.LayoutParams lp_date = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_date.setMargins(7, 7, 7, 0);
        date.setLayoutParams(lp_date);
        date.setTypeface(font);
        date.setPaintFlags(date.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        date.setText(getPrettyDate(m.date));
        date.setTextSize(16);
        date.setTextColor(Color.parseColor("#222222"));
        date.setId(1);
        layout.addView(date);

        //MSJ
        TextView msj = new TextView(this);
        RelativeLayout.LayoutParams lp_msj = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_msj.setMargins(7, 7, 7, 0);
        lp_msj.addRule(RelativeLayout.BELOW, 1);
        msj.setLayoutParams(lp_msj);
        msj.setText(m.text);
        msj.setTextSize(12);
        msj.setTextColor(Color.parseColor("#ffffff"));
        msj.setId(2);
        layout.addView(msj);

        //PAY
        if(m.pay_link.length()>1){
            payLink=m.pay_link;
            TextView pay = new TextView(this);
            RelativeLayout.LayoutParams lp_pay = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_pay.setMargins(7,7,7,0);
            lp_pay.addRule(RelativeLayout.BELOW, 2);
            lp_pay.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            pay.setLayoutParams(lp_pay);
            pay.setTypeface(font);
            pay.setPaintFlags(pay.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            pay.setText("PAGAR");
            pay.setTextSize(20);
            pay.setTextColor(Color.parseColor("#008358"));
            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(payLink));
                    startActivity(browserIntent);
                }
            });
            layout.addView(pay);
        }

        //END
        ll.addView(layout);
    }

    private void scrollToBottom(){
        getScrollView().post(new Runnable() {

            @Override
            public void run() {
                getScrollView().fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public ScrollView getScrollView(){
        return (ScrollView)findViewById(R.id.scrollViewMensajes);
    }


    private String getPrettyDate(String s_date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        try {
            Date d = dateFormat.parse(s_date);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(d);

            String week_day="";
            switch(calendar.get(Calendar.DAY_OF_WEEK)){
                case 1:
                    week_day=getString(R.string.monday);
                    break;
                case 2:
                    week_day=getString(R.string.tuesday);
                    break;
                case 3:
                    week_day=getString(R.string.wednesday);
                    break;
                case 4:
                    week_day=getString(R.string.thursday);
                    break;
                case 5:
                    week_day=getString(R.string.friday);
                    break;
                case 6:
                    week_day=getString(R.string.saturday);
                    break;
                case 7:
                    week_day=getString(R.string.sunday);
                    break;
            }

            String day;
            if(calendar.get(Calendar.DAY_OF_MONTH)<10){
                day="0"+Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
            }else{
                day=Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
            }

            String month;
            if((calendar.get(Calendar.MONTH)+1)<10){
                month="0"+Integer.toString(calendar.get(Calendar.MONTH)+1);
            }else{
                month=Integer.toString(calendar.get(Calendar.MONTH)+1);
            }

            return week_day+" "+day+"/"+month+"/"+calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            Log.e("EventsSitioActivity","Error al parsear fechas");
            e.printStackTrace();
            return "";
        }

    }



    @Override
    public void onBackPressed() {
    }
}



/*
public class MessageInfoActivity extends android.support.v4.app.FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

    }


}*/