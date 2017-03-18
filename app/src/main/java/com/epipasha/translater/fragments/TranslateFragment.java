package com.epipasha.translater.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.epipasha.translater.R;
import com.epipasha.translater.Translater;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TranslateFragment extends Fragment implements Translater.SuppotedLangs.OnCompletedListener{

    Spinner spLang;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_translate, container, false);

        EditText textIn = (EditText) v.findViewById(R.id.textIn);
        TextView textOut = (TextView) v.findViewById(R.id.textOut);
        spLang = (Spinner) v.findViewById(R.id.spLang);

        Translater.SuppotedLangs task = new Translater.SuppotedLangs();
        task.setCompleteListener(this);
        task.execute(getActivity());

        return v;
    }

    @Override
    public void onTaskCompleted(Map<String, String> result) {
        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, result.values().toArray());
        spLang.setAdapter(adapter);
    }
}
