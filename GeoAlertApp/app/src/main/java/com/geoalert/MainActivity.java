package com.geoalert;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public EditText alertNumEditText;
    public TextView distanceNumTextView;
    private Button saveButton;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alertNumEditText = findViewById(R.id.alertNumEditText);
        distanceNumTextView = findViewById(R.id.distanceNumTextView);
        saveButton = findViewById(R.id.saveButton);

        // Initialize the map fragment
        mapFragment = new MapFragment(); // Instantiate MapFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapFrame, mapFragment)
                .commit();


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int alertDistance = getAlertDistance();
                mapFragment.drawCircle(mapFragment.targetLocation, getAlertDistance());

                int distance = Integer.parseInt(distanceNumTextView.getText().toString());
                if (alertDistance >= distance) {
                    // Show toast message
                    Toast.makeText(MainActivity.this, "User reached area for the set alarm.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updateDistance(int distance) {
        distanceNumTextView.setText(String.valueOf(distance));
    }

    public int getAlertDistance() {
        String alertDistanceStr = alertNumEditText.getText().toString();
        if (!alertDistanceStr.isEmpty()) {
            return Integer.parseInt(alertDistanceStr);
        }
        return 0;
    }
}
