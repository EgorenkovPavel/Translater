package com.epipasha.translater.objects;

/**
 * Created by Pavel on 16.04.2017.
 */

public class Answer<T> {

    private boolean sucсess;
    private T result;
    private String errMes;

    public boolean isSucсess() {
        return sucсess;
    }

    public void setSucсess(boolean sucсess) {
        this.sucсess = sucсess;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getErrMes() {
        return errMes;
    }

    public void setErrMes(String errMes) {
        this.errMes = errMes;
    }
}
