package com.example.newsmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import es.upm.hcid.pui.assignment.exceptions.AuthenticationError;

public class LoginActivity extends AppCompatActivity {

    private DataManager dataManager;
    private TextInputEditText username;
    private TextInputEditText password;
    private MaterialButton loginButton;
    private MaterialButton continueButton;

    private TextView numTries;
    private int counter = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.editUser);
        password = findViewById(R.id.editPass);
        numTries = findViewById(R.id.numTries);
        loginButton = findViewById(R.id.loginButton);
        continueButton = findViewById(R.id.continueButton);

        numTries.setText("Number of attempts left: 5");

        dataManager = DataManager.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManagerTask loginManagerTask = new LoginManagerTask();
                loginManagerTask.execute(username.getText().toString(), password.getText().toString());
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

    }


    private final class LoginManagerTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                dataManager.getModelManager().login(strings[0], strings[1]);
                return true;

            } catch (AuthenticationError authenticationError) {
                authenticationError.printStackTrace();
                Log.e(authenticationError.toString(), "credentials -" + strings[0] + "-" + strings[1]);
                return false;
            }


        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                Toast.makeText(getApplicationContext(), "You have successfully logged in!", Toast.LENGTH_LONG).show();
                dataManager.setCurrentUser(LoginActivity.this.username.getText().toString());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                counter--;
                numTries.setText("Number of attempts left: " + counter);

                if (counter == 0) {
                    loginButton.setEnabled(false);
                }
            }

        }
    }

    // for testing purposes
    private void validate(String userName, String userPassword) {

        if (((userName.equals("us_3_1")) && (userPassword.equals("48392"))) ||
                ((userName.equals("us_3_2")) && (userPassword.equals("48392"))) ||
                ((userName.equals("us_3_3")) && (userPassword.equals("48392")))) {

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

        } else {
            counter--;

            numTries.setText("Number of attempts left: " + counter);

            if (counter == 0) {
                loginButton.setEnabled(false);
            }
        }
    }

}
