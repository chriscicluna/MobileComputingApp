package com.example.gaminglog.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gaminglog.MainActivity;
import com.example.gaminglog.R;
import com.example.gaminglog.data.UserDataManager;

public class RegisterActivity extends AppCompatActivity {
    private UserDataManager userDataManager;
    private EditText editTextEmail, editTextUsername, editTextPassword;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userDataManager = new UserDataManager(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        Button loginButton = findViewById(R.id.buttonGoToLogin);

        buttonRegister.setOnClickListener(view -> registerUser());
        loginButton.setOnClickListener(v -> goToLogin());
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // validation for fields
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email address.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (username.isEmpty() || username.length() < 3) {
            Toast.makeText(this, "Username must be at least 3 characters long.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidPassword(password)) {
            Toast.makeText(this, "Password must be at least 6 characters and include a number.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userDataManager.isEmailExists(email)) {
            Toast.makeText(this, "Email is already in use.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userDataManager.isUsernameExists(username)) {
            Toast.makeText(this, "Username is already taken.", Toast.LENGTH_SHORT).show();
            return;
        }

        long userId = userDataManager.addUser(username, email, password);
        if (userId != -1) {
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to register user.", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // checks that password is at least 6 characters long and contains a number
    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches(".*\\d.*");
    }

    public void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
