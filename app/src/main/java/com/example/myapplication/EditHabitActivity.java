package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditHabitActivity extends AppCompatActivity {

    private EditText editTextHabitName, editTextHabitDescription;
    private Button buttonUpdateHabit;
    private DatabaseReference databaseReference;
    private String habitId, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_habit);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Habit");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black);
        }

        editTextHabitName = findViewById(R.id.editTextHabitName);
        editTextHabitDescription = findViewById(R.id.editTextHabitDescription);
        buttonUpdateHabit = findViewById(R.id.buttonUpdateHabit);

        habitId = getIntent().getStringExtra("habitId");
        username = getIntent().getStringExtra("username");
        String habitName = getIntent().getStringExtra("habitName");
        String habitDescription = getIntent().getStringExtra("habitDescription");

        if (habitId == null || username == null || habitName == null || habitDescription == null) {
            Toast.makeText(this, "Failed to load habit details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editTextHabitName.setText(habitName);
        editTextHabitDescription.setText(habitDescription);

        databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(username)
                .child("habits")
                .child(habitId);

        buttonUpdateHabit.setOnClickListener(v -> {
            String updatedName = editTextHabitName.getText().toString().trim();
            String updatedDescription = editTextHabitDescription.getText().toString().trim();

            if (!updatedName.isEmpty() && !updatedDescription.isEmpty()) {
                databaseReference.child("name").setValue(updatedName);
                databaseReference.child("description").setValue(updatedDescription)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditHabitActivity.this, "Habit updated successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(EditHabitActivity.this, "Failed to update habit", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(EditHabitActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
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
