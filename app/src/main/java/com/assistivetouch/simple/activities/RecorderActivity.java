package com.assistivetouch.simple.activities;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.assistivetouch.simple.R;
import com.assistivetouch.simple.database.GestureDatabase;
import com.assistivetouch.simple.databinding.ActivityRecorderBinding;
import com.assistivetouch.simple.models.Gesture;
import com.assistivetouch.simple.models.GestureEvent;
import com.assistivetouch.simple.views.RecordingView;
import java.util.List;

public class RecorderActivity extends AppCompatActivity {
    private ActivityRecorderBinding binding;
    private GestureDatabase database;
    private boolean isRecording = false;
    private List<GestureEvent> recordedEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecorderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = new GestureDatabase(this);

        setupRecordingView();
        setupButtons();
        updateOrientationIndicator();
    }

    private void setupRecordingView() {
        binding.recordingView.setOnGestureRecordedListener(events -> {
            recordedEvents = events;
            runOnUiThread(() -> {
                binding.btnSave.setEnabled(true);
                binding.tvStatus.setText("Recorded " + events.size() + " points");
            });
        });
    }

    private void setupButtons() {
        binding.btnStartStop.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });

        binding.btnSave.setOnClickListener(v -> {
            showSaveDialog();
        });

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void startRecording() {
        isRecording = true;
        binding.btnStartStop.setText(R.string.stop_recording);
        binding.tvStatus.setText("Recording...");
        binding.tvStatus.setTextColor(getColor(R.color.recording_red));
        binding.recordingView.startRecording();
        binding.btnSave.setEnabled(false);
    }

    private void stopRecording() {
        isRecording = false;
        binding.btnStartStop.setText(R.string.start_recording);
        binding.tvStatus.setTextColor(getColor(R.color.white));
        binding.recordingView.stopRecording();
    }

    private void updateOrientationIndicator() {
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        binding.tvOrientation.setText(isLandscape ? "Landscape Mode" : "Portrait Mode");
    }

    private void showSaveDialog() {
        if (recordedEvents == null || recordedEvents.isEmpty()) return;

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(R.string.gesture_name);

        new AlertDialog.Builder(this)
                .setTitle(R.string.save_gesture)
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        saveGesture(name);
                    } else {
                        Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveGesture(String name) {
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        long duration = recordedEvents.get(recordedEvents.size() - 1).getTimestamp();

        Gesture gesture = Gesture.fromEvents(
                name,
                recordedEvents,
                duration,
                isLandscape ? 1 : 0,
                getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels
        );

        database.saveGesture(gesture);
        Toast.makeText(this, "Gesture saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) {
            binding.recordingView.stopRecording();
        }
    }
}
