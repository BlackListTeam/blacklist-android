package cat.andreurm.blacklist.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;

import cat.andreurm.blacklist.model.Message;
import cat.andreurm.blacklist.model.MessageThread;
import cat.andreurm.blacklist.model.Party;
import cat.andreurm.blacklist.model.Reservation;
import cat.andreurm.blacklist.model.User;

/**
 * Created by air on 17/07/13.
 */
public class WebService {

    static final String WS_URL="http://www.blacklistmeetings.com/ws/";

    static final String WS_PROTOCOL="http";
    static final String WS_HOST="www.blacklistmeetings.com";
    static final String WS_PATH="/ws/";

    Activity pare;

    public WebService(Activity a){
        pare=a;
    }

    private String parseJSON(String URL) {
        return parseJSON(URL,null);
    }

    private String parseJSON(String URL, List<NameValuePair> nameValuePairs) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();

        try {
            HttpResponse response;
            if(nameValuePairs == null){
                HttpGet http = new HttpGet(URL);
                response = httpClient.execute(http);
            }else{
                HttpPost http = new HttpPost(URL);
                UrlEncodedFormEntity form = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
                form.setContentEncoding(HTTP.UTF_8);
                http.setEntity(form);
                response = httpClient.execute(http);
            }

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Hashtable<String,Object> ret= new Hashtable<String,Object>();
                ret.put("authError",true);
                ret.put("errorMessage","Error al conectar con los servidores");
                Log.d("AND-parseJSon", "Failed to download file");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }

        } catch (Exception e) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            ret.put("authError",true);
            ret.put("errorMessage","Error al conectar con los servidores");
            ((WebServiceCaller) pare).webServiceReady(ret);

            Log.d("AND-parseJSon", e.getLocalizedMessage());
        }
        return stringBuilder.toString();
    }



    public void validatePromoterCode(String  promoterCode){
        try {
            URI uri = new URI(
                    WS_PROTOCOL,
                    WS_HOST,
                    WS_PATH+"validatePromoterCode",
                    "promoterCode="+promoterCode,
                    null);

            Object[] call={uri.toASCIIString()};
            new callValidatePromoterCode().execute(call);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            ret.put("authError",true);
            ret.put("errorMessage","Error al conectar con los servidores");
            ((WebServiceCaller) pare).webServiceReady(ret);
        }
    }

    private class callValidatePromoterCode extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... urls) {
            return parseJSON(urls[0].toString());
        }

        protected void onPostExecute(String result) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d("AND-callValidatePromoterCode",jsonObject.toString());

                ret.put("authError",false);
                ret.put("errorMessage","");
                ret.put("valid",(jsonObject.getJSONObject("response").getInt("valid") !=0));
                ((WebServiceCaller) pare).webServiceReady(ret);

            } catch (Exception e) {
                ret.put("authError",true);
                ret.put("errorMessage","Error al conectar con los servidores");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }
        }
    }



    public void addUser(User user, String promoter_code){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("name", user.name));
        nameValuePairs.add(new BasicNameValuePair("birth_year", user.birth_year));
        nameValuePairs.add(new BasicNameValuePair("email", user.email));
        nameValuePairs.add(new BasicNameValuePair("promoter_code", promoter_code));

        Object[] call={WS_URL+"addUser",nameValuePairs};
        new callAddUser().execute(call);
    }

    private class callAddUser extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... urls) {
            return parseJSON(urls[0].toString(),(List<NameValuePair>)urls[1]);
        }

        protected void onPostExecute(String result) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d("AND-callAddUser",jsonObject.toString());

                ret.put("authError",false);
                ret.put("errorMessage",jsonObject.getJSONObject("response").getString("errorMessage"));
                ret.put("added",(jsonObject.getJSONObject("response").getInt("added") !=0));
                ((WebServiceCaller) pare).webServiceReady(ret);

            } catch (Exception e) {
                ret.put("authError",true);
                ret.put("errorMessage","Error al conectar con los servidores");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }
        }
    }



    //TODO: Adaptar per push
    public void login(String name, String password){
        try {
            URI uri = new URI(
                    WS_PROTOCOL,
                    WS_HOST,
                    WS_PATH+"login",
                    "name="+name+"&password="+password,
                    null);

            Object[] call={uri.toASCIIString()};
            new callLogin().execute(call);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            ret.put("authError",true);
            ret.put("errorMessage","Error al conectar con los servidores");
            ((WebServiceCaller) pare).webServiceReady(ret);
        }
    }

    private class callLogin extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... urls) {
            return parseJSON(urls[0].toString());
        }

        protected void onPostExecute(String result) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d("AND-callLogin",jsonObject.toString());

                ret.put("authError",false);
                ret.put("errorMessage",jsonObject.getJSONObject("response").getString("errorMessage"));
                ret.put("logged",(jsonObject.getJSONObject("response").getInt("logged") !=0));
                ret.put("sessionId",jsonObject.getJSONObject("response").getString("sessionId"));
                ((WebServiceCaller) pare).webServiceReady(ret);

            } catch (Exception e) {
                Log.d("callLoginException",e.getLocalizedMessage());
                ret.put("authError",true);
                ret.put("errorMessage","Error al conectar con los servidores");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }
        }
    }



    //TODO: Fer el parsing
    public void getPartyCovers(String session_id){
        try {
            URI uri = new URI(
                    WS_PROTOCOL,
                    WS_HOST,
                    WS_PATH+"getPartyCovers",
                    "session_id="+session_id,
                    null);

            Object[] call={uri.toASCIIString()};
            new callGetPartyCovers().execute(call);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            ret.put("authError",true);
            ret.put("errorMessage","Error al conectar con los servidores");
            ((WebServiceCaller) pare).webServiceReady(ret);
        }
    }

    private class callGetPartyCovers extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... urls) {
            return parseJSON(urls[0].toString());
        }

        protected void onPostExecute(String result) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d("AND-callGetPartyCovers",jsonObject.toString());

                ArrayList<Party> parties=new ArrayList<Party>();

                JSONArray jsonparties=jsonObject.getJSONObject("response").getJSONArray("parties");
                for(int i=0;i<jsonparties.length();i++){
                    Party p=new Party();
                    JSONObject aux=jsonparties.getJSONObject(i);
                    JSONObject p_aux=aux.getJSONObject("Party");
                    JSONArray p_gallery=aux.getJSONArray("Image");
                    p.cover = p_aux.getString("cover");
                    p.cover = "http://www.blacklistmeetings.com/files/party/cover/"+p_aux.getString("cover_dir")+"/iphone_"+p_aux.getString("cover");
                    p.image = "http://www.blacklistmeetings.com/files/party/img/"+p_aux.getString("img_dir")+"/iphone_"+p_aux.getString("img");
                    p.party_id = Integer.parseInt(p_aux.getString("id"));
                    p.name = p_aux.getString("name");
                    p.date = p_aux.getString("date");
                    p.info = p_aux.getString("info");
                    p.place_text = p_aux.getString("place_text");
                    p.price_info = p_aux.getString("price_info");
                    p.latitude = Float.parseFloat(p_aux.getString("place_lat"));
                    p.longitude = Float.parseFloat(p_aux.getString("place_long"));
                    p.max_rooms = Integer.parseInt(p_aux.getString("max_rooms"));
                    p.max_escorts = Integer.parseInt(p_aux.getString("max_escorts"));
                    p.vip_allowed = Boolean.parseBoolean(p_aux.getString("vip_allowed"));
                    p.location_date = p_aux.getString("place_date");
                    p.address = p_aux.getString("address");
                    p.gallery = new String[p_gallery.length()];
                    for(int j=0;j<p_gallery.length();j++){
                        JSONObject image=p_gallery.getJSONObject(j);
                        p.gallery[j] = "http://www.blacklistmeetings.com/files/image/img/"+image.getString("img_dir")+"/iphone_"+image.getString("img");
                    }
                    parties.add(p);
                }
                Log.d("AND-parties",parties.get(0).cover.toString());

                ret.put("parties",parties);

                ret.put("authError",jsonObject.getJSONObject("response").getInt("authError") !=0);
                ret.put("errorMessage",jsonObject.getJSONObject("response").getString("errorMessage"));
                ((WebServiceCaller) pare).webServiceReady(ret);

            } catch (Exception e) {
                ret.put("authError",true);
                ret.put("errorMessage","Error al conectar con los servidores");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }
        }
    }

    public void getCurrentReservation(String  session_id){
        try {
            URI uri = new URI(
                    WS_PROTOCOL,
                    WS_HOST,
                    WS_PATH+"getCurrentReservation",
                    "session_id="+session_id,
                    null);

            Object[] call={uri.toASCIIString()};
            new callGetCurrentReservation().execute(call);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            ret.put("authError",true);
            ret.put("errorMessage","Error al conectar con los servidores");
            ((WebServiceCaller) pare).webServiceReady(ret);
        }
    }

    private class callGetCurrentReservation extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... urls) {
            return parseJSON(urls[0].toString());
        }

        protected void onPostExecute(String result) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d("AND-callGetCurrentReservation",jsonObject.toString());

                ret.put("authError",(jsonObject.getJSONObject("response").getInt("authError") !=0));
                ret.put("errorMessage",jsonObject.getJSONObject("response").getString("errorMessage"));



                if(!jsonObject.getJSONObject("response").isNull("reservation")){
                    JSONObject r_json=jsonObject.getJSONObject("response").getJSONObject("reservation").getJSONObject("Reservation");
                    Log.d("AND-RESERVATION",r_json.toString());
                    Reservation r=new Reservation();
                    r.escorts=r_json.getInt("escorts");
                    r.vip=r_json.getBoolean("vip");
                    r.rooms=r_json.getInt("rooms");
                    r.qr=r_json.getString("qr");
                    r.party_name=r_json.getString("party_name");
                    ret.put("reservation",r);
                }


                Log.d("AND-callGetCurrentReservation",ret.toString());
                ((WebServiceCaller) pare).webServiceReady(ret);

            } catch (Exception e) {
                Log.d("AND-Exception",e.getMessage());
                ret.put("authError",true);
                ret.put("errorMessage","Error al conectar con los servidores");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }
        }
    }



    public void makeReservation(Reservation r,String session_id){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("escorts", Integer.toString(r.escorts)));
        nameValuePairs.add(new BasicNameValuePair("vip", Boolean.toString(r.vip)));
        nameValuePairs.add(new BasicNameValuePair("rooms", Integer.toString(r.rooms)));
        nameValuePairs.add(new BasicNameValuePair("session_id", session_id));

        Object[] call={WS_URL+"makeReservation",nameValuePairs};
        Log.v("DADES RESERVA ",Integer.toString(r.escorts)+"    "+Boolean.toString(r.vip)+"     "+Integer.toString(r.rooms)+"    "+session_id );
        new callMakeReservation().execute(call);
    }

    private class callMakeReservation extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... urls) {
            return parseJSON(urls[0].toString(),(List<NameValuePair>)urls[1]);
        }

        protected void onPostExecute(String result) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d("AND-callMakeReservation",jsonObject.toString());


                ret.put("authError",(jsonObject.getJSONObject("response").getInt("authError") !=0));

                ret.put("reservated",(jsonObject.getJSONObject("response").getInt("reservated") !=0));


                ret.put("errorMessage",jsonObject.getJSONObject("response").getString("errorMessage"));

                //TODO: fer El parsing

                ((WebServiceCaller) pare).webServiceReady(ret);

            } catch (Exception e) {
                ret.put("authError",true);
                ret.put("errorMessage","Error al conectar con los servidores");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }
        }
    }



    public void deleteReservation(String  session_id){
        try {
            URI uri = new URI(
                    WS_PROTOCOL,
                    WS_HOST,
                    WS_PATH+"deleteReservation",
                    "session_id="+session_id,
                    null);

            Object[] call={uri.toASCIIString()};
            new callDeleteReservation().execute(call);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            ret.put("authError",true);
            ret.put("errorMessage","Error al conectar con los servidores");
            ((WebServiceCaller) pare).webServiceReady(ret);
        }
    }

    private class callDeleteReservation extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... urls) {
            return parseJSON(urls[0].toString());
        }

        protected void onPostExecute(String result) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d("AND-callDeleteReservation",jsonObject.toString());

                ret.put("authError",(jsonObject.getJSONObject("response").getInt("authError") !=0));
                ret.put("errorMessage",jsonObject.getJSONObject("response").getString("errorMessage"));
                ret.put("deleted",(jsonObject.getJSONObject("response").getInt("deleted") !=0));


                ((WebServiceCaller) pare).webServiceReady(ret);

            } catch (Exception e) {
                ret.put("authError",true);
                ret.put("errorMessage","Error al conectar con los servidores");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }
        }
    }



    public void getMessages(String  session_id){
        try {
            URI uri = new URI(
                    WS_PROTOCOL,
                    WS_HOST,
                    WS_PATH+"getMessages",
                    "session_id="+session_id,
                    null);

            Object[] call={uri.toASCIIString()};
            new callGetMessages().execute(call);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            ret.put("authError",true);
            ret.put("errorMessage","Error al conectar con los servidores");
            ((WebServiceCaller) pare).webServiceReady(ret);
        }
    }

    private class callGetMessages extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... urls) {
            return parseJSON(urls[0].toString());
        }

        protected void onPostExecute(String result) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d("AND-callGetMessages",jsonObject.toString());

                ArrayList<MessageThread> messageThreads = new ArrayList<MessageThread>();

                JSONArray jsonthreadmessages = jsonObject.getJSONObject("response").getJSONArray("messages");

                for(int i=0;i<jsonthreadmessages.length();i++){

                    MessageThread mT=new MessageThread();
                    JSONObject aux=jsonthreadmessages.getJSONObject(i);
                    aux=jsonthreadmessages.getJSONObject(i);
                    JSONObject message_thread_aux=aux.getJSONObject("MessageThread");
                    JSONArray jsonmessages = aux.getJSONArray("Message");

                    mT.mt_id = message_thread_aux.getInt("id");
                    mT.from = message_thread_aux.getString("from");
                    mT.subject= message_thread_aux.getString("subject");
                    mT.messages = new ArrayList<Message>();

                    for(int j=0;j<jsonmessages.length();j++){
                        JSONObject aux_message=jsonmessages.getJSONObject(j);
                        Message m = new Message();
                        m.m_id = aux_message.getInt("id");
                        m.answer = aux_message.getBoolean("answer");
                        m.text = aux_message.getString("text");
                        m.pay_link = aux_message.getString("pay_link");
                        //m.date = aux_message.getString("created");
                        mT.messages.add(m);
                    }
                    messageThreads.add(mT);
                }

                ret.put("messages",messageThreads);
                ret.put("authError",jsonObject.getJSONObject("response").getInt("authError") !=0);
                ret.put("errorMessage",jsonObject.getJSONObject("response").getString("errorMessage"));

                ((WebServiceCaller) pare).webServiceReady(ret);

            } catch (Exception e) {
                Log.d("AND-ex",e.getLocalizedMessage());
                ret.put("authError", true);
                ret.put("errorMessage","Error al conectar con los servidores");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }
        }
    }



    public void replyMessage(String message, int message_stream_id, String session_id){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("message", message));
        nameValuePairs.add(new BasicNameValuePair("message_stream_id", Integer.toString(message_stream_id)));
        nameValuePairs.add(new BasicNameValuePair("session_id", session_id));

        Object[] call={WS_URL+"replyMessage",nameValuePairs};
        new callReplyMessage().execute(call);
    }

    private class callReplyMessage extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... urls) {
            return parseJSON(urls[0].toString(),(List<NameValuePair>)urls[1]);
        }

        protected void onPostExecute(String result) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d("AND-callReplyMessage",jsonObject.toString());

                ret.put("authError",(jsonObject.getJSONObject("response").getInt("authError") !=0));
                ret.put("errorMessage",jsonObject.getJSONObject("response").getString("errorMessage"));

                //TODO: fer El parsing

                ((WebServiceCaller) pare).webServiceReady(ret);

            } catch (Exception e) {
                ret.put("authError",true);
                ret.put("errorMessage","Error al conectar con los servidores");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }
        }
    }



    public void addMessage(String message, String session_id){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("message", message));
        nameValuePairs.add(new BasicNameValuePair("session_id", session_id));

        Object[] call={WS_URL+"addMessage",nameValuePairs};
        new callAddMessage().execute(call);
    }

    private class callAddMessage extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... urls) {
            return parseJSON(urls[0].toString(),(List<NameValuePair>)urls[1]);
        }

        protected void onPostExecute(String result) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d("AND-callAddMessage",jsonObject.toString());

                ret.put("authError",(jsonObject.getJSONObject("response").getInt("authError") !=0));
                ret.put("errorMessage",jsonObject.getJSONObject("response").getString("errorMessage"));
                ret.put("added",(jsonObject.getJSONObject("response").getInt("added") !=0));

                ((WebServiceCaller) pare).webServiceReady(ret);

            } catch (Exception e) {
                ret.put("authError",true);
                ret.put("errorMessage","Error al conectar con los servidores");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }
        }
    }



    public void deleteMessage(int message_id,String  session_id){

        try {
            URI uri = new URI(
                    WS_PROTOCOL,
                    WS_HOST,
                    WS_PATH+"deleteMessage",
                    "message_stream_id="+Integer.toString(message_id)+"&session_id="+session_id,
                    null);

            Object[] call={uri.toASCIIString()};
            new callDeleteMessage().execute(call);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            ret.put("authError",true);
            ret.put("errorMessage","Error al conectar con los servidores");
            ((WebServiceCaller) pare).webServiceReady(ret);
        }
    }

    private class callDeleteMessage extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... urls) {
            return parseJSON(urls[0].toString());
        }

        protected void onPostExecute(String result) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d("AND-callDeleteMessage",jsonObject.toString());

                ret.put("authError",(jsonObject.getJSONObject("response").getInt("authError") !=0));
                ret.put("errorMessage",jsonObject.getJSONObject("response").getString("errorMessage"));
                ret.put("deleted",(jsonObject.getJSONObject("response").getInt("deleted") !=0));

                ((WebServiceCaller) pare).webServiceReady(ret);

            } catch (Exception e) {
                ret.put("authError",true);
                ret.put("errorMessage","Error al conectar con los servidores");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }
        }
    }



    public void sendInvitation(String email, String session_id){
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("email", email));
        nameValuePairs.add(new BasicNameValuePair("session_id", session_id));

        Object[] call={WS_URL+"sendInvitation",nameValuePairs};
        new callSendInvitation().execute(call);
    }

    private class callSendInvitation extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... urls) {
            return parseJSON(urls[0].toString(),(List<NameValuePair>)urls[1]);
        }

        protected void onPostExecute(String result) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d("AND-callSendInvitation",jsonObject.toString());

                ret.put("authError",(jsonObject.getJSONObject("response").getInt("authError") !=0));
                ret.put("errorMessage",jsonObject.getJSONObject("response").getString("errorMessage"));
                ret.put("sended",(jsonObject.getJSONObject("response").getInt("sended") !=0));

                ((WebServiceCaller) pare).webServiceReady(ret);

            } catch (Exception e) {
                ret.put("authError",true);
                ret.put("errorMessage","Error al conectar con los servidores");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }
        }
    }




    public void getNewMessages(String  session_id){
        try {
            URI uri = new URI(
                    WS_PROTOCOL,
                    WS_HOST,
                    WS_PATH+"getNewMessages",
                    "session_id="+session_id,
                    null);

            Object[] call={uri.toASCIIString()};
            new callGetNewMessages().execute(call);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            ret.put("authError",true);
            ret.put("errorMessage","Error al conectar con los servidores");
            ((WebServiceCaller) pare).webServiceReady(ret);
        }
    }

    private class callGetNewMessages extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... urls) {
            return parseJSON(urls[0].toString());
        }

        protected void onPostExecute(String result) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d("AND-callGetNewMessages",jsonObject.toString());

                ret.put("authError",(jsonObject.getJSONObject("response").getInt("authError") !=0));
                ret.put("errorMessage",jsonObject.getJSONObject("response").getString("errorMessage"));

                //TODO:Fer el parsing

                ((WebServiceCaller) pare).webServiceReady(ret);

            } catch (Exception e) {
                ret.put("authError",true);
                ret.put("errorMessage","Error al conectar con los servidores");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }
        }
    }




    public void readMessages(String  session_id){
        try {
            URI uri = new URI(
                    WS_PROTOCOL,
                    WS_HOST,
                    WS_PATH+"readMessages",
                    "session_id="+session_id,
                    null);

            Object[] call={uri.toASCIIString()};
            new callReadMessages().execute(call);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            ret.put("authError",true);
            ret.put("errorMessage","Error al conectar con los servidores");
            ((WebServiceCaller) pare).webServiceReady(ret);
        }
    }

    private class callReadMessages extends AsyncTask<Object, Void, String> {
        protected String doInBackground(Object... urls) {
            return parseJSON(urls[0].toString());
        }

        protected void onPostExecute(String result) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.d("AND-callReadMessages",jsonObject.toString());

                ret.put("authError",(jsonObject.getJSONObject("response").getInt("authError") !=0));
                ret.put("errorMessage",jsonObject.getJSONObject("response").getString("errorMessage"));

                //TODO:Fer el parsing

                ((WebServiceCaller) pare).webServiceReady(ret);

            } catch (Exception e) {
                ret.put("authError",true);
                ret.put("errorMessage","Error al conectar con los servidores");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }
        }
    }
}
