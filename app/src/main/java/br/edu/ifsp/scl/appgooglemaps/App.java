package br.edu.ifsp.scl.appgooglemaps;

import android.app.Application;

import com.google.android.libraries.places.api.Places;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializa o Places
        Places.initialize(this, getString(R.string.google_maps_key));
    }
}
