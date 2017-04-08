package com.epipasha.translater.objects;

import android.content.Context;
import android.support.annotation.NonNull;

import com.epipasha.translater.R;

public class Language implements Comparable<Language>{

    private String code;
    private String name;

    private static Language autoLang = null;

    public Language(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Language getAutoLang(Context context){
        if (autoLang ==null){
            autoLang = new Language("auto", context.getResources().getString(R.string.auto_lang));
        }
        return autoLang;
    }

    public boolean isAutoLang(){
        return this.equals(autoLang);
    }

    @Override
    public int compareTo(Language o) {
        return this.name.compareTo(o.name);
    }
}
