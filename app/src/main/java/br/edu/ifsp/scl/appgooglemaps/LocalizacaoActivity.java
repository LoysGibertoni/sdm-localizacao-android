package br.edu.ifsp.scl.appgooglemaps;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class LocalizacaoActivity extends AppCompatActivity implements OnMapReadyCallback, OnMyLocationClickListener, OnMyLocationButtonClickListener {

    private static final int PERMISSOES_REQUEST = 1;
    private static final int AUTOCOMPLETE_REQUEST = 2;

    private static final float RAIO_PADRAO_GEOFENCE = 50f;

    private ImageView homeLocalizacao;

    private GoogleMap mMap;
    private Marker mMarker;

    private GeofencingClient mGeofencingClient;

    private String[] permissoesApp = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacao);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // Um GoogleMap deve ser obtido usando getMapAsync (OnMapReadyCallback).
        // Esta classe inicializa automaticamente o sistema de mapas.
        // E chama o método onMapReady quando a instância do GoogleMap estiver pronta para ser usada.
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        Permissao.validaPermissoes(PERMISSOES_REQUEST, this, permissoesApp);

        mGeofencingClient = LocationServices.getGeofencingClient(this);
        criarGeofences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.busca, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.buscar) {
            // Campos que serão retornados pelo autocomplete
            final List<Place.Field> campos = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.RATING);

            // Cria e lança o intent de autocomplete
            final Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, campos)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Localização solicitada!!!", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Sua localização atual é: \n" + "Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    /*
     * Manipula o mapa uma vez que esteja disponível.
     *   Este retorno da chamada é acionado quando o mapa estiver pronto para ser usado.
     *   É aqui que podemos adicionar marcadores ou linhas, adicionar ouvintes ou mover a câmera. Nesse caso,
     *   apenas foi adicionado alguns marcadores onde constam opções de alimentação na cidade de São Carlos.
     *
     *   Se o Google Play Services não estiver instalado no dispositivo, o usuário será solicitado a instalar
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Cria as localizações que serão marcadas no mapa
        LatLng restaurante = new LatLng(-22.0119929, -47.8948402);        // Restaurante Mosaico
        LatLng restaurante2 = new LatLng(-22.008513, -47.8907951);       // Restaurante Chez Marcel
        LatLng restaurante3 = new LatLng(-22.0064587, -47.89076906);    // Restaurante Kallas
        LatLng restaurante4 = new LatLng(-22.0130382, -47.8856678);    // Restaurante YaSan
        LatLng restaurante5 = new LatLng(-22.0207571, -47.8945995);   // Pizzaria Bella Capri

        // Adiciona os marcadores
        mMap.addMarker(new MarkerOptions().position(restaurante).title("Mosaico Bar e Restaurante").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_restaurante)));
        mMap.addMarker(new MarkerOptions().position(restaurante2).title("Restaurante Chez Marcel").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_restaurante)));
        mMap.addMarker(new MarkerOptions().position(restaurante3).title("Restaurante Kallas").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_restaurante)));
        mMap.addMarker(new MarkerOptions().position(restaurante4).title("Restaurante Ya San").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_restaurante)));
        mMap.addMarker(new MarkerOptions().position(restaurante5).title("Pizzaria Bella Capri").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_restaurante)));

        float zoomLevel = 15.0f;

        //movimenta a câmera até o ponto marcado  (Restaurante YaSan)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurante4, zoomLevel));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGeofencingClient.removeGeofences(Collections.singletonList("Posto pantanal"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Se o autocomplete trouxe um resultado
        if (requestCode == AUTOCOMPLETE_REQUEST && resultCode == RESULT_OK) {
            // Recupera o local do intent
            final Place place = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));

            // Remove o Marker da busca anterior
            if (mMarker != null) {
                mMarker.remove();
            }
            // Adiciona o novo Marker ao mapa
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(Objects.requireNonNull(place.getLatLng()))
                    .title(place.getName())
                    .snippet(getString(R.string.avaliacao, place.getRating())));

            // Centraliza o mapa no Marker
            mMap.animateCamera(CameraUpdateFactory.newLatLng(mMarker.getPosition()));
        }
    }

    private void criarGeofences() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final Geofence geofence = new Geofence.Builder()
                // Define o ID do request do geofence
                .setRequestId("Posto pantanal")
                // Define a posição (lat, lng) e raio do geofence
                .setCircularRegion(-21.9959073, -47.8905199, RAIO_PADRAO_GEOFENCE)
                // Define os tipos de transição que serão monitorados
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                // Define o tempo de expiração
                .setExpirationDuration(3600000)
                .build();

        final GeofencingRequest request = new GeofencingRequest.Builder()
                // Define que evento será emitido caso o usuário já esteja dentro do geofence ao inciar o monitoramento
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                // Adiciona o geofence criado
                .addGeofence(geofence)
                .build();

        // Cria um PendingIntent que iniciará um serviço ao ser lançado
        final PendingIntent intent = PendingIntent.getService(this, 0,
                new Intent(this, GeofenceService.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mGeofencingClient.addGeofences(request, intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSOES_REQUEST) {
            for (final int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }

            criarGeofences();
        }
    }
}
