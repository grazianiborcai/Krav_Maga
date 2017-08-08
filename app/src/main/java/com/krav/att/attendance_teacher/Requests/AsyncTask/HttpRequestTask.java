package com.krav.att.attendance_teacher.Requests.AsyncTask;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.krav.att.attendance_teacher.Authentication.AttAccountGeneral;
import com.krav.att.attendance_teacher.Requests.Util.HTTPMethodEnum;
import com.krav.att.attendance_teacher.Requests.Util.NamedPairParameter;
import com.krav.att.attendance_teacher.Requests.Interface.OnTaskFinished;

import org.joda.time.LocalDateTime;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HttpRequestTask extends AsyncTask<String, Void, Intent> {
    //    public static final String mServer = "http://192.168.1.7:8080/Agenda_WS/";
//    public static final String mServer = "https://4lc.com.br:8443/FastBar_WS/";
//    public static final String mServer = "http://192.168.43.231:8080/FastBar_WS/";

    //Loaded from string resource in config.xml
    protected static String mServer = null;  //"https://mind5.com.br:8443/Agenda_WS/";

    public static final String BASIC = "Basic ";
    public static final String AUTHORIZATION = "Authorization";
    public static final String OAUTH = "oAuth";
    protected static final String SELECT_CODE = "selectCode";
    protected static final String UPDATE_CODE = "updateCode";
    protected static final String SELECT_MSG = "selectMessage";
    protected static final String UPDATE_MSG = "updateMessage";
    public static final String RESULTS = "results";
    public static final String HTTP_CODE = "HttpCode";
    public static final String SERVICE_MESSAGE = "MESSAGE";
    public static final String SERVICE_CODE = "Code";
    public static final String IDENT = "ident";
    public static final String TIME_GET_TIME = "Time/getTime";
    public static final String GET = "GET";

    //used on loading images
    private static String mLastAuthtoken;

    protected String basicAuth = null;
    protected String oAuth = null;
    private String mEmail = null;
    private String mPassword = null;
    protected String mPath = null;
    protected HTTPMethodEnum mMethod = null;
    protected Class<?> mClass;
    protected OnTaskFinished mOnTaskFinished;
    protected int identifying;

    private static MessageDigest digester;

    static {
        try {
            digester = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<NamedPairParameter> headers=null;

    public HttpRequestTask(@NonNull String path, HTTPMethodEnum method, Class<?> mclass, @NonNull String authtoken, @NonNull OnTaskFinished onTaskFinished, int ident) {
        mPath = path;
        mMethod = method;
        mClass = mclass;
        oAuth = authtoken;
        mOnTaskFinished = onTaskFinished;
        identifying = ident;
    }

    public HttpRequestTask(@NonNull String path, HTTPMethodEnum method, @NonNull Class<?> mclass, @NonNull String email, @NonNull String password, @NonNull OnTaskFinished onTaskFinished, int ident) {
        mPath = path;
        mMethod = method;
        mClass = mclass;
        mEmail = email;
        mPassword = password;
        mOnTaskFinished = onTaskFinished;
        identifying = ident;
    }

    public HttpRequestTask() {
    }

    public boolean isRunning() {
        return this.getStatus() == Status.RUNNING;
    }

    @Override
    protected void onPreExecute() {

    }

    protected void defineToken() throws IOException {
        if (mEmail != null && !mEmail.isEmpty() && mPassword != null && !mPassword.isEmpty()) {
            String login = mEmail + ":" + mPassword;
            basicAuth = Base64.encodeToString(login.getBytes(), Base64.DEFAULT).replace("\n", "");
        }

        /*if (mAuthtoken!=null) {
            mLastAuthtoken = mAuthtoken;
            token = token(mPath, BASIC + mAuthtoken);
        }*/
    }

    protected HttpURLConnection prepareConnection(String[] params) throws IOException {
        URL url = prepareUrl(params);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(15000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.setRequestMethod(mMethod.toString());

        //filling up http header
        if (basicAuth!=null) {
            urlConnection.setRequestProperty(AUTHORIZATION, BASIC + basicAuth);
        }
        if (oAuth!=null) {
            urlConnection.setRequestProperty(OAUTH, oAuth);
        }
        //urlConnection.setRequestProperty("app", "client");
        //urlConnection.setRequestProperty("Accept-Encoding", "gzip");
        //urlConnection.setRequestProperty("zoneId", TimeZone.getDefault().getID());

        if (headers!=null) {
            for (NamedPairParameter n : headers) {
                urlConnection.setRequestProperty(n.getKey(), n.getValue());
            }
        }

        if (mMethod==HTTPMethodEnum.HTTP_POST) {
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
        }
        return urlConnection;
    }

    public void setHeaders(ArrayList<NamedPairParameter> headers) {
        this.headers = headers;
    }

    @Override
    protected Intent doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        Bundle data = new Bundle();
        data.putInt(IDENT, identifying);
        try {
            defineToken();
            urlConnection = prepareConnection(params);

            if (mMethod==HTTPMethodEnum.HTTP_POST) {
                writeBody(urlConnection, params);
            }

            int responseCode = urlConnection.getResponseCode();
            StringBuffer response = null;
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                //InputStream inStream = new GZIPInputStream(urlConnection.getInputStream());
                InputStream inStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
                String inputLine;
                response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String responseString = response.toString();

//            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                //{"updateCode":200,"updateMessage":"Records inserted"}
                JsonParser parser = new JsonParser();
                JsonObject object = parser.parse(responseString).getAsJsonObject();

                int code = 0;
                String message = "";
                if (object.has(SELECT_CODE)) {
                    code = object.get(SELECT_CODE).getAsInt();
                    if (object.has(SELECT_MSG)) {
                        message = object.get(SELECT_MSG).getAsString();
                    }
                } else if (object.has(UPDATE_CODE)) {
                    code = object.get(UPDATE_CODE).getAsInt();
                    if (object.has(UPDATE_MSG)) {
                        message = object.get(UPDATE_MSG).getAsString();
                    }
                }

                fillBundle(object, data);

                if (mClass!=null) {
                    if (object.has(RESULTS)) {
                        ArrayList<?> results = jsonToObjectList(object.get(RESULTS).getAsJsonArray(), mClass);
                        data.putParcelableArrayList(RESULTS, (ArrayList<? extends Parcelable>) results);
                    } else {
                        try {
                            data.putParcelable(RESULTS, (Parcelable) jsonToObject(object, mClass));
                        } catch (Exception e) {
                        }
                    }
                    data.putString("request_class", mClass.toString());
                }

                data.putString(AccountManager.KEY_ACCOUNT_NAME, AttAccountGeneral.ACCOUNT_NAME);
                data.putString(AccountManager.KEY_ACCOUNT_TYPE, AttAccountGeneral.ACCOUNT_TYPE);
                data.putString(AccountManager.KEY_PASSWORD, AttAccountGeneral.ACCOUNT_PASSWORD);
//s                }

                data.putInt(SERVICE_CODE, code);
                data.putString(SERVICE_MESSAGE, message);
            } else {  //printing error from server on local console
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        urlConnection.getErrorStream()));
                String inputLine;
                response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String responseString = response.toString();
                System.out.println(responseString);
            }

            data.putInt(HTTP_CODE, responseCode);

        } catch (Exception e) {
            e.printStackTrace();
            data.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage());
        } finally {
            try {
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final Intent res = new Intent();
        res.putExtras(data);
        return res;

    }

    private void fillBundle(JsonObject object, Bundle data) {
        final Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
        Iterator it = entries.iterator();
        while (it.hasNext()) {
            Map.Entry<String, JsonElement> pack = (Map.Entry<String, JsonElement>) it.next();
            if (!RESULTS.equalsIgnoreCase(pack.getKey())) {
                if (!pack.getValue().isJsonNull()) {
                    data.putString(pack.getKey(), pack.getValue().getAsString());
                } else {
                    data.putString(pack.getKey(), null);
                }
            }
        }
    }

    protected void writeBody(HttpURLConnection conn, String[] body) throws IOException {
        if (body==null || body.length==0 || TextUtils.isEmpty(body[0])) return;

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(body[0]);
            writer.flush();
        } finally {
            if (writer!=null) writer.close();
            os.close();
        }
    }

    protected URL prepareUrl(String[] params) throws MalformedURLException {
        if (mMethod==HTTPMethodEnum.HTTP_GET && params!=null && params.length>0) {
            return new URL(mServer + mPath + (TextUtils.isEmpty(params[0])?"":"?"+params[0]));
        } else {
            return new URL(mServer + mPath);
        }
    }

    private ArrayList<?> jsonToObjectList(JsonArray array, Class<?> c) {
        ArrayList<Object> objectList = new ArrayList<Object>();
        Gson gson = new Gson();
        for (int i = 0; i < array.size(); i++) {
            objectList.add(gson.fromJson(array.get(i), c));
        }
        return objectList;
    }

    private Object jsonToObject(JsonObject obj, Class<?> c) {
        Gson gson = new Gson();
        return gson.fromJson(obj, c);
    }

    private static String crypt(String str) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("String to encript cannot be null or zero length");
        }

        digester.update(str.getBytes());
        byte[] hash = digester.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            if ((0xff & hash[i]) < 0x10) {
                hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
            } else {
                hexString.append(Integer.toHexString(0xFF & hash[i]));
            }
        }
        return hexString.toString();
    }

    private String token(String path, String auth) throws IOException {

        HttpURLConnection urlConnection = null;
        URL url = new URL(mServer + TIME_GET_TIME);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(GET);
        int responseCode = urlConnection.getResponseCode();
        StringBuffer response = null;
        String randomKey = "";
        String pathDataMD5 = "";
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(response.toString()).getAsJsonObject();

            LocalDateTime dateTime = LocalDateTime.parse(object.get("time").getAsString());
            dateTime = dateTime.minusYears(3);
            randomKey = dateTime.toLocalDate().toString() + dateTime.getHourOfDay() + dateTime.getMinuteOfHour();
            pathDataMD5 = crypt(path + auth);
        } else {
            throw new IOException();
        }

        return crypt(pathDataMD5 + randomKey);
    }

    @Override
    protected void onPostExecute(final Intent intent) {
         mOnTaskFinished.postExecute(intent);
    }

    @Override
    protected void onCancelled() {
        mOnTaskFinished.cancelled();
    }

    public static HashMap<String, String> fillImagesHeaderData() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("app", "client");
        headers.put(AUTHORIZATION, BASIC + mLastAuthtoken);
        //TODO - verificar se precisa realmente do token na carga das imagens.
//        try {
//            headers.put(TOKEN, token("File/download", BASIC + mLastAuthtoken));
//        } catch (IOException e){ }
        return headers;
    }

    public void setBaseURL(String url) {
        mServer = url;
    }

    public static String getBaseURL() {
        return mServer;
    }
}