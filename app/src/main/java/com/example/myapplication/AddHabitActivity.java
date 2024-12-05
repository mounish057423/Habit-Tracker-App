package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.FirebaseApp;

public class AddHabitActivity extends AppCompatActivity {

    private EditText editTextHabitName, editTextHabitDescription;
    private Button buttonSaveHabit;
    private DatabaseReference databaseReference;

    private String username; // To store the username passed from MainActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        // Enable ActionBar and the "Up" button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Habit");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black);
        }

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        editTextHabitName = findViewById(R.id.editTextHabitName);
        editTextHabitDescription = findViewById(R.id.editTextHabitDescription);
        buttonSaveHabit = findViewById(R.id.buttonSaveHabit);

        // Retrieve username passed from MainActivity
        username = getIntent().getStringExtra("username");
        if (username == null) {
            Toast.makeText(this, "No user logged in. Returning to home screen.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase Database reference for the specific user
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username).child("habits");

        // Handle save habit button click
        buttonSaveHabit.setOnClickListener(v -> {
            String name = editTextHabitName.getText().toString().trim();
            String description = editTextHabitDescription.getText().toString().trim();

            if (!name.isEmpty() && !description.isEmpty()) {
                // Generate a unique ID for the habit
                String id = databaseReference.push().getKey();
                if (id != null) {
                    Habit habit = new Habit(id, name, description, false);
                    databaseReference.child(id).setValue(habit).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddHabitActivity.this, "Habit added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddHabitActivity.this, "Failed to add habit", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Error generating habit ID", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle "Up" button to navigate back
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
