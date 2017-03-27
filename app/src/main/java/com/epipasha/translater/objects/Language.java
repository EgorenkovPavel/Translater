package com.epipasha.translater.objects;

import android.content.Context;
import com.epipasha.translater.R;

/**
 * Created by Pavel on 20.03.2017.
 */

public class Language {

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

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
