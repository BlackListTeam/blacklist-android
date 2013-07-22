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

import cat.andreurm.blacklist.model.Party;
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
                Log.d("parseJSon", "Failed to download file");
                ((WebServiceCaller) pare).webServiceReady(ret);
            }

        } catch (Exception e) {
            Hashtable<String,Object> ret= new Hashtable<String,Object>();
            ret.put("authError",true);
            ret.put("errorMessage","Error al conectar con los servidores");
            ((WebServiceCaller) pare).webServiceReady(ret);

            Log.d("parseJSon", e.getLocalizedMessage());
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
                Log.d("callValidatePromoterCode",jsonObject.toString());

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
                Log.d("callAddUser",jsonObject.toString());

                ret.put("authError",false);
                ret.put("errorMessage",jsonObject.getJSONObject("response").getString("errorMessage"));
                ret.put("valid",(jsonObject.getJSONObject("response").getInt("added") !=0));
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
                Log.d("callLogin",jsonObject.toString());

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
                Log.d("callGetPartyCovers",jsonObject.toString());

                ArrayList<Party> parties=new ArrayList<Party>();

                JSONArray jsonparties=jsonObject.getJSONObject("response").getJSONArray("parties");
                for(int i=0;i<jsonparties.length();i++){
                    Party p=new Party();
                    JSONObject aux=jsonparties.getJSONObject(i);
                    JSONObject p_aux=aux.getJSONObject("Party");
                    p.cover=p_aux.getString("cover");
                    parties.add(p);
                }
                Log.d("parties",parties.get(0).cover.toString());

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

}
