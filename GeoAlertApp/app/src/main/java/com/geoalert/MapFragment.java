package com.geoalert;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Circle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap mMap;
    private Marker targetMarker;
    private FusedLocationProviderClient fusedLocationClient;
    private MainActivity mainActivity;
    private Marker userMarker;
    private Button saveButton;
    private Circle circle;
    private int alertDistanceThreshold = 0; // Initialize the threshold distance


    // Target location coordinates
    public LatLng targetLocation = new LatLng(36.38, -86.46);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFrame);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.mapFrame, mapFragment)
                    .commit();
        }
        mapFragment.getMapAsync(this); // This line is necessary to trigger onMapReady
        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add target marker
        targetMarker = mMap.addMarker(new MarkerOptions().position(targetLocation).title("Target"));

        // Check location permission
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, update location UI and start location updates
            updateLocationUI();
            startLocationUpdates();
        }

        // Add user marker
        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                userMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("User"));

                // Calculate the bounds to include both markers
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(targetMarker.getPosition());
                builder.include(userMarker.getPosition());
                LatLngBounds bounds = builder.build();

                // Set camera position to show both markers with padding
                int padding = 100; // Padding in pixels
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cameraUpdate);

                // Get the radius from alertNumEditText and convert to meters
                int radiusInMeters = mainActivity.getAlertDistance();

            }
        });
        // Get the radius from alertNumEditText and convert to meters
        alertDistanceThreshold = mainActivity.getAlertDistance();
    }


    public void drawCircle(LatLng center, int radius) {
        if (mMap != null) {
            // Remove the previous circle if it exists
            if (circle != null) {
                circle.remove();
            }

            // Create a new circle and add it to the map
            CircleOptions circleOptions = new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeWidth(2)
                    .strokeColor(Color.BLACK)
                    .fillColor(0x30ff0000);
            circle = mMap.addCircle(circleOptions);
        }
    }



    // Update location UI settings
    private void updateLocationUI() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // Start location updates
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, handle it here
            return;
        }
        fusedLocationClient.requestLocationUpdates(getLocationRequest(), locationCallback, null);
    }


    // Define location request parameters
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000); // Update location every 5 seconds
        return locationRequest;
    }

    // Location callback to handle location updates
    // Location callback to handle location updates
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    Log.d("LocationUpdate", "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());

                    // Update user's location marker
                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    updateLocationMarker(userLatLng);

                    // Calculate distance between user's location and target location
                    float[] distanceResults = new float[1];
                    Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                            targetLocation.latitude, targetLocation.longitude, distanceResults);
                    float distanceInMeters = distanceResults[0];

                    // Convert distance to meters
                    mainActivity.updateDistance((int) distanceInMeters);

                    // Check if the distance is less than or equal to the threshold
                    if (distanceInMeters <= alertDistanceThreshold) {
                        // Show toast message
                        Toast.makeText(getActivity(), "User is within the set alarm distance.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    // Update user's location marker on the map
    private void updateLocationMarker(LatLng latLng) {
        if (userMarker != null) {
            userMarker.setPosition(latLng);
        } else {
            userMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("User"));
        }
    }


    // Handle location permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, update location UI and start location updates
                updateLocationUI();
                startLocationUpdates();
            } else {
                // Permission denied
                Toast.makeText(getActivity(), "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
