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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.epipasha.translater.R;
import com.epipasha.translater.Translater;
import com.epipasha.translater.db.DbManager;
import com.epipasha.translater.objects.Language;

import java.util.ArrayList;
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
    ImageButton btnStar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_translate, container, false);

        textIn = (EditText) v.findViewById(R.id.textIn);
        textOut = (TextView) v.findViewById(R.id.textOut);
        spLang = (Spinner) v.findViewById(R.id.spLang);
        btnStar = (ImageButton)v.findViewById(R.id.btnStar);

        textIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                translate();
            }
        });

         btnStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbManager.getInstance(getActivity()).addFavorites(textIn.getText().toString(), "", textOut.getText().toString(), (String) ((Language)spLang.getSelectedItem()).getCode());
            }
        });

        Translater.SuppotedLangs task = new Translater.SuppotedLangs();
        task.setCompleteListener(this);
        task.execute(getActivity());

        return v;
    }

    private void translate() {
        Translater.Trans task = new Translater.Trans();
        task.setLang((String) ((Language)spLang.getSelectedItem()).getCode());
        task.setInputString(textIn.getText().toString());
        task.setCompleteListener(this);
        task.execute(getActivity());
    }

    @Override
    public void onTaskCompleted(ArrayList<Language> result) {
        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, result.toArray());
        spLang.setAdapter(adapter);
    }

    @Override
    public void onTaskCompleted(String result) {
        //Spanned s = Html.fromHtml(result);
        textOut.setText(result);
    }

}
