package com.assistivetouch.simple.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;
import com.assistivetouch.simple.R;
import com.assistivetouch.simple.database.GestureDatabase;
import com.assistivetouch.simple.databinding.ActivitySettingsBinding;
import com.assistivetouch.simple.models.CustomAction;
import com.assistivetouch.simple.models.Gesture;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private GestureDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = new GestureDatabase(this);

        setupClickListeners();
        updateStatus();
        setupReplaySpeed();
    }

    private void setupClickListeners() {
        binding.btnEnableAccessibility.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        });

        binding.btnEnableOverlay.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
        });

        binding.btnSingleTap.setOnClickListener(v -> showActionSelection(CustomAction.SINGLE_TAP));
        binding.btnDoubleTap.setOnClickListener(v -> showActionSelection(CustomAction.DOUBLE_TAP));
        binding.btnLongPress.setOnClickListener(v -> showActionSelection(CustomAction.LONG_PRESS));

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupReplaySpeed() {
        binding.seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float speed = progress / 100f;
                binding.tvSpeedValue.setText(String.format("%.1fx", speed));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void showActionSelection(String actionType) {
        List<Gesture> gestures = database.getAllGestures();
        String[] gestureNames = new String[gestures.size() + 1];
        gestureNames[0] = "None";
        for (int i = 0; i < gestures.size(); i++) {
            gestureNames[i + 1] = gestures.get(i).getName();
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Select " + actionType.replace("_", " "))
                .setItems(gestureNames, (dialog, which) -> {
                    CustomAction action = new CustomAction();
                    action.setActionType(actionType);
                    if (which == 0) {
                        action.setGestureId(null);
                        action.setActionName("None");
                    } else {
                        action.setGestureId(gestures.get(which - 1).getId());
                        action.setActionName(gestures.get(which - 1).getName());
                    }
                    database.saveCustomAction(action);
                    updateStatus();
                })
                .show();
    }

    private void updateStatus() {
        String serviceName = "com.assistivetouch.simple/com.assistivetouch.simple.services.GestureReplayService";
        String enabledServices = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        boolean accessibilityEnabled = enabledServices != null && enabledServices.contains(serviceName);

        binding.tvAccessibilityStatus.setText(accessibilityEnabled ? "Enabled" : "Disabled");
        binding.tvAccessibilityStatus.setTextColor(getColor(accessibilityEnabled ? android.R.color.holo_green_dark : android.R.color.holo_red_dark));

        boolean overlayEnabled = Settings.canDrawOverlays(this);
        binding.tvOverlayStatus.setText(overlayEnabled ? "Enabled" : "Disabled");
        binding.tvOverlayStatus.setTextColor(getColor(overlayEnabled ? android.R.color.holo_green_dark : android.R.color.holo_red_dark));

        CustomAction singleTap = database.getCustomAction(CustomAction.SINGLE_TAP);
        binding.tvSingleTapValue.setText(singleTap != null ? singleTap.getActionName() : "Open Menu");

        CustomAction doubleTap = database.getCustomAction(CustomAction.DOUBLE_TAP);
        binding.tvDoubleTapValue.setText(doubleTap != null ? doubleTap.getActionName() : "None");

        CustomAction longPress = database.getCustomAction(CustomAction.LONG_PRESS);
        binding.tvLongPressValue.setText(longPress != null ? longPress.getActionName() : "None");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }
}
