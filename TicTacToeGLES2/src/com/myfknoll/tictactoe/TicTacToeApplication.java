package com.myfknoll.tictactoe;


import android.app.Application;
import android.util.Log;

/**
 * TicTacToe Applikation
 *
 * @author Florian Knoll -  myfknoll(at)gmail.com
 *
 * @version 1.0.0
 */
public class TicTacToeApplication extends Application {
    private static String LOG_TAG = "TicTacToeApplication";

    /**
     * This method is called when the top level application is created.
     */
    @Override
    public void onCreate() {
        Log.v(LOG_TAG, "onCreate ()");

        super.onCreate();
    }
}
