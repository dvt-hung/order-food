package com.example.orderfood.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.orderfood.Model.User;
import com.example.orderfood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private TextInputEditText edt_SignIn_Username, edt_SignIn_Password;
    private TextInputLayout layout_SignIn_Username, layout_SignIn_Password;
    private Button btn_SignIn_Confirm;
    private ProgressDialog progressDialog;
    private DatabaseReference referenceUser;
    public static FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        // initView
        initView();

        // init Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        referenceUser = database.getReference("User");

        // check value
        checkValueEditText(layout_SignIn_Username, edt_SignIn_Username);
        checkValueEditText(layout_SignIn_Password, edt_SignIn_Password);
    }

    @Override
    protected void onResume() {
        super.onResume();

        btn_SignIn_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Objects.requireNonNull(edt_SignIn_Username.getText()).toString().trim();
                String password = Objects.requireNonNull(edt_SignIn_Password.getText()).toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Không được bỏ trống thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        signIn();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void signIn() {
        progressDialog.setMessage("Vui lòng đợi....");
        progressDialog.show();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            referenceUser.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss();
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        if (!user.isAdmin()) {
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        } else {
                            startActivity(new Intent(SignInActivity.this, AdminMainActivity.class));
                        }
                    }
                    finishAffinity();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    // ============ Check Value Order ============
    private void checkValueEditText(TextInputLayout layout, TextInputEditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    layout.setError("Không được bỏ trống");
                    editText.setFocusable(true);
                } else {
                    layout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void initView() {
        progressDialog = new ProgressDialog(this);
        edt_SignIn_Username = findViewById(R.id.edt_SignIn_Username);
        layout_SignIn_Username = findViewById(R.id.layout_SignIn_Username);
        edt_SignIn_Password = findViewById(R.id.edt_SignIn_Password);
        layout_SignIn_Password = findViewById(R.id.layout_SignIn_Password);
        btn_SignIn_Confirm = findViewById(R.id.btn_SignIn_Confirm);
    }
}