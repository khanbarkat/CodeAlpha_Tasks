package com.example.codealpha_tasks.FitnessTrackingApp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codealpha_tasks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Locale;

public class StepsFragment extends Fragment implements SensorEventListener{

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String email;
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private int stepCounts = 0;
    private boolean isPaused = false;
    private long timePaused = 0;
    private final float stepsLengthInMeters = 0.762f;
    private float distanceInKm = 0f;
    private float calories = 0f;
    private long startTime;
    private final int stepTarget = 100;
    ProgressBar progress;
    LinearLayout layout1, layout2, layout3, layoutbtnplay, layoutbtnreset;
    TextView stepstxt, distances, timers, colories, goaltxt;
    ImageView btnplay,btnrset;
    private final Handler handler = new Handler();
    private final Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timers.setText(String.format(Locale.getDefault(), "Time: %02d:%02d", minutes, seconds));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_steps, container, false);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startTime = System.currentTimeMillis();
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        layout1 = view.findViewById(R.id.linearLayout2);
        layout2 = view.findViewById(R.id.linearLayout3);
        layout3 = view.findViewById(R.id.linearLayout4);
        layoutbtnplay = view.findViewById(R.id.playButton);
        layoutbtnreset = view.findViewById(R.id.resetButton);
        stepstxt = view.findViewById(R.id.steps);
        distances = view.findViewById(R.id.distance);
        timers = view.findViewById(R.id.timer);
        colories = view.findViewById(R.id.calories);
        goaltxt = view.findViewById(R.id.goal);
        btnplay=view.findViewById(R.id.buttonImage);
        progress=view.findViewById(R.id.progressBar1);

        if (currentUser != null) {
            email = currentUser.getEmail();
            fetchDataAndInitialize();
        } else {
            Toast.makeText(getContext(), "No user found!", Toast.LENGTH_SHORT).show();
        }

        onResetButtonClick();

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }

        progress.setMax(stepTarget);
        goaltxt.setText("Steps Goal: " + stepTarget);

        if (stepDetectorSensor == null) {
            stepstxt.setText("Step detector not available!");
        }

        layoutbtnplay.setOnClickListener(this::onPauseButtonClick);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            // Increment step count for each step detected
            stepCounts++;
            distanceInKm = stepCounts * stepsLengthInMeters / 1000;
            calories = stepCounts * 0.04f;
            stepstxt.setText(String.format(Locale.getDefault(), "Step Count: %d", stepCounts));
            progress.setProgress(stepCounts);

            if (stepCounts >= stepTarget) {
                stepstxt.setText("Goal Achieved!");
            }

            distances.setText(String.format(Locale.getDefault(), "Distance: %.2f km", distanceInKm));
            colories.setText(String.format(Locale.getDefault(), "Calories: %.2f kcal", calories));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sensorManager != null) {
            // Unregister the listener to save resources
            sensorManager.unregisterListener(this);
        }
        // Stop the handler for timing
        handler.removeCallbacks(timeRunnable);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null && stepDetectorSensor != null) {
            // Register the listener for the step detector sensor
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        // Start the time handler
        handler.postDelayed(timeRunnable, 0);
    }

    public void onPauseButtonClick(View view) {
        if (isPaused) {
            isPaused = false;
            btnplay.setImageResource(R.drawable.img_9);
            btnplay.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));

            startTime = System.currentTimeMillis() - timePaused;
            handler.postDelayed(timeRunnable, 0);
        } else {
            isPaused = true;
            btnplay.setImageResource(R.drawable.img_13);
            btnplay.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));

            addToFireStore(stepCounts, distanceInKm, calories);
            handler.removeCallbacks(timeRunnable);
            timePaused = System.currentTimeMillis() - startTime;
        }
    }

    public void onResetButtonClick() {
        layoutbtnreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset internal step count
                stepCounts = 0;
                progress.setProgress(0);

                stepstxt.setText(String.format(Locale.getDefault(), "Step Count: %d", stepCounts));

                distanceInKm = stepCounts * stepsLengthInMeters / 1000;
                calories = stepCounts * 0.04f;
                distances.setText(String.format(Locale.getDefault(), "Distance: %.2f km", distanceInKm));

                addToFireStore(stepCounts, distanceInKm, calories);
            }
        });
    }

    private void addToFireStore(int steps, float distance, float calories) {
        String formattedDistance = String.format(Locale.getDefault(), "%.2f", distance);
        String formattedCalories = String.format(Locale.getDefault(), "%.2f", calories);

        HashMap<String, Object> data = new HashMap<>();
        data.put("steps", steps);
        data.put("distance", formattedDistance);
        data.put("calories", formattedCalories);

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();

                                db.collection("users")
                                        .document(documentId)
                                        .update(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(), "Data Updated!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "failed to update!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void fetchDataAndInitialize() {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int getSteps = Math.toIntExact(document.getLong("steps"));
                                String fetchCalories = document.getString("calories");
                                String fetchDistance = document.getString("distance");

                                float getDistance = Float.parseFloat(fetchDistance);
                                float getCalories = Float.parseFloat(fetchCalories);

                                // Update UI and internal variables
                                stepCounts = getSteps;
                                distanceInKm = getDistance;
                                calories = getCalories;
                                stepstxt.setText(String.format(Locale.getDefault(), "Step Count: %d", stepCounts));
                                distances.setText(String.format(Locale.getDefault(), "Distance: %.2f km", distanceInKm));
                                colories.setText(String.format(Locale.getDefault(), "Calories: %.2f kcal", calories));
                                progress.setProgress(stepCounts);
                            }
                        }
                    }
                });
    }
}
