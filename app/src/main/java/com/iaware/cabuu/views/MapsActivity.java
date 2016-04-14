package com.iaware.cabuu.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.iaware.cabuu.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;

    Button confirmarEndereco;
    ImageView location;

    int visao_mapa = GoogleMap.MAP_TYPE_NORMAL;
    Double latitude = null;
    Double longitude = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        Double latitudeAux = intent.getDoubleExtra("latitude", 0);
        Double longitudeAux = intent.getDoubleExtra("longitude", 0);
        if (latitudeAux != 0.0 && longitudeAux != 0.0) {
            latitude = latitudeAux;
            longitude = longitudeAux;
        }else{
            //Localização do Piauí
            latitude = -7.6974561;
            longitude = -42.5753236;
        }


        confirmarEndereco = (Button) findViewById(R.id.confirmarEndereco);
        confirmarEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LatLng latLng = mMap.getCameraPosition().target;

                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putDouble("latitude", latLng.latitude);
                b.putDouble("longitude", latLng.longitude);
                intent.putExtras(b);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);

        if(latitude == -7.6974561 && longitude == -42.5753236){
            LatLng minhaPosicao = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(minhaPosicao, 8));
        }else{
            LatLng minhaPosicao = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(minhaPosicao, 17));
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_maps, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_visao_mapa:
                if(visao_mapa == GoogleMap.MAP_TYPE_NORMAL){
                    visao_mapa = GoogleMap.MAP_TYPE_SATELLITE;
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }else{
                    visao_mapa = GoogleMap.MAP_TYPE_NORMAL;
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
