package com.assistivetouch.simple.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.assistivetouch.simple.R;
import com.assistivetouch.simple.adapters.GestureAdapter;
import com.assistivetouch.simple.database.GestureDatabase;
import com.assistivetouch.simple.databinding.ActivityGestureListBinding;
import com.assistivetouch.simple.models.Gesture;
import com.assistivetouch.simple.services.GestureReplayService;
import java.util.List;

public class GestureListActivity extends AppCompatActivity {
    private ActivityGestureListBinding binding;
    private GestureDatabase database;
    private GestureAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGestureListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = new GestureDatabase(this);
        setupRecyclerView();
        loadGestures();

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new GestureAdapter(
                this::replayGesture,
                this::showRenameDialog,
                this::showDeleteDialog
        );
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadGestures() {
        List<Gesture> gestures = database.getAllGestures();
        adapter.setGestures(gestures);
        binding.tvEmpty.setVisibility(gestures.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void replayGesture(Gesture gesture) {
        GestureReplayService service = GestureReplayService.getInstance();
        if (service == null) {
            Toast.makeText(this, "Please enable accessibility service", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "Replaying: " + gesture.getName(), Toast.LENGTH_SHORT).show();
        service.replayGesture(gesture, success -> runOnUiThread(() -> 
                Toast.makeText(this, success ? "Replay completed" : "Replay failed", Toast.LENGTH_SHORT).show()
        ));
    }

    private void showRenameDialog(Gesture gesture) {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setText(gesture.getName());

        new AlertDialog.Builder(this)
                .setTitle("Rename Gesture")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        database.renameGesture(gesture.getId(), newName);
                        loadGestures();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteDialog(Gesture gesture) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Gesture")
                .setMessage("Are you sure you want to delete " + gesture.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    database.deleteGesture(gesture.getId());
                    loadGestures();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGestures();
    }
}
