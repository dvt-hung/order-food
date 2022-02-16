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
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    TextInputEditText edt_SignUp_YourName, edt_SignUp_Username, edt_SignUp_Password,edt_SignUp_Password_Confirm;
    TextInputLayout layout_SignUp_YourName, layout_SignUp_Username, layout_SignUp_Password,layout_SignUp_Password_Confirm;

    Button btn_SignUp_Confirm;
    DatabaseReference referenceUser;
    private final String  emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // initView
        initView();

        //init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        referenceUser = database.getReference("User");

        // check value
        checkValueEditText(layout_SignUp_YourName,edt_SignUp_YourName);
        checkValueEditText(layout_SignUp_Username,edt_SignUp_Username);
        checkValueEditText(layout_SignUp_Password,edt_SignUp_Password);
        checkValueEditText(layout_SignUp_Password_Confirm,edt_SignUp_Password_Confirm);


    }

    @Override
    protected void onResume() {
        super.onResume();
        // Button Sign Up
        btn_SignUp_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String yourName = Objects.requireNonNull(edt_SignUp_YourName.getText()).toString().trim();
                String userName = Objects.requireNonNull(edt_SignUp_Username.getText()).toString().trim();
                String passWord = Objects.requireNonNull(edt_SignUp_Password.getText()).toString().trim();
                String passWordConfirm = Objects.requireNonNull(edt_SignUp_Password_Confirm.getText()).toString().trim();
                if (yourName.isEmpty() || userName.isEmpty() || passWord.isEmpty() || passWordConfirm.isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "Không được bỏ trống thông tin", Toast.LENGTH_SHORT).show();
                }
                else if (!userName.matches(emailPattern))
                {
                    Toast.makeText(SignUpActivity.this, "Bạn chưa nhập đúng định dạng Email", Toast.LENGTH_SHORT).show();
                }else if (passWord.equals(passWordConfirm))
                {
                    // add user
                    signUpUser(yourName,userName,passWord);
                }
                else
                {
                    Toast.makeText(SignUpActivity.this, "Xác nhận mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void signUpUser(String name, String userName, String passWord) {

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Vui lòng đợi...");
        dialog.show();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(userName, passWord)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null)
                            {
                                User user = new User(name,currentUser.getUid(),false);
                                addUser(user);
                            }
                        }

                    }
                });
    }

    private void addUser(User user) {
        referenceUser.child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    Toast.makeText(SignUpActivity.this, "Đã sai sót ở đâu đó", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
        edt_SignUp_Username                 = findViewById(R.id.edt_SignUp_Username);
        layout_SignUp_Username              = findViewById(R.id.layout_SignUp_Username);
        edt_SignUp_Password                 = findViewById(R.id.edt_SignUp_Password);
        layout_SignUp_Password              = findViewById(R.id.layout_SignUp_Password);
        edt_SignUp_Password_Confirm         = findViewById(R.id.edt_SignUp_Password_Confirm);
        layout_SignUp_Password_Confirm      = findViewById(R.id.layout_SignUp_Password_Confirm);
        edt_SignUp_YourName                 = findViewById(R.id.edt_SignUp_YourName);
        layout_SignUp_YourName              = findViewById(R.id.layout_SignUp_YourName);
        btn_SignUp_Confirm                  = findViewById(R.id.btn_SignUp_Confirm);
    }
}