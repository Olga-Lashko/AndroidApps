/**
 * Module 11 Assignment Map Marker Fun
 *
 * Class: CITC 2376, Spring 2024
 *
 * @author  Olga Lashko
 * @version April 14, 2024
 */
package com.mapmarker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {

    EditText placeNameTextView;
    TextView coordinatesTextView;
    Button delButton;
    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        placeNameTextView = findViewById(R.id.placeNameTextView);
        coordinatesTextView = findViewById(R.id.coordinatesTextView);
        delButton = findViewById(R.id.delButton);

        // Initialize MapFragment and add it to the activity
        mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mapFrame, mapFragment).commit();

        // Set EditText field in MapFragment to update place names
        mapFragment.setPlaceNameTextView(placeNameTextView);

        // Set listener for coordinate updates in MapFragment
        mapFragment.setCoordinateUpdateListener(new MapFragment.OnCoordinateUpdateListener() {
            @Override
            public void onUpdateCoordinates(String coordinates) {
                coordinatesTextView.setText(coordinates);
            }
        });

        // Set onClickListener for delete button to delete selected marker
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.deleteSelectedMarker();
            }
        });
    }
}
