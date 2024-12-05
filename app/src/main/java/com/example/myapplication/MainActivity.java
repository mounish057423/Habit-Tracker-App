package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHabits;
    private HabitAdapter habitAdapter;
    private List<Habit> habitList;
    private DatabaseReference databaseReference;
    private FloatingActionButton fabAddHabit;

    private String username; // To store the logged-in user's username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewHabits = findViewById(R.id.recyclerViewHabits);
        recyclerViewHabits.setLayoutManager(new LinearLayoutManager(this));

        habitList = new ArrayList<>();

        // Get the logged-in user's username
        username = getIntent().getStringExtra("username");
        if (username == null) {
            Toast.makeText(this, "No user logged in. Returning to login screen.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize HabitAdapter after retrieving username
        habitAdapter = new HabitAdapter(habitList, username);
        recyclerViewHabits.setAdapter(habitAdapter);

        // Initialize Firebase Database for the specific user
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username).child("habits");

        // Initialize FloatingActionButton
        fabAddHabit = findViewById(R.id.fabAddHabit);

        // Set click listener for FloatingActionButton
        fabAddHabit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddHabitActivity.class);
            intent.putExtra("username", username); // Pass the username to the AddHabitActivity
            startActivity(intent);
        });

        // Fetch data from Firebase
        fetchHabitsFromFirebase();
    }

    private void fetchHabitsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                habitList.clear();
                for (DataSnapshot habitSnapshot : snapshot.getChildren()) {
                    Habit habit = habitSnapshot.getValue(Habit.class);
                    if (habit != null) {
                        habitList.add(habit);
                    }
                }
                habitAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load habits.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
