package com.developpement.ogawi.keeptherythm;

public class StringModified {


    public Boolean initialised = false;
    public String valeur="";
    private onValueChangeListener valueChangeListener;

    public Boolean isInitialised() {
        return initialised;
    }

    public void setVariable(String value) {
        initialised = true;
        valeur=value;

        if (valueChangeListener != null) valueChangeListener.onChange();
    }

    public String getValeur(){
        return  valeur;
    }

    public onValueChangeListener getValueChangeListener() {
        return valueChangeListener;
    }

    public void setValueChangeListener(onValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }

    public interface onValueChangeListener {
        void onChange();
    }

}