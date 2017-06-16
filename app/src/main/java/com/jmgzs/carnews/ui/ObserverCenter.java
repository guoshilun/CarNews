package com.jmgzs.carnews.ui;

import android.os.Bundle;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by mac on 17/6/16.
 * Description:
 */

public class ObserverCenter extends Observable {

    private volatile static ObserverCenter observer;

    private ObserverCenter() {
    }

    public static ObserverCenter getObserver() {
        if (observer == null) {
            synchronized (ObserverCenter.class) {
                if (observer == null) {
                    observer = new ObserverCenter();
                }
            }
        }
        return observer;
    }


    public void doNotify(Bundle b) {

        setChanged();
        notifyObservers(b);
    }

    public void addOb(Observer ob) {
        addObserver(ob);
    }

    public void removeOb(Observer ob) {
        deleteObserver(ob);
    }

    public void removeAll() {
        deleteObservers();
    }
}
