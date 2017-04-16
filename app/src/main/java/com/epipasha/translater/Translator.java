package com.epipasha.translater;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.epipasha.translater.db.DbManager;
import com.epipasha.translater.objects.Answer;
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
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

public class Translator {

    private static final String API_KEY = "trnsl.1.1.20170316T165017Z.6542920cb7c835ce.bd48c1d999a2f34330ff01fe6b07792cdc8da13d";
    private static final int TRANSLATE_DELAY = 500;

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

    private static Answer<ArrayList<Language>> gerSuppotedLangs(String baseLang){

        Answer answer = new Answer();

        ArrayList<Language> result = new ArrayList<Language>();

        try{
            String url = Uri.parse("https://translate.yandex.net/api/v1.5/tr.json/getLangs")
                    .buildUpon()
                    .appendQueryParameter("key", API_KEY)
                    .appendQueryParameter("ui", baseLang)
                    .build().toString();
            String jsonString = getUrlString(url);

            JSONObject jsonBody = new JSONObject(jsonString);

            JSONObject langs = jsonBody.getJSONObject("langs");

            Iterator<String> i = langs.keys();
            while(i.hasNext()){
                String key = i.next();
                Language lang = new Language(key, langs.getString(key));
                result.add(lang);
            };

            answer.setSucсess(true);
        } catch (IOException e) {
            answer.setErrMes(e.getMessage());
            answer.setSucсess(false);
        } catch (JSONException e) {
            answer.setErrMes(e.getMessage());
            answer.setSucсess(false);
        }

        Collections.sort(result);
        answer.setResult(result);
        return answer;
    }

    private static Answer<String> translate(String inputString, Language langIn, Language langOut){

        Answer answer = new Answer();
        String result = "";

        String lang = (langIn.isAutoLang()) ? langOut.getCode() : langIn.getCode() + "-" + langOut.getCode();

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
                result = result + (result.isEmpty() ? "":", ") + text.get(i);
            }

            answer.setSucсess(true);
        } catch (IOException e) {
            answer.setErrMes(e.getMessage());
            answer.setResult(false);
        } catch (JSONException e) {
            answer.setErrMes(e.getMessage());
            answer.setResult(false);
        }

        answer.setResult(result);
        return answer;
    }


    public static class SuppotedLangs extends AsyncTask<Context,Void,Answer<ArrayList<Language>>> {

        private OnCompletedListener listener;

        public interface OnCompletedListener{
            void onSupportedLangsTaskCompleted(Answer<ArrayList<Language>> result);
        }

        public void setCompleteListener(OnCompletedListener listener){
            this.listener = listener;
        }

        @Override
        protected Answer<ArrayList<Language>> doInBackground(Context... con) {

            Answer<ArrayList<Language>> result = gerSuppotedLangs(Locale.getDefault().getLanguage());

            return result;
        }

        @Override
        protected void onPostExecute(Answer<ArrayList<Language>> langs) {
            super.onPostExecute(langs);
            if (listener != null)
                listener.onSupportedLangsTaskCompleted(langs);
        }
    }

    public static class Trans extends AsyncTask<Context,Void,Answer<String>> {

        private OnCompletedListener listener;
        private String inputString;
        private Language langOut;
        private Language langIn;

        public void setInputString(String inputString) {
            this.inputString = inputString;
        }

        public void setLangOut(Language langOut) {
            this.langOut = langOut;
        }

        public void setLangIn(Language langIn) {
            this.langIn = langIn;
        }

        public interface OnCompletedListener{
            void onTranslateTaskCompleted(Answer<String> result);
        }

        public void setCompleteListener(OnCompletedListener listener){
            this.listener = listener;
        }

        @Override
        protected Answer<String> doInBackground(Context... con) {

            try {
                Thread.sleep(TRANSLATE_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isCancelled()){
                return new Answer<String>();
            }

            Answer<String> result = translate(inputString, langIn, langOut);

            if (result.isSucсess())
                DbManager.getInstance(con[0]).addHistory(inputString, langIn, result.getResult(), langOut);

            return result;
        }

        @Override
        protected void onPostExecute(Answer<String> result) {
            super.onPostExecute(result);
            if (listener != null)
                listener.onTranslateTaskCompleted(result);
        }
    }

}
