package com.example.orderfood.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.orderfood.Activity.LoginActivity;
import com.example.orderfood.Activity.MainActivity;
import com.example.orderfood.Model.User;
import com.example.orderfood.Activity.OrderActivity;
import com.example.orderfood.MyService.UserService;
import com.example.orderfood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Fragment_Profile extends Fragment {

    private TextView txt_Order_Profile,txt_Change_Profile,txt_Pass_Profile,txt_LogOut, txt_Name_Profile,txt_Email_Profile;
    private FirebaseAuth auth;
    private MainActivity mMainActivity;
    private DatabaseReference referenceUser;
    private final String  emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private FirebaseUser user;
    private String nameUser, emailUser;
    private User currentUser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        
        // init view
        initView(view);


        // load info user
        loadInfoUser();
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();

        // Log out
        txt_LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiaLogLogOut();

            }
        });

        // List order
        txt_Order_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mMainActivity, OrderActivity.class));
            }
        });

        // Change password
        txt_Pass_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogChangePassword();
            }
        });

        // Change info
        txt_Change_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogChangeInfo();
            }
        });
    }
    // ============ SHOW DIALOG CHANGE INFO ============
    private void showDialogChangeInfo() {
        Dialog dialogInfo = new Dialog(getContext());
        dialogInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInfo.setContentView(R.layout.dialog_change_info);

        Window windowChangePass = dialogInfo.getWindow();
        windowChangePass.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        windowChangePass.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextInputLayout layout_ChangeInfo_Name       = dialogInfo.findViewById(R.id.layout_ChangeInfo_Name);
        TextInputLayout layout_ChangeInfo_Email      = dialogInfo.findViewById(R.id.layout_ChangeInfo_Email);
        TextInputEditText edt_ChangeInfo_Name        = dialogInfo.findViewById(R.id.edt_ChangeInfo_Name);
        TextInputEditText edt_ChangeInfo_Email       = dialogInfo.findViewById(R.id.edt_ChangeInfo_Email);
        Button btn_ChangeInfo_Confirm                = dialogInfo.findViewById(R.id.btn_ChangeInfo_Confirm);
        Button btn_ChangeInfo_Cancel                 = dialogInfo.findViewById(R.id.btn_ChangeInfo_Cancel);

        // Set original value
        edt_ChangeInfo_Name.setText(nameUser);
        edt_ChangeInfo_Email.setText(emailUser);

        // Check value edit text
        checkValue(layout_ChangeInfo_Name,edt_ChangeInfo_Name);
        checkValue(layout_ChangeInfo_Email,edt_ChangeInfo_Email);

        // Button cancel
        btn_ChangeInfo_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInfo.dismiss();
            }
        });

        // Button confirm
        btn_ChangeInfo_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = Objects.requireNonNull(edt_ChangeInfo_Name.getText()).toString().trim();
                String email = Objects.requireNonNull(edt_ChangeInfo_Email.getText()).toString().trim();
                
                if (name.isEmpty() || email.isEmpty())
                {
                    Toast.makeText(getActivity(), "Không được bỏ trống dữ liệu", Toast.LENGTH_SHORT).show();
                }
                else if (!email.matches(emailPattern))
                {
                    Toast.makeText(getActivity(), "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    changeInfoUser(name,email);
                }
            }
        });

        // show dialog change info
        dialogInfo.show();
    }

    // ============ CHANGE INFO USER ============
    private void changeInfoUser(String name, String email) {
        ProgressDialog dialogUpdateInfo = new ProgressDialog(getContext());
        dialogUpdateInfo.setMessage("Đang cập nhật");
        dialogUpdateInfo.show();

        referenceUser.child(user.getUid()).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialogUpdateInfo.dismiss();
                if (task.isSuccessful())
                {
                    user.updateEmail(email);
                    loadInfoUser();
                    Toast.makeText(getActivity(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity(), "Vui long kiểm tra lại !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // ============ LOAD INFO USER ============
    private void loadInfoUser() {

        if (user != null)
        {
            emailUser = user.getEmail();
            txt_Email_Profile.setText(emailUser);

            referenceUser.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    nameUser = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    txt_Name_Profile.setText(nameUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    // ============ SHOW DIALOG LOG OUT ============
    private void showDiaLogLogOut() {
        Dialog dialogLogOut = new Dialog(getContext());
        dialogLogOut.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLogOut.setContentView(R.layout.dialog_log_out);

        Window windowChangePass = dialogLogOut.getWindow();
        windowChangePass.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        windowChangePass.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // init view
        Button btn_logOut = dialogLogOut.findViewById(R.id.btn_logOut);
        TextView txt_Cancel = dialogLogOut.findViewById(R.id.txt_Cancel_LogOut);


        // txt Cancel
        txt_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLogOut.dismiss();
            }
        });

        // Button confirm
        btn_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                startActivity(new Intent(mMainActivity, LoginActivity.class));
                requireActivity().finishAffinity();
            }
        });
        dialogLogOut.show();
    }

    // ============ SHOW DIALOG CHANGE PASSWORD ============
    private void showDialogChangePassword() {
        Dialog dialogChangePass = new Dialog(getContext());
        dialogChangePass.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChangePass.setContentView(R.layout.dialog_change_password);
        dialogChangePass.setCanceledOnTouchOutside(false);

        Window windowChangePass = dialogChangePass.getWindow();
        windowChangePass.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        windowChangePass.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // init view
        TextInputLayout layout_PassNew          = dialogChangePass.findViewById(R.id.layout_ChangePass_New);
        TextInputLayout layout_PassConfirm      = dialogChangePass.findViewById(R.id.layout_ChangePass_Confirm);
        TextInputEditText edt_PassNew           = dialogChangePass.findViewById(R.id.edt_ChangePass_New);
        TextInputEditText edt_PassConfirm       = dialogChangePass.findViewById(R.id.edt_ChangePass_Confirm);
        Button btn_Cancel                       = dialogChangePass.findViewById(R.id.btn_Cancel_Change_Pass);
        Button btn_Confirm                      = dialogChangePass.findViewById(R.id.btn_Confirm_Change_Pass);

        // Check value edit text
        checkValue(layout_PassNew,edt_PassNew);
        checkValue(layout_PassConfirm,edt_PassConfirm);

        // Button cancel
        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChangePass.dismiss();
            }
        });

        // Button confirm
        btn_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass_new = Objects.requireNonNull(edt_PassNew.getText()).toString().trim();
                String pass_confirm = Objects.requireNonNull(edt_PassConfirm.getText()).toString().trim();
                
                if (pass_new.isEmpty() || pass_confirm.isEmpty())
                {
                    Toast.makeText(getActivity(), "Vui lòng không bỏ trống", Toast.LENGTH_SHORT).show();
                }
                else if (!pass_new.equals(pass_confirm))
                {
                    Toast.makeText(getActivity(), "Mật khẩu nhập lại không đúng", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ProgressDialog dialogProgressChangePass = new ProgressDialog(getActivity());
                    dialogProgressChangePass.setMessage("Đang cập nhật");
                    dialogProgressChangePass.show();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    user.updatePassword(pass_new)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dialogChangePass.dismiss();
                                    dialogProgressChangePass.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        dialogChangePass.show();
    }

    // ============ Check Value Edit text ============
    private void checkValue(TextInputLayout layout, TextInputEditText editText){

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                {
                    layout.setError("Không được bỏ trống");
                    editText.setFocusable(true);
                }
                else {
                    layout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // ============ INIT VIEW ============
    private void initView(View view) {
        txt_Change_Profile  = view.findViewById(R.id.txt_Change_Profile);
        txt_Order_Profile   = view.findViewById(R.id.txt_Order_Profile);
        txt_Pass_Profile    = view.findViewById(R.id.txt_Pass_Profile);
        txt_LogOut          = view.findViewById(R.id.txt_LogOut);
        txt_Name_Profile    = view.findViewById(R.id.txt_Name_Profile);
        txt_Email_Profile   = view.findViewById(R.id.txt_Email_Profile);
        mMainActivity       = (MainActivity) getActivity();
        referenceUser       = FirebaseDatabase.getInstance().getReference("User");
        user                = FirebaseAuth.getInstance().getCurrentUser();
    }
}
