package com.epipasha.translater.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.epipasha.translater.R;
import com.epipasha.translater.Translator;
import com.epipasha.translater.db.DbManager;
import com.epipasha.translater.objects.Language;

import java.util.ArrayList;

public class TranslateFragment extends Fragment
        implements Translator.SuppotedLangs.OnCompletedListener, Translator.Trans.OnCompletedListener{

    private static final String KEY_OUTPUT_TEXT = "outputText";

    EditText textIn;
    TextView textOut;
    Spinner spLangIn, spLangOut;
    ImageButton btnStar, btnCross;

    private ArrayList<Language> supportedLangs;
    private String outputText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_translate, container, false);

        textIn = (EditText) v.findViewById(R.id.textIn);
        textOut = (TextView) v.findViewById(R.id.textOut);
        spLangIn = (Spinner) v.findViewById(R.id.spLangIn);
        spLangOut = (Spinner) v.findViewById(R.id.spLangOut);
        btnStar = (ImageButton)v.findViewById(R.id.btnStar);
        btnCross = (ImageButton)v.findViewById(R.id.btnClear);

        if (outputText!=null){
            textOut.setText(outputText);
        }

        textIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                btnStar.setImageDrawable(ResourcesCompat.getDrawable(getResources(), android.R.drawable.star_big_off, null));
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
                btnStar.setImageDrawable(ResourcesCompat.getDrawable(getResources(), android.R.drawable.star_big_on, null));
                DbManager.getInstance(getActivity()).addFavorites(
                        textIn.getText().toString(),
                        (String) ((Language) spLangIn.getSelectedItem()).getCode(),
                        textOut.getText().toString(),
                        (String) ((Language) spLangOut.getSelectedItem()).getCode());
            }
        });

        btnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textIn.setText("");
            }
        });

        if (supportedLangs == null){
            Translator.SuppotedLangs task = new Translator.SuppotedLangs();
            task.setCompleteListener(this);
            task.execute(getActivity());
        }else{
            setAdapter();
        }

        return v;
    }

    private void translate() {
        if (!isResumed()){
            return;
        }

        String inputText = textIn.getText().toString();
        if (inputText.isEmpty()) {
            textOut.setText("");
        }else{
            Translator.Trans task = new Translator.Trans();
            task.setLangOut((Language) spLangOut.getSelectedItem());
            task.setLangIn((Language) spLangIn.getSelectedItem());
            task.setInputString(inputText);
            task.setCompleteListener(this);
            task.execute(getActivity());
        }
    }

    @Override
    public void onTaskCompleted(ArrayList<Language> result) {
        supportedLangs = result;
        setAdapter();
    }

    private void setAdapter() {
        ArrayList<Language> l = (ArrayList<Language>) supportedLangs.clone();
        l.add(0, Language.getAutoLang(getActivity()));

        ArrayAdapter<String> adapterIn = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, l.toArray());
        adapterIn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLangIn.setAdapter(adapterIn);

        ArrayAdapter<String> adapterOut = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, supportedLangs.toArray());
        adapterOut.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLangOut.setAdapter(adapterOut);
    }

    @Override
    public void onTaskCompleted(String result) {
        outputText = result;
        textOut.setText(result);
    }

}
