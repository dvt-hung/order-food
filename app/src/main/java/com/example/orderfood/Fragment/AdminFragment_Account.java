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
import com.example.orderfood.Activity.SaleActivity;
import com.example.orderfood.MyService.AdminService;
import com.example.orderfood.MyService.UserService;
import com.example.orderfood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class AdminFragment_Account extends Fragment {
    private TextView txt_Sale_Admin, txt_Pass_Admin, txt_LogOut_Admin;
    private FirebaseAuth auth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.admin_fragment_account,container,false);

        // init view 
        initView(view);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Log out
        txt_LogOut_Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiaLogLogOut();
            }
        });

        // Change password
        txt_Pass_Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogChangePassword();
            }
        });

        // Sale
        txt_Sale_Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), SaleActivity.class));
            }
        });
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
                startActivity(new Intent(requireActivity(), LoginActivity.class));
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

    private void initView(View view) {
        txt_Sale_Admin      = view.findViewById(R.id.txt_Sale_Admin);
        txt_Pass_Admin      = view.findViewById(R.id.txt_Pass_Admin);
        txt_LogOut_Admin    = view.findViewById(R.id.txt_LogOutAdmin);
    }
}
