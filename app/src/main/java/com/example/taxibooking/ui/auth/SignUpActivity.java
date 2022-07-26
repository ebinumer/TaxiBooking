package com.example.taxibooking.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.taxibooking.BaseActivity;
import com.example.taxibooking.R;
import com.example.taxibooking.data.prefrence.SessionManager;
import com.example.taxibooking.databinding.ActivitySignUpBinding;
import com.example.taxibooking.ui.home.HomeActivity;
import com.example.taxibooking.utils.NetworkManager;
import com.example.taxibooking.utils.Validation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends BaseActivity {
    private ActivitySignUpBinding binding;
    private long mLastClickTime = 0;
    private String uuId;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(this);
        btnClick();
    }

    private void btnClick() {
        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (binding.txtUname.getText().toString() != null && !binding.txtUname.getText().toString().isEmpty()) {
                    if (Validation.isValidEmail(Objects.requireNonNull(binding.txtEmail.getText()).toString())) {
                        if (binding.txtMob.getText().toString() != null && !binding.txtMob.getText().toString().isEmpty()) {
                            if (Validation.isValidPassword(Objects.requireNonNull(binding.txtPass.getText()).toString())) {

                                registerUser();

                            } else binding.txtPass.setError("Please enter valid password");
                        } else binding.txtMob.setError("Please enter mobile number");
                    } else binding.txtEmail.setError("Please enter valid email");
                } else binding.txtUname.setError("Please enter valid name");
            }
        });
    }

    private void registerUser() {
        if (NetworkManager.isNetworkAvailable(SignUpActivity.this)) {
            showLoading(this);
            sessionManager.setUserName(binding.txtUname.getText().toString());
            sessionManager.setMobile(binding.txtMob.getText().toString());
            Map<String, Object> user = new HashMap<>();
            user.put("username", binding.txtUname.getText().toString());
            user.put("email", binding.txtEmail.getText().toString());
            user.put("password", binding.txtPass.getText().toString());
            user.put("mobile", binding.txtMob.getText().toString());

            FirebaseFirestore fireStoreInstance = getFireStoreInstance();
            fireStoreInstance.collection("User")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            hideLoading();
                            sessionManager.setLogin(true);
                            sessionManager.setUserName(binding.txtUname.getText().toString());
                            sessionManager.setDocumentId(documentReference.getId());
                            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            showToast(SignUpActivity.this, "Registered Successfully");
                            startActivity(intent);
                            finishAffinity();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideLoading();
                            showToast(SignUpActivity.this, getString(R.string.error));
                        }
                    });
        } else {
            binding.containerNoInternet.setVisibility(View.VISIBLE);
        }
    }
}