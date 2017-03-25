package com.epipasha.translater;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.epipasha.translater.db.DbManager;
import com.epipasha.translater.objects.Language;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by Pavel on 16.03.2017.
 */

public class Translater {

    private static final String API_KEY = "trnsl.1.1.20170316T165017Z.6542920cb7c835ce.bd48c1d999a2f34330ff01fe6b07792cdc8da13d";

    private static byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer))>0){
                out.write(buffer, 0 , bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    private static String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }


    private static ArrayList<Language> gerSuppotedLangs(String baseLang){

        ArrayList<Language> result = new ArrayList<Language>();

        try{
            String url = Uri.parse("https://translate.yandex.net/api/v1.5/tr.json/getLangs")
                    .buildUpon()
                    .appendQueryParameter("key", API_KEY)
                    .appendQueryParameter("ui", baseLang)
                    .build().toString();
            String jsonString = getUrlString(url);

            JSONObject jsonBody = new JSONObject(jsonString);

            JSONArray dirs = jsonBody.getJSONArray("dirs");
            JSONObject langs = jsonBody.getJSONObject("langs");

            for (int i=0; i < dirs.length(); i++){
                String item = dirs.getString(i);

                if(item.startsWith(baseLang)){
                    int ind = item.indexOf("-");
                    String code = item.substring(ind+1, item.length());
                    String name = langs.getString(code);
                    Language lang = new Language(code, name);
                    result.add(lang);
                }
            }

        } catch (IOException e) {

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String translate(String inputString, String lang){
        String result = "";

        try{
            String url = Uri.parse("https://translate.yandex.net/api/v1.5/tr.json/translate")
                    .buildUpon()
                    .appendQueryParameter("key", API_KEY)
                    .appendQueryParameter("text", inputString)
                    .appendQueryParameter("lang", lang)
                    .appendQueryParameter("format", "html")
                    .build().toString();
            String jsonString = getUrlString(url);

            JSONObject jsonBody = new JSONObject(jsonString);


            JSONArray text = jsonBody.getJSONArray("text");
            StringBuilder b = new StringBuilder();
            for (int i=0; i < text.length(); i++) {
                result = result + text.get(i) + "\n";
            }

        } catch (IOException e) {

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static class SuppotedLangs extends AsyncTask<Context,Void,ArrayList<Language>> {

        private OnCompletedListener listener;

        public interface OnCompletedListener{
            void onTaskCompleted(ArrayList<Language> result);
        }

        public void setCompleteListener(OnCompletedListener listener){
            this.listener = listener;
        }

        @Override
        protected ArrayList<Language> doInBackground(Context... con) {

            ArrayList<Language> result = gerSuppotedLangs(Locale.getDefault().getLanguage());

            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Language> langs) {
            super.onPostExecute(langs);
            if (listener != null)
                listener.onTaskCompleted(langs);
        }
    }

    public static class Trans extends AsyncTask<Context,Void,String> {

        private OnCompletedListener listener;
        private String inputString;
        private String lang;

        public void setInputString(String inputString) {
            this.inputString = inputString;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public interface OnCompletedListener{
            void onTaskCompleted(String result);
        }

        public void setCompleteListener(OnCompletedListener listener){
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Context... con) {

            String result = translate(inputString, lang);

            DbManager.getInstance(con[0]).addHistory(inputString, "", result, lang);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (listener != null)
                listener.onTaskCompleted(result);
        }
    }

}
