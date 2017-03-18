package com.epipasha.translater.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.epipasha.translater.R;
import com.epipasha.translater.Translater;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TranslateFragment extends Fragment
        implements Translater.SuppotedLangs.OnCompletedListener, Translater.Trans.OnCompletedListener{

    EditText textIn;
    TextView textOut;
    Spinner spLang;
    Button btnGo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_translate, container, false);

        textIn = (EditText) v.findViewById(R.id.textIn);
        textOut = (TextView) v.findViewById(R.id.textOut);
        spLang = (Spinner) v.findViewById(R.id.spLang);
        btnGo = (Button)v.findViewById(R.id.btnGo);

        spLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                translate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate();
            }
        });

        Translater.SuppotedLangs task = new Translater.SuppotedLangs();
        task.setCompleteListener(this);
        task.execute(getActivity());

        return v;
    }

    private void translate() {
        Translater.Trans task = new Translater.Trans();
        task.setLang((String) ((Map.Entry)spLang.getSelectedItem()).getKey());
        task.setInputString(textIn.getText().toString());
        task.setCompleteListener(this);
        task.execute(getActivity());
    }

    @Override
    public void onTaskCompleted(Map<String, String> result) {
        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, result.entrySet().toArray());
        spLang.setAdapter(adapter);
    }

    @Override
    public void onTaskCompleted(String result) {
        //Spanned s = Html.fromHtml(result);
        textOut.setText(result);
    }

    private class CustomAdaper extends ArrayAdapter<Map.Entry>{

        private Map.Entry[] objects;

        public CustomAdaper(@NonNull Context context, @LayoutRes int resource, @NonNull Map.Entry[] objects) {
            super(context, resource, objects);
            this.objects = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TextView t = (TextView) convertView.findViewById(android.R.id.text1);
            t.setText(objects[position].getValue().toString());
            return convertView;
            //return super.getView(position, convertView, parent);
        }
    }
}
