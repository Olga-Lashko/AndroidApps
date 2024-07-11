package com.mapmarker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker selectedMarker;
    private OnCoordinateUpdateListener coordinateUpdateListener;
    private EditText placeNameTextView;

    public interface OnCoordinateUpdateListener {
        void onUpdateCoordinates(String coordinates);
    }

    public void setCoordinateUpdateListener(OnCoordinateUpdateListener listener) {
        this.coordinateUpdateListener = listener;
    }

    public void setPlaceNameTextView(EditText placeNameTextView) {
        this.placeNameTextView = placeNameTextView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFrame);
        supportMapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                createMarker(latLng);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                placeNameTextView.setText(marker.getTitle());
                selectedMarker = marker;
                return false;
            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void createMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        Marker marker = mMap.addMarker(markerOptions);

        // Round latitude and longitude to whole numbers
        int roundedLat = (int) latLng.latitude;
        int roundedLng = (int) latLng.longitude;

        if (coordinateUpdateListener != null) {
            String coordinates = "Lng: " + roundedLng + ", Lat: " + roundedLat;
            coordinateUpdateListener.onUpdateCoordinates(coordinates);
        }

        if (placeNameTextView != null) {
            String markerTitle = placeNameTextView.getText().toString();
            marker.setTitle(markerTitle);
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
    }

    public void deleteSelectedMarker() {
        if (selectedMarker != null) {
            selectedMarker.remove();
            selectedMarker = null;
            placeNameTextView.setText(""); // Clear placeNameTextView after deleting marker
        }
    }
}
