package com.example.taxibooking.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.taxibooking.BaseActivity;
import com.example.taxibooking.R;
import com.example.taxibooking.data.prefrence.SessionManager;
import com.example.taxibooking.databinding.ActivityLoginBinding;
import com.example.taxibooking.ui.home.HomeActivity;
import com.example.taxibooking.utils.NetworkManager;
import com.example.taxibooking.utils.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    private final FirebaseFirestore fb = getFireStoreInstance();
    private boolean isMatch;
    private long mLastClickTime = 0;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(LoginActivity.this);
        initView();
    }

    private void initView() {
        binding.tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validation.isValidEmail(Objects.requireNonNull(binding.txtEmail.getText()).toString())) {
                    if (!Objects.requireNonNull(binding.txtPass.getText()).toString().isEmpty()) {
                        login(binding.txtEmail.getText().toString(), binding.txtPass.getText().toString());
                    } else
                        binding.txtPass.setError("Please enter valid password");
                } else
                    binding.txtEmail.setError("Please enter valid email");
            }
        });
    }

    private void login(String userName, String passWord) {
        if (NetworkManager.isNetworkAvailable(LoginActivity.this)) {
            binding.containerNoInternet.setVisibility(View.GONE);
            showLoading(this);
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            fb.collection("User")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                hideLoading();
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    if (Objects.requireNonNull(documentSnapshot.get("email")).toString().equals(userName) &&
                                            Objects.requireNonNull(documentSnapshot.get("password")).toString().equals(passWord)) {
                                        isMatch = true;
                                        sessionManager.setUserId(documentSnapshot.get("userid").toString());
                                        sessionManager.setDocumentId(documentSnapshot.getId());
                                        sessionManager.setUserName(documentSnapshot.get("username").toString());
                                        sessionManager.setMobile(documentSnapshot.get("mobile").toString());
                                        sessionManager.setLogin(true);
                                        Log.e("uname", documentSnapshot.get("username").toString());
                                        showToast(LoginActivity.this, "Login Successfully");
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finishAffinity();
                                    } else
                                        isMatch = false;
                                }

                                if (!isMatch)
                                    showToast(LoginActivity.this, "Enter valid user details");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideLoading();
                            Log.e("e", String.valueOf(e));
                            showToast(LoginActivity.this, getString(R.string.error));
                        }
                    });
        } else
            binding.containerNoInternet.setVisibility(View.VISIBLE);
        // showSnackBar(binding.getRoot(), getString(R.string.check_internet));
    }
}