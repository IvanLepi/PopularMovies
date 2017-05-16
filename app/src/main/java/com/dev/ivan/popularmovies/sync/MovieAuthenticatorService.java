package com.dev.ivan.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * @author Ivan Lepojevic
 *
 * The service which allows the sync adapter framework to access the authenticator.
 */

public class MovieAuthenticatorService extends Service {
    // Instance field that stores authenticator object.
    private MovieAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create new authenticator object
        mAuthenticator = new MovieAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
    */

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
