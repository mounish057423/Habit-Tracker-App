package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private List<Habit> habitList;
    private DatabaseReference databaseReference;
    private String username; // Added username to reference specific user's habits

    public HabitAdapter(List<Habit> habitList, String username) {
        this.habitList = habitList;
        this.username = username;
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username).child("habits");
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_item, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);
        holder.textViewHabitName.setText(habit.getName());
        holder.textViewHabitDescription.setText(habit.getDescription());

        // Reset the listener to avoid triggering it when setting the checkbox state
        holder.checkBoxCompleted.setOnCheckedChangeListener(null);
        holder.checkBoxCompleted.setChecked(habit.isCompleted());

        // Handle checkbox change
        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            habit.setCompleted(isChecked);
            updateHabitInFirebase(habit, holder.itemView);
        });

        // Handle delete button click
        holder.buttonDeleteHabit.setOnClickListener(v -> {
            deleteHabitFromFirebase(habit, holder.itemView);
        });

        // Handle item click for editing the habit
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditHabitActivity.class);
            intent.putExtra("username", username); // Ensure the username is passed
            intent.putExtra("habitId", habit.getId());
            intent.putExtra("habitName", habit.getName());
            intent.putExtra("habitDescription", habit.getDescription());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    private void updateHabitInFirebase(Habit habit, View itemView) {
        // Update the specific habit for the user
        databaseReference.child(habit.getId()).setValue(habit).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(itemView.getContext(), "Habit updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(itemView.getContext(), "Failed to update habit", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteHabitFromFirebase(Habit habit, View itemView) {
        String habitId = habit.getId();
        // Delete the specific habit for the user
        databaseReference.child(habitId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(itemView.getContext(), "Habit deleted", Toast.LENGTH_SHORT).show();

                // Find and remove the habit by ID instead of using the position
                for (int i = 0; i < habitList.size(); i++) {
                    if (habitList.get(i).getId().equals(habitId)) {
                        habitList.remove(i);
                        break;
                    }
                }

                // Refresh the entire list to prevent UI inconsistencies
                notifyDataSetChanged();
            } else {
                Toast.makeText(itemView.getContext(), "Failed to delete habit", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {

        TextView textViewHabitName, textViewHabitDescription;
        CheckBox checkBoxCompleted;
        ImageButton buttonDeleteHabit;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHabitName = itemView.findViewById(R.id.textViewHabitName);
            textViewHabitDescription = itemView.findViewById(R.id.textViewHabitDescription);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
            buttonDeleteHabit = itemView.findViewById(R.id.buttonDeleteHabit);
        }
    }
}
