package com.example.paysheetemployee;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.paysheetemployee.databinding.ActivityMapsBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private TextView hour, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            binding = ActivityMapsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            hour = (TextView) findViewById(R.id.textViewHoraMarcar);
            date = (TextView) findViewById(R.id.textViewFechaMarcar);
            hour.setText(getHour());
            date.setText(getDate());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    //    obtener hora
    public String getHour() {
        String horaMarcada = "";
        try {
            Date date = new Date();
            DateFormat hourFormat = new SimpleDateFormat("HH:mm");
            System.out.println("Hora: " + hourFormat.format(date));
            horaMarcada = hourFormat.format(date);
        } catch (Exception e){
            e.printStackTrace();
        }
        return horaMarcada;
    }

    //    obtener fecha
    public String getDate() {
        String fechaMarcada = "";
        try {
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            System.out.println("Fecha: " + dateFormat.format(date));
            fechaMarcada = dateFormat.format(date);
        } catch (Exception e){
            e.printStackTrace();
        }
        return fechaMarcada;
    }

    public void throwMarkActivity(View view){
        try {
            Intent intent = new Intent(this, MarkActivity.class);
            startActivity(intent);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}