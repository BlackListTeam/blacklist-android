package cat.andreurm.blacklist.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.model.Message;
import cat.andreurm.blacklist.model.MessageThread;
import cat.andreurm.blacklist.utils.Utils;
import cat.andreurm.blacklist.utils.WebService;
import cat.andreurm.blacklist.utils.WebServiceCaller;

public class ListMessagesActivity extends Activity implements WebServiceCaller {

    Utils u;
    WebService ws;
    LinearLayout ll;
    Boolean delete=false;
    Hashtable<String,MessageThread> message_threads=null;
    private ProgressDialog pdl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_messages);

        ws=new WebService(this);
        u=new Utils(this);
        ll=(LinearLayout) findViewById(R.id.listMessages);
        message_threads=new Hashtable<String, MessageThread>();



        TextView txtTitulo = (TextView) findViewById(R.id.textViewTituloMensajes);
        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));
        txtTitulo.setTypeface(font);
        txtTitulo.setPaintFlags(txtTitulo.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        pdl= ProgressDialog.show(getParent(), null, getString(R.string.loading), true, false);

        ws.getMessages(u.getSessionId());
    }

    @Override
    protected void onNewIntent(Intent i){
        ll.removeAllViews();
        message_threads=new Hashtable<String, MessageThread>();
        pdl= ProgressDialog.show(getParent(), null, getString(R.string.loading), true, false);
        ws.getMessages(u.getSessionId());
    }

    public void writeMessage(View view){
        Intent i = new Intent(getParent(), WriteMessageActivity.class);
        i.putExtra("mt_id", 0);
        TabGroupActivity parentActivity = (TabGroupActivity)getParent();
        parentActivity.startChildActivity("WriteMessageActivity", i);
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


        if(delete){
            ll.removeAllViews();
            new AlertDialog.Builder(ListMessagesActivity.this.getParent())
                    .setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.message_deleted))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delete = false;
                            message_threads=new Hashtable<String, MessageThread>();
                            ws.getMessages(u.getSessionId());
                        }

                    })
                    .show();


        }else{
            ArrayList<MessageThread> res= (ArrayList<MessageThread>) result.get("messages");

            if(res != null){
                Boolean first=true;
                for(MessageThread mt:res){
                    addMessageInList(mt,first);
                    message_threads.put(Integer.toString(mt.mt_id),mt);
                    first=false;
                }
            }else{
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.warning))
                        .setMessage(getString(R.string.messages_no))
                        .setPositiveButton(getString(R.string.ok), null)
                        .show();
            }
        }

        delete=false;
        pdl.dismiss();

    }

    private void addMessageInList(MessageThread m, Boolean first){
        RelativeLayout layout=new RelativeLayout(this);

        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);

        if(!first){
            rlp.setMargins(0,5,0,0);
        }

        layout.setLayoutParams(rlp);

        layout.setPadding(5,5,5,15);
        layout.setBackgroundColor(Color.parseColor("#000000"));


        Typeface font = Typeface.createFromAsset(getAssets(), getString(R.string.bebas_neue));

        // FROM
        TextView from = new TextView(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp.setMargins(5,7,0,0);

        from.setLayoutParams(lp);

        from.setTypeface(font);
        from.setPaintFlags(from.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        from.setText(getString(R.string.from));
        from.setTextSize(16);
        from.setTextColor(Color.parseColor("#444444"));

        layout.addView(from);

        //FROM name
        TextView fromName = new TextView(this);
        RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp4.setMargins(25,5,0,0);

        fromName.setLayoutParams(lp4);

        fromName.setTypeface(font);
        fromName.setPaintFlags(fromName.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        fromName.setText(m.from);
        fromName.setTextSize(20);
        fromName.setTextColor(Color.parseColor("#444444"));

        layout.addView(fromName);


        //SUBJECT
        TextView subject = new TextView(this);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp2.setMargins(5,30,0,0);

        subject.setLayoutParams(lp2);


        subject.setTypeface(font);
        subject.setPaintFlags(subject.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        subject.setText(m.subject);
        subject.setTextSize(22);
        subject.setTextColor(Color.parseColor("#ffffff"));

        subject.setOnClickListener(new viewMessage(m.mt_id));

        layout.addView(subject);


        //Trash
        Button trash = new Button(this);

        RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp3.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        trash.setLayoutParams(lp3);

        trash.setBackgroundResource(R.drawable.button_trash);
        trash.setOnClickListener(new deleteMessage(m.mt_id));

        layout.addView(trash);

        //END
        ll.addView(layout);

    }

    @Override
    public void onBackPressed() {
    }

    public class viewMessage implements View.OnClickListener
    {
        private int message_id;

        public viewMessage(int m_id)
        {
            this.message_id = m_id;
        }
        @Override
        public void onClick(View v)
        {

            Intent i = new Intent(getParent(), MessageInfoActivity.class);
            i.putExtra("message_thread", (Parcelable) message_threads.get(Integer.toString(message_id)));
            TabGroupActivity parentActivity = (TabGroupActivity)getParent();
            parentActivity.startChildActivity("MessageInfoActivity", i);
        }
    }

    public class deleteMessage implements View.OnClickListener
    {
        private int message_id;

        public deleteMessage(int m_id)
        {
            this.message_id = m_id;
        }

        @Override
        public void onClick(View v)
        {
            new AlertDialog.Builder(ListMessagesActivity.this.getParent())
                    .setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.delete_message))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delete=true;
                            pdl= ProgressDialog.show(getParent(), null, getString(R.string.loading), true, false);
                            ws.deleteMessage(message_id, u.getSessionId());
                        }

                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();

        }

    }
    
}
