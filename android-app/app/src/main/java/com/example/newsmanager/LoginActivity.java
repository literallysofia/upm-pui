package com.example.newsmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText username;
    private TextInputEditText password;
    private TextView numTries;
    private MaterialButton loginButton;
    private int counter = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextInputLayout usernameField = findViewById(R.id.usernameText);
        username = new TextInputEditText(usernameField.getContext());

        TextInputLayout passwordField = findViewById(R.id.passwordText);
        password = new TextInputEditText(passwordField.getContext());

        numTries = findViewById(R.id.numTries);
        loginButton = findViewById(R.id.loginButton);

        numTries.setText("Number of attempts left: 5");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(username.getText().toString(), password.getText().toString());
            }
        });
    }


    private void validate(String userName, String userPassword) {

        if (((userName.equals("us_3_1")) && (userPassword.equals("48392"))) ||
                ((userName.equals("us_3_2")) && (userPassword.equals("48392"))) ||
                ((userName.equals("us_3_3")) && (userPassword.equals("48392")))) {

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

        } else {
            counter--;

            numTries.setText("Number of attempts left: " + String.valueOf(counter));

            if (counter == 0) {
                loginButton.setEnabled(false);
            }
        }
    }

}
