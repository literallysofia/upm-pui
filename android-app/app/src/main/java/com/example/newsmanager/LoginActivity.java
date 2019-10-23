package com.example.newsmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private EditText Username;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private int counter=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Username = (EditText) findViewById(R.id.editUsername);
        Password = (EditText) findViewById(R.id.editPassword);
        Info = (TextView) findViewById(R.id.textInfo);
        Login = (Button) findViewById(R.id.btnLogin);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Username.getText().toString(), Password.getText().toString());
            }
        });
    }


    private void validate( String userName, String userPassword){

        if( (userName.equals("Admin")) && (userPassword.equals("1234"))){

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

        }else{
            counter--;

            Info.setText("Number of attempts leff: " + String.valueOf(counter));

            if(counter == 0){
                Login.setEnabled(false);
            }
        }
    }

}
