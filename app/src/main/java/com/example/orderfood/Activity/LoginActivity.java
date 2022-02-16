package com.example.orderfood.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.orderfood.R;

public class LoginActivity extends AppCompatActivity {

    private Button btn_LoginSignIn,btn_LoginSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_LoginSignIn = (Button) findViewById(R.id.btn_LoginSignIn);
        btn_LoginSignUp = (Button) findViewById(R.id.btn_LoginSignUp);



            // onClick SignIn
            btn_LoginSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this,SignInActivity.class));
                }
            });

            btn_LoginSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                }
            });

    }
}