package com.bongo.roaim.locationpractice;

import android.os.AsyncTask;

/**
 * Created by Roaim on 03-Nov-17.
 */

public abstract class Awaiter extends AsyncTask {
//    protected abstract boolean getCondition();
    protected abstract long getWaitInterval();
    protected abstract void onFinished();

    public void start() {
        execute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
//        do {
            try {
                Thread.sleep(getWaitInterval());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//        } while (getCondition());
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        onFinished();
    }
}
