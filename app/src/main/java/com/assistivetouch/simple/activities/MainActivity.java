package com.assistivetouch.simple.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.assistivetouch.simple.R;
import com.assistivetouch.simple.databinding.ActivityMainBinding;
import com.assistivetouch.simple.services.FloatingButtonService;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private boolean isFloatingButtonEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupClickListeners();
        updateAccessibilityStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAccessibilityStatus();
    }

    private void setupClickListeners() {
        binding.btnRecordGesture.setOnClickListener(v -> {
            startActivity(new Intent(this, RecorderActivity.class));
        });

        binding.btnSavedGestures.setOnClickListener(v -> {
            startActivity(new Intent(this, GestureListActivity.class));
        });

        binding.btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        binding.btnFloatingButton.setOnClickListener(v -> {
            toggleFloatingButton();
        });
    }

    private void toggleFloatingButton() {
        if (isFloatingButtonEnabled) {
            stopService(new Intent(this, FloatingButtonService.class));
            isFloatingButtonEnabled = false;
            binding.btnFloatingButton.setText(R.string.floating_button);
        } else {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Please grant overlay permission", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                return;
            }
            startService(new Intent(this, FloatingButtonService.class));
            isFloatingButtonEnabled = true;
            binding.btnFloatingButton.setText("Disable Floating Button");
        }
    }

    private void updateAccessibilityStatus() {
        String serviceName = "com.assistivetouch.simple/com.assistivetouch.simple.services.GestureReplayService";
        String enabledServices = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        boolean isEnabled = enabledServices != null && enabledServices.contains(serviceName);

        binding.tvAccessibilityStatus.setText("Accessibility: " + (isEnabled ? "Enabled" : "Disabled"));
    }
}
