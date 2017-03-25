package com.epipasha.translater.objects;

/**
 * Created by Pavel on 20.03.2017.
 */

public class Language {

    private String code;
    private String name;

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
}
