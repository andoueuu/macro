package com.assistivetouch.simple.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.assistivetouch.simple.R;
import com.assistivetouch.simple.models.Gesture;
import java.util.ArrayList;
import java.util.List;

public class GestureAdapter extends RecyclerView.Adapter<GestureAdapter.GestureViewHolder> {
    private List<Gesture> gestures = new ArrayList<>();
    private OnGestureClickListener replayListener;
    private OnGestureClickListener renameListener;
    private OnGestureClickListener deleteListener;

    public interface OnGestureClickListener {
        void onClick(Gesture gesture);
    }

    public GestureAdapter(OnGestureClickListener replayListener, 
                         OnGestureClickListener renameListener,
                         OnGestureClickListener deleteListener) {
        this.replayListener = replayListener;
        this.renameListener = renameListener;
        this.deleteListener = deleteListener;
    }

    public void setGestures(List<Gesture> gestures) {
        this.gestures = gestures;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GestureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gesture, parent, false);
        return new GestureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GestureViewHolder holder, int position) {
        Gesture gesture = gestures.get(position);
        holder.tvName.setText(gesture.getName());
        holder.tvDuration.setText(gesture.getDuration() + "ms");

        holder.btnReplay.setOnClickListener(v -> replayListener.onClick(gesture));
        holder.btnRename.setOnClickListener(v -> renameListener.onClick(gesture));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onClick(gesture));
    }

    @Override
    public int getItemCount() {
        return gestures.size();
    }

    static class GestureViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDuration;
        ImageButton btnReplay, btnRename, btnDelete;

        GestureViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvGestureName);
            tvDuration = itemView.findViewById(R.id.tvGestureDuration);
            btnReplay = itemView.findViewById(R.id.btnReplay);
            btnRename = itemView.findViewById(R.id.btnRename);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
